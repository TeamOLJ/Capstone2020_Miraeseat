package com.capstondesign.miraeseat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

public class UnsubscribePage extends AppCompatActivity {

    TextView titleText;

    TextInputLayout textLayoutPwd;

    Button btnUnsubscribe;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unsubscribe_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("회원 탈퇴");

        textLayoutPwd = (TextInputLayout)findViewById(R.id.unsubscribePwd);
        btnUnsubscribe = (Button)findViewById(R.id.btnUnsubscribe);

        final EditText edtUnsubPwd = textLayoutPwd.getEditText();

        btnUnsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenPwd = edtUnsubPwd.getText().toString();

                if(givenPwd.getBytes().length <= 0)
                {
                    textLayoutPwd.setError("비밀번호를 입력하세요.");
                }
                else
                {
                    // 서버로 전송하여 비밀번호 일치여부 확인
                    // if 일치하지 않으면
                    // textLayoutPwd.setError("잘못 된 입력입니다. 비밀번호를 확인하세요.");
                    // else 일치하면
                    // 모든 데이터베이스에서 ID가 일치하는 정보 삭제
                    // 창을 새로 띄울까 아니면 바로 홈으로 보내고 토스트만 띄울까?
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