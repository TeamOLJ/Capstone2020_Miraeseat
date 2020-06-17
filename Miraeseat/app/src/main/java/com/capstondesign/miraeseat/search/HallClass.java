package com.capstondesign.miraeseat.search;

public class HallClass implements InformationClass {
    private String Hall_name;
    private String ID;

    //공연장 세부정보를 통해 얻는 정보
    private double Latitude;    //위도
    private double Longitude;  //경도

    //메인이나 검색 시 띄울 포스터
    private String Poster;

    //이건 어떡하지?
    private String Seatplan_link;


    public HallClass(String hall_name, String id) {
        this.Hall_name = hall_name;
        ID = id;
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

    @Override
    public String getName() {
        return Hall_name;
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
