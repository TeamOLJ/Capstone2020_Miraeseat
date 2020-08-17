package com.capstondesign.miraeseat.search;

public class TheaterClass {
    private String Theater_name;
    private String Theater_ID;


    public TheaterClass(String theater_name, String theater_id) {
        Theater_name = theater_name;
        Theater_ID = theater_id;
    }

    public TheaterClass() {
    }


    public String getTheater_name() {
        return Theater_name;
    }

    public void setTheater_name(String theater_name) {
        Theater_name = theater_name;
    }

    public String getTheater_ID() {
        return Theater_ID;
    }

    public void setTheater_ID(String theater_ID) {
        Theater_ID = theater_ID;
    }

}
