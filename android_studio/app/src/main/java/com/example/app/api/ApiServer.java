package com.example.app.api;

import com.example.app.model.Info;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiServer {
    ApiServer apiServer = new Retrofit.Builder()  // Retrofit.Builder类使用Builder API来定义HTTP操作的URL端点
            .baseUrl("http://10.0.2.2:5000")
//            .baseUrl("http:172.20.10.3:5000")
//            .baseUrl("http://172.27.94.183:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServer.class);
    // 创建接口
    @FormUrlEncoded //使用表单方式提交填写@FormUrlEncoded
    @POST("/getImage") // POST方式请求
    Call<Info> getImage(@Field("file") String file); // 字段用Field("file")会由file参数替换补充到url
}
