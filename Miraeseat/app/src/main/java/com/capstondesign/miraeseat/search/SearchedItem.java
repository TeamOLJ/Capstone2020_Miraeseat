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

import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.hall.HallInfo;

public class SearchedItem {
    private final TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT);
    private int count = 0;

    Context context;
    LayoutInflater inflater;
    ViewGroup container;
    private String search_word;

    private ViewGroup rootView;
    private TableLayout item_table;
    private TableRow tableRow;

    String[] ss = {"lee", "oh", "jang", "jazz", "level", "open the door!!!", "join", "jail"}; //예시

    public SearchedItem(Context context, LayoutInflater inflater, ViewGroup container, String search_word) {
        this.context = context;
        this.inflater = inflater;
        this.container = container;
        this.search_word = search_word;
    }

    private boolean IsEmpty() {
        if (search_word.length() != 0) { //DB 연동 필요. 포함 이름이 있는지 없는지만 확인하고 빠져나옴
            for (int i = 0; i < ss.length; ++i) {
                if (ss[i].contains(search_word)) {
                    return false;
                }
            }
        }
        return true;
    }

    public ViewGroup createRootView() { //rootView의 fragment 설정
        if (IsEmpty()) {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_no_result, container, false);
        } else {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_exist_result, container, false);
            item_table = rootView.findViewById(R.id.search_result);
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(params);


            for (int i = 0; i < ss.length; ++i) {   //DB 연동이 필요함.
                if (ss[i].contains(search_word)) {
                    AddTableItem(ss[i]);
                }
            }
            if (count % 3 != 0) item_table.addView(tableRow);
        }
        return rootView;
    }

    public void AddTableItem(String name) { //DB에서 이름과 이미지 가져오기
        View item = inflater.inflate(R.layout.layout_search_item, null);
        item.setOnClickListener(ResultOnClickListener);

        TextView item_name = item.findViewById(R.id.item_name);
        item_name.setText(name);
        ImageView item_image = item.findViewById(R.id.item_image);
        item_image.setImageResource(R.drawable.theater1);

        ++count;
        tableRow.addView(item);
        if (count % 3 == 0) {
            item_table.addView(tableRow);
            tableRow = new TableRow(context);
            tableRow.setLayoutParams(params);
        }
    }

    public View.OnClickListener ResultOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String data = ((TextView) v.findViewById(R.id.item_name)).getText().toString(); //공연 또는 공연장
            Intent intent = new Intent(context, HallInfo.class);
            intent.putExtra("search item", data);
            context.startActivity(intent);
        }
    };
}
