package com.capstondesign.miraeseat;

public class seatList_item {

    private String nickname;
    private int profile_image;
    private int review_image;
    private float ratingbar;
    private String review_writing;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(int profile_image) {
        this.profile_image = profile_image;
    }

    public int getReview_image() {
        return review_image;
    }

    public void setReview_image(int review_image) {
        this.review_image = review_image;
    }

    public float getRatingbar() {
        return ratingbar;
    }

    public void setRatingbar(float ratingbar) {
        this.ratingbar = ratingbar;
    }

    public String getReview_writing() {
        return review_writing;
    }

    public void setReview_writing(String review_writing) {
        this.review_writing = review_writing;
    }

    public seatList_item(String nickname, int profile_image, int review_image, float ratingbar, String review_writing) {
        this.nickname = nickname;
        this.profile_image = profile_image;
        this.review_image = review_image;
        this.ratingbar = ratingbar;
        this.review_writing = review_writing;
    }
}
