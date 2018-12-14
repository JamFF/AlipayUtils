package com.jamff.alipay.util;

/**
 * description:
 * author: JamFF
 * time: 2018/12/11 19:32
 */
public class StringUtils {

    public static boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
