package com.percy.helloworld.recyclerview;

public class Data {

    private String info;
    private String title;
    private  String Hotvalue;

    public Data(String info,String title,String Hotvalue) {
        this.info = info;
        this.Hotvalue = Hotvalue;
        this.title = title;
    }

    public String getInfo() { return info; }
    public String gettitle() {
        return title;
    }
    public String getHotvalue() {
        return Hotvalue;
    }
}
