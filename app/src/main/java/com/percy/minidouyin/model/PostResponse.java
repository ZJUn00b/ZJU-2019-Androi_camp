package com.percy.minidouyin.model;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class PostResponse {
    //创建序号的参数有三个
    //result包含互相关联的ID和NAME
    //1.
    @SerializedName("result") private Map<String,String> result;
    //2.
    @SerializedName("url") private String url;
    //3.
    @SerializedName("success") private Boolean success;
    //result
    public Map<String,String> getResult() {return result;}
    public void setResult(Map<String,String> result) {this.result = result;}
    //url
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    //success
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }


}