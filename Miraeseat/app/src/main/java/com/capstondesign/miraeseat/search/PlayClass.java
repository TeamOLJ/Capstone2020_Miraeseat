package com.capstondesign.miraeseat.search;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PlayClass {
    private String Play_name;
    private String Poster;
    private String ID;

    //공연 목록조회를 통해 얻는 정보
    private String Date;
    private String State;

    public PlayClass(String id, String play_name, String poster) {
        ID = id;
        Play_name = play_name;
        Poster = poster;
    }


    //hall.HallInfoAPI.Get_Play 에 사용
    public PlayClass(String play_name, String date, String poster, String state) {
        Play_name = play_name;
        Date = date;
        Poster = poster;
        State = state;
    }

    public String getPlay_name() {
        return Play_name;
    }

    public String getPoster() {
        return Poster;
    }

    public String getID() {
        return ID;
    }

    public String getDate() {
        return Date;
    }

    public String getState() {
        return State;
    }



    public void setID(String id) {
        ID = id;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setState(String state) {
        State = state;
    }

    static public String getToday() {
        Calendar today = Calendar.getInstance();
        return new SimpleDateFormat("yyyyMMdd").format(today.getTime());
    }

    static public String getAfter3Month() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);

        String After3Mon = new SimpleDateFormat("yyyyMMdd").format(cal.getTime());
        return After3Mon;
    }
}
