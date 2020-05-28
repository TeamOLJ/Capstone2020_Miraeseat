package com.capstondesign.miraeseat.notice;

public class Notice {
    int _id;
    String title;
    String date;
    String noticeContext;

    public Notice(int _id, String title, String date, String noticeContext) {
        this._id = _id;
        this.title = title;
        this.date = date;
        this.noticeContext = noticeContext;
    }

    public int get_id() { return _id; }

    public void set_id(int _id) { this._id = _id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNoticeContext() {
        return noticeContext;
    }

    public void setNoticeContext(String noticeContext) {
        this.noticeContext = noticeContext;
    }
}
