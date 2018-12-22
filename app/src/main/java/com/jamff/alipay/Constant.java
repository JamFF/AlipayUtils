package com.jamff.alipay;

import android.os.Environment;

import java.io.File;

public interface Constant {

    boolean IS_DEBUG = false;

    boolean IS_WRITE_FILE = false;

    String TAG_ACTIVITY = "tag_activity";

    String TAG_LOGIN = "tag_login_fragment";

    String TAG_TRADE = "tag_trade_fragment";

    String TAG_SERVICE = "tag_service";

    String TAG_HTTP = "tag_http";

    String TAG_PERMISSIONS = "tag_permissions";

    String TAG_INSTALL = "tag_install";

    String BASE_URL = "http://inter.arphoto.fun/";

    String SD_CARD = Environment.getExternalStorageDirectory().getAbsolutePath();

    String ROOT_PATH = SD_CARD + File.separator + "payUtils";

    String NEW_APK_PATH = ROOT_PATH + File.separator + "apk_new.apk";// 升级apk的目录

    String CRASH_PATH = ROOT_PATH + File.separator + "crash" + File.separator;// crash日志

    String LOG_PATH = ROOT_PATH + File.separator + "log" + File.separator;// log日志

    String MD5_KRY = "&key=pf567";

    /**
     * 网络请求成功
     */
    int HTTP_OK = 10000;

    int REQUEST_CODE = 666;

    /**
     * 通知栏转账关键字
     */
    // String KEY_NOTIFICATION = "成功向你转了";
    String KEY_NOTIFICATION = "通过扫码向你付款";

    /**
     * 消息界面转账关键字
     */
    String KEY_CHAT_MESSAGE = "你收到了 一条转账消息";

    /**
     * 转账备注关键字
     */
    String KEY_ORDER = "9850";

    /**
     * 支付宝包名
     */
    String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";

    /**
     * 聊天界面 UI的变化
     */
    String CHAT_ACTIVITY = "com.alipay.mobile.chatapp.ui.PersonalChatMsgActivity_";

    /**
     * 聊天界面 ListView的id
     */
    String CHAT_LIST_ID = "com.alipay.mobile.chatapp:id/chat_msg_list";

    /**
     * 聊天界面 金额TextView的id
     */
    String CHAT_DESC_ID = "com.alipay.mobile.chatapp:id/biz_desc";

    /**
     * 聊天界面 备注TextView的id
     */
    String CHAT_TITLE_ID = "com.alipay.mobile.chatapp:id/biz_title";

    /**
     * 聊天界面 返回键ImageButton的id
     */
    String CHAT_BACK_ID = "com.alipay.mobile.ui:id/title_bar_back_button";

    /**
     * 账单详细界面 UI的变化
     */
    String PAY_ACTIVITY = "com.alipay.mobile.nebulacore.ui.H5Activity";

    /**
     * 账单详细界面 WebView的父布局的id
     */
    String PAY_ROOT_ID = "com.alipay.mobile.nebula:id/h5_pc_container";

    /**
     * 账单详细界面 返回键TextView的id
     */
    String PAY_BACK_ID = "com.alipay.mobile.nebula:id/h5_tv_nav_back";
}
