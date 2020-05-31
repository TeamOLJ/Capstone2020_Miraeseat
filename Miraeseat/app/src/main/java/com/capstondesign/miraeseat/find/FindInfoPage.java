package com.capstondesign.miraeseat.find;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.SignUpPage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;

public class FindInfoPage extends AppCompatActivity {
    private static final String TAG = "FindInfoPage";

    static int SIGN_UP_SUCCESS = 1111;
    static int SIGN_UP_CANCLE = 2222;

    TextView titleText;

    LinearLayout layoutFindPwd;
    LinearLayout layoutFoundPwd;

    TextInputLayout inputLayoutEmail;

    Button btnFindPwd;
    Button btnSignUp;
    Button btnGoLogin;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_info_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("비밀번호 찾기");

        layoutFindPwd = findViewById(R.id.layoutFindPwd);
        layoutFoundPwd = findViewById(R.id.layoutFoundPwd);

        layoutFindPwd.setVisibility(View.VISIBLE);
        layoutFoundPwd.setVisibility(View.INVISIBLE);

        inputLayoutEmail = findViewById(R.id.inputLayoutEmail);

        final EditText edtEmail = inputLayoutEmail.getEditText();

        btnFindPwd = findViewById(R.id.btnFindInfoPwd);
        btnSignUp = findViewById(R.id.btnFindInfoSignUp);
        btnGoLogin = findViewById(R.id.btnGoLogin);

        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenEmail = edtEmail.getText().toString();

                if(givenEmail.getBytes().length <= 0)
                {
                    inputLayoutEmail.setError("이메일을 입력하세요.");
                }
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(givenEmail).matches())
                {
                    inputLayoutEmail.setError("유효하지 않은 이메일 형식입니다.");
                }
                else
                {
                    inputLayoutEmail.setError(null);
                    inputLayoutEmail.setErrorEnabled(false);
                    // 서버로 전송해 DB 확인
                    // if(!Pattern.matches("[+a-zA-Z0-9[-[_]]]{5,20}", givenID) || (매칭되는 아이디와 이메일이 없으면))
                    // inputLayoutEmail.setError("가입 정보가 없습니다. 입력 정보를 다시 확인하시거나 회원가입 후 이용해주세요.");
                    // else 매칭되는 정보가 있으면
                    // layoutFindPwd.setVisibility(View.INVISIBLE);
                    // layoutFoundPwd.setVisibility(View.VISIBLE);
                    // 그에 따른 정보처리 진행
                }

                // 뭔가를 작성하면 하단의 오류문구를 지워주기 위한 코드
                edtEmail.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        inputLayoutEmail.setError(null);
                        inputLayoutEmail.setErrorEnabled(false);
                    }
                });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(intent);
            }
        });

        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

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
