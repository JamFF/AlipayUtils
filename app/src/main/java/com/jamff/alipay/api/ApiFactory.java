package com.jamff.alipay.api;

import com.jamff.alipay.Constant;
import com.jamff.alipay.util.LogUtil;
import com.jamff.alipay.util.UIUtils;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * description:
 * author: JamFF
 * time: 2018/12/12 13:39
 */
public class ApiFactory {

    private volatile static ApiFactory sInstance;

    private Retrofit mRetrofit;

    private ApiFactory() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        int size = 1024 * 1024 * 10;
        File cacheFile = new File(UIUtils.getContext().getCacheDir(), "okHttp");
        LogUtil.d(Constant.TAG_HTTP, "cacheFile " + cacheFile.getAbsolutePath());
        Cache cache = new Cache(cacheFile, size);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .writeTimeout(12, TimeUnit.SECONDS)
                // .retryOnConnectionFailure(true)
                // .addInterceptor(new SignInterceptor())
                // .addNetworkInterceptor(new CacheInterceptor())
                // .addNetworkInterceptor(new NetworkInterceptor())
                .addInterceptor(loggingInterceptor)// 增加OKHttp请求log，tag为OKHttp
                .cache(cache)
                .build();

        mRetrofit = new Retrofit.Builder()
                // 域名
                .baseUrl(Constant.BASE_URL)
                .client(client)
                // 增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                // 增加返回值为Gson解析的支持
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiFactory getInstance() {
        if (sInstance == null) {
            synchronized (ApiFactory.class) {
                sInstance = new ApiFactory();
            }
        }
        return sInstance;
    }

    public ApiService getApiService() {
        return mRetrofit.create(ApiService.class);
    }
}
