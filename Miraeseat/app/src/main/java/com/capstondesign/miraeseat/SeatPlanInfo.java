package com.capstondesign.miraeseat;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

public class SeatPlanInfo {
    float width_ratio;
    int seat_width;

    float height_ratio;
    int seat_height;

    float margin_left_ratio;
    int margin_left;

    float margin_top_ratio;
    int margin_top;

    ArrayList<Double> margin_row_ratio;
    ArrayList<Integer> margin_row;

    float margin_col_ratio;
    int margin_col;

    ArrayList<Long> floor_row;
    Map<String, ArrayList<Long>> aisle_col;
    int max_col;

    Map<String, ArrayList<Long>> row_start_end;

    boolean isgy;
    boolean isColRepeat;

    SeatPlanInfo() { }

    public SeatPlanInfo(float seat_width_ratio, float seat_height_ratio, float margin_left_ratio, float margin_top_ratio,
                        ArrayList<Double> margin_row_ratio, float margin_col_ratio,
                        ArrayList<Long> floor_row, Map<String, ArrayList<Long>> aisle_col, int max_col,
                        Map<String, ArrayList<Long>> row_start_end, boolean isgy, boolean isColRepeat) {

        this.width_ratio = seat_width_ratio;
        this.height_ratio = seat_height_ratio;
        this.margin_left_ratio = margin_left_ratio;
        this.margin_top_ratio = margin_top_ratio;
        this.margin_row_ratio = margin_row_ratio;
        this.margin_col_ratio = margin_col_ratio;
        this.floor_row = floor_row;
        this.aisle_col = aisle_col;
        this.max_col = max_col;
        this.row_start_end = row_start_end;
        this.isgy = isgy;
        this.isColRepeat = isColRepeat;

    }

    public void init(int layout_width, int layout_height) {
        seat_width = (int)Math.round(width_ratio*layout_width);
        seat_height = (int)Math.round(height_ratio*layout_height);

        margin_left = (int)Math.round(margin_left_ratio*layout_width);
        margin_top = (int)Math.round(margin_top_ratio*layout_height);

        margin_row = new ArrayList<Integer>();
        for(int i=0; i<margin_row_ratio.size(); ++i) {
            margin_row.add((int)Math.round(margin_row_ratio.get(i)*layout_height));
        }

        margin_col = (int)Math.round(margin_col_ratio*layout_width);

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

    public ArrayList<Integer> getMargin_row() {
        return margin_row;
    }

    public ArrayList<Long> getFloor_row() {
        return floor_row;
    }

    public int getMargin_col() {
        return margin_col;
    }

    public Map<String, ArrayList<Long>> getAisle_col() {
        return aisle_col;
    }

    public int getMax_col() {
        return max_col;
    }

    public Map<String, ArrayList<Long>> getRow_start_end() {
        return row_start_end;
    }

    public boolean get_isgy() {return isgy;}

    public boolean get_isColRepeat() { return isColRepeat; }
}