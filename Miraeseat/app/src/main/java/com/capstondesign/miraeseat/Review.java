package com.capstondesign.miraeseat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    private String ownerUser;
    @ServerTimestamp
    private Date timestamp;
    private String reviewDate;
    private String theaterName;
    private String seatNum;
    private String imagepath;
    private float rating;
    private String reviewText;

    public Review() {}

    public Review(String ownerUser, Date timestamp, String reviewDate, String theaterName, String seatNum, String imagepath, float rating, String reviewText) {
        this.ownerUser = ownerUser;
        this.timestamp = timestamp;
        this.reviewDate = reviewDate;
        this.theaterName = theaterName;
        this.seatNum = seatNum;
        this.imagepath = imagepath;
        this.rating = rating;
        this.reviewText = reviewText;
    }

    public String getOwnerUser() { return ownerUser; }

    public Date getTimestamp() { return timestamp; }

    public String getReviewDate() { return reviewDate; }

    public String getTheaterName() { return theaterName; }

    public String getSeatNum() { return seatNum; }

    public String getImagepath() { return imagepath; }

    public float getRating() { return rating; }

    public String getReviewText() { return reviewText; }

}
