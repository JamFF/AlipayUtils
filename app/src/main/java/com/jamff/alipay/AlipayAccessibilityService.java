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
import com.jamff.alipay.bean.BillBean;
import com.jamff.alipay.bean.NotifyParamBean;
import com.jamff.alipay.bean.NotifyResultBean;
import com.jamff.alipay.util.EncryptUtil;
import com.jamff.alipay.util.FastJsonUtil;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.SPUtils;
import com.jamff.alipay.util.StringUtils;
import com.jamff.alipay.util.UIUtils;

import java.util.ArrayList;
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

    private boolean isNotification;// 收到转账通知后进入聊天界面，老的方式

    private AccessibilityNodeInfo webInfo;

    private boolean untreatedOrder;// 是否存在未处理的订单，新的方式

    private String device_id;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        // 当启动服务的时候就会被调用

        device_id = SPUtils.INSTANCE.getString(Constant.SP_DEVICE_ID);

        LogUtil.d(Constant.TAG_SERVICE, "onServiceConnected: intercept = " + intercept());

        // FIXME: 2018/12/18 FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY废弃，不再使用
        AccessibilityServiceInfo serviceInfo = getServiceInfo();
        serviceInfo.flags = serviceInfo.flags | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;

        startTimeTask();
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!intercept()) {
            LogUtil.i(Constant.TAG_SERVICE, "onAccessibilityEvent: not intercept");
            return;
        }
        if (TextUtils.isEmpty(device_id)) {
            LogUtil.i(Constant.TAG_SERVICE, "onAccessibilityEvent: device_id is empty");
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
                            if (Constant.IS_NEW) {// 新的抓单方式
                                AccessibilityNodeInfo contentNode = findViewById(getRootInActiveWindow(), Constant.TAB_CONTENT_ID, 0);

                                if (contentNode == null) {
                                    LogUtil.w(Constant.TAG_SERVICE, "contentNode == null");
                                    untreatedOrder = true;
                                    LogUtil.d(Constant.TAG_SERVICE, "untreated set true");
                                    return;
                                }

                                try {
                                    // 适配小米、vivo在跟布局不能找到
                                    AccessibilityNodeInfo parent_info = findViewById(contentNode.getChild(2).getChild(3),
                                            Constant.BILL_PARENT_ID, 1);
                                    if (parent_info == null) {
                                        // 没在"我的"界面，找不到账单
                                        LogUtil.w(Constant.TAG_SERVICE, "parent_info == null");
                                        untreatedOrder = true;
                                        LogUtil.d(Constant.TAG_SERVICE, "untreated set true");
                                    } else {
                                        // 进入账单界面
                                        clickView(parent_info.getChild(0));
                                    }
                                } catch (Exception e) {
                                    LogUtil.e(Constant.TAG_SERVICE, "find bill Exception" + e);
                                    untreatedOrder = true;
                                    LogUtil.d(Constant.TAG_SERVICE, "untreated set true");
                                    e.printStackTrace();
                                }

                            } else {// 老的抓单方式
                                // 关闭上次账单详情界面，不关闭也无所谓
                                clickViewById(Constant.PAY_BACK_ID);
                                // 模拟点击通知栏消息，打开支付宝
                                startNotification(event);
                                break;
                            }
                        }
                    }
                } else {
                    LogUtil.e(Constant.TAG_SERVICE, "texts is empty");
                }
                break;

            // 当窗口的状态发生改变时
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                final String className = event.getClassName().toString();
                LogUtil.d(Constant.TAG_SERVICE, "className = " + className);

                UIUtils.postDelayedTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        // 界面变化后统一延迟1秒，给支付宝网络请求的时间
                        if (Constant.IS_NEW) {
                            batchMode(className);
                        } else {
                            singleMode(className);
                        }
                    }
                }, 1000);

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

    private boolean intercept() {
        return SPUtils.INSTANCE.getBoolean(Constant.SP_INTERCEPT, false);
    }

    /**
     * 新的抓单方式
     */
    private void batchMode(String className) {
        if (Constant.ALIPAY_MAIN.equals(className)) {
            // 进入支付宝，就打开"我的"界面
            if (clickMineView()) {
                LogUtil.d(Constant.TAG_SERVICE, "click mine success");
            } else {
                LogUtil.w(Constant.TAG_SERVICE, "click mine failure");
                // 支付宝欢迎页也是AlipayLogin，延迟再获取
                UIUtils.postDelayedTaskSafely(new Runnable() {
                    @Override
                    public void run() {
                        LogUtil.w(Constant.TAG_SERVICE, clickMineView() ?
                                "delay click success" : "delay click failure");
                    }
                }, 1000);
            }

            // 回到"我的"界面，检测是否有未处理的订单
            LogUtil.d(Constant.TAG_SERVICE, "untreated = " + untreatedOrder);

            if (untreatedOrder) {
                // 有未处理订单
                AccessibilityNodeInfo contentNode = findViewById(getRootInActiveWindow(), Constant.TAB_CONTENT_ID, 0);

                if (contentNode == null) {
                    LogUtil.w(Constant.TAG_SERVICE, "contentNode == null");
                    return;
                }

                try {
                    AccessibilityNodeInfo parent_info = findViewById(contentNode.getChild(2).getChild(3),
                            Constant.BILL_PARENT_ID, 1);
                    if (parent_info == null) {
                        LogUtil.e(Constant.TAG_SERVICE, "parent_info == null");
                    } else {
                        untreatedOrder = false;
                        LogUtil.d(Constant.TAG_SERVICE, "untreated set false");
                        // 进入账单界面
                        if (clickView(parent_info.getChild(0))) {
                            LogUtil.d(Constant.TAG_SERVICE, "click bill success");
                        } else {
                            LogUtil.d(Constant.TAG_SERVICE, "click bill failure");
                        }
                    }
                } catch (Exception e) {
                    LogUtil.e(Constant.TAG_SERVICE, "find bill Exception" + e);
                    e.printStackTrace();
                }
            }
        } else if (Constant.BILL_ACTIVITY.equals(className)) {
            // 账单列表界面
            findBillDelayed();
        }
    }

    /**
     * 点击"我的"，适配小米、vivo在跟布局不能找到
     */
    private boolean clickMineView() {
        AccessibilityNodeInfo tabNode = findViewById(getRootInActiveWindow(), Constant.TAB_ID, 0);
        if (tabNode == null) {
            return false;
        }
        return clickView(tabNode.getChild(4));
    }

    /**
     * 延迟1秒后，再抓取最近账单
     */
    private void findBillDelayed() {
        UIUtils.postDelayedTaskSafely(new Runnable() {
            @Override
            public void run() {
                findBillList();
            }
        }, 1000);
    }

    int findBillCount = 0;

    private void findBillList() {

        List<AccessibilityNodeInfo> billNames = findViewsById(getRootInActiveWindow(),
                Constant.BILL_NAME_ID);
        List<AccessibilityNodeInfo> billAmounts = findViewsById(getRootInActiveWindow(),
                Constant.BILL_AMOUNT_ID);

        if (billNames == null || billAmounts == null) {
            LogUtil.e(Constant.TAG_SERVICE, "billNames == null || billAmounts == null");

            if (findBillCount < 5) {
                findBillCount++;
                findBillDelayed();// 重新获取账单
            } else {
                findBillCount = 0;
            }
            backBillList();
            return;
        }

        int i = Math.min(billNames.size(), billAmounts.size());

        LogUtil.d(Constant.TAG_SERVICE, "i = " + i);

        // 封装需要订单集合
        List<BillBean> billBeans = new ArrayList<>();
        for (int j = 0; j < i; j++) {
            billBeans.add(new BillBean(billNames.get(j).getText().toString(),
                    billAmounts.get(j).getText().toString()));
        }

        uploadData(billBeans);
    }

    /**
     * 上传数据
     */
    private void uploadData(@NonNull List<BillBean> billBeans) {

        if (billBeans.isEmpty()) {
            LogUtil.w(Constant.TAG_SERVICE, "billBeans is empty");
            backBillList();
            return;
        }

        LogUtil.d(Constant.TAG_SERVICE, "uploadData");

        for (BillBean bean : billBeans) {
            String billAmount = bean.getBillAmount();
            if (!billAmount.startsWith("+")) {
                // 非有效订单
                LogUtil.w(Constant.TAG_SERVICE, "not startsWith +");
                continue;
            }

            String billName = bean.getBillName();
            // 收款理由
            String order_sn = billName.substring(0, billName.lastIndexOf("-"));

            String trade_amount = billAmount.substring(1, billAmount.length())// 去除"+"
                    .replace(".", "")// 换算为分
                    .replace(",", "")// 去掉分隔符
                    .replaceFirst("^0*", "");// 去除头部"0"

            // TODO: 2018/12/31 unicode
            /*String order_sn_unicode = StringUtils.string2Unicode(order_sn, true);

            LogUtil.d(Constant.TAG_SERVICE, "order_sn_unicode: " + order_sn_unicode);*/

            notifyData(new NotifyParamBean(device_id, order_sn, trade_amount));
        }

        // 退出账单列表
        UIUtils.postDelayedTaskSafely(new Runnable() {
            @Override
            public void run() {
                backBillList();
            }
        }, 1000);

    }

    /**
     * 退出账单列表
     */
    private void backBillList() {
        LogUtil.d(Constant.TAG_SERVICE, "backBillList");
        clickViewById(Constant.BILL_BACK_ID);
    }

    /**
     * 老的抓单方式
     */
    private void singleMode(String className) {
        if (Constant.PAY_ACTIVITY.equals(className)) {
            if (isNotification) {
                // 打开账单详细界面
                isNotification = false;
                findTradeInfo(findViewById(getRootInActiveWindow(), Constant.PAY_ROOT_ID, 0));
            } else {
                LogUtil.w(Constant.TAG_SERVICE, "isNotification = false");
            }
        }
    }

    private void startTimeTask() {
        UIUtils.postTaskSafely(new Runnable() {
            @Override
            public void run() {

                if (!mFailureBeans.isEmpty()) {
                    for (NotifyParamBean bean : mFailureBeans) {
                        notifyData(bean);
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
                findTradeInfo(findViewById(getRootInActiveWindow(), Constant.PAY_ROOT_ID, 0));
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
     * 在指定节点，根据id找到控件集合
     *
     * @param rootNode 父布局
     */
    private List<AccessibilityNodeInfo> findViewsById(AccessibilityNodeInfo rootNode, String id) {

        if (rootNode == null) {
            LogUtil.e(Constant.TAG_SERVICE, "findViewsById: rootNode is null");
            return null;
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(id);
        if (list != null && !list.isEmpty()) {
            return list;
        }

        LogUtil.e(Constant.TAG_SERVICE, "findViewsById: Did not find id = " + id);
        return null;
    }

    /**
     * 在指定节点，根据id找到控件
     *
     * @param rootNode 父布局
     */
    private AccessibilityNodeInfo findViewById(AccessibilityNodeInfo rootNode, String id, int i) {

        if (rootNode == null) {
            LogUtil.e(Constant.TAG_SERVICE, "findViewById: rootNode is null");
            return null;
        }

        List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId(id);
        if (list != null && !list.isEmpty()) {
            return list.get(i < list.size() ? i : 0);
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
     * 根据id点击控件
     */
    private boolean clickViewById(String id) {

        AccessibilityNodeInfo bt = findViewById(getRootInActiveWindow(), id, 0);

        if (bt != null) {
            return bt.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

        return false;
    }

    /**
     * 根据AccessibilityNodeInfo点击控件
     */
    private boolean clickView(AccessibilityNodeInfo info) {

        if (info != null) {
            return info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        LogUtil.e(Constant.TAG_SERVICE, "info is null");
        return false;
    }

    /**
     * 获取聊天界面ListView中最后一个转账信息
     */
    /*private void getLastMessage() {

        LogUtil.i(Constant.TAG_SERVICE, "getLastMessage: ");
        findTradeInfoByChat(findViewById(getRootInActiveWindow(), Constant.CHAT_LIST_ID));

        if (!mParamBeans.isEmpty()) {
            notifyData(mParamBeans.get(mParamBeans.size() - 1));
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

                    mParamBeans.add(new NotifyParamBean(device_id, "9850181213213015459", trade_amount));
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

            if (!amount_temp.startsWith("+")) {
                // 非有效订单
                LogUtil.w(Constant.TAG_SERVICE, "not startsWith +");
                return;
            }

            // 收款理由
            String order_temp = view.getChild(6).getContentDescription().toString();

            // 不使用过滤规则
            /*if (!order_temp.contains(Constant.KEY_ORDER)) {
                // 非有效订单
                return;
            }*/

            String order_sn;
            if (order_temp.contains(Constant.KEY_RECEIPT)) {
                // 适配收款理由是收款的情况
                LogUtil.d(Constant.TAG_SERVICE, order_temp);
                // 收款理由是"转账"时取转账备注
                if (view.getChild(7) == null || view.getChild(8) == null) {
                    // 找不到WebView子节点、找不到关键数据，重新找
                    findTradeInfoDelayed();
                    return;
                }
                String remark = view.getChild(7).getContentDescription().toString();
                if (remark.contains(Constant.KEY_REMARK)) {
                    order_sn = view.getChild(8).getContentDescription().toString();
                    LogUtil.d(Constant.TAG_SERVICE, "remark order_sn: " + order_sn);
                } else {
                    LogUtil.w(Constant.TAG_SERVICE, "not find reason, not find remark");
                    return;
                }
            } else {
                order_sn = order_temp;
                LogUtil.d(Constant.TAG_SERVICE, "reason order_sn: " + order_sn);
            }

            String trade_amount = amount_temp.substring(1, amount_temp.length())// 去除"+"
                    .replace(".", "")// 换算为分
                    .replace(",", "")// 去掉分隔符
                    .replaceFirst("^0*", "");// 去除头部"0"

            String order_sn_unicode = StringUtils.string2Unicode(order_sn, true);

            LogUtil.d(Constant.TAG_SERVICE, "order_sn_unicode: " + order_sn_unicode);

            notifyData(new NotifyParamBean(device_id, order_sn_unicode, trade_amount));
        } catch (Exception e) {
            // WebView第一次加载时找不到，会空指针异常
            LogUtil.e(Constant.TAG_SERVICE, "findTradeInfo: Exception = " + e, e);
            e.printStackTrace();
            findTradeInfoDelayed();
        }
    }

    private void notifyData(final NotifyParamBean bean) {

        String data_sign = EncryptUtil.getSign(FastJsonUtil.bean2Json(bean));
        LogUtil.i(Constant.TAG_HTTP, "notifyData sign = " + data_sign);

        ApiFactory.getInstance().getApiService().notify(data_sign).enqueue(
                new Callback<NotifyResultBean>() {
                    @Override
                    public void onResponse(@NonNull Call<NotifyResultBean> call,
                                           @NonNull Response<NotifyResultBean> response) {
                        NotifyResultBean resultBean = response.body();
                        LogUtil.i(Constant.TAG_HTTP, "notifyData onResponse: " + resultBean);

                        if (resultBean == null) {
                            LogUtil.e(Constant.TAG_HTTP, "notifyData onResponse: NotifyResultBean is null");
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
                            LogUtil.i(Constant.TAG_HTTP, "notifyData success: ");
                        } else {
                            if (TextUtils.isEmpty(resultBean.getMsg())) {
                                LogUtil.i(Constant.TAG_HTTP, "notifyData onResponse: msg is empty");
                            } else {
                                LogUtil.i(Constant.TAG_HTTP, "notifyData onResponse: " + resultBean.getMsg());
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
                        LogUtil.e(Constant.TAG_HTTP, "notifyData onFailure: " + t);
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
