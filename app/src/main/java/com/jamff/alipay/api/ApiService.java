package com.jamff.alipay.api;

import com.jamff.alipay.bean.LoginResultBean;
import com.jamff.alipay.bean.NotifyResultBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 14:00
 */
public interface ApiService {

    /**
     * 登录接口
     */
    @FormUrlEncoded
    @POST("fixedv2/api")
    Call<LoginResultBean> login(@Field("data") String data);

    /**
     * 获取回调数据接口
     */
    @FormUrlEncoded
    @POST("fixedv2/api")
    Call<NotifyResultBean> notify(@Field("data") String data);

    /**
     * 停止设备接口
     */
    @FormUrlEncoded
    @POST("fixedv2/api")
    Call<String> end(@Field("data") String data);
}
