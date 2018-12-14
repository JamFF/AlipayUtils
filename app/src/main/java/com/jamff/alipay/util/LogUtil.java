package com.jamff.alipay.util;

import android.util.Log;

import com.jamff.alipay.Constant;

/**
 * description:
 * author: JamFF
 * time: 2018/12/11 15:55
 */
public class LogUtil {

    public static void i(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (Constant.IS_DEBUG) {
            Log.e(tag, msg, tr);
        }
    }
}
