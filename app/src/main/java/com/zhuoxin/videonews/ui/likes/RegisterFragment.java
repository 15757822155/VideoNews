package com.zhuoxin.videonews.ui.likes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.zhuoxin.videonews.R;
import com.zhuoxin.videonews.bombapi.BombClient;
import com.zhuoxin.videonews.bombapi.UserApi;
import com.zhuoxin.videonews.bombapi.entity.UserEntity;
import com.zhuoxin.videonews.bombapi.result.ErrorResult;
import com.zhuoxin.videonews.bombapi.result.UserResult;
import com.zhuoxin.videonews.commons.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2016/12/21.
 */

public class RegisterFragment extends DialogFragment {
    @BindView(R.id.etUsername)
    EditText mEtUsername;
    @BindView(R.id.etPassword)
    EditText mEtPassword;
    @BindView(R.id.btnRegister)
    Button mBtnRegister;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //取消标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.dialog_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btnRegister)
    public void onClick() {
        final String username = mEtUsername.getText().toString();
        String password = mEtPassword.getText().toString();
        //用户名和密码不能为空
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ToastUtils.showShort(R.string.username_or_password_can_not_be_null);
            return;
        }
        //显示进度条
        mBtnRegister.setVisibility(View.GONE);
        //网络模块,注册请求
        //注册api
        UserApi userApi = BombClient.getInstance().getUserApi();
        //请求体数据
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //创建请求体
        RequestBody body = RequestBody.create(null, jsonObject.toString());
        //用户实体类
        UserEntity userEntity = new UserEntity(username, password);
        //获取Call
        Call<UserResult> call = userApi.register(userEntity);
        //执行网络请求
        call.enqueue(new Callback<UserResult>() {
            //成功的响应
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                //隐藏加载的进度条
                mBtnRegister.setVisibility(View.VISIBLE);
                if (!response.isSuccessful()) {
                    try {
                        //拿到失败的json
                        String error = response.errorBody().string();
                        //通过gson将拿到的json数据解析成失败结果类
                        ErrorResult errorResult = new Gson().fromJson(error, ErrorResult.class);
                        //提示用户注册失败
                        ToastUtils.showShort(errorResult.getError());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return;
                }

                //注册成功
                UserResult userResult = response.body();
                listener.registerSuccess(username, userResult.getObjectId());
                //提示注册成功
                ToastUtils.showShort(R.string.register_success);
            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                //隐藏进度条
                mBtnRegister.setVisibility(View.VISIBLE);
                //提示失败原因
                ToastUtils.showShort(t.getMessage());
            }
        });

    }
    //内部接口,在注册成功时调用
    public interface OnRegisterSuccessListener {
        //注册成功时,调用
        void registerSuccess(String username, String objectId);
    }

    private OnRegisterSuccessListener listener;

    public void setOnRegisterSuccessListener(OnRegisterSuccessListener listener) {
        this.listener = listener;
    }
}
