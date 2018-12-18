package com.jamff.alipay.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jamff.alipay.BaseApplication;
import com.jamff.alipay.Constant;
import com.jamff.alipay.R;
import com.jamff.alipay.api.ApiFactory;
import com.jamff.alipay.bean.LoginParamBean;
import com.jamff.alipay.bean.LoginResultBean;
import com.jamff.alipay.util.GsonUtil;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.StringUtils;
import com.jamff.alipay.util.ToastUtil;
import com.jamff.alipay.util.UIUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * description:
 * author: JamFF
 * time: 2018/12/14 10:51
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private TextInputLayout til_username;
    private TextInputLayout til_password;

    private EditText et_username;
    private EditText et_password;

    private String username;
    private String password;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        LogUtil.d(Constant.TAG_LOGIN, "onCreateView: ");

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        til_username = view.findViewById(R.id.til_username);
        til_password = view.findViewById(R.id.til_password);
        et_username = til_username.getEditText();
        et_password = til_password.getEditText();

        TextView tv_version = view.findViewById(R.id.tv_version);
        tv_version.setText(UIUtils.getVersionName());

        view.findViewById(R.id.bt_login).setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        LogUtil.d(Constant.TAG_LOGIN, "onDestroyView: ");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(Constant.TAG_LOGIN, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        LogUtil.d(Constant.TAG_LOGIN, "onDetach: ");
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login:
                if (validateAccount(et_username.getText()) &
                        validatePassword(et_password.getText())) {
                    login(new LoginParamBean(username, password));
                }
                break;
        }
    }

    private void login(LoginParamBean bean) {

        if (mListener != null) {
            mListener.showProgressDialog("登录中");
        }

        String data = GsonUtil.bean2Json(bean);
        LogUtil.d(Constant.TAG_HTTP, "login data = " + data);

        ApiFactory.getInstance().getApiService().login(data).enqueue(
                new Callback<LoginResultBean>() {
                    @Override
                    public void onResponse(@NonNull Call<LoginResultBean> call,
                                           @NonNull Response<LoginResultBean> response) {

                        LoginResultBean resultBean = response.body();
                        LogUtil.i(Constant.TAG_HTTP, "login onResponse: " + resultBean);

                        if (resultBean == null) {
                            loginFail("登录失败", "login onResponse: LoginResultBean is null");
                            if (mListener != null) {
                                mListener.dismissProgressDialog();
                            }
                            return;
                        }

                        if (resultBean.getErrcode() == Constant.HTTP_OK) {
                            if (resultBean.getData() == null) {
                                loginFail("登录失败", "login onResponse: LoginResultBean.DataBean is null");
                                if (mListener != null) {
                                    mListener.dismissProgressDialog();
                                }
                                return;
                            }
                            LogUtil.i(Constant.TAG_HTTP, "login success: " + resultBean.getData());
                            BaseApplication.setUserInfo(resultBean.getData());
                            if (mListener != null) {
                                mListener.onLoginSuccess();
                                mListener.dismissProgressDialog();
                            }
                        } else {
                            if (StringUtils.isEmpty(resultBean.getMsg())) {
                                loginFail("登录失败", "login onResponse: msg is empty");
                            } else {
                                loginFail(resultBean.getMsg(), "login onResponse: " + resultBean.getMsg());
                            }
                            if (mListener != null) {
                                mListener.dismissProgressDialog();
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<LoginResultBean> call,
                                          @NonNull Throwable t) {
                        LogUtil.e(Constant.TAG_HTTP, "login onFailure: " + t);
                    }
                });

        /*ApiFactory.getInstance().getApiService().login(data).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                LogUtil.i(Constant.TAG_HTTP, "login success: " + response.body());
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                LogUtil.e(Constant.TAG_HTTP, "login onFailure: " + t);
            }
        });*/
    }

    /**
     * 验证账号
     */
    private boolean validateAccount(Editable ed) {

        if (ed == null || StringUtils.isEmpty(ed.toString())) {
            til_username.setError("账号不能为空");
            return false;
        }
        til_username.setErrorEnabled(false);
        username = ed.toString();
        return true;
    }

    /**
     * 验证密码
     */
    private boolean validatePassword(Editable ed) {

        if (ed == null || StringUtils.isEmpty(ed.toString())) {
            til_password.setError("密码不能为空");
            return false;
        }
        til_password.setErrorEnabled(false);
        password = ed.toString();
        return true;
    }

    private void loginFail(String toast, String log) {
        ToastUtil.showShort(toast);
        LogUtil.e(Constant.TAG_HTTP, log);
    }
}
