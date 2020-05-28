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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.SignUpPage;
import com.google.android.material.textfield.TextInputLayout;

public class FindIDFragment extends Fragment {
    private static final String TAG = "FindIDFragment";

    static int SIGN_UP_SUCCESS = 1111;
    static int SIGN_UP_CANCLE = 2222;

    LinearLayout layoutFindID;
    LinearLayout layoutFoundID;

    TextInputLayout inputLayoutEmail;

    Button btnFindID;
    Button btnSignUp;
    Button btnGoLogin;

    TextView textFoundID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.findid_fragment, container, false);

        layoutFindID = rootView.findViewById(R.id.layoutFindID);
        layoutFoundID = rootView.findViewById(R.id.layoutFoundID);

        layoutFindID.setVisibility(View.VISIBLE);
        layoutFoundID.setVisibility(View.INVISIBLE);

        inputLayoutEmail = rootView.findViewById(R.id.inputLayoutEmail);
        btnFindID = rootView.findViewById(R.id.btnFindInfoID);
        btnSignUp = rootView.findViewById(R.id.btnFindInfoSignUp);
        btnGoLogin = rootView.findViewById(R.id.btnGoLogin);

        textFoundID = rootView.findViewById(R.id.textFoundID);

        final EditText edtEmail = inputLayoutEmail.getEditText();

        btnFindID.setOnClickListener(new View.OnClickListener() {
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
                    // if 등록된 이메일이 있으면 ...
                    // layoutFindID.setVisibility(View.INVISIBLE);
                    // layoutFoundID.setVisibility(View.VISIBLE);
                    // textFoundId.settext((DB에서 찾아온 아이디));
                    // else 없으면
                    // inputLayoutEmail.setError("가입 되어 있지 않은 이메일입니다.");
                }

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
            // 회원가입에 성공했으면 아이디찾기 창을 끄고 바로 로그인 화면으로 돌아감
            getActivity().finish();
        }
        else if (resultCode == SIGN_UP_CANCLE) {
            // 회원가입을 취소했으면 다시 아이디찾기 창으로
            // do nothing
        }
    }
}
