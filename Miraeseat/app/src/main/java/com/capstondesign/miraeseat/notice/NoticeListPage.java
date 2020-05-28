package com.capstondesign.miraeseat.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.R;

import java.util.ArrayList;
import java.util.Date;

public class NoticeListPage extends AppCompatActivity {
    private static final String TAG = "NoticeListPage";

    DrawerHandler drawer;
    TextView titleText;

    RecyclerView recyclerView;
    NoticeAdapter adapter;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_list_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("공지사항");

        initUI();

        loadNoticeListData();
    }

    public void initUI() {
        recyclerView =(RecyclerView)findViewById(R.id.recyclerNoticeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        // 항목 사이에 구분선 넣어주는 코드
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));

        // 임시 데이터
        ArrayList<Notice> dataSet = new ArrayList<Notice>();
        dataSet.add(new Notice(0, "공지1번", "2020.05.01", "첫 번째 공지"));
        dataSet.add(new Notice(1, "공지2번", "2020.05.02", "두 번째 공지"));
        dataSet.add(new Notice(2, "공지3번", "2020.05.03", "세 번째 공지"));

        adapter = new NoticeAdapter();

        // 어댑터에 임시 데이터를 삽입하기 위한 코드
        adapter.setItems(dataSet);

        recyclerView.setAdapter(adapter);

        adapter.setOnNoticeClickListener(new OnNoticeClickListener() {
            @Override
            public void onItemClick(NoticeAdapter.ViewHolder holder, View view, int position) {
                Notice item = adapter.getItem(position);
                Log.d(TAG, "아이템 선택됨 : " + item.get_id());

                Intent intent = new Intent(NoticeListPage.this, NoticeContext.class);
                intent.putExtra("NoticeId", item.get_id());

                startActivity(intent);
            }
        });

    }

    // 서버에서 공지 DB 읽어오는 함수
    public int loadNoticeListData() {
        // 작성일 역순(공지번호 역순)으로 읽어오기
        // 읽어온 데이터 가공하여 보여주기
        return 0;
    }

    // 뒤로가기 버튼(홈버튼)을 누르면 창이 꺼지는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
