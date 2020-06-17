package com.capstondesign.miraeseat.search;

public class PlayClass {
    private String Name;
    private String ID;
    private String Start_date;
    private String End_date;
    private String Poster;
    private String Play_hall;
    private String Cast;

    public PlayClass(String name, String play_hall, String poster) {
        Name = name;
        Play_hall = play_hall;
        Poster = poster;
    }


    public String getName() {
        return Name;
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

    public String getPoster() {
        return Poster;
    }

    public String getPlay_hall() {
        return Play_hall;
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
}
