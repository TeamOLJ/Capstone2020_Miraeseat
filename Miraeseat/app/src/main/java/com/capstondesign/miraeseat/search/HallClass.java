package com.capstondesign.miraeseat.search;

public class HallClass implements InformationClass {
    private String Hall_name;
    private String ID;
    private String Poster;

    private double Latitude;    //위도
    private double Longitude;  //경도
    private String Seatplan_link;   //좌석배치도


    public HallClass(String hall_name, String id) {
        Hall_name = hall_name;
        ID = id;
    }


    public HallClass() {
    }


    @Override
    public String getName() {
        return Hall_name;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getPoster() {
        return Poster;
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


    public void setHall_name(String hall_name) {
        Hall_name = hall_name;
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
