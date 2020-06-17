package com.capstondesign.miraeseat.search;

public class PlayClass implements InformationClass {
    private String Play_name;
    private String Poster;
    private String Hall_name;

    //공연 세부정보를 통해 얻는 정보
    private String ID;
    private String Start_date;
    private String End_date;
    private String Cast;

    public PlayClass(String play_name, String hall_name, String poster) {
        Play_name = play_name;
        Hall_name = hall_name;
        Poster = poster;
    }


    public String getPlay_name() {
        return Play_name;
    }

    public String getID() {
        return ID;
    }

    public String getStart_date() {
        return Start_date;
    }

    public String getEnd_date() {
        return End_date;
    }

    public String getCast() {
        return Cast;
    }



    public void setID(String id) {
        ID = id;
    }

    public void setStart_date(String start_date) {
        Start_date = start_date;
    }

    public void setEnd_date(String end_date) {
        End_date = end_date;
    }

    public void setCast(String cast) {
        Cast = cast;
    }

    @Override
    public String getName() {
        return Play_name;
    }

    @Override
    public String getHall_name() {
        return Hall_name;
    }

    @Override
    public String getPoster() {
        return Poster;
    }
}
