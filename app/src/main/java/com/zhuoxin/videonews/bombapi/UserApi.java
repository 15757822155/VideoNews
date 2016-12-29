package com.zhuoxin.videonews.bombapi;

import com.zhuoxin.videonews.bombapi.entity.UserEntity;
import com.zhuoxin.videonews.bombapi.result.UserResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2016/12/22.
 * 用户相关网络接口
 */

public interface UserApi {

    //用户注册
//    请求头
//    @Headers({"X-Bmob-Application-Id: 623aaef127882aed89b9faa348451da3",
//            "X-Bmob-REST-API-Key: c00104962a9b67916e8cbcb9157255de",
//            "Content-Type: application/json"})
    @POST("1/users")
    Call<UserResult> register(@Body UserEntity userEntity);
    //泛型中放成功的响应体的类型
    //用户登录
    @GET("1/login")
    Call<UserResult> login(@Query("username") String username, @Query("password") String password);
}
