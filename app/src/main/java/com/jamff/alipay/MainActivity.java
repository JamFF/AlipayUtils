package com.jamff.alipay;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jamff.alipay.api.ApiFactory;
import com.jamff.alipay.bean.EndParamBean;
import com.jamff.alipay.ui.LoginFragment;
import com.jamff.alipay.ui.OnFragmentInteractionListener;
import com.jamff.alipay.ui.TradeFragment;
import com.jamff.alipay.util.GsonUtil;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.ToastUtil;
import com.jamff.alipay.util.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private FrameLayout mRoot;

    // 避免锁屏
    private PowerManager.WakeLock mWakelock;

    // 网络请求加载框
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRoot = new FrameLayout(this);
        mRoot.setId(View.generateViewId());// API 17以上
        findViewById(mRoot.getId());
        mRoot.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(mRoot);

        requestPermission();

        if (savedInstanceState == null) {
            LoginFragment fragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(mRoot.getId(), fragment)
                    .commit();
        }

        initData();
    }

    private void initData() {
        LogUtil.d(Constant.TAG_ACTIVITY, "versionName = " + UIUtils.getVersionName());

        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (pm == null) {
            return;
        }
        mWakelock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK, "alipayutils:waketag");
        if (mWakelock == null) {
            return;
        }
        mWakelock.acquire();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(Constant.TAG_ACTIVITY, "onDestroy: ");
        if (mWakelock != null) {
            // 释放
            mWakelock.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        LogUtil.d(Constant.TAG_ACTIVITY, "onBackPressed: ");
        exit();
    }

    @Override
    public void showProgressDialog(String text) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCanceledOnTouchOutside(false);
            // mProgressDialog.setCancelable(false);
        }
        mProgressDialog.setMessage(text);
        mProgressDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onLoginSuccess() {

        if (BaseApplication.getUserInfo() == null) {
            return;
        }

        startAccessibilityService();

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
            // LogUtil.d(Constant.TAG_SERVICE, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            // LogUtil.e(Constant.TAG_SERVICE, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            // LogUtil.d(Constant.TAG_SERVICE, "ACCESSIBILITY IS ENABLED");
            String settingValue = Settings.Secure.getString(
                    UIUtils.getContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    // LogUtil.d(Constant.TAG_SERVICE, "accessibilityService: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        // LogUtil.d(Constant.TAG_SERVICE, "We've found the correct setting - accessibility is switched on!");
                        return false;
                    }
                }
            }
        } else {
            // LogUtil.d(Constant.TAG_SERVICE, "ACCESSIBILITY IS DISABLED");
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
    public boolean openAlipay() {
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

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("确定退出")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        end();
                    }
                })
                .show();
    }

    private void end() {

        if (BaseApplication.getUserInfo() == null) {
            finish();
            return;
        }

        showProgressDialog("退出中");

        String data = GsonUtil.bean2Json(new EndParamBean(BaseApplication.getUserInfo().getDevice_id()));
        LogUtil.d(Constant.TAG_HTTP, "exit data = " + data);

        ApiFactory.getInstance().getApiService().end(data).enqueue(
                new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,
                                           @NonNull Response<String> response) {
                        LogUtil.d(Constant.TAG_HTTP, "end onResponse: " + response.body());
                        dismissProgressDialog();
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,
                                          @NonNull Throwable t) {
                        LogUtil.e(Constant.TAG_HTTP, "end onFailure: " + t);
                        dismissProgressDialog();
                        finish();
                    }
                });
    }

    private void requestPermission() {

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            LogUtil.d(Constant.TAG_PERMISSIONS, "checkSelfPermission");
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                LogUtil.d(Constant.TAG_PERMISSIONS, "shouldShowRequestPermissionRationale");
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.REQUEST_CODE);

            } else {
                LogUtil.d(Constant.TAG_PERMISSIONS, "requestPermissions");
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        Constant.REQUEST_CODE);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            LogUtil.d(Constant.TAG_PERMISSIONS, "permission = true");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    LogUtil.d(Constant.TAG_PERMISSIONS, "onRequestPermissionsResult granted");
                    // permission was granted, yay! Do the contacts-related task you need to do.

                } else {
                    LogUtil.d(Constant.TAG_PERMISSIONS, "onRequestPermissionsResult denied");
                    // permission denied, boo! Disable the functionality that depends on this permission.
                    showWaringDialog();
                }
            }
            break;
        }
    }

    private void showWaringDialog() {
        new AlertDialog.Builder(this)
                .setTitle("警告！")
                .setMessage("读写SD卡权限尚未打开，部分功能无法正常运行！")
                .setPositiveButton("确定", null).show();
    }
}
