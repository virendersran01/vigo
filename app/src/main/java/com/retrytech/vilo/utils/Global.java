package com.retrytech.vilo.utils;

import com.retrytech.vilo.api.ApiService;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Global {

    public static final int RC_SIGN_IN = 100;
    public static String ACCESS_TOKEN = "";
    public static String FIREBASE_DEVICE_TOKEN = "";
    public static String USER_ID;


    public static ApiService initRetrofit() {
        // For logging request & response (Optional)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        Interceptor interceptor = chain -> {
            Request request = chain.request();
            Request newRequest = request.newBuilder()
                    //.addHeader("Authorization", Global.ACCESS_TOKEN)
                    .build();
            return chain.proceed(newRequest);
        };
        OkHttpClient.Builder builder =
                new OkHttpClient.Builder();
        builder.networkInterceptors().add(interceptor);
        OkHttpClient client = builder.addInterceptor(loggingInterceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();
        return retrofit.create(ApiService.class);
    }


    public static String prettyCount(Number number) {

        try {
            char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
            long numValue = number.longValue();
            int value = (int) Math.floor(Math.log10(numValue));
            int base = value / 3;
            if (value >= 3 && base < suffix.length) {
                double value2 = numValue / Math.pow(10, base * 3);
                if (String.valueOf(value2).contains(".")) {
                    String num = String.valueOf(value2).split("\\.")[String.valueOf(value2).split("\\.").length - 1];
                    if (num.contains("0")) {
                        return new DecimalFormat("#0").format(value2) + suffix[base];
                    } else {
                        return new DecimalFormat("#0.0").format(value2) + suffix[base];
                    }
                } else {
                    return new DecimalFormat("#0").format(value2) + suffix[base];
                }
            } else {
                return new DecimalFormat("#,##0").format(numValue);
            }
        } catch (Exception e) {
            return String.valueOf(number);
        }
    }


}
