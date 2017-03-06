package com.mwf.analyze.intf;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 请求参数接口
 */
public interface IFamousInfo {

    @POST("/analysis")
    Call<String> getFamousResult(@Query("api_key") String api_key,
                                       @Query("text") String text,
                                       @Query("pattern") String pattern,
                                       @Query("format") String format);
}
