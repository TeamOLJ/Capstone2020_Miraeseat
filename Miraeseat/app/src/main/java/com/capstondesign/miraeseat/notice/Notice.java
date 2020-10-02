package com.capstondesign.miraeseat.notice;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

// 인텐트 간 객체 전달을 위해 Parcelable을 상속받도록 함
public class Notice implements Parcelable {
    private String noticeTitle;
    private String noticeDate;
    private String noticeContext;

    @ServerTimestamp
    private Date timestamp;

    public Notice() {}

    public Notice(String noticeTitle, String noticeDate, String noticeContext, Date timestamp) {
        this.noticeTitle = noticeTitle;
        this.noticeDate = noticeDate;
        this.noticeContext = noticeContext;
        this.timestamp = timestamp;
    }

    public Notice(Parcel src) {
        noticeTitle = src.readString();
        noticeDate = src.readString();
        noticeContext = src.readString();
        // timestamp는 client에서 더이상 중요하지 않으므로 값을 그냥 버림
        timestamp = null;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public String getNoticeContext() {
        return noticeContext;
    }

    public Date getTimestamp() { return timestamp; }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Notice createFromParcel(Parcel src) {
            return new Notice(src);
        }

        public Notice[] newArray(int size) {
            return new Notice[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(noticeTitle);
        dest.writeString(noticeDate);
        dest.writeString(noticeContext);
    }

}
