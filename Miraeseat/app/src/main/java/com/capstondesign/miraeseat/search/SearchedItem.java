package com.capstondesign.miraeseat.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.hall.HallInfo;

import java.util.ArrayList;

public class SearchedItem {
    private final TableLayout.LayoutParams layout_params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
    private final TableRow.LayoutParams row_params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
    private int count = 0;

    private Context context;
    private LayoutInflater inflater;
    private ViewGroup container;

    private ViewGroup rootView;
    private TableLayout item_table;
    private TableRow tableRow;

    private ArrayList<HallDetailedClass> datas = new ArrayList<HallDetailedClass>();

    public SearchedItem(Context context, LayoutInflater inflater, ViewGroup container, ArrayList arrayList) {
        this.context = context;
        this.inflater = inflater;
        this.container = container;
        datas = arrayList;
    }


    public ViewGroup createRootView() { //rootView의 fragment 설정
        int count = datas.size();

        if (count == 0) {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_no_result, container, false);
        } else {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_exist_result, container, false);
            item_table = rootView.findViewById(R.id.search_result);
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(layout_params);
            tableRow.setWeightSum(3);

            for (int i = 0; i < count; ++i) {
                AddTableItem(datas.get(i));
            }
            if (count % 3 != 0) item_table.addView(tableRow);
        }
        return rootView;
    }

    public void AddTableItem(HallDetailedClass mhallDetailedClass) {
        View item = inflater.inflate(R.layout.layout_search_item, null);
        item.setOnClickListener(ResultOnClickListener);
        item.setLayoutParams(row_params);
        item.setTag(1111, mhallDetailedClass.getId());   //아이템 클릭 시 시설 id와 시설명, 객석배치도 유무를 클릭리스너로 전달
        item.setTag(2222, mhallDetailedClass.getName());
        item.setTag(3333, mhallDetailedClass.getIsSeatplan());

        //아이템 이름 설정: 공연시설+공연장명
        TextView item_name = item.findViewById(R.id.item_name);
        item_name.setText(mhallDetailedClass.getName());

        //아이템 이미지 설정
        ImageView item_image = item.findViewById(R.id.item_image);
        String poster_link = mhallDetailedClass.getPoster();

        if (poster_link == null) { //임시. 포스터가 없는 경우 기본 이미지
            item_image.setImageResource(R.drawable.theater1);
        } else {
            Glide.with(context).load(poster_link).into(item_image);
        }

        ++count;
        tableRow.addView(item);
        if (count % 3 == 0) {
            item_table.addView(tableRow);
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(layout_params);
            tableRow.setWeightSum(3);
        }
    }


    public View.OnClickListener ResultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String combinedID = v.getTag(1111).toString();   //공연시설+공연장 id
            String combinedName = v.getTag(2222).toString();
            boolean isSeatplan = (boolean) v.getTag(3333);   //객석배치도 존재여부

            Intent intent = new Intent(context, HallInfo.class);
            intent.putExtra("Combined_ID", combinedID);
            intent.putExtra("Combined_Name", combinedName);
            intent.putExtra("is_Seatplan", isSeatplan);
            context.startActivity(intent);
        }
    };
}
