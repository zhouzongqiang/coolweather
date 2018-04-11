package com.zesson.coolweather.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zesson.coolweather.R;
import com.zesson.coolweather.db.City;
import com.zesson.coolweather.db.County;
import com.zesson.coolweather.db.Province;
import com.zesson.coolweather.util.HttpUtil;
import com.zesson.coolweather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/3/30.
 */

public class ChooseAreaFragment extends Fragment {
    private int currentLevel;
    public final static int LEVEL_PROVINCE = 0;
    public final static int LEVEL_CITY = 1;
    public final static int LEVEL_COUNTY = 2;
    private TextView chooseAreaTitleText;
    private Button chooseAreaBackBtn;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private List<String> dataList = new ArrayList<>();
    private ProgressDialog progressDialog;
    /**
     * 选中的省份
     */
    private Province selectedProvince;

    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 选中的城市
     */
    private City selectedCounty;
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County>countyList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("tag", "onCreateView");
        View view = inflater.inflate(R.layout.choose_area, container, false);
        chooseAreaTitleText = (TextView) view.findViewById(R.id.chooseAreaTitleText);
        chooseAreaBackBtn = (Button) view.findViewById(R.id.chooseAreaBackBtn);
        chooseAreaBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel == LEVEL_CITY)
                {
                    chooseAreaBackBtn.setVisibility(View.GONE);
                    queryProvinces();
                }
                if (currentLevel == LEVEL_COUNTY)
                {
                    queryCiyts();
                }
            }
        });
        listView = (ListView) view.findViewById(R.id.chooseAreaListView);
        arrayAdapter = new ArrayAdapter(getContext(), R.layout.simple_list_item_1, dataList);
        //  getContext()：这个是View类中提供的方法，在继承了View的类中才可以调用，
        //  返回的是当前View运行在哪个Activity Context中。
        // getActivity():获得Fragment依附的Activity对象。Fragment里边的getActivity()不推荐使用原因如下：
        //  这个方法会返回当前Fragment所附加的Activity，当Fragment生命周期结束并销毁时，getActivity()返回的是null，
        //  所以在使用时要注意判断null或者捕获空指针异常。
        listView.setAdapter(arrayAdapter);
        return view;
    }

    // fragment 生命周期的利用

    //  生命周期有 onAttach（），onCreate（），onCreateView（），onActivityCreated（）；此为创建时会执行的方法
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i("tag", "onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("tag",currentLevel+"");
                if(currentLevel == LEVEL_PROVINCE)
                {
                    selectedProvince = provinceList.get(position);
                    queryCiyts();
                }else if (currentLevel == LEVEL_CITY)
                {
                    selectedCity = cityList.get(position);
                    queryCounties();
                }

            }
        });
        queryProvinces();
    }

    private void queryProvinces(){
        chooseAreaTitleText.setText("中国");
        chooseAreaBackBtn.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);

        if(provinceList.size()>0)
        {
            Log.i("tag","数据库已存在数据");
            dataList.clear();
            for (Province province :provinceList)
            {
                dataList.add(province.getProvinceName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        }else
        {
            Log.i("tag","从服务器加载数据");
            String address = "http://guolin.tech/api/china";
            queryFormServer(address,"province");
        }

    }

    private void queryCiyts(){

        chooseAreaTitleText.setText(selectedProvince.getProvinceName());
        chooseAreaBackBtn.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectedProvince.getId())).find(City.class);
        if(cityList.size()>0)
        {
            dataList.clear();
            for (City city :cityList)
            {
                dataList.add(city.getCityName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        }else
        {
            int provinceCode = selectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/"+provinceCode;
            queryFormServer(address,"city");
        }
    }
    private void  queryCounties()
    {
        chooseAreaTitleText.setText(selectedCity.getCityName());
        chooseAreaBackBtn.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectedCity.getId())).find(County.class);
        if(countyList.size()>0)
        {
            dataList.clear();
            for (County county :countyList)
            {
                dataList.add(county.getCountyName());
            }
            arrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        }else
        {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFormServer(address,"county");
        }
    }
    private void  queryFormServer(String address,final String type)
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String responseText = response.body().string();
                Log.i("tag",responseText);

                boolean result =false;
                if ("province".equals(type))
                {
                    result = Utility.handleProvinceResponse(responseText);
                }else if ("city".equals(type))
                {
                    result = Utility.handleCityResponse(responseText,selectedProvince.getId());

                }else if ("county".equals(type))
                {
                    result = Utility.handleCountyResponse(responseText,selectedCity.getId());
                }

                if (result)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type))
                            {
                                queryProvinces();
                            }else if ("city".equals(type))
                            {
                                queryCiyts();
                            }else if ("county".equals(type))
                            {
                                queryCounties();
                            }
                        }
                    });

                }
            }
        });

    }

    private void showProgressDialog(){
        if (progressDialog == null)
        {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void  closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

}


