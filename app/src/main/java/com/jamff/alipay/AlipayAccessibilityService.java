package com.jamff.alipay;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.jamff.alipay.api.ApiFactory;
import com.jamff.alipay.bean.NotifyParamBean;
import com.jamff.alipay.bean.NotifyResultBean;
import com.jamff.alipay.util.EncryptUtil;
import com.jamff.alipay.util.FastJsonUtil;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.UIUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * description:获取支付宝转账信息
 * author: JamFF
 * time: 2018/12/8 15:54
 */
public class AlipayAccessibilityService extends AccessibilityService {

    private final List<NotifyParamBean> mFailureBeans = new CopyOnWriteArrayList<>();

    private boolean isNotification;// 收到转账通知后进入聊天界面

    private AccessibilityNodeInfo webInfo;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 当启动服务的时候就会被调用
        LogUtil.d(Constant.TAG_SERVICE, "onServiceConnected: ");

        // FIXME: 2018/12/18 FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY废弃，不再使用
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;

        startTimeTask();
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!BaseApplication.isStart()) {
            LogUtil.i(Constant.TAG_SERVICE, "onAccessibilityEvent: start = false");
            return;
        }
        // 监听窗口变化的回调
        int eventType = event.getEventType();
        // 根据事件回调类型进行处理
        switch (eventType) {
            // 当通知栏发生改变时
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                LogUtil.i(Constant.TAG_SERVICE, "Notification bar changes");
                List<CharSequence> texts = event.getText();
                if (!texts.isEmpty()) {
                    for (CharSequence text : texts) {
                        String content = text.toString();
                        LogUtil.i(Constant.TAG_SERVICE, content);
                        if (content.contains(Constant.KEY_NOTIFICATION)) {
                            // 关闭上次账单详情界面，不关闭也无所谓
                            clickViewById(Constant.PAY_BACK_ID);
                            // 模拟点击通知栏消息，打开支付宝
                            startNotification(event);
                            break;
                        }
                    }
                }
                break;

            // 当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                LogUtil.d(Constant.TAG_SERVICE, "className = " + className);
                if (Constant.PAY_ACTIVITY.equals(className)) {
                    if (isNotification) {
                        // 打开账单详细界面
                        isNotification = false;
                        findTradeInfoDelayed();
                    } else {
                        LogUtil.w(Constant.TAG_SERVICE, "isNotification = false");
                    }
                }

                /*if (isNotification && Constant.CHAT_ACTIVITY.equals(className)) {
                    isNotification = false;
                    // 打开聊天界面
                    getLastMessage();
                }*/

                break;
        }
    }

    @Override
    public void onInterrupt() {
        // 中断服务的回调
        LogUtil.d(Constant.TAG_SERVICE, "onInterrupt: ");
    }

    private void startTimeTask() {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {

                if (!mFailureBeans.isEmpty()) {
                    for (NotifyParamBean bean : mFailureBeans) {
                        uploadData(bean);
                    }
                }

                UIUtils.postDelayedTaskSafely(this, 1000);
            }
        });
    }

    /**
     * 延时寻找转账信息
     */
    private void findTradeInfoDelayed() {
        UIUtils.postDelayedTaskSafely(new Runnable() {
            @Override
            public void run() {

                // WebView需要耗时
                findTradeInfo(findViewById(getRootInActiveWindow(), Constant.PAY_ROOT_ID));
            }
        }, 1000);
    }

    /**
     * 模拟点击通知栏消息，打开支付宝
     */
    private void startNotification(AccessibilityEvent event) {
        if (event.getParcelableData() != null &&
                event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event.getParcelableData();
            PendingIntent pendingIntent = notification.contentIntent;
            try {
                pendingIntent.send();
                isNotification = true;
                LogUtil.i(Constant.TAG_SERVICE, "进入支付宝");
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
                LogUtil.e(Constant.TAG_SERVICE, "onAccessibilityEvent: ", e);
            }
        }
    }

    /**
     * 在指定节点，根据id找到控件
     *
     * @param rootNode 父布局
     */
    private AccessibilityNodeInfo findViewById(AccessibilityNodeInfo rootNode, String id) {

        if (rootNode == null) {
            LogUtil.e(Constant.TAG_SERVICE, "findViewById: rootNode is null");
            return null;
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(id);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }

        LogUtil.e(Constant.TAG_SERVICE, "findViewById: Did not find id = " + id);
        return null;
    }

    /**
     * 在指定节点，根据text找到控件
     *
     * @param rootNode 父布局
     */
    private AccessibilityNodeInfo findViewByText(AccessibilityNodeInfo rootNode, String text) {

        if (rootNode == null) {
            LogUtil.e(Constant.TAG_SERVICE, "findViewById: rootNode is null");
            return null;
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByText(text);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }

        LogUtil.e(Constant.TAG_SERVICE, "findViewById: Did not find text = " + text);
        return null;
    }

    /**
     * 遍历指定节点，找到WebView
     */
    private void findWebViewNode(AccessibilityNodeInfo rootInfo) {

        for (int i = 0; i < rootInfo.getChildCount(); i++) {

            AccessibilityNodeInfo child = rootInfo.getChild(i);

            if (child.getClassName().equals("android.webkit.WebView")) {
                webInfo = child;
                return;
            }
            if (child.getChildCount() > 0) {
                findWebViewNode(child);
            }
        }
    }

    /**
     * 点击某个id控件
     */
    private void clickViewById(String back_id) {

        AccessibilityNodeInfo bt_back = findViewById(getRootInActiveWindow(), back_id);

        if (bt_back != null) {
            bt_back.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    /**
     * 获取聊天界面ListView中最后一个转账信息
     */
    /*private void getLastMessage() {

        LogUtil.i(Constant.TAG_SERVICE, "getLastMessage: ");
        findTradeInfoByChat(findViewById(getRootInActiveWindow(), Constant.CHAT_LIST_ID));

        if (!mParamBeans.isEmpty()) {
            uploadData(mParamBeans.get(mParamBeans.size() - 1));
        }
    }*/


    /**
     * 聊天界面找到转账消息{@link Constant#KEY_CHAT_MESSAGE}
     *
     * @param info listView
     */
    /*public void findTradeInfoByChat(AccessibilityNodeInfo info) {

        if (info == null) {
            LogUtil.e(Constant.TAG_SERVICE, "findTradeInfoByChat: listView is null");
            return;
        }

        mParamBeans.clear();

        for (int i = 0; i < info.getChildCount(); i++) {

            AccessibilityNodeInfo message = info.getChild(i);
            if (message != null && message.getContentDescription() != null) {
                if (message.getContentDescription().toString().contains(Constant.KEY_CHAT_MESSAGE)) {

                    // 金额的TextView
                    AccessibilityNodeInfo tv_desc = findViewById(message, Constant.CHAT_DESC_ID);
                    // 备注的TextView
                    AccessibilityNodeInfo tv_title = findViewById(message, Constant.CHAT_TITLE_ID);

                    if (tv_desc == null || tv_title == null) {
                        continue;
                    }

                    // 金额
                    String desc = tv_desc.getText().toString();
                    // 备注
                    String title = tv_title.getText().toString();

                    if (TextUtils.isEmpty(title) || !title.contains(Constant.KEY_ORDER)) {
                        LogUtil.d(Constant.TAG_SERVICE, "title not start with " + Constant.KEY_ORDER);
                        continue;
                    }

                    if (TextUtils.isEmpty(desc)) {
                        LogUtil.d(Constant.TAG_SERVICE, "desc is empty");
                        continue;
                    }

                    String trade_amount = desc.substring(0, desc.length() - 1)// 去除"元"
                            .replace(".", "")// 换算为分
                            .replaceFirst("^0*", "");// 去除头部"0"

                    mParamBeans.add(new NotifyParamBean(BaseApplication.getUserInfo().getDevice_id(), "9850181213213015459", trade_amount));
                }
            }
        }
    }*/

    /**
     * 转账详情页找到转账信息
     */
    public void findTradeInfo(final AccessibilityNodeInfo rootInfo) {

        Log.d(Constant.TAG_SERVICE, "findTradeInfo: ");

        if (rootInfo == null) {
            LogUtil.e(Constant.TAG_SERVICE, "findTradeInfo: WebView is null");
            return;
        }

        // FIXME: 2018/12/13 支付宝WebView变更代码可能会崩溃
        try {
            // AccessibilityNodeInfo view = rootInfo.getChild(0).getChild(0).getChild(0).getChild(0);

            // 兼容性更高
            findWebViewNode(rootInfo);

            if (webInfo == null) {
                // 找不到WebView，重新找
                findTradeInfoDelayed();
                return;
            }

            AccessibilityNodeInfo view = webInfo.getChild(0);
            if (view == null || view.getChild(1) == null || view.getChild(6) == null) {
                // 找不到WebView子节点、找不到关键数据，重新找
                findTradeInfoDelayed();
                return;
            }

            String amount_temp = view.getChild(1).getContentDescription().toString();
            String order_temp = view.getChild(6).getContentDescription().toString();

            if (!order_temp.contains(Constant.KEY_ORDER)) {
                // 非有效订单
                return;
            }

            if (!amount_temp.startsWith("+")) {
                // 非有效订单
                return;
            }

            String order_sn = order_temp.replaceAll("=", "");// 去除"="

            String trade_amount = amount_temp.substring(1, amount_temp.length())// 去除"+"
                    .replace(".", "")// 换算为分
                    .replaceFirst("^0*", "");// 去除头部"0"

            uploadData(new NotifyParamBean(BaseApplication.getUserInfo().getDevice_id(), order_sn, trade_amount));
        } catch (Exception e) {
            // WebView第一次加载时找不到，会空指针异常
            LogUtil.e(Constant.TAG_SERVICE, "findTradeInfo: Exception = " + e, e);
            e.printStackTrace();
            findTradeInfoDelayed();
        }
    }

    private void uploadData(final NotifyParamBean bean) {

        String data = FastJsonUtil.bean2Json(bean);
        LogUtil.d(Constant.TAG_HTTP, "notify data = " + data);

        String sign = EncryptUtil.getSign(data);
        LogUtil.i(Constant.TAG_HTTP, "login sign = " + sign);

        ApiFactory.getInstance().getApiService().notify(data).enqueue(
                new Callback<NotifyResultBean>() {
                    @Override
                    public void onResponse(@NonNull Call<NotifyResultBean> call,
                                           @NonNull Response<NotifyResultBean> response) {
                        NotifyResultBean resultBean = response.body();
                        LogUtil.i(Constant.TAG_HTTP, "notify onResponse: " + resultBean);

                        if (resultBean == null) {
                            LogUtil.e(Constant.TAG_HTTP, "notify onResponse: NotifyResultBean is null");
                            if (bean.getCount() == 0) {
                                bean.setCount(bean.getCount() + 1);
                                // 添加到错误队列
                                mFailureBeans.add(bean);
                            } else if (bean.getCount() < 8) {
                                mFailureBeans.remove(bean);
                                bean.setCount(bean.getCount() + 1);
                            } else {
                                mFailureBeans.remove(bean);
                            }
                            return;
                        }

                        if (resultBean.getErrcode() == Constant.HTTP_OK) {
                            LogUtil.i(Constant.TAG_HTTP, "notify success: ");
                        } else {
                            if (TextUtils.isEmpty(resultBean.getMsg())) {
                                LogUtil.i(Constant.TAG_HTTP, "notify onResponse: msg is empty");
                            } else {
                                LogUtil.i(Constant.TAG_HTTP, "notify onResponse: " + resultBean.getMsg());
                            }

                            // 用重复订单进行测试重试
                            /*if (resultBean.getErrcode() == 0) {
                                if (bean.getCount() == 0) {
                                    bean.setCount(bean.getCount() + 1);
                                    // 添加到错误队列
                                    mFailureBeans.add(bean);
                                } else if (bean.getCount() < 8) {
                                    bean.setCount(bean.getCount() + 1);
                                } else {
                                    mFailureBeans.remove(bean);
                                }
                            }*/
                        }
                        // 测试重试时注释掉
                        if (bean.getCount() != 0) {
                            mFailureBeans.remove(bean);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<NotifyResultBean> call, @NonNull Throwable t) {
                        LogUtil.e(Constant.TAG_HTTP, "uploadData onFailure: " + t);
                        if (bean.getCount() == 0) {
                            bean.setCount(bean.getCount() + 1);
                            // 添加到错误队列
                            mFailureBeans.add(bean);
                        } else if (bean.getCount() < 8) {
                            bean.setCount(bean.getCount() + 1);
                        } else {
                            mFailureBeans.remove(bean);
                        }
                    }
                });
    }
}
