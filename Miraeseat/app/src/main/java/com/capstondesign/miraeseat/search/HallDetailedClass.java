package com.capstondesign.miraeseat.search;

public class HallDetailedClass implements InformationClass {
    private String Combined_name;
    private String Combined_ID;
    private String Hall_Image;
    private boolean isSeatplan;


    public HallDetailedClass(String Combined_name, String Combined_ID, String Hall_Image, boolean isSeatplan) {
        this.Combined_name = Combined_name;
        this.Combined_ID = Combined_ID;
        this.Hall_Image = Hall_Image;
        this.isSeatplan = isSeatplan;
    }

    public HallDetailedClass() { }


    @Override
    public String getName() {
        return Combined_name;
    }

    @Override
    public String getPoster() {
        return Hall_Image;
    }

    @Override
    public String getId() {
        return Combined_ID;
    }

    public String getHall_Image() {
        return Hall_Image;
    }

    public boolean getIsSeatplan() {
        return isSeatplan;
    }
}
