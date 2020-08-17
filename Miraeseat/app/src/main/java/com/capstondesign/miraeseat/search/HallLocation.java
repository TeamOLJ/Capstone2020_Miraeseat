package com.capstondesign.miraeseat.search;

public class HallLocation {
    private String Theater_name;
    private double Latitude;   //위도
    private double Longitude;  //경도

    public HallLocation() { }

    public String getTheater_name() {
        return Theater_name;
    }

    public void setTheater_name(String theater_name) {
        Theater_name = theater_name;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }
}
