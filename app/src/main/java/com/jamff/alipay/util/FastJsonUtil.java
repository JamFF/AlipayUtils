package com.jamff.alipay.util;

import com.alibaba.fastjson.JSON;

/**
 * description:
 * author: JamFF
 * time: 2018/12/21 22:53
 */
public class FastJsonUtil {

    /**
     * 将对象的属性，按照key排序后，转换json数据
     *
     * @param object java对象
     * @return json或jsonArray
     */
    public static String bean2Json(Object object) {
        return JSON.toJSONString(object);
    }
}