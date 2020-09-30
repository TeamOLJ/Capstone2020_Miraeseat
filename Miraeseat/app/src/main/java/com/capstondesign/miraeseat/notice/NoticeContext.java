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

    TextView noticeTitle;
    TextView noticeDate;
    TextView noticeContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice_context);

        Intent intent = getIntent();
        Notice notice = (Notice)intent.getParcelableExtra("SelectedNotice");

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

        noticeTitle.setText(notice.getNoticeTitle());
        noticeDate.setText(notice.getNoticeDate());
        // 파이어베이스는 \n 등의 특수표기문자를 지원하지 않으므로, "\\n"로 표기해둔 부분을 모두 newline 기호로 치환하는 식으로 새 줄 구성
        noticeContext.setText(notice.getNoticeContext().replace("\\\\n", "\n"));
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
