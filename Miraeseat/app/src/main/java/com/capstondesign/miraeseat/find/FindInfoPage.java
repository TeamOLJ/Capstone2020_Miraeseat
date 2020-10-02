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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.SignUpPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class FindInfoPage extends AppCompatActivity {
    private static final String TAG = "FindInfoPage";

    TextView titleText;

    LinearLayout layoutFindPwd;
    LinearLayout layoutFoundPwd;

    TextInputLayout inputLayoutEmail;

    Button btnFindPwd;
    Button btnSignUp;
    Button btnGoLogin;

    DrawerHandler drawer;

    FirebaseAuth auth = FirebaseAuth.getInstance();

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

                    // 그에 따른 정보처리 진행
                    auth.sendPasswordResetEmail(givenEmail)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        layoutFindPwd.setVisibility(View.INVISIBLE);
                                        layoutFoundPwd.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        Toast.makeText(FindInfoPage.this, "이메일 발송에 실패했습니다. 인터넷 연결 확인 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
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
