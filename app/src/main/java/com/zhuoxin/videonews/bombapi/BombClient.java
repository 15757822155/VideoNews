package com.zhuoxin.videonews.bombapi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/12/21.
 */

public class BombClient {
    private static BombClient bombClient;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private Retrofit retrofit_cloud;
    private UserApi userApi;
    private NewsApi newsApi;
    private NewsApi newsApi_cloud;

    private BombClient() {
        //日志拦截器
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //日志拦截器等级
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        //构建OKhttp
        okHttpClient = new OkHttpClient.Builder()
                //自定义拦截器,为了传递请求头信息
                .addInterceptor(new BombInterceptor())
                //日志拦截器
                .addInterceptor(httpLoggingInterceptor)
                .build();
        //让Gson能够将Bomb返回的时间戳自动转换为Date对象
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        //构建retrofit
        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.bmob.cn/")
                //添加转换器

                //添加自动转换日期的转换器
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        //构建retrofit
        retrofit_cloud = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://cloud.bmob.cn/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static BombClient getInstance() {
        if (bombClient == null) {
            bombClient = new BombClient();
        }
        return bombClient;
    }

    //拿到UserApi
    public UserApi getUserApi() {
        if (userApi == null) {
            userApi = retrofit.create(UserApi.class);
        }
        return userApi;
    }

    //拿到NewsApi
    public NewsApi getNewsApi() {
        if (newsApi == null) {
            newsApi = retrofit.create(NewsApi.class);
        }
        return newsApi;
    }

    public NewsApi getNewsApi_cloud() {
        if (newsApi_cloud == null) {
            newsApi_cloud = retrofit_cloud.create(NewsApi.class);
        }
        return newsApi_cloud;
    }
//    以下是以okhttp创建的请求,已不用,改用retrofit
//    public Call register(String username, String password) {
//        //json数据类型的请求体
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("username", username);
//            jsonObject.put("password", password);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        //请求体
//        RequestBody requestBody = RequestBody.create(null, jsonObject.toString());
//        //新建请求
//        Request request = new Request.Builder()
//                .url("https://api.bmob.cn/1/users")
//                .post(requestBody)
//                .build();
//        //返回一个call类型的数据
//        return okHttpClient.newCall(request);
//    }
}

