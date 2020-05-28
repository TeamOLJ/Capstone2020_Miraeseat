package com.capstondesign.miraeseat;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class SignUpPage extends AppCompatActivity {
    private static final String TAG = "SignUpPage";

    static int SIGN_UP_SUCCESS = 1111;
    static int SIGN_UP_CANCLE = 2222;

    TextView titleText;

    private TextInputLayout inputLayoutID;
    private TextInputLayout inputLayoutNick;
    private TextInputLayout inputLayoutPwd;
    private TextInputLayout inputLayoutCheckPwd;
    private TextInputLayout inputLayoutEmail;

    CheckBox checkTermCondition;
    CheckBox checkPersonalInfo;

    ScrollView scrollTermCondition;
    ScrollView scrollPersonalInfo;

    Button btnCheckID;
    Button btnCheckNick;
    Button btnReadTC;
    Button btnReadPI;
    Button btnSignUp;

    TextView textFullTC;
    TextView textFullPI;

    // 중복확인 여부 체크하기 위한 변수값
    private Boolean isIDChecked = false;
    private Boolean isNickChecked = false;
    private Boolean isEmailChecked = false;

    // 입력 가능한 값으로만 텍스트가 이루어져있는지 확인
    private Boolean isIDValid = false;
    private Boolean isNickValid = false;
    private Boolean isPwdValid = false;
    private Boolean isEmailValid = false;

    // 비밀번호 확인 값과 비밀번호 값이 동일한지 확인
    private Boolean isPwdChecked = false;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("회원가입");

        inputLayoutID = (TextInputLayout)findViewById(R.id.textlayoutID);
        inputLayoutNick = (TextInputLayout)findViewById(R.id.textlayoutNick);
        inputLayoutPwd = (TextInputLayout)findViewById(R.id.textlayoutPwd);
        inputLayoutCheckPwd = (TextInputLayout)findViewById(R.id.textlayoutPwdCheck);
        inputLayoutEmail = (TextInputLayout)findViewById(R.id.textlayoutEmail);

        final EditText edtID = inputLayoutID.getEditText();
        final EditText edtNick = inputLayoutNick.getEditText();
        final EditText edtPwd = inputLayoutPwd.getEditText();
        final EditText edtCheckPwd = inputLayoutCheckPwd.getEditText();
        final EditText edtEmail = inputLayoutEmail.getEditText();

        checkTermCondition = (CheckBox)findViewById(R.id.checkTermCondition);
        checkPersonalInfo = (CheckBox)findViewById(R.id.checkPersonalInfoCollection);

        scrollTermCondition = (ScrollView)findViewById(R.id.scrollTermCondition);
        scrollPersonalInfo = (ScrollView)findViewById(R.id.scrollPersonalInfo);

        btnCheckID = (Button)findViewById(R.id.btnCheckID);
        btnCheckNick = (Button)findViewById(R.id.btnCheckNick);
        btnReadTC = (Button)findViewById(R.id.btnReadTermCondition);
        btnReadPI = (Button)findViewById(R.id.btnReadPersonalInfo);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        textFullTC = (TextView)findViewById(R.id.textFullTC);
        textFullPI = (TextView)findViewById(R.id.textFullPI);

        // 약관 사항은 전체보기 하기 전에는 보이지 않게 설정
        scrollTermCondition.setVisibility(View.GONE);
        scrollPersonalInfo.setVisibility(View.GONE);

        // 데이터베이스에서 약관을 읽어와 텍스트에 설정
        // textFullTC.setText(읽어온 이용약관);
        // textFullPI.setText(읽어온 개인 정보 동의);

        // 아이디 입력칸 입력 정보에 따라 오류메시지 생성하는 코드
        edtID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력값에 변화가 생기면 중복확인을 하지 않은 것으로 봄
                isIDChecked = false;
                // 자바 정규식을 사용하여 입력값의 유효성 검사
                if (!Pattern.matches("[+a-zA-Z0-9[-[_]]]{5,20}", s.toString())) {
                    // 패턴에 매칭되지 않으면 (입력되면 안 되는 값이 포함되면) isIDValid 값을 false로 설정
                    isIDValid = false;
                    // inputLayoutID.setErrorEnabled(true);
                    inputLayoutID.setError("5~20 글자의 영문 소문자, 숫자, _, -를 사용할 수 있습니다.");
                }
                else {
                    isIDValid = true;
                    inputLayoutID.setError(null); // null은 에러 메시지를 지워주는 기능
                    // 에러메시지가 사라졌을 때 생기는 빈칸을 없애기 위해 Enabled 값을 false로 바꿔줌
                    inputLayoutID.setErrorEnabled(false);
                }
            }
        });

        // setOnFocusChangeLister을 사용한 방식
        // 텍스트를 입력할 때마다 확인하는 것이 아니라 EditText가 Focus를 잃었을 때만 확인
//        edtID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus) {
//                    if (!Pattern.matches("[+a-zA-Z0-9[-[_]]]{5,20}", edtID.getText().toString())) {
//                        isIDValid = false;
//                        inputLayoutID.setError("5~20 글자의 영문 소문자, 숫자, _, -를 사용할 수 있습니다.");
//                    }
//                    else {
//                        isIDValid = true;
//                        inputLayoutID.setError(null);
//                        inputLayoutID.setErrorEnabled(false);
//                    }
//                }
//            }
//        });

        edtNick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isNickChecked = false;
                if(!Pattern.matches("[+ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,7}", s.toString())) {
                    isNickValid = false;
                    inputLayoutNick.setError("2~7글자의 한글, 영문, 숫자를 사용할 수 있습니다.");
                }
                else {
                    isNickValid = true;
                    inputLayoutNick.setError(null);
                    inputLayoutNick.setErrorEnabled(false);
                }
            }
        });

        edtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 비밀번호 입력란에 변화가 생기면 비밀번호 확인도 하지 않은 것으로 간주
                isPwdChecked = false;

                if(!Pattern.matches("[+a-zA-Z0-9[`~!@#$%^&*()-_=,./<>?;:\"'{}\\[\\][+[|]]]]{8,20}", s.toString())) {
                    isPwdValid = false;
                    inputLayoutPwd.setError("8~20자의 영문 대소문자, 숫자, 특수문자를 사용할 수 있습니다.");
                }
                else {
                    isPwdValid = true;
                    inputLayoutPwd.setError(null);
                    inputLayoutPwd.setErrorEnabled(false);
                }

                // 비밀번호 확인란의 오류문구 설정
                // 비밀번호 확인란이 비어있지 않은 상태에서 비밀번호!=비밀번호확인 일 때 오류문구 띄우기
                if(edtCheckPwd.getText().toString().length() > 0 && !edtCheckPwd.getText().toString().equals(edtPwd.getText().toString())) {
                    isPwdChecked = false;
                    inputLayoutCheckPwd.setError("비밀번호를 확인하세요.");
                }
            }
        });

        edtCheckPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String givenPwd = edtPwd.getText().toString();
                String givenPwdCheck = edtCheckPwd.getText().toString();
                // 비밀번호 확인란의 입력값과 비밀번호란의 입력값이 같지 않으면
                if(!givenPwdCheck.equals(givenPwd)) {
                    isPwdChecked = false;
                    inputLayoutCheckPwd.setError("비밀번호를 확인하세요.");
                }
                else {
                    isPwdChecked = true;
                    inputLayoutCheckPwd.setError(null);
                    inputLayoutCheckPwd.setErrorEnabled(false);
                }
            }
        });

        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 이메일 형식 유효성 검사
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()) {
                    isEmailValid = false;
                    inputLayoutEmail.setError("유효하지 않은 이메일 형식입니다.");
                }
                else {
                    isEmailValid = true;
                    inputLayoutEmail.setError(null);
                    inputLayoutEmail.setErrorEnabled(false);
                }

            }
        });

        edtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && isEmailValid) {
                    // 작성 된 이메일 주소를 서버에 전송, 이미 존재하는 계정의 이메일인지 확인
                    // if 이미 가입된 이메일이면:
                    // isEmailChecked = false;
                    // inputLayoutEmail.setError("이미 가입 된 이메일입니다.");
                    // else (가입되지 않은 이메일이면):
                    // isEmailChecked = true;
                    // inputLayoutEmail.setError(null);
                    // inputLayoutEmail.setErrorEnabled(false);
                }
            }
        });

        // 약관에 동의하지 않고 회원가입 버튼을 눌렀을 때 체크박스 배경색이 붉게 변하는 것의 대응
        // 약관 동의 박스에 체크하면 붉었던 배경색이 사라진다.
        checkTermCondition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkTermCondition.setBackgroundColor(Color.parseColor("#00FF0000"));
            }
        });

        checkPersonalInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkPersonalInfo.setBackgroundColor(Color.parseColor("#00FF0000"));
            }
        });

        btnCheckID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ID값이 DB에 이미 존재하는지 확인

                // if 존재하지 않으면 (사용 가능한 아이디이면):
                isIDChecked = true;
                // inputLayoutID.setError(null);
                // inputLayoutID.setErrorEnabled(false);
                // 입력칸 하단에 "사용 가능한 아이디입니다!"를 띄워줘야 하는데...
            }
        });

        btnCheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 닉네임이 DB에 이미 존재하는지 확인

                // if 존재하지 않으면 (사용 가능한 닉네임이면):
                isNickChecked = true;
                // inputLayoutNick.setError(null);
                // inputLayoutNick.setErrorEnabled(false);
                // 입력칸 하단에 "사용 가능한 닉네임입니다!" 출력
            }
        });

        // 전체보기 버튼 작동 코드
        // ScrollView 가 GONE 상태이면 VISIBLE로, VISIBLE 상태이면 GONE으로 변환
        btnReadTC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollTermCondition.setVisibility((scrollTermCondition.getVisibility() == View.VISIBLE)? View.GONE : View.VISIBLE);
            }
        });

        btnReadPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollPersonalInfo.setVisibility((scrollPersonalInfo.getVisibility() == View.VISIBLE)? View.GONE : View.VISIBLE);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 위에서부터 차례로 빈칸 존재하는지 확인
                // 빈칸이 두 개 이상이면 가장 윗 줄에만 오류 메시지 띄움
                if(edtID.getText().toString().getBytes().length <= 0) {
                    inputLayoutID.setError("아이디를 입력하세요.");
                    edtID.requestFocus(); //해당 입력칸에 커서를 이동시키는 함수
                }
                else if(edtNick.getText().toString().getBytes().length <= 0) {
                    inputLayoutNick.setError("닉네임을 입력하세요.");
                    edtNick.requestFocus();
                }
                else if(edtPwd.getText().toString().getBytes().length <= 0) {
                    inputLayoutPwd.setError("비밀번호를 입력하세요.");
                    edtPwd.requestFocus();
                }
                else if(edtCheckPwd.getText().toString().getBytes().length <= 0) {
                    inputLayoutCheckPwd.setError("비밀번호를 한 번 더 입력해주세요.");
                    edtCheckPwd.requestFocus();
                }
                else if(edtEmail.getText().toString().getBytes().length <= 0) {
                    inputLayoutEmail.setError("이메일을 입력하세요");
                    edtEmail.requestFocus();
                }   // 모든 칸이 채워져 있는 경우에 다음 검사 실행:
                else if (!isIDChecked) {
                    // 중복확인 여부 체크
                    inputLayoutID.setError("아이디 중복확인을 해주세요.");
                }
                else if (!isNickChecked) {
                    inputLayoutNick.setError("닉네임 중복 확인을 해주세요.");
                }
                else {
                    // 모든 칸이 채워져있고 중복확인도 체크했다면:
                    // 모든 칸의 값이 유효한지 확인
                    if(isIDValid && isNickValid && isPwdValid && isPwdChecked && isEmailValid && isEmailChecked) {
                        // 약관 동의 여부 확인
                        if(!checkTermCondition.isChecked()) {
                            // 약관 동의에 체크표시 하지 않았다면 해당 약관 체크박스의 배경색을 붉게 바꿈
                            checkTermCondition.setBackgroundColor(Color.parseColor("#FFA7A7"));
                            Toast.makeText(getApplicationContext(), "이용 약관 동의가 필요합니다.", Toast.LENGTH_LONG).show();
                        }
                        if(!checkPersonalInfo.isChecked()) {
                            checkPersonalInfo.setBackgroundColor(Color.parseColor("#FFA7A7"));
                            Toast.makeText(getApplicationContext(), "개인정보 수집 동의가 필요합니다.", Toast.LENGTH_LONG).show();
                        }
                        if(checkTermCondition.isChecked() && checkPersonalInfo.isChecked()) {
                            // 약관 동의에도 모두 체크했다면!
                            // 입력된 모든 정보를 서버로 전송, DB에 추가
                            setResult(SIGN_UP_SUCCESS);
                            Toast.makeText(getApplicationContext(), "환영합니다! 회원가입에 성공하였습니다. 다시 로그인해주시기 바랍니다.", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                }

            }
        });
    }


    private void showEndMsg()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("회원가입을 취소하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(SIGN_UP_CANCLE);
                finish();
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 뒤로가기 버튼(홈버튼)을 누르면 창이 꺼지는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                showEndMsg();
                // finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        showEndMsg();
    }
}
