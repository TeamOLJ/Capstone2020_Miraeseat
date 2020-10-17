package com.capstondesign.miraeseat;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TheaterItem_try extends TheaterActivity {
    final static String TAG = "TheaterItem";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int WIDTH;
    private int HEIGHT;

    final int PADDING = 3;

    private String selectedSeat = null;

    private Context ctx;
    private ViewGroup seatplan_layout;

    private TableLayout.LayoutParams seatRow_params;
    private TableRow.LayoutParams seat_params, vertical_space_params, horizontal_space_params;

    private TableLayout seatTableLayout;
    private TableRow seatRow;
    private View seatBtn;
    private View previous_seat = null;

    private TextView empty_view;

    private String[] table_info;

    int check = 0;


    TheaterItem_try(Context ctx, ViewGroup viewGroup) {
        this.ctx = ctx;
        seatplan_layout = viewGroup;
    }


    public void getSize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }

//
//    public void init(SeatPlanInfo seatPlanInfo) {
//        table_info = ctx.getResources().getStringArray(R.array.charlotte_ratio);
//
//        int vertical_space = Math.round(Float.parseFloat(table_info[4]) * WIDTH);
//        int horizontal_space = Math.round(Float.parseFloat(table_info[5]) * HEIGHT);
//        tmp_maxRow = Integer.parseInt(table_info[6]);
//        tmp_maxCol = Integer.parseInt(table_info[7]);
//
//        seatRow_params = new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
//        seat_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//
//        vertical_space_params = new TableRow.LayoutParams(vertical_space, 0);
//        horizontal_space_params = new TableRow.LayoutParams(0, horizontal_space);
//    }
//
//
//    public void execute() {
//        seatTableLayout = CreateTable();
//        seatplan_layout.addView(seatTableLayout);
//
//        for (int i = 0; i < start_end_indexes.length; ++i) {
//            int start = start_end_indexes[i][0];
//            int end = start_end_indexes[i][1];
//            seatTableLayout.addView(CreateRow(i + 1, start, end));
//
//            if (i + 1 == tmp_row[1]) {
//                empty_view = new TextView(ctx);
//                empty_view.setLayoutParams(horizontal_space_params);
//                TableRow empty_row = new TableRow(ctx);
//                empty_row.addView(empty_view);
//                seatTableLayout.addView(empty_row);
//            }
//        }
//    }
//
//
//    private TableLayout CreateTable() { //테이블 레이아웃 설정 및 생성
//        int width = Math.round(Float.parseFloat(table_info[0]) * WIDTH);
//        int height = Math.round(Float.parseFloat(table_info[1]) * HEIGHT);
//        int margin_left = Math.round(Float.parseFloat(table_info[2]) * WIDTH);
//        int margin_top = Math.round(Float.parseFloat(table_info[3]) * HEIGHT);
//
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
//        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.seatplan);
//        params.addRule(RelativeLayout.ALIGN_TOP, R.id.seatplan);
//        params.setMargins(margin_left, margin_top, 0, 0);
//
//        TableLayout tableLayout = new TableLayout(ctx);
//        tableLayout.setLayoutParams(params);
//
//        return tableLayout;
//    }
//
//    private TableRow CreateRow(int row_index, int start_index, int end_index) { //테이블 행과 내부 버튼 생성
//        seatRow = new TableRow(ctx);
//        seatRow.setLayoutParams(seatRow_params);
//        seatRow.setPadding(PADDING, PADDING, PADDING, PADDING);
//
//
//        int n = 1;
//        while (n <= tmp_maxCol) {
//            if (n >= start_index && n <= end_index) {
//                seatBtn = new View(ctx);
//                seatBtn.setLayoutParams(seat_params);
//                seatBtn.setBackgroundResource(R.drawable.seatbutton_unclicked);
//                seatBtn.setOnTouchListener(SeatButtonOnTouchListener);
//
//                seatBtn.setTag(row_index + "_" + n);
//                seatRow.addView(seatBtn);
//            } else { //가장자리 빈 공간
//                empty_view = new TextView(ctx);
//                empty_view.setLayoutParams(seat_params);
//                seatRow.addView(empty_view);
//            }
//
//            if (tmp_col.contains(n)) { //구역 간 빈 공간
//                empty_view = new TextView(ctx);
//                empty_view.setLayoutParams(vertical_space_params);
//                seatRow.addView(empty_view);
//            }
//
//            n += 1;
//        }
//
//        return seatRow;
//    }
//
//
//    public String getSelectedSeat() {
//        return selectedSeat;
//    }
//
//
//    private boolean IsOverLess(int num, int start, int end) {
//        return (num > start) && (num <= end);
//
//    }
//
//
//
//    public void Test(View v) {
//        if (previous_seat == v) {
//            previous_seat.setBackgroundResource(R.drawable.seatbutton_unclicked);
//            previous_seat = null;
//            selectedSeat = null;
//        } else {
//            if (previous_seat != null) {
//                previous_seat.setBackgroundResource(R.drawable.seatbutton_unclicked);
//            }
//
//            v.setBackgroundResource(R.drawable.seatbutton_clicked);
//
//            String result = null;
//            String[] tag = v.getTag().toString().split("_"); //버튼의 태그 받아와서 행/열 번호로 나누기
//
//            int[] index = new int[2];
//            index[0] = Integer.parseInt(tag[0]); //층 나누기
//            index[1] = Integer.parseInt(tag[1]); //구역 나누기 - 구역 번호 알려줄 필요 없을 듯
//
//            int floor = 1;
//
//            for (; floor <= tmp_row.length; ++floor) { //층을 나누는 행 번호 넣기
//                if (IsOverLess((index[0]), tmp_row[floor - 1], tmp_row[floor])) {
//                    result = floor + "층 ";
//                    break;
//                }
//            }
//            index[0] -= tmp_row[floor - 1];
//
//            result += (index[0] + "열 " + index[1] + "번");
//
//            selectedSeat = result;
//
//            Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
//
//            previous_seat = v;
//        }
//    }
//
//    public View.OnTouchListener SeatButtonOnTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    TheaterActivity.dX = seatplan_layout.getX() - event.getRawX();
//                    TheaterActivity.dY = seatplan_layout.getY() - event.getRawY();
//                    check = 0;
//                    return true;
//
//                case MotionEvent.ACTION_MOVE:
//                    check = check + 1;
//                    return false;
//
//                case MotionEvent.ACTION_UP:
//                    if (check < 6) {
//                        Test(v);
//                    }
//                    return true;
//
//                default:
//                    return false;
//            }
//        }
//    };
//
}