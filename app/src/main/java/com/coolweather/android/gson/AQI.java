package com.coolweather.android.gson;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AQI {
    public Basic basic;
    public Update update;
    public String status;
    @SerializedName("air_now_city")
    public AirNow airNow;

}
