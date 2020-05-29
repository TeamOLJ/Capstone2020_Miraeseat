package com.capstondesign.miraeseat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstondesign.miraeseat.find.FindInfoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {
    private static final String TAG = "LoginPage";

    // Firebase 인증 변수
    private FirebaseAuth loginAuth;

    TextInputLayout inputLayoutEmail;
    TextInputLayout inputLayoutPwd;

    CheckBox checkAutoLogin;

    Button btnLogin;
    Button btnFindPwd;
    Button btnSignUp;

    boolean isAutoLoginChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

//        ActionBar bar =getSupportActionBar();
//        bar.hide();

        inputLayoutEmail = (TextInputLayout)findViewById(R.id.InputEmail);
        inputLayoutPwd = (TextInputLayout) findViewById(R.id.InputPwd);

        checkAutoLogin = (CheckBox)findViewById(R.id.checkAutoLogin);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnFindPwd = (Button)findViewById(R.id.btnFindPwd);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        final EditText inputEmail = inputLayoutEmail.getEditText();
        final EditText inputPwd = inputLayoutPwd.getEditText();

        // Initialize Firebase Auth
        loginAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenEmail = inputEmail.getText().toString();
                String givenPwd = inputPwd.getText().toString();

                isAutoLoginChecked = checkAutoLogin.isChecked();

                // 이메일 칸에 입력된 것이 없을 경우
                if(givenEmail.getBytes().length <= 0)
                {
                    inputLayoutEmail.setError("이메일을 입력해주세요");
                    inputEmail.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        // 아이디 창에 무언가 입력하면 에러 메시지를 지워줌
                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.toString().length() > 0) {
                                inputLayoutEmail.setError(null);
                                // 에러메시지가 사라진 자리에 남는 빈칸을 없애기 위해 Enabled 를 false로 변경
                                inputLayoutEmail.setErrorEnabled(false);
                            }
                        }
                    });
                }
                // 에러메시지를 지우는 코드를 if 내부에 삽입하였으므로 아래 코드는 필요하지 않음
//                else
//                {
//                    inputLayoutID.setError(null);
//                    inputLayoutID.setErrorEnabled(false);
//                }

                else if(givenPwd.getBytes().length <= 0)
                {
                    inputLayoutPwd.setError("비밀번호를 입력해주세요");

                    inputPwd.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if(s.toString().length() > 0) {
                                inputLayoutPwd.setError(null);
                                inputLayoutPwd.setErrorEnabled(false);
                            }
                        }
                    });
                }

                // 이메일과 비밀번호 모두 입력되어 있으면
                else
                {
                    // sql injection 대비, 형식에 맞지 않으면 오류 띄움
                    // 비밀번호는 Hash할 거니까 상관없나...
                    if(!android.util.Patterns.EMAIL_ADDRESS.matcher(givenEmail).matches())
                    {
                        inputLayoutPwd.setError("잘못된 로그인 정보입니다. 이메일과 비밀번호를 확인해주세요.");
                    }
                    else
                    {
                        loginAuth.signInWithEmailAndPassword(givenEmail, givenPwd)
                                .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()) {
                                            // 로그인 성공
                                            Log.d(TAG, "LogInWithEmail:success");
                                            // 로그인 버튼 누르기 전 화면으로 돌아가기
                                            // 자동로그인 여부에 따른 처리와 로그인 성공에 따른 화면 전환 처리도 필요
                                        }
                                        else {
                                            // 로그인 실패
                                            Log.w(TAG, "LogInWithEmail:failure", task.getException());
                                            Toast.makeText(getApplicationContext(), "잘못된 로그인 정보입니다. 이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });

        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindInfoPage.class);
                intent.putExtra("selectedTab", 1);
                startActivity(intent);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpPage.class);
                startActivity(intent);
            }
        });
    }
}
