package com.capstondesign.miraeseat.notice;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.capstondesign.miraeseat.R;

public class NoticeContext extends AppCompatActivity {

    Toolbar toolbar;
    TextView toolbarText;

    int contentId;

    TextView noticeTitle;
    TextView noticeDate;
    TextView noticeContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_context);

        Intent intent = getIntent();
        contentId = intent.getIntExtra("NoticeId", 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        toolbarText = (TextView) findViewById(R.id.titleText);
        toolbarText.setText("공지사항");

        noticeTitle = (TextView)findViewById(R.id.textNoticeTitle);
        noticeDate = (TextView)findViewById(R.id.textNoticeDate);
        noticeContext = (TextView)findViewById(R.id.textNoticeContext);

        applyItem();
    }

    public void applyItem() {
        // DB에서 공지번호가 contentId인 공지를 읽어와 화면에 적용시키는 함수
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
