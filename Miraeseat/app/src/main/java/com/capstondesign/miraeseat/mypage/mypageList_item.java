package com.capstondesign.miraeseat.mypage;

public class mypageList_item {

    private String listInfo;
    private Float listRatingbar;
    private String listImagePath;
    private String listWriting;
    private String listDate;

    public mypageList_item(String listInfo, Float listRatingbar, String listImagePath, String listWriting, String listDate) {
        this.listInfo = listInfo;
        this.listRatingbar = listRatingbar;
        this.listImagePath = listImagePath;
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


    public String getListImagePath() {
        return listImagePath;
    }

    public void setListImagePath(String listImagePath) {
        this.listImagePath = listImagePath;
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
