package com.mwf.analyze;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mwf.analyze.utils.ToStringConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class RetrofitWrapper {
    private static RetrofitWrapper instance;
    private Context mContext;
    private Retrofit mRetrofit;

    public RetrofitWrapper(String url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // Log
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.i("OkHttp", message);
            }
        });
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = builder.addInterceptor(logging)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mRetrofit = new Retrofit.Builder().baseUrl(url)
//                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(new ToStringConverterFactory())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();
    }

    public  static RetrofitWrapper getInstance(String url){

        if(null == instance){
            synchronized (RetrofitWrapper.class){
                instance = new RetrofitWrapper(url);
            }
        }
        return instance;
    }

    public <T> T create(final Class<T> service) {
        return mRetrofit.create(service);
    }
}
