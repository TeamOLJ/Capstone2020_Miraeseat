package com.capstondesign.miraeseat.hall;

public class HallList_item {

    private int poster_image;
    private String playName;
    private String playDate;
    private String playInfo;

    public HallList_item(int poster_image, String playName, String playDate, String playInfo) {
        this.poster_image = poster_image;
        this.playName = playName;
        this.playDate = playDate;
        this.playInfo = playInfo;
    }

    public int getPoster_image() {
        return poster_image;
    }

    public void setPoster_image(int poster_image) {
        this.poster_image = poster_image;
    }

    public String getPlayName() {
        return playName;
    }

    public void setPlayName(String playName) {
        this.playName = playName;
    }

    public String getPlayDate() {
        return playDate;
    }

    public void setPlayDate(String playDate) {
        this.playDate = playDate;
    }

    public String getPlayInfo() {
        return playInfo;
    }

    public void setPlayInfo(String playInfo) {
        this.playInfo = playInfo;
    }
}
