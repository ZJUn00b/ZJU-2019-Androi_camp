package com.percy.minidouyin.model;

import com.google.gson.annotations.SerializedName;

public class Video {

  @SerializedName("student_id")
  private String studentId;
  @SerializedName("user_name")
  private String userName;
  @SerializedName("image_url")
  private String imageUrl;
  @SerializedName("video_url")
  private String videoUrl;
  @SerializedName("updatedAt")
  private String updatedAt;
  @SerializedName("image_w")
  private int image_w;
  @SerializedName("image_h")
  private int image_h;


  public String getStudentId() {
    return studentId;
  }

  public void setStudentId(String studentId) {
    this.studentId = studentId;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public void setImage_h(int image_h){
    this.image_h = image_h;
  }
  public int getImage_h() {
    return image_h;
  }

  public void setImage_w(int image_w) {
    this.image_w = image_w;
  }
  public int getImage_w(){
    return  image_w;
  }

  public void setupdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }
  public String getupdatedAt(){
    return  updatedAt;
  }
}