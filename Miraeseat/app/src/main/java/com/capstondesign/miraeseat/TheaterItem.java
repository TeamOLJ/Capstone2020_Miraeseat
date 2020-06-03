package com.capstondesign.miraeseat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class TheaterItem {
    private int WIDTH;
    private int HEIGHT;
    private final float DENSITY;

    private Context ctx;
    private ViewGroup seatplan_layout;

    private TableLayout btnTableLayout;
    private TableRow btnRow;

    private TableLayout.LayoutParams btn_params, vertical_space_params, horizontal_space_params;

    private String[] table_info;
    private int[] vertical_criteria, horizontal_criteria;

    TheaterItem(Context ctx, ViewGroup viewGroup) {
        this.ctx = ctx;
        seatplan_layout = viewGroup;
        DENSITY = ctx.getResources().getDisplayMetrics().xdpi/DisplayMetrics.DENSITY_DEFAULT;
    }

    public void init() {
        table_info = ctx.getResources().getStringArray(R.array.charlotte);

        btn_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        btn_params.weight = 1;

        int vertical_space =Math.round(Float.parseFloat(table_info[4])*WIDTH);
        int horizontal_space =Math.round(Float.parseFloat(table_info[5])*HEIGHT);

        vertical_space_params = new TableLayout.LayoutParams(vertical_space, TableLayout.LayoutParams.WRAP_CONTENT);
        horizontal_space_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, horizontal_space);
    }

    public void execute() {
        btnTableLayout = CreateTable();
        seatplan_layout.addView(btnTableLayout);
        CreateRow();
    }

    private TableLayout CreateTable() {
        int width = Math.round(Float.parseFloat(table_info[0])*WIDTH);
        int height = Math.round(Float.parseFloat(table_info[1])*HEIGHT);
        int margin_left = Math.round(Float.parseFloat(table_info[2])*WIDTH);
        int margin_top = Math.round(Float.parseFloat(table_info[3])*HEIGHT);
//        int width = convertDpToPx(table_info[0]);
//        int height = convertDpToPx(table_info[1]);
//        int margin_left = convertDpToPx(table_info[2]);
//        int margin_top = convertDpToPx(table_info[3]);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.setMargins(margin_left, margin_top, 0, 0);

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(params);
        tableLayout.setBackgroundColor(ctx.getResources().getColor(R.color.main_color));

        return tableLayout;
    }

    private void CreateRow() {
        btnRow = new TableRow(ctx);
        btnRow.setLayoutParams(btn_params);

        CreateButton(11, 14);
        btnTableLayout.addView(btnRow);

        //왼쪽 패딩 x=8x+(x%4==0) 저 boolean을 어떡할까
    }

    private void CreateButton(int start_index, int end_index) {
        int n = start_index;

        for(; n<=end_index; n++) {
            Button btn = new Button(ctx);
            btn.setLayoutParams(btn_params);
            btn.setBackgroundResource(R.drawable.seatbutton);
            btn.setTag(n);
            btn.setOnClickListener(SeatButtonOnClickListner);

            btnRow.addView(btn);
        }
    }

    public View.OnClickListener SeatButtonOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = (String) v.getTag();
            Toast.makeText(ctx, tag, Toast.LENGTH_SHORT).show();
        }
    };

    private int convertDpToPx(int dp){
        return Math.round(dp*DENSITY);
    }
}