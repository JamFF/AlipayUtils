package com.jamff.alipay.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.jamff.alipay.Constant;

import java.io.File;

/**
 * description:
 * author: JamFF
 * time: 2018/12/22 15:50
 */
public class APKUtils {

    /**
     * 安装Apk
     */
    public static void installApk(String apkPath) {

        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Log.d(Constant.TAG_INSTALL, "SDK >= N，FileProvider进行安装");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(
                    UIUtils.getContext()
                    , UIUtils.getPackageName() + ".FileProvider" //BuildConfig.APPLICATION_ID + ".fileProvider"
                    , apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            Log.d(Constant.TAG_INSTALL, "SDK < N，普通安装");
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        UIUtils.getContext().startActivity(intent);
    }
}