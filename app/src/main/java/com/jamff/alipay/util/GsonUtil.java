package com.jamff.alipay.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 21:05
 */
public class GsonUtil {

    private static Gson sGson = new Gson();

    /**
     * 解析json数据
     *
     * @param json  json数据
     * @param clazz 映射的类
     * @param <T>   泛型
     * @return T类元素
     */
    public static <T> T json2Bean(String json, Class<T> clazz) {
        return sGson.fromJson(json, clazz);
    }

    /**
     * 解析jsonArray
     *
     * @param json  json数据
     * @param clazz 映射的类
     * @param <T>   泛型
     * @return ArrayList
     */
    public static <T> ArrayList<T> json2BeanArray(String json, Class<T> clazz) {
        ArrayList<T> list = new ArrayList<>();
        try {
            JsonArray jsonArray = new JsonParser().parse(json).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                list.add(sGson.fromJson(element, clazz));
            }
            return list;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象的属性转换json数据
     *
     * @param object java对象
     * @return json或jsonArray
     */
    public static String bean2Json(Object object) {
        return sGson.toJson(object);
    }
}