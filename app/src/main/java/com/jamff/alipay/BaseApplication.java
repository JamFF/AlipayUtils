package com.jamff.alipay;

import android.app.Application;
import android.os.Handler;

import com.jamff.alipay.bean.LoginResultBean;
import com.jamff.alipay.util.CrashHandler;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.SPUtils;
import com.jamff.alipay.util.UIUtils;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 13:40
 */
public class BaseApplication extends Application {

    // 保存用户登录返回的信息
    private static LoginResultBean.DataBean userInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        UIUtils.init(this, new Handler(), android.os.Process.myTid());
        LogUtil.d(Constant.TAG_ACTIVITY, "versionName = " + UIUtils.getVersionName());
        CrashHandler.INSTANCE.init();
        SPUtils.INSTANCE.init();
    }

    public static LoginResultBean.DataBean getUserInfo() {
        return userInfo;
    }

    public static void setUserInfo(LoginResultBean.DataBean dataBean) {
        userInfo = dataBean;
    }
}
