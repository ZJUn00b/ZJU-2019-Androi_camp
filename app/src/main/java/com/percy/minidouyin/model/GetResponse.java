package com.percy.minidouyin.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetResponse {
    //GET方法
    //拿到的有两个项：一个是视频
    // 一个是是否成功GET
    @SerializedName("success") Boolean success;
    @SerializedName("feeds")
    List<Video> videos;
    public List<Video> getVideos() { return videos; }
    //int image_w,image_h;
    //public int getImage_w(){return image_w;}
    //public int getImage_h(){return image_h;}
    public Boolean getSuccess() { return success; }
}