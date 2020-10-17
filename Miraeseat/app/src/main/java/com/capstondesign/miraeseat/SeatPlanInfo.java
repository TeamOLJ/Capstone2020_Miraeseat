package com.capstondesign.miraeseat;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Map;

public class SeatPlanInfo {
    int seat_width;
    int seat_height;

    int margin_left;
    int margin_top;

    int margin_row;
    int margin_col;

    int max_row;
    int max_col;

    ArrayList<Integer> floor_row;
    Map<Integer, ArrayList<Integer>> row_start_end;
    ArrayList<Integer> aisle_col;

    boolean isgy;
    boolean isColRepeat;

    SeatPlanInfo() { }

    public SeatPlanInfo(int seat_width, int seat_height, int margin_left, int margin_top, int margin_row, int margin_col, int max_row, int max_col, ArrayList<Integer> floor_row, Map<Integer, ArrayList<Integer>> row_start_end, ArrayList<Integer> aisle_col, boolean isgy, boolean isColRepeat) {
        this.seat_width = seat_width;
        this.seat_height = seat_height;
        this.margin_left = margin_left;
        this.margin_top = margin_top;
        this.margin_row = margin_row;
        this.margin_col = margin_col;
        this.max_row = max_row;
        this.max_col = max_col;
        this.floor_row = floor_row;
        this.row_start_end = row_start_end;
        this.aisle_col = aisle_col;
        this.isgy = isgy;
        this.isColRepeat = isColRepeat;

    }

    public int getSeat_width() {
        return seat_width;
    }

    public int getSeat_height() {
        return seat_height;
    }

    public int getMargin_left() {
        return margin_left;
    }

    public int getMargin_top() {
        return margin_top;
    }

    public int getMargin_row() {
        return margin_row;
    }

    public int getMargin_col() {
        return margin_col;
    }

    public int getMax_row() {
        return max_row;
    }

    public int getMax_col() {
        return max_col;
    }

    public ArrayList<Integer> getFloor_row() {
        return floor_row;
    }

    public Map<Integer, ArrayList<Integer>> getRow_start_end() {
        return row_start_end;
    }

    public ArrayList<Integer> getAisle_col() {
        return aisle_col;
    }
}
