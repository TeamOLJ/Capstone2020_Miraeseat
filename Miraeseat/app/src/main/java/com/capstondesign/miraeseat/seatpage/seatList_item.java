package com.capstondesign.miraeseat.seatpage;

public class seatList_item {

    private String documentID;
    private String nickname;
    private String profile_image;
    private String review_image;
    private float ratingbar;
    private String review_writing;
    private boolean isOwner;

    public seatList_item(String documentID, String nickname, String profile_image, String review_image, float ratingbar, String review_writing, boolean isOwner) {
        this.documentID = documentID;
        this.nickname = nickname;
        this.profile_image = profile_image;
        this.review_image = review_image;
        this.ratingbar = ratingbar;
        this.review_writing = review_writing;
        this.isOwner = isOwner;
    }

    public String getDocumentID() { return documentID; }

    public void setDocumentID(String documentID) { this.documentID = documentID; }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getReview_image() {
        return review_image;
    }

    public void setReview_image(String review_image) {
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

    public boolean getIsOwner() { return isOwner; }

    public void setIsOwner(boolean isOwner) { this.isOwner = isOwner; }

}
