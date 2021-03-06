package com.capstondesign.miraeseat;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.capstondesign.miraeseat.find.FindInfoPage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginPage extends AppCompatActivity implements EditText.OnEditorActionListener {
    private static final String TAG = "LoginPage";

    // Firebase 인증 변수
    private FirebaseAuth loginAuth;
    FirebaseFirestore db;

    TextInputLayout inputLayoutEmail;
    TextInputLayout inputLayoutPwd;

    EditText inputEmail;
    EditText inputPwd;

    CheckBox checkAutoLogin;

    Button btnLogin;
    Button btnFindPwd;
    Button btnSignUp;

    boolean isAutoLoginChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        inputLayoutEmail = (TextInputLayout)findViewById(R.id.InputEmail);
        inputLayoutPwd = (TextInputLayout) findViewById(R.id.InputPwd);

        checkAutoLogin = (CheckBox)findViewById(R.id.checkAutoLogin);

        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnFindPwd = (Button)findViewById(R.id.btnFindPwd);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        inputEmail = inputLayoutEmail.getEditText();
        inputPwd = inputLayoutPwd.getEditText();

        // Initialize Firebase Auth
        loginAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(clickLoginListener);
        inputPwd.setOnEditorActionListener(this);

        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindInfoPage.class);
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

    OnOneOffClickListener clickLoginListener = new OnOneOffClickListener() {
        @Override
        public void onOneClick(View v) {
            final String givenEmail = inputEmail.getText().toString().trim();
            String givenPwd = inputPwd.getText().toString();

            isAutoLoginChecked = checkAutoLogin.isChecked();

            // 이메일 칸에 입력된 것이 없을 경우
            if(givenEmail.getBytes().length <= 0)
            {
                inputLayoutEmail.setError("이메일을 입력해주세요");
                reset();
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
            else if(givenPwd.getBytes().length <= 0)
            {
                inputLayoutPwd.setError("비밀번호를 입력해주세요");
                reset();

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
                // 인터넷 연결 확인 먼저
                ConnectivityManager conManager = (ConnectivityManager) LoginPage.this.getSystemService(CONNECTIVITY_SERVICE);
                if(conManager.getActiveNetworkInfo() == null) {
                    reset();
                    Toast.makeText(getApplicationContext(),"인터넷 연결을 먼저 확인해주세요.",Toast.LENGTH_LONG).show();
                }
                else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(givenEmail).matches())
                {
                    inputLayoutPwd.setError("잘못된 로그인 정보입니다. 이메일과 비밀번호를 확인해주세요.");
                    reset();
                }
                else
                {
                    loginAuth.signInWithEmailAndPassword(givenEmail, givenPwd)
                            .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        // 로그인 성공
                                        // DB에서 닉네임 읽어오기
                                        db.collection("UserInfo").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                UserClass loginedUser = documentSnapshot.toObject(UserClass.class);

                                                if(loginedUser != null) {
                                                    // 사용자의 닉네임과 프로필사진을 저장
                                                    SaveSharedPreference.setUserNickName(getApplicationContext(), loginedUser.getNick());
                                                    SaveSharedPreference.setProfileImage(getApplicationContext(), loginedUser.getImagepath());
                                                    // 메인화면으로 복귀
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    // 열려있던 모든 액티비티를 닫고 지정된 액티비티(메인)만 열도록
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    // 자동로그인 체크 여부 저장
                                                    SaveSharedPreference.setIsAutoLogin(getApplicationContext(), isAutoLoginChecked);
                                                    Toast.makeText(getApplicationContext(), SaveSharedPreference.getUserNickName(getApplicationContext()) + "님 환영합니다!", Toast.LENGTH_LONG).show();
                                                    startActivity(intent);
                                                }
                                                else {
                                                    reset();
                                                    Toast.makeText(getApplicationContext(), "로그인에 실패했습니다. 반복될 경우 관리자에게 문의주세요.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        // 로그인 실패
                                        reset();
                                        Toast.makeText(getApplicationContext(), "잘못된 로그인 정보입니다. 이메일과 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        }
    };

    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(actionId==EditorInfo.IME_ACTION_DONE) {
            btnLogin.performClick();
        }
        return false;
    }
}
