package com.capstondesign.miraeseat;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class UserReport {
    private String userEmail;
    private String targetReview;
    private String reportContext;
    @ServerTimestamp
    private Date reportTime;
    private Boolean isChecked;

    public UserReport() { }

    public UserReport(String userEmail, String targetReview, String reportContext, Date reportTime, Boolean isChecked) {
        this.userEmail = userEmail;
        this.targetReview = targetReview;
        this.reportContext = reportContext;
        this.reportTime = reportTime;
        this.isChecked = isChecked;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTargetReview() {
        return targetReview;
    }

    public void setTargetReview(String targetReview) {
        this.targetReview = targetReview;
    }

    public String getReportContext() {
        return reportContext;
    }

    public void setReportContext(String reportContext) {
        this.reportContext = reportContext;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean checked) {
        isChecked = checked;
    }
}
