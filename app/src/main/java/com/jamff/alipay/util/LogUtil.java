package com.jamff.alipay.util;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.jamff.alipay.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        printLine("I: " + tag + " : " + msg);
    }

    public static void d(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.d(tag, msg);
        }

        printLine("D: " + tag + " : " + msg);
    }

    public static void w(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.w(tag, msg);
        }

        printLine("W: " + tag + " : " + msg);
    }

    public static void e(String tag, String msg) {
        if (Constant.IS_DEBUG) {
            Log.e(tag, msg);
        }

        printLine("E: " + tag + " : " + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (Constant.IS_DEBUG) {
            Log.e(tag, msg, tr);
        }

        printLine("E: " + tag + " : " + msg);
    }

    private static void printLine(String msg) {
        if (!Constant.IS_WRITE_FILE || TextUtils.isEmpty(msg)) {
            return;
        }

        try {
            writeFileToSD(Environment.getExternalStorageDirectory().getPath() + Constant.LOG_PATH + "records/", getCurTime() + "_" + msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取特定格式的时间
     */
    private static String getCurTime() {
        String curTime = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd/ HH:mm:ss");
            curTime = "[" + formatter.format(System.currentTimeMillis()) + "] ";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curTime;
    }

    private static boolean writeFileToSD(String path, String logstr) {
        FileHelp.createFolderIfNotExist(path);
        File file = new File(path);
        File[] filelist = file.listFiles();
        int size;
        if (filelist == null) {
            size = 0;
        } else {
            size = filelist.length;
        }
        try {
            if (size <= 0) {
                String fileName = CreateSysTimeFileName();
                return FileHelp.saveFile(logstr, path, fileName, false);
            } else {
                for (int i = 0; i < size; i++) {
                    if (filelist[i].length() < 512000) {
                        return FileHelp.saveFile(logstr, path, filelist[i].getName(), true);
                    }
                }
                String fileName = CreateSysTimeFileName();
                return FileHelp.saveFile(logstr, path, fileName, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static String CreateSysTimeFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate) + ".txt";
    }
}
