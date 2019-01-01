package com.jamff.alipay;

import android.os.Environment;

import java.io.File;

public interface Constant {

    boolean IS_DEBUG = false;// log日志开关

    boolean IS_WRITE_FILE = false;// log写文件开关

    boolean IS_NEW = true;// 账单批量抓单方式

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
     * 收款理由关键字
     */
    String KEY_RECEIPT = "收款";

    /**
     * 转账备注关键字
     */
    String KEY_REMARK = "备注";

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
     * 支付宝主界面
     */
    String ALIPAY_MAIN = "com.eg.android.AlipayGphone.AlipayLogin";

    /**
     * 账单主页面
     */
    String BILL_ACTIVITY = "com.alipay.mobile.bill.list.ui.BillMainListActivity";

    /**
     * 账单详细界面 WebView的父布局的id
     */
    String PAY_ROOT_ID = "com.alipay.mobile.nebula:id/h5_pc_container";

    /**
     * 账单详细界面 返回键TextView的id
     */
    String PAY_BACK_ID = "com.alipay.mobile.nebula:id/h5_tv_nav_back";

    /**
     * 底部"我的"RelativeLayout的id，在小米、vivo上不能找到
     */
    String HOME_ID = "com.alipay.android.phone.wealth.home:id/sigle_tab_bg";

    /**
     * 底部TabWidget的id
     */
    String TAB_ID = "android:id/tabs";

    /**
     * 账单父布局LinearLayout的id
     */
    String BILL_PARENT_ID = "com.alipay.android.phone.wealth.home:id/widget_container";

    /**
     * 底部TabWidget上面FrameLayout的id
     */
    String TAB_CONTENT_ID = "android:id/tabcontent";

    /**
     * 账单ListView中订单TextView的id
     */
    String BILL_NAME_ID = "com.alipay.mobile.bill.list:id/billName";

    /**
     * 账单ListView中金额TextView的id
     */
    String BILL_AMOUNT_ID = "com.alipay.mobile.bill.list:id/billAmount";

    /**
     * 账单ListView中金额TextView的id
     */
    String BILL_BACK_ID = "com.alipay.mobile.antui:id/back_button";
}
