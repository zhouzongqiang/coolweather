package com.zesson.coolweather.util;

import android.text.TextUtils;
import android.util.Log;

import com.zesson.coolweather.db.City;
import com.zesson.coolweather.db.County;
import com.zesson.coolweather.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2018/3/15.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response))//???TextUtils
            try
            {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0;i<allProvinces.length();i++)
                {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return  true;
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
            return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response))//???TextUtils
            try
            {
                JSONArray allCitys = new JSONArray(response);
                for (int i = 0;i<allCitys.length();i++)
                {
                    JSONObject cityObject = allCitys.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return  true;
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        return false;
    }
    public static boolean handleCountyResponse(String response,int cityId){
        Log.i("tag","reee===="+response);
        if (!TextUtils.isEmpty(response))//???TextUtils
            try
            {
                JSONArray allCountys = new JSONArray(response);
                for (int i = 0;i<allCountys.length();i++)
                {
                    JSONObject countyObject = allCountys.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return  true;
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        return false;
    }


}
