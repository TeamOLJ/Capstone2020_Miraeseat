package com.capstondesign.miraeseat.mypage;

public class mypageList_item {

    private String documentID;
    private String theaterName;
    private String seatNum;
    private Float seatRating;
    private String imagePath;
    private String reviewContext;
    private String reviewDate;

    public mypageList_item(String documentID, String theaterName, String seatNum, Float seatRating, String imagePath, String reviewContext, String reviewDate) {
        this.documentID = documentID;
        this.theaterName = theaterName;
        this.seatNum = seatNum;
        this.seatRating = seatRating;
        this.imagePath = imagePath;
        this.reviewContext = reviewContext;
        this.reviewDate = reviewDate;
    }

    public String getDocumentID() { return documentID; }

    public void setDocumentID(String documentID) { this.documentID = documentID; }

    public String getTheaterName() {
        return theaterName;
    }

    public void setTheaterName(String theaterName) {
        this.theaterName = theaterName;
    }

    public String getSeatNum() { return seatNum; }

    public void setSeatNum(String seatNum) { this.seatNum = seatNum; }

    public Float getSeatRating() {
        return seatRating;
    }

    public void setSeatRating(Float seatRating) {
        this.seatRating = seatRating;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getReviewContext() {
        return reviewContext;
    }

    public void setReviewContext(String reviewContext) {
        this.reviewContext = reviewContext;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String listDate) {
        this.reviewDate = reviewDate;
    }
}
