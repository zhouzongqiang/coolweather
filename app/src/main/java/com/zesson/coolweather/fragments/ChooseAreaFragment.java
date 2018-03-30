package com.zesson.coolweather.fragments;

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

import com.zesson.coolweather.R;
import com.zesson.coolweather.db.City;
import com.zesson.coolweather.db.County;
import com.zesson.coolweather.db.Province;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

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


            }
        });
    }

    private void queryProvinces(){
        chooseAreaTitleText.setText("中国");
        chooseAreaBackBtn.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size()>0)
        {
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
            String address = "http://guolin.tech/api/china";
           // que
        }

    }
}


