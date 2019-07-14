package com.bytedance.androidcamp.network.dou.api;

import com.bytedance.androidcamp.network.dou.model.GetResponse;
import com.bytedance.androidcamp.network.dou.model.PostResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


//--------------------------retrofit2相关知识-----------------------------------


/**
 * 参考结构：https://www.jianshu.com/p/b25669052335
 * post：创建一个新的项
 * 第二类：标记类
 * Multipart：表示请求体是一个支持文件上传的Form表单
 * 第三类：参数类 Query
 */

//-----------------------------------------------------------------------------
public interface IMiniDouyinService {
    // TODO 7: Define IMiniDouyinService
    String BASE_URL = "http://test.androidcamp.bytedance.com/mini_douyin/invoke/";
    //GET方法

    //POST方法
    @Multipart
    @POST("video")
    Call<PostResponse> postVideo(
            @Query("student_id") String studentId,
            @Query("user_name") String userName,
            @Part MultipartBody.Part image, @Part MultipartBody.Part video);
    @GET("video")
    Call<GetResponse> getVideos();
}
