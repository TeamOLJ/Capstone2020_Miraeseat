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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

public class SeatplanItem {
    private float DENSITY;  //좌표 전환이 안된다...
    private Context ctx;
    private ViewGroup seatplan_layout;

    private TableLayout btnTableLayout;
    private TableRow btnRow;

    private TableLayout.LayoutParams btn_params, horizontal_space_params, vertical_space_params;

    private int[] table_info;

    SeatplanItem(Context ctx, ViewGroup viewGroup) {
        this.ctx = ctx;
        seatplan_layout = viewGroup;
        DENSITY = ctx.getResources().getDisplayMetrics().density;
    }

    public void init() {
        table_info = ctx.getResources().getIntArray(R.array.charlotte);

        btn_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        btn_params.weight = 1;

        horizontal_space_params = new TableLayout.LayoutParams(pxFromDp(table_info[4]), TableLayout.LayoutParams.WRAP_CONTENT);
        vertical_space_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, pxFromDp(table_info[5]));
    }

    public void execute() {
        btnTableLayout = CreateTable();
        seatplan_layout.addView(btnTableLayout);
        CreateRow();
    }

    private TableLayout CreateTable() {
        int width = pxFromDp(table_info[0]);
        int height = pxFromDp(table_info[1]);
        int margin_left = pxFromDp(table_info[2]);
        int margin_top = pxFromDp(table_info[3]);

        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
        params.setMargins(margin_left, margin_top, 0, 0);

        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setLayoutParams(params);
        tableLayout.setBackgroundColor(Color.BLACK);

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
            Log.d("this", "make button");
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

    private int pxFromDp(float dp)
    {
        return Math.round(dp *DENSITY);
    }
}