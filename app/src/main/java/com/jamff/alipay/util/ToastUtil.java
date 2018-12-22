package com.jamff.alipay.util;


import android.text.TextUtils;
import android.widget.Toast;

/**
 * description:
 * author: JamFF
 * time: 2018/12/11 19:36
 */
public class ToastUtil {

    private static Toast mToast;

    /**
     * Toast短时间的提示
     *
     * @param info 需要显示的文字
     */
    public static void showShort(final String info) {

        if (TextUtils.isEmpty(info)) {
            return;
        }
        UIUtils.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null == mToast) {
                    mToast = Toast.makeText(UIUtils.getContext(), info, Toast.LENGTH_SHORT);
                }
                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setText(info);
                mToast.show();
            }
        });
    }

    /**
     * Toast长时间的提示
     *
     * @param info 需要显示的文字
     */
    public static void showLong(final String info) {

        if (TextUtils.isEmpty(info)) {
            return;
        }
        UIUtils.getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null == mToast) {
                    mToast = Toast.makeText(UIUtils.getContext(), info, Toast.LENGTH_SHORT);
                }
                mToast.setDuration(Toast.LENGTH_LONG);
                mToast.setText(info);
                mToast.show();
            }
        });
    }
}
