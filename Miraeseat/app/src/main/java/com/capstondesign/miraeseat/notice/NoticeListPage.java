package com.capstondesign.miraeseat.notice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NoticeListPage extends AppCompatActivity {
    private static final String TAG = "NoticeListPage";

    DrawerHandler drawer;
    TextView titleText;

    private FirebaseFirestore db;

    RecyclerView recyclerView;
    NoticeAdapter adapter;

    Notice notice;

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

        recyclerView =(RecyclerView)findViewById(R.id.recyclerNoticeList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        // 항목 사이에 구분선 넣어주는 코드
        recyclerView.addItemDecoration(new DividerItemDecoration(this, 1));

        // 어댑터 선언
        adapter = new NoticeAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnNoticeClickListener(new OnNoticeClickListener() {
            @Override
            public void onItemClick(NoticeAdapter.ViewHolder holder, View view, int position) {
                Notice item = adapter.getItem(position);

                Intent intent = new Intent(NoticeListPage.this, NoticeContext.class);
                intent.putExtra("SelectedNotice", item);

                startActivity(intent);
            }
        });

        loadNoticeListData();
    }

    // 서버에서 공지 DB 읽어오는 함수
    public void loadNoticeListData() {
        final ArrayList<Notice> dataSet = new ArrayList<Notice>();

        db = FirebaseFirestore.getInstance();

        // timestamp에 따라 정렬한 후 읽어옴
        db.collection("OfficialNotice").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // DB를 읽는 데에 성공했으면,
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // 각각의 쿼리 반환값을 dataSet에 추가
                        notice = document.toObject(Notice.class);
                        dataSet.add(notice);
                    }

                    // 어댑터에 데이터 삽입
                    adapter.setItems(dataSet);
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
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
