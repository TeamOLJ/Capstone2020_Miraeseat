package com.capstondesign.miraeseat.mypage;

public class mypageList_item {

    private String listInfo;
    private Float listRatingbar;
    private int listImage;
    private String listWriting;
    private String listDate;

    public mypageList_item(String listInfo, Float listRatingbar, int listImage, String listWriting, String listDate) {
        this.listInfo = listInfo;
        this.listRatingbar = listRatingbar;
        this.listImage = listImage;
        this.listWriting = listWriting;
        this.listDate = listDate;
    }


    public String getListInfo() {
        return listInfo;
    }

    public void setListInfo(String listInfo) {
        this.listInfo = listInfo;
    }

    public Float getListRatingbar() {
        return listRatingbar;
    }

    public void setListRatingbar(Float listRatingbar) {
        this.listRatingbar = listRatingbar;
    }


    public int getListImage() {
        return listImage;
    }

    public void setListImage(int listImage) {
        this.listImage = listImage;
    }

    public String getListWriting() {
        return listWriting;
    }

    public void setListWriting(String listWriting) {
        this.listWriting = listWriting;
    }

    public String getListDate() {
        return listDate;
    }

    public void setListDate(String listDate) {
        this.listDate = listDate;
    }
}
