package com.coolweather.android.gson;

import com.google.gson.annotations.SerializedName;

public class Lifestyle {

    //舒适指数
   @SerializedName("brf")
    public String lifeIndex;
   @SerializedName("type")
    public String lifeType;
   @SerializedName("txt")
    public String lifeDetail;
}
