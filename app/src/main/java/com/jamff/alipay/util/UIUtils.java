package com.jamff.alipay.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;

/**
 * description:
 * author: JamFF
 * time: 2018/12/11 19:38
 */
public class UIUtils {

    private static Application sContext;
    private static Handler sHandler;
    private static int sMainThreadId;


    public static void init(Application context, Handler handler, int mainThreadId) {
        sContext = context;
        sHandler = handler;
        sMainThreadId = mainThreadId;
    }

    /**
     * 得到上下文
     */
    public static Context getContext() {
        return sContext;
    }

    /**
     * 得到resouce对象
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 得到string.xml中的一个字符串
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 得到string.xml中的一个字符串数组
     */
    public static String[] getStringArr(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 得到color.xml中的颜色值
     */
    public static int getColor(int colorId) {
        return getResources().getColor(colorId);
    }

    /**
     * 得到应用程序的包名
     */
    public static String getPackageName() {
        return getContext().getPackageName();
    }

    /**
     * 得到主线程id
     */
    public static int getMainThreadId() {
        return sMainThreadId;
    }

    /**
     * 得到一个主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return sHandler;
    }

    /**
     * 安全的执行一个task
     */
    public static void postTaskSafely(Runnable task) {
        int curThreadId = android.os.Process.myTid();
        int mainThreadId = getMainThreadId();
        // 如果当前线程是主线程
        if (curThreadId == mainThreadId) {
            task.run();
        } else {
            // 如果当前线程不是主线程
            getMainThreadHandler().post(task);
        }
    }

    /**
     * 安全的延迟执行一个task
     */
    public static void postDelayedTaskSafely(Runnable task, long delayMillis) {
        getMainThreadHandler().postDelayed(task, delayMillis);
    }

    /**
     * 获取versionCode
     */
    public static int getVersionCode() {
        try {
            PackageInfo pi = UIUtils.getContext().getPackageManager().getPackageInfo(UIUtils.getContext().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取versionName
     */
    public static String getVersionName() {
        try {
            PackageInfo pi = UIUtils.getContext().getPackageManager().getPackageInfo(UIUtils.getContext().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }
}
