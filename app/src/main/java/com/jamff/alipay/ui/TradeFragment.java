package com.jamff.alipay.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jamff.alipay.BaseApplication;
import com.jamff.alipay.Constant;
import com.jamff.alipay.R;
import com.jamff.alipay.util.LogUtil;

/**
 * description:
 * author: JamFF
 * time: 2018/12/14 11:09
 */
public class TradeFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private Button bt_start;
    private Button bt_end;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trade, container, false);

        TextView tv_device_no = view.findViewById(R.id.tv_device_no);
        tv_device_no.setText(BaseApplication.getUserInfo().getDevice_no());

        TextView tv_merchant_no = view.findViewById(R.id.tv_merchant_no);
        tv_merchant_no.setText(BaseApplication.getUserInfo().getMerchant_no());

        bt_start = view.findViewById(R.id.bt_start);
        bt_start.setOnClickListener(this);

        bt_end = view.findViewById(R.id.bt_end);
        bt_end.setOnClickListener(this);

        return view;
    }

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
    public void onDestroyView() {
        LogUtil.d(Constant.TAG_TRADE, "onDestroyView: ");
        BaseApplication.setStart(false);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        LogUtil.d(Constant.TAG_TRADE, "onDestroy: ");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        LogUtil.d(Constant.TAG_TRADE, "onDetach: ");
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_start:
                if (!BaseApplication.isStart()) {
                    // 开始
                    startService();
                }
                break;

            case R.id.bt_end:
                if (BaseApplication.isStart()) {
                    // 停止
                    stopService();
                }
                break;
        }
    }

    private void startService() {
        if (mListener != null) {
            if (mListener.startAlipay()) {
                BaseApplication.setStart(true);
                bt_start.setEnabled(false);
                bt_end.setVisibility(View.VISIBLE);
            }
        }
    }

    private void stopService() {
        BaseApplication.setStart(false);
        if (mListener != null) {
            mListener.exit();
        }
    }
}
