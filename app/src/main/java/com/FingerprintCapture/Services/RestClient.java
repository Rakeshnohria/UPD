package com.FingerprintCapture.Services;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chicmic on 8/14/17.
 */

public class RestClient {
    private static final String AUTHENTICATION_HEADER = "Authorization";
    private static final String CONTENT_TYPE = "Content-type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final int TIME_CONSTANT = 5;
    private static Retrofit retrofit = null;

    public static Retrofit build(String url) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(url).client(getClient())
                    .addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }


    private static OkHttpClient getClient() {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder().connectTimeout(TIME_CONSTANT, TimeUnit.MINUTES).
                readTimeout(TIME_CONSTANT, TimeUnit.MINUTES)
                .writeTimeout(TIME_CONSTANT, TimeUnit.MINUTES)
                .addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder().header(AUTHENTICATION_HEADER, "").
                        addHeader(CONTENT_TYPE, CONTENT_TYPE_JSON).method(original.method(), original.body());
                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        }).addInterceptor(httpLoggingInterceptor).build();
    }
}
