package com.capstondesign.miraeseat.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.EditInfo;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.UnsubscribePage;
import com.capstondesign.miraeseat.UserClass;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPage extends AppCompatActivity {

    static final int IS_INFO_MODIFIED = 1234;

    FirebaseUser user;
    FirebaseFirestore db;

    Button mypage_edit;
    Button mypage_withdrawl;
    CircleImageView profile;
    TextView nickname;

    ListView listView;
    MyPageAdapter myPageAdapter;

    DrawerHandler drawer;

    //임시 리스트뷰 데이터
    ArrayList<mypageList_item> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);


        mypage_edit = (Button) findViewById(R.id.mypage_edit);
        mypage_withdrawl = (Button) findViewById(R.id.mypage_withdrawl);

        profile = (CircleImageView) findViewById(R.id.profilePic);
        nickname = (TextView) findViewById(R.id.nickName);

        listView = (ListView)findViewById(R.id.mypage_list);

        mData = new ArrayList<mypageList_item>();

        initUI();

        // 해당 계정 글 정보 서버에서 불러옴

        //리스트뷰 임시데이터
        mData.add(new mypageList_item("세종문화회관 3열 1", (float) 4,R.mipmap.ic_launcher,"자리 좋아요 너무 잘보임 굿굿","2020/04/05"));
        mData.add(new mypageList_item("체조경기장 b구역 15열 2", (float) 4,R.mipmap.ic_launcher,"중앙이 잘 안보여용","2020/05/05"));

        myPageAdapter = new MyPageAdapter(MyPage.this, mData);
        listView.setAdapter(myPageAdapter);

        //회원정보수정 버튼
        mypage_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditInfo.class);
                startActivityForResult(intent, IS_INFO_MODIFIED);
            }
        });

        //회원탈퇴 버튼
        mypage_withdrawl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnsubscribePage.class);
                startActivity(intent);
            }
        });

    }

    public void initUI() {
        // Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        final String userEmail = user.getEmail();

        // 프로필사진 & 닉네임 데이터베이스에서 읽어오기
        db.collection("UserInfo").document(userEmail).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserClass loginedUser = documentSnapshot.toObject(UserClass.class);
                nickname.setText(loginedUser.getNick());
                if(loginedUser.getImagepath() != null) {
                    Glide.with(getApplicationContext()).load(loginedUser.getImagepath()).into(profile);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            initUI();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // 뒤로가기 버튼(홈버튼)을 누르면 창이 꺼지는 메소드
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


