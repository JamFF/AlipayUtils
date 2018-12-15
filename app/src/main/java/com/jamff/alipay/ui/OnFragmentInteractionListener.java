package com.jamff.alipay.ui;

/**
 * description:
 * author: JamFF
 * time: 2018/12/15 15:29
 */
public interface OnFragmentInteractionListener {

    /**
     * 开启加载框
     *
     * @param text 加载框中的文字
     */
    void showProgressDialog(String text);

    /**
     * 关闭加载框
     */
    void dismissProgressDialog();

    /**
     * 登录成功
     */
    void onLoginSuccess();

    /**
     * 打开支付宝
     *
     * @return
     */
    boolean startAlipay();

    /**
     * 退出应用
     */
    void exit();
}
