package com.bytedance.androidcamp.network.dou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponse {
    //GET方法
    //拿到的有两个项：一个是视频
    @SerializedName("feeds")
    List<Video> videos;
    public List<Video> getVideos() { return videos; }
    //一个是是否成功GET
    @SerializedName("success") Boolean success;
    public Boolean getSuccess() { return success; }
}