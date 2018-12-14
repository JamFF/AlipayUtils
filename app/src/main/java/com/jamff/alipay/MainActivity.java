package com.jamff.alipay;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jamff.alipay.api.ApiFactory;
import com.jamff.alipay.bean.EndParamBean;
import com.jamff.alipay.ui.LoginFragment;
import com.jamff.alipay.ui.TradeFragment;
import com.jamff.alipay.util.GsonUtil;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.ToastUtil;
import com.jamff.alipay.util.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        LoginFragment.OnFragmentInteractionListener, TradeFragment.OnFragmentInteractionListener {

    private FrameLayout mRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoot = new FrameLayout(this);
        mRoot.setId(View.generateViewId());// API 17以上
        findViewById(mRoot.getId());
        mRoot.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mRoot);

        if (savedInstanceState == null) {
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(mRoot.getId(), fragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(Constant.TAG_ACTIVITY, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        LogUtil.d(Constant.TAG_ACTIVITY, "onBackPressed: ");
        end();
    }

    @Override
    public void onLoginSuccess() {

        if (BaseApplication.getUserInfo() == null) {
            return;
        }

        startAccessibilityService();

        // TODO: 2018/12/14 开启后跳转
        TradeFragment fragment = new TradeFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(mRoot.getId(), fragment)
                //.addToBackStack(null)
                .commit();
    }

    private boolean isAccessibilitySettingsOff() {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + AlipayAccessibilityService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    UIUtils.getContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            LogUtil.d(Constant.TAG_SERVICE, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            LogUtil.e(Constant.TAG_SERVICE, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            LogUtil.d(Constant.TAG_SERVICE, "ACCESSIBILITY IS ENABLED");
            String settingValue = Settings.Secure.getString(
                    UIUtils.getContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    LogUtil.d(Constant.TAG_SERVICE, "accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        LogUtil.d(Constant.TAG_SERVICE, "We've found the correct setting - accessibility is switched on!");
                        return false;
                    }
                }
            }
        } else {
            LogUtil.d(Constant.TAG_SERVICE, "ACCESSIBILITY IS DISABLED");
        }

        return true;
    }

    /**
     * 没有开启辅助功能时打开
     */
    private void startAccessibilityService() {
        if (isAccessibilitySettingsOff()) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, Constant.REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Constant.REQUEST_CODE == requestCode) {
            startAccessibilityService();
        }
    }

    @Override
    public boolean startAlipay() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(Constant.ALIPAY_PACKAGE_NAME);
        if (intent == null) {
            ToastUtil.showShort("尚未安装支付宝");
            return false;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return true;
    }

    @Override
    public void exit() {
        end();
    }

    private void end() {

        if (BaseApplication.getUserInfo() == null) {
            finish();
            return;
        }

        String data = GsonUtil.bean2Json(new EndParamBean(BaseApplication.getUserInfo().getDevice_id()));
        LogUtil.d(Constant.TAG_HTTP, "data = " + data);

        ApiFactory.getInstance().getApiService().end(data).enqueue(
                new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        LogUtil.d(Constant.TAG_HTTP, "end onResponse: " + response.body());
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,
                                          @NonNull Throwable t) {
                        LogUtil.e(Constant.TAG_HTTP, "end onFailure: " + t);
                        finish();
                    }
                });
    }
}
