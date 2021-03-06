package com.capstondesign.miraeseat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserInquiry {
    private String userName;
    private String userEmail;
    private String inquiryTitle;
    private String inquiryContext;
    @ServerTimestamp
    private Date inquiryTime;
    private Boolean isChecked;

    public UserInquiry() { }

    public UserInquiry(String userName, String userEmail, String inquiryTitle, String inquiryContext, Date inquiryTime, Boolean isChecked) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.inquiryTitle = inquiryTitle;
        this.inquiryContext = inquiryContext;
        this.inquiryTime = inquiryTime;
        this.isChecked = isChecked;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getInquiryTitle() {
        return inquiryTitle;
    }

    public void setInquiryTitle(String inquiryTitle) {
        this.inquiryTitle = inquiryTitle;
    }

    public String getInquiryContext() {
        return inquiryContext;
    }

    public void setInquiryContext(String inquiryContext) {
        this.inquiryContext = inquiryContext;
    }

    public Date getInquiryTime() {
        return inquiryTime;
    }

    public void setInquiryTime(Date inquiryTime) {
        this.inquiryTime = inquiryTime;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean checked) {
        isChecked = checked;
    }
}
