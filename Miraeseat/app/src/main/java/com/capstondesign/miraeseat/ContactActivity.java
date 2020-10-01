package com.capstondesign.miraeseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = "ContactActivity";

    FirebaseAuth mainAuth;
    FirebaseFirestore db;

    TextView titleText;
    TextView cntTxt;

    EditText edtName;
    EditText edtEmail;
    EditText edtTitle;
    EditText edtContent;

    Button btnSend;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("의견 보내기");

        mainAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtName = (EditText) findViewById(R.id.edt_name);
        edtEmail = (EditText)findViewById(R.id.edt_email);
        edtTitle = (EditText)findViewById(R.id.edt_title);
        edtContent = (EditText)findViewById(R.id.edt_content);

        btnSend = (Button)findViewById(R.id.btnSend);

        cntTxt = (TextView)findViewById(R.id.cntTxt);


        if(mainAuth.getCurrentUser() != null)
        {
            db.collection("UserInfo").document(mainAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    UserClass loginedUser = documentSnapshot.toObject(UserClass.class);
                    edtEmail.setText(loginedUser.getEmail());
                    edtName.setText(loginedUser.getNick());
                }
            });

            //닉네임, 이메일칸 수정 불가
            edtName.setFocusableInTouchMode(false);
            edtName.setClickable(false);
            edtEmail.setFocusableInTouchMode(false);
            edtEmail.setClickable(false);
        }

        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cntTxt.setText(edtContent.length() + "자 / 최대 500자");
            }
        });

        btnSend.setOnClickListener(clickSendListener);
    }

    OnOneOffClickListener clickSendListener = new OnOneOffClickListener() {
        @Override
        public void onOneClick(View v) {
            // 인터넷 연결 확인 먼저
            ConnectivityManager conManager = (ConnectivityManager) ContactActivity.this.getSystemService(CONNECTIVITY_SERVICE);
            if(conManager.getActiveNetworkInfo() == null) {
                reset();
                Toast.makeText(getApplicationContext(),"인터넷 연결을 먼저 확인해주세요.",Toast.LENGTH_LONG).show();
            }
            else if(edtName.getText().toString().getBytes().length<=0){
                reset();
                Toast.makeText(getApplicationContext(),"이름을 입력해 주세요.",Toast.LENGTH_LONG).show();
            }
            else if(edtEmail.getText().toString().getBytes().length<=0) {
                reset();
                Toast.makeText(getApplicationContext(),"이메일을 입력해 주세요.",Toast.LENGTH_LONG).show();
            }
            else if(edtTitle.getText().toString().getBytes().length<=0) {
                reset();
                Toast.makeText(getApplicationContext(),"제목을 입력해 주세요.",Toast.LENGTH_LONG).show();
            }
            else if(edtContent.getText().toString().getBytes().length<10||edtContent.getText().toString().getBytes().length>500) {
                reset();
                Toast.makeText(getApplicationContext(),"의견 내용은 10자 이상, 500자 이하로 입력해 주세요.",Toast.LENGTH_LONG).show();
            }
            else {
                db.collection("UserInquiry").add(new UserInquiry(edtName.getText().toString(), edtEmail.getText().toString(), edtTitle.getText().toString(),
                        edtContent.getText().toString().replace("\n", "\\\\n"), null, false))
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "의견 업로드 성공");
                                Toast.makeText(getApplicationContext(), "의견이 접수되었습니다.", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                reset();
                                Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                showEndMsg();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEndMsg()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("문의 작성을 취소하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showEndMsg();
    }
}
