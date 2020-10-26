package com.capstondesign.miraeseat;

import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

public class SeatPlanInfo {
    float seatWidth;
    int seatWidth_relative;

    float seatHeight;
    int seatHeight_relative;

    float marginLeft;
    int marginLeft_relative;

    float marginTop;
    int marginTop_relative;

    ArrayList<Double> marginRow;
    ArrayList<Integer> marginRow_relative;

    float marginCol;
    int marginCol_relative;

    ArrayList<Long> floorRow;
    Map<String, ArrayList<Long>> aisleSeat;
    ArrayList<Long> maxCol;

    Map<String, ArrayList<Long>> rowStartEnd;

    boolean _isgy;
    boolean _isColRepeat;

    SeatPlanInfo() { }

    public SeatPlanInfo(float seat_width, float seat_height, float margin_left, float margin_top,
                        ArrayList<Double> margin_row, float margin_col,
                        ArrayList<Long> floor_row, Map<String, ArrayList<Long>> aisle_seat, ArrayList<Long> max_col,
                        Map<String, ArrayList<Long>> row_start_end, boolean _isgy, boolean _isColRepeat) {

        this.seatWidth = seat_width;
        this.seatHeight = seat_height;
        this.marginLeft = margin_left;
        this.marginTop = margin_top;
        this.marginRow = margin_row;
        this.marginCol = margin_col;
        this.floorRow = floor_row;
        this.aisleSeat = aisle_seat;
        this.maxCol = max_col;
        this.rowStartEnd = row_start_end;
        this._isgy = _isgy;
        this._isColRepeat = _isColRepeat;

    }

    public void init(int layout_width, int layout_height) {
        seatWidth_relative = (int)Math.round(seatWidth *layout_width);
        seatHeight_relative = (int)Math.round(seatHeight *layout_height);

        marginLeft_relative = (int)Math.round(marginLeft *layout_width);
        marginTop_relative = (int)Math.round(marginTop *layout_height);

        marginRow_relative = new ArrayList<Integer>();
        for(int i = 0; i< marginRow.size(); ++i) {
            try{
                marginRow_relative.add((int) Math.round(marginRow.get(i) * layout_height));
            } catch (ClassCastException e) {
                marginRow_relative.add(0);
            }
        }

        marginCol_relative = (int)Math.round(marginCol *layout_width);

    }

    public float getSeatWidth() {
        return seatWidth;
    }

    public int getSeatWidth_relative() {
        return seatWidth_relative;
    }

    public float getSeatHeight() {
        return seatHeight;
    }

    public int getSeatHeight_relative() {
        return seatHeight_relative;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public int getMarginLeft_relative() {
        return marginLeft_relative;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public int getMarginTop_relative() {
        return marginTop_relative;
    }

    public ArrayList<Double> getMarginRow() {
        return marginRow;
    }

    public ArrayList<Integer> getMarginRow_relative() {
        return marginRow_relative;
    }

    public ArrayList<Long> getFloorRow() {
        return floorRow;
    }

    public int getMarginCol() {
        return marginCol_relative;
    }

    public int getMarginCol_relative() {
        return marginCol_relative;
    }

    public Map<String, ArrayList<Long>> getAisleSeat() {
        return aisleSeat;
    }

    public ArrayList<Long> getMaxCol() {
        return maxCol;
    }

    public Map<String, ArrayList<Long>> getRowStartEnd() {
        return rowStartEnd;
    }

    public boolean get_isgy() {return _isgy;}

    public boolean get_isColRepeat() { return _isColRepeat; }
}