package com.jamff.alipay.api;

import com.jamff.alipay.bean.LoginResultBean;
import com.jamff.alipay.bean.NotifyResultBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

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

    /**
     * 升级接口
     */
    @FormUrlEncoded
    @POST("fixedv2/api")
    Call<String> version(@Field("data") String data);

    /**
     * 下载新版本
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadApp(@Url String fileUrl);
}
