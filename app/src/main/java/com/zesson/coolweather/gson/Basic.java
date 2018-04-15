package com.zesson.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2018/4/11.
 */

public class Basic {
    @SerializedName("city")
private String cityName;
    @SerializedName("id")
private String weatherID;

private Update update;
    public class Update
    {
        @SerializedName("loc")
        private String updateTime;
    }
}
