package com.capstondesign.miraeseat;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class TheaterItem_try extends TheaterActivity {
    final static String TAG = "TheaterItem";

    private int WIDTH;
    private int HEIGHT;

    int[] tmp_row = {0, 20, 32}; //n초과 m 이하로 충/구역을 나누어야 하기 때문에 0부터 시작
    ArrayList<Integer> tmp_col = new ArrayList<Integer>(Arrays.asList(14, 30));
    int tmp_maxRow, tmp_maxCol;

    int[][] start_end_indexes = {{15, 30}, {11, 34}, {10, 35}, {9, 36}, {8, 37}, {7, 38}, {7, 38}, {6, 39}, {5, 40}, {5, 40}, {4, 41}, {3, 42}, {3, 42}, {2, 43}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44},
            {2, 43}, {2, 43}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}, {1, 44}};

    final int PADDING = 3;

    private String selectedSeat = null;

    private Context ctx;
    private ViewGroup seatplan_layout;

    private TableLayout.LayoutParams btnRow_params;
    private TableRow.LayoutParams btn_params, vertical_space_params, horizontal_space_params;

    private TableLayout btnTableLayout;
    private TableRow btnRow;
    private Button btn;
    private Button previous_btn = null;

    private TextView empty_view;

    private String[] table_info;


    TheaterItem_try(Context ctx, ViewGroup viewGroup) {
        this.ctx = ctx;
        seatplan_layout = viewGroup;
    }


    public void getSize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }


    public void init() {
        table_info = ctx.getResources().getStringArray(R.array.charlotte_ratio);

        int vertical_space = Math.round(Float.parseFloat(table_info[4]) * WIDTH);
        int horizontal_space = Math.round(Float.parseFloat(table_info[5]) * HEIGHT);
        tmp_maxRow = Integer.parseInt(table_info[6]);
        tmp_maxCol = Integer.parseInt(table_info[7]);

        btnRow_params = new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        btn_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);

        vertical_space_params = new TableRow.LayoutParams(vertical_space, 0);
        horizontal_space_params = new TableRow.LayoutParams(0, horizontal_space);

//        mScaleGestureDetector = new ScaleGestureDetector(ctx, new ScaleListener());
    }


    public void execute() {
        btnTableLayout = CreateTable();
        seatplan_layout.addView(btnTableLayout);
        //seatplan_layout.setOnTouchListener(SeatButtonOnTouchListener);

        for (int i = 0; i < start_end_indexes.length; ++i) {
            int start = start_end_indexes[i][0];
            int end = start_end_indexes[i][1];
            btnTableLayout.addView(CreateRow(i + 1, start, end));

            if (i + 1 == tmp_row[1]) {
                empty_view = new TextView(ctx);
                empty_view.setLayoutParams(horizontal_space_params);
                TableRow empty_row = new TableRow(ctx);
                empty_row.addView(empty_view);
                btnTableLayout.addView(empty_row);
            }
        }
    }


    private TableLayout CreateTable() { //테이블 레이아웃 설정 및 생성
        int width = Math.round(Float.parseFloat(table_info[0]) * WIDTH);
        int height = Math.round(Float.parseFloat(table_info[1]) * HEIGHT);
        int margin_left = Math.round(Float.parseFloat(table_info[2]) * WIDTH);
        int margin_top = Math.round(Float.parseFloat(table_info[3]) * HEIGHT);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.seatplan);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.seatplan);
        params.setMargins(margin_left, margin_top, 0, 0);

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(params);

        return tableLayout;
    }

    private TableRow CreateRow(int row_index, int start_index, int end_index) { //테이블 행과 내부 버튼 생성
        btnRow = new TableRow(ctx);
        btnRow.setLayoutParams(btnRow_params);
        btnRow.setPadding(PADDING, PADDING, PADDING, PADDING);


        int n = 1;
        while (n <= tmp_maxCol) {
            if (n >= start_index && n <= end_index) {
                btn = new Button(ctx);
                btn.setLayoutParams(btn_params);
                btn.setBackgroundResource(R.drawable.seatbutton_unclicked);
                //btn.setOnTouchListener(SeatButtonOnTouchListener);

                btn.setTag(row_index + "_" + n);
                btnRow.addView(btn);
            } else { //가장자리 빈 공간
                empty_view = new TextView(ctx);
                empty_view.setLayoutParams(btn_params);
                btnRow.addView(empty_view);
            }

            if (tmp_col.contains(n)) { //구역 간 빈 공간
                empty_view = new TextView(ctx);
                empty_view.setLayoutParams(vertical_space_params);
                btnRow.addView(empty_view);
            }

            n += 1;
        }

        return btnRow;
    }


    public String getSelectedSeat() {
        return selectedSeat;
    }


    private boolean IsOverLess(int num, int start, int end) {
        return (num > start) && (num <= end);

    }


    public void Test(View v) {
        if (previous_btn == v) {
            previous_btn.setBackgroundResource(R.drawable.seatbutton_unclicked);
            previous_btn = null;
            selectedSeat = null;
        } else {
            if (previous_btn != null) {
                previous_btn.setBackgroundResource(R.drawable.seatbutton_unclicked);
            }

            v.setBackgroundResource(R.drawable.seatbutton_clicked);

            String result = null;
            String[] tag = v.getTag().toString().split("_"); //버튼의 태그 받아와서 행/열 번호로 나누기

            int[] index = new int[2];
            index[0] = Integer.parseInt(tag[0]); //층 나누기
            index[1] = Integer.parseInt(tag[1]); //구역 나누기 - 구역 번호 알려줄 필요 없을 듯

            int floor = 1;

            for (; floor <= tmp_row.length; ++floor) { //층을 나누는 행 번호 넣기
                if (IsOverLess((index[0]), tmp_row[floor - 1], tmp_row[floor])) {
                    result = floor + "층 ";
                    break;
                }
            }
            index[0] -= tmp_row[floor - 1];

            result += (index[0] + "열 " + index[1] + "번");

            selectedSeat = result;

            Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();

            previous_btn = (Button) v;
        }
    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        final int action = ev.getAction();
//        Log.d("this", "dispatch");
//
//        if(action==MotionEvent.ACTION_UP) {
//            Log.d("this", "INTERCEPT!!!!!!");
//
//            return true;
//        }
//        else {
//            return false;
//        }
//    }
//
//
//    public View.OnTouchListener SeatButtonOnTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            float X = 0;
//            float Y = 0;
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_UP:
//                    X = event.getX();
//                    Y = event.getY();
//
//                    if ((X < v.getWidth() && 0 < X) && (Y < v.getHeight() && 0 < Y)) {
//                        Test(v);
//                    }
//                    return true;
//            }
//
//            return false;
//        }
//    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean check=false;
        View.OnTouchListener SeatButton = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float X = 0;
                float Y = 0;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        X = event.getX();
                        Y = event.getY();

                        if ((X < v.getWidth() && 0 < X) && (Y < v.getHeight() && 0 < Y)) {
                            Test(v);
                        }
                        break;

                }
                return true;
            }
        };
        return check;
    }
}