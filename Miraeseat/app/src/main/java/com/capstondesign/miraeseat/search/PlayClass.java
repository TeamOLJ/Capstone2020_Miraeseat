package com.capstondesign.miraeseat.search;

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
        Date = getDate();
    }

    public void setState(String state) {
        State = state;
    }

    static public String getToday() {
        Calendar calendar = Calendar.getInstance();

        String Today = Integer.toString(calendar.get(Calendar.YEAR)) + String.format("%02d", calendar.get(Calendar.MONTH) + 1) + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));

        return Today;
    }

    //오늘 날짜와 3달 뒤 날짜를 구함
    static public String[] getThreeMonthDate() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String Today = Integer.toString(year) + String.format("%02d", month) + String.format("%02d", day);

        if (month > 9) {
            month -= 9;
            year += 1;
        } else month += 3;

        String AfterThreeMonth = Integer.toString(year) + String.format("%02d", month) + String.format("%02d", day);

        return new String[]{Today, AfterThreeMonth};
    }
}
