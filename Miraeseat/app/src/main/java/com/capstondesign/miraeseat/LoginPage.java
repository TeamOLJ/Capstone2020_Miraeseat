package com.capstondesign.miraeseat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.capstondesign.miraeseat.find.FindInfoPage;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity {

    TextInputLayout inputLayoutID;
    TextInputLayout inputLayoutPwd;

    CheckBox checkAutoLogin;

    Button btnLogin;
    Button btnFindID;
    Button btnFindPwd;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

//        ActionBar bar =getSupportActionBar();
//        bar.hide();

        inputLayoutID = (TextInputLayout)findViewById(R.id.InputID);
        inputLayoutPwd = (TextInputLayout) findViewById(R.id.InputPwd);

        checkAutoLogin = (CheckBox)findViewById(R.id.checkAutoLogin);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnFindID = (Button)findViewById(R.id.btnFindID);
        btnFindPwd = (Button)findViewById(R.id.btnFindPwd);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        final EditText inputID = inputLayoutID.getEditText();
        final EditText inputPwd = inputLayoutPwd.getEditText();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenID = inputID.getText().toString();
                String givenPwd = inputPwd.getText().toString();

                // 아이디 칸에 입력된 것이 없을 경우
                if(givenID.getBytes().length <= 0)
                {
                    inputLayoutID.setError("아이디를 입력해주세요");
                    inputID.addTextChangedListener(new TextWatcher() {
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
                                inputLayoutID.setError(null);
                                // 에러메시지가 사라진 자리에 남는 빈칸을 없애기 위해 Enabled 를 false로 변경
                                inputLayoutID.setErrorEnabled(false);
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

                // 비밀번호와 아이디 모두 입력되어 있으면
                else
                {
                    // sql injection 대비, ID 형식에 맞지 않으면 오류 띄움
                    // 서버 쪽에서도 한 번 더 확인이 필요할 듯...?
                    // 비밀번호는 Hash할 거니까 상관없나...
                    if(!Pattern.matches("[+a-zA-Z0-9[-[_]]]{5,20}", givenID))
                    {
                        inputLayoutPwd.setError("잘못된 로그인 정보입니다. 아이디와 비밀번호를 확인해주세요.");
                    }
                    else
                    {
                        // 서버로 전송
                        // 변경요청테이블(FindPwdFragment 주석 참조)에 아이디가 존재하는지 확인
                        // 존재할 경우 요청시점을 확인하고 유효시간 이내이면 DB 비교하여 로그인 허용
                        // 존재하지 않을 경우 일반적인 로그인 루틴 진행

                        // if 로그인에 성공한 경우
                        // checkAutoLogin.isChecked()가 True면 자동로그인을 위한 정보 저장
                        //SaveSharedPreference.setUserName(ctx, "jhj5897");

                        // 성공처리 후 로그인 버튼 누르기 전 화면으로 돌아가기
                        //Intent intent = new Intent(ctx, MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        // ctx.startActivity(intent);

                        // else 로그인에 실패한 경우
                        // Toast.makeText(getApplicationContext(), "잘못된 로그인 정보입니다. 아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btnFindID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindInfoPage.class);

                // 선택된 것이 아이디 찾기인지 비밀번호 찾기인지 판별하기 위함
                // 아이디/비밀번호 찾기를 누른 경우에 따라 각각 다른 값을 호출된 인텐트에 전달
                intent.putExtra("selectedTab", 0);
                startActivity(intent);
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
