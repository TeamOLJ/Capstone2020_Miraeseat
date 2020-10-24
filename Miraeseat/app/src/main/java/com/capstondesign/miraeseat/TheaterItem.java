package com.capstondesign.miraeseat;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class TheaterItem extends TheaterActivity {
    final static String TAG = "TheaterItem";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int WIDTH, HEIGHT;

    final int PADDING = 1;

    private SeatPlanInfo seatInfo;

    private String selectedSeat = null;

    private Context ctx;
    private ViewGroup seatplan_layout;

    private TableLayout.LayoutParams seatRow_params;
    private TableRow.LayoutParams seatView_params;

    private TableLayout seatTableLayout;
    private TableRow seatRow;
    private View seatView;
    private TextView emptyView;
    private View previous_seat = null;

    int check = 0;

    TheaterItem(Context ctx, ViewGroup viewGroup) {
        this.ctx = ctx;
        seatplan_layout = viewGroup;
    }


    public void setSize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
    }


    public void init(SeatPlanInfo seat_plan_info) {
        seatInfo = seat_plan_info;
        seatInfo.init(WIDTH, HEIGHT);

        seatRow_params = new TableLayout.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        seatView_params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);

        max_col = seatInfo.getMaxCol();
    }

    public void execute() { //메모리 문제가 발생하여 아예 나눠서 돌림
        boolean isColRepeat=seatInfo.get_isColRepeat();
        boolean isgy = seatInfo.get_isgy();

        if(isColRepeat) {
            run_CR(isgy);
        } else {
            run(isgy);
        }

    }

    private int aisle_index, floor_index, row_start_end_index;
    private  int current, start, end, total_row, zero_line_count;

    private  int max_col;
    private  ArrayList<Long> col;

    private void run(boolean isgy) {
        total_row = 1;    //Map key가 1부터이므로...
        floor_index = 0;  //0번 행

        CreateTable();

        while (true) {   //한 층씩 구현
            zero_line_count = 0;
            col = seatInfo.getAisleSeat().get(String.valueOf(floor_index+1));   //Map key "1"부터...
            int row = seatInfo.getFloorRow().get(floor_index).intValue(); //floor_index+1 층의 행 수. 0번 인덱스부터..


            for (int r = 1; r <= row; ++r) { //1행부터 row행까지 한 행씩 구현
                //행 생성, 초기화
                seatRow = new TableRow(ctx);
                seatRow.setLayoutParams(seatRow_params);
                seatRow.setPadding(PADDING, PADDING, PADDING, PADDING);

                //변수 초기화
                current = 1;
                start = end = aisle_index = row_start_end_index = 0;  //시작좌석, 끝좌석, 복도 인덱스, 시작끝 인덱스

                while (true) {
                    try {
                        start = seatInfo.getRowStartEnd().get(String.valueOf(total_row)).get(row_start_end_index).intValue();
                        if (start == 0) {    //{0, 0}이면 행 사이 빈 공간으로 간주함.
                            AddEmptyView(1);
                            zero_line_count += 1;   //삥줄. 없는걸로 치기 때문에 zero_line_count 증가시켜서 증가된 r 상쇄시키기~
                            break;
                        } else if(start==45) {  //그러나 {max_col, max_col}이면 빈 공간이지만 행 번호는 부여함.
                            AddEmptyView(1);
                            break;
                        }
                        end = seatInfo.getRowStartEnd().get(String.valueOf(total_row)).get(row_start_end_index + 1).intValue();
                    } catch (Exception e) {
                        start = max_col + 1;
                    }

                    AddEmptyView(start - current);
                    for (; current < start; ++current) {
                        try {
                            if (current == col.get(aisle_index)) {
                                AddEmptyView();
                                ++aisle_index;
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }

                    if (max_col < start) {
                        break;
                    }

                    for (current = start; current <= end; ++current) {
                        AddSeatView(r - zero_line_count, current, isgy);
                        try {
                            if (current == col.get(aisle_index)) {
                                AddEmptyView();
                                ++aisle_index;
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                    row_start_end_index += 2;
                } //한 행 생성 끝

                seatTableLayout.addView(seatRow);   //레이아웃에 추가
                ++total_row;
            }
            try {   //층 사이 공간 추가
                emptyView = new TextView(ctx);
                emptyView.setLayoutParams(new TableRow.LayoutParams(0, seatInfo.getMarginRow_relative().get(floor_index++).intValue()));

                TableRow emptyRow = new TableRow(ctx);
                emptyRow.addView(emptyView);
                seatTableLayout.addView(emptyRow);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        seatplan_layout.addView(seatTableLayout);
    }


    public void run_CR(boolean isgy) {
        total_row = 1;    //Map key가 1부터이므로...
        floor_index = 0;  //0번 행

        CreateTable();

        while (true) {   //한 층씩 구현
            zero_line_count = 0;
            col = seatInfo.getAisleSeat().get(String.valueOf(floor_index+1));   //Map key "1"부터...
            int row = seatInfo.getFloorRow().get(floor_index).intValue(); //floor_index+1 층의 행 수. 0번 인덱스부터..


            for (int r = 1; r <= row; ++r) { //1행부터 row행까지 한 행씩 구현
                //행 생성, 초기화
                seatRow = new TableRow(ctx);
                seatRow.setLayoutParams(seatRow_params);
                seatRow.setPadding(PADDING, PADDING, PADDING, PADDING);

                //변수 초기화
                current = 1;
                start = end = aisle_index = row_start_end_index = 0;  //시작좌석, 끝좌석, 복도 인덱스, 시작끝 인덱스

                while (true) {
                    try {
                        start = seatInfo.getRowStartEnd().get(String.valueOf(total_row)).get(row_start_end_index).intValue();
                        if (start == 0) {    //{0, 0}이면 행 사이 빈 공간으로 간주함.
                            AddEmptyView(1);
                            zero_line_count += 1;   //삥줄. 없는걸로 치기 때문에 zero_line_count 증가시켜서 증가된 r 상쇄시키기~
                            break;
                        } else if(start==45) {  //그러나 {max_col, max_col}이면 빈 공간이지만 행 번호는 부여함.
                            AddEmptyView(1);
                            break;
                        }
                        end = seatInfo.getRowStartEnd().get(String.valueOf(total_row)).get(row_start_end_index + 1).intValue();
                    } catch (Exception e) {
                        start = max_col + 1;
                    }

                    AddEmptyView(start - current);
                    for (; current < start; ++current) {
                        try {
                            if (current == col.get(aisle_index)) {
                                AddEmptyView();
                                ++aisle_index;
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }

                    if (max_col < start) {
                        break;
                    }

                    for (current = start; current <= end; ++current) {
                        int tmp = (aisle_index==0)?start-1:col.get(aisle_index-1).intValue();
                        AddSeatView(r - zero_line_count, current-tmp, isgy);    //반복되는 번호의 좌석은 구역 정보가 필수
                        try {
                            if (current == col.get(aisle_index)) {

                                AddEmptyView();
                                ++aisle_index;
                            }
                        } catch (IndexOutOfBoundsException e) {
                        }
                    }
                    row_start_end_index += 2;
                } //한 행 생성 끝

                seatTableLayout.addView(seatRow);   //레이아웃에 추가
                ++total_row;
            }
            try {   //층 사이 공간 추가
                emptyView = new TextView(ctx);
                emptyView.setLayoutParams(new TableRow.LayoutParams(0, seatInfo.getMarginRow_relative().get(floor_index++).intValue()));

                TableRow emptyRow = new TableRow(ctx);
                emptyRow.addView(emptyView);
                seatTableLayout.addView(emptyRow);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        seatplan_layout.addView(seatTableLayout);
    }


    private void CreateTable() { //테이블 레이아웃 설정 및 생성
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(seatInfo.getSeatWidth_relative(), seatInfo.getSeatHeight_relative());
        params.addRule(RelativeLayout.ALIGN_LEFT, R.id.seatplan);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.seatplan);
        params.setMargins(seatInfo.getMarginLeft_relative(), seatInfo.getMarginTop_relative(), 0, 0);

        seatTableLayout = new TableLayout(ctx);
        seatTableLayout.setLayoutParams(params);

    }

    private void AddEmptyView() {
        emptyView = new TextView(ctx);
        emptyView.setLayoutParams(new TableRow.LayoutParams(seatInfo.getMarginCol_relative(), 0));
        seatRow.addView(emptyView);

    }

    private void AddEmptyView(int weight) {
        if (weight == 0) return;
        emptyView = new TextView(ctx);
        emptyView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, weight));
        emptyView.setBackgroundResource(R.drawable.seatbutton_clicked);
        seatRow.addView(emptyView);

    }


    private void AddSeatView(int row, int number, boolean b) {
        seatView = new View(ctx);
        seatView.setLayoutParams(seatView_params);
        seatView.setBackgroundResource(R.drawable.seatbutton_unclicked);
        seatView.setOnTouchListener(SeatButtonOnTouchListener);

        String s = (floor_index+1)+"층 ";
        if(b) s+=(char) (aisle_index + 'A') + "구역 ";
        s+=row + "열 " + number + "번";

        seatView.setTag(s);
        seatRow.addView(seatView);
    }


    public String getSelectedSeat() {
        return selectedSeat;
    }


    public void SelectSeat(View v) {
        if (previous_seat == v) {
            previous_seat.setBackgroundResource(R.drawable.seatbutton_unclicked);
            previous_seat = null;
            selectedSeat = null;
        } else {
            if (previous_seat != null) {
                previous_seat.setBackgroundResource(R.drawable.seatbutton_unclicked);
            }

            v.setBackgroundResource(R.drawable.seatbutton_clicked);

            String seat_name = v.getTag().toString();

            selectedSeat = seat_name;

            Toast.makeText(ctx, seat_name, Toast.LENGTH_SHORT).show();

            previous_seat = v;
        }
    }

    public View.OnTouchListener SeatButtonOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    TheaterActivity.dX = seatplan_layout.getX() - event.getRawX();
                    TheaterActivity.dY = seatplan_layout.getY() - event.getRawY();
                    check = 0;
                    return true;

                case MotionEvent.ACTION_MOVE:
                    check = check + 1;
                    return false;

                case MotionEvent.ACTION_UP:
                    if (check < 6) {
                        SelectSeat(v);
                    }
                    return true;

                default:
                    return false;
            }
        }
    };

}