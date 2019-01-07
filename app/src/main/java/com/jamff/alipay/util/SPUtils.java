package com.jamff.alipay.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.jamff.alipay.Constant;

/**
 * description:
 * author: JamFF
 * time: 2019/1/7 14:46
 */
public enum SPUtils {

    INSTANCE;

    private SharedPreferences sp;

    public void init() {
        if (sp == null) {
            sp = UIUtils.getContext().getSharedPreferences(Constant.SP_FILE_NAME, Context.MODE_PRIVATE);
        }
    }

    public void putBoolean(String key, boolean value) {
        init();
        sp.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        init();
        return sp.getBoolean(key, defValue);
    }

    public void putString(String key, @Nullable String value) {
        init();
        sp.edit().putString(key, value).apply();
    }

    public String getString(String key) {
        return getString(key, "");
    }

    public String getString(String key, @Nullable String defValue) {
        init();
        return sp.getString(key, defValue);
    }
}
