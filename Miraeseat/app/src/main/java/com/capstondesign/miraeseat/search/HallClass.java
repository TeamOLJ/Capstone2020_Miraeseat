package com.capstondesign.miraeseat.search;

public class HallClass {
    private String Name;
    private String ID;
    private double Latitude;    //위도
    private double Longitude;  //경도

    private String Seatplan_link;

    public HallClass(String name, String id) {
        this.Name = name;
        ID = id;
    }

    public String getName() {
        return Name;
    }

    public String getID() {
        return ID;
    }

    public double getLatitude() {
        return Latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public String getSeatplan_link() {
        return Seatplan_link;
    }



    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public void setSeatplan_link(String seatplan_link) {
        Seatplan_link = seatplan_link;
    }
}
