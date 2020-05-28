package com.capstondesign.miraeseat.find;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.SignUpPage;
import com.google.android.material.textfield.TextInputLayout;

// 비밀번호 찾기 기능 흐름:
// 아이디와 이메일을 입력하면 해당 이메일로 임시 비밀번호(8글자 랜덤조합, 유효시간 있음) 전송
// 전송 즉시 1.기존 비밀번호 폐기 - DB 비밀번호 컬럼값을 임시 비밀번호(의 해시값)으로 대체
//         2.변경요청테이블(아이디, 변경요청시점)에 새 행 삽입
// 임시 비밀번호로 로그인 했을 때의 루틴은 LoginPage 주석 참조

// 비밀번호 변경 페이지에서 비밀번호를 바꾸면 (성공하면)
// 1. 변경여부 테이블에 사용자의 아이디 존재여부 확인
// 2. 변경여부 테이블에서 사용자의 아이디가 주키인 행 삭제

public class FindPwdFragment extends Fragment {
    private static final String TAG = "FindPwdFragment";

    static int SIGN_UP_SUCCESS = 1111;
    static int SIGN_UP_CANCLE = 2222;

    LinearLayout layoutFindPwd;
    LinearLayout layoutFoundPwd;

    TextInputLayout inputLayoutID;
    TextInputLayout inputLayoutEmail;

    Button btnFindPwd;
    Button btnSignUp;
    Button btnGoLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.findpwd_fragment, container, false);

        layoutFindPwd = rootView.findViewById(R.id.layoutFindPwd);
        layoutFoundPwd = rootView.findViewById(R.id.layoutFoundPwd);

        layoutFindPwd.setVisibility(View.VISIBLE);
        layoutFoundPwd.setVisibility(View.INVISIBLE);

        inputLayoutID = rootView.findViewById(R.id.inputLayoutID);
        inputLayoutEmail = rootView.findViewById(R.id.inputLayoutEmail);

        final EditText edtID = inputLayoutID.getEditText();
        final EditText edtEmail = inputLayoutEmail.getEditText();

        btnFindPwd = rootView.findViewById(R.id.btnFindInfoPwd);
        btnSignUp = rootView.findViewById(R.id.btnFindInfoSignUp);
        btnGoLogin = rootView.findViewById(R.id.btnGoLogin);

        btnFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenID = edtID.getText().toString();
                String givenEmail = edtEmail.getText().toString();

                if(givenID.getBytes().length <= 0)
                {
                    inputLayoutID.setError("아이디를 입력하세요.");
                }
                else if(givenEmail.getBytes().length <= 0)
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

                edtID.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        inputLayoutID.setError(null);
                        inputLayoutID.setErrorEnabled(false);
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
                        inputLayoutEmail.setError(null);
                        inputLayoutEmail.setErrorEnabled(false);
                    }
                });
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), SignUpPage.class);
                startActivityForResult(intent, 0);
            }
        });

        btnGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == SIGN_UP_SUCCESS) {
            // 회원가입에 성공했으면 비밀번호찾기 창을 끄고 바로 로그인 화면으로 돌아감
            getActivity().finish();
        }
        else if (resultCode == SIGN_UP_CANCLE) {
            // 회원가입을 취소했으면 다시 아이디찾기 창으로
            // do nothing
        }
    }
}
