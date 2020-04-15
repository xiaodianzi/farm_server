package com.plansolve.farm.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Author: 高一平
 * @Date: 2018/10/15
 * @Description:
 **/
public class JsonUtil {

    public static String toJson(Object object){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }

}
