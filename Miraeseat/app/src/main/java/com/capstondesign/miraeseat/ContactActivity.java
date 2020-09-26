package com.capstondesign.miraeseat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtName.length()<=0){
                    Toast.makeText(getApplicationContext(),"닉네임을 입력해 주세요.",Toast.LENGTH_LONG).show();
                }
                else if(edtEmail.length()<=0) {
                    Toast.makeText(getApplicationContext(),"이메일을 입력해 주세요.",Toast.LENGTH_LONG).show();
                }
                else if(edtTitle.length()<=0) {
                    Toast.makeText(getApplicationContext(),"제목을 입력해 주세요.",Toast.LENGTH_LONG).show();
                }
                else if(edtContent.length()<10||edtContent.length()>500) {
                    Toast.makeText(getApplicationContext(),"문의 내용은 10자 이상, 500자 이하로 입력해 주세요.",Toast.LENGTH_LONG).show();
                }

                //의견 DB로 보내기
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
