package com.capstondesign.miraeseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.Theme.ThemeUtil;
import com.capstondesign.miraeseat.hall.HallInfo;
import com.capstondesign.miraeseat.search.HallDetailedClass;
import com.capstondesign.miraeseat.search.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    final static String TAG = "MainActivity";

    final String SEARCH_WORD = "search_word";

    // Firebase 인증 변수
    private FirebaseAuth mainAuth;
    FirebaseFirestore db;

    BackPressCloseHandler backpress;
    EditText searchText;
    DrawerHandler drawer;

    String themeName;

    ImageButton[] btnList = new ImageButton[5];
    TextView[] textList = new TextView[5];

    ArrayList<HallDetailedClass> hdc;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
//        super.setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        themeName = SaveSharedPreference.getTheme(getApplicationContext());
        if(themeName==null) themeName="default";
        ThemeUtil.applyTheme(themeName);

        // Initialize Firebase Auth
        mainAuth = FirebaseAuth.getInstance();

        backpress = new BackPressCloseHandler(this);
        drawer = new DrawerHandler(this);
        drawer.init();

        setSupportActionBar(drawer.toolbar);
        ViewCompat.setLayoutDirection(drawer.toolbar, ViewCompat.LAYOUT_DIRECTION_RTL);
        getSupportActionBar().setHomeAsUpIndicator(drawer.toolbar_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        searchText = findViewById(R.id.searchText);
        searchText.setOnEditorActionListener(this);

        btnList[0] = (ImageButton) findViewById(R.id.btnTheater1);
        btnList[1] = (ImageButton) findViewById(R.id.btnTheater2);
        btnList[2] = (ImageButton) findViewById(R.id.btnTheater3);
        btnList[3] = (ImageButton) findViewById(R.id.btnTheater4);
        btnList[4] = (ImageButton) findViewById(R.id.btnTheater5);

        textList[0] = (TextView) findViewById(R.id.textTheater1);
        textList[1] = (TextView) findViewById(R.id.textTheater2);
        textList[2] = (TextView) findViewById(R.id.textTheater3);
        textList[3] = (TextView) findViewById(R.id.textTheater4);
        textList[4] = (TextView) findViewById(R.id.textTheater5);

        CreateButton();
    }

    //검색 버튼 눌렀을 때
    public void onSearchButtonClicked(View v) {
        String data = searchText.getText().toString();

        //검색어가 없으면
        if(data.length() == 0) {
            Toast.makeText(MainActivity.this, "검색어를 입력하세요", Toast.LENGTH_SHORT).show();
        }
        //있으면
        else {
            Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
            searchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            searchIntent.putExtra(SEARCH_WORD, data);
            startActivity(searchIntent);

            searchText.setText(null);
        }
    }

    //엔터키 눌렀을 때 = 검색 버튼 눌렀을 때
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.searchText && actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearchButtonClicked(v);
        }
        return false;
    }

    //버튼에 공연시설 페이지로 가는 링크 만들기
    private void CreateButton() {
        db = FirebaseFirestore.getInstance();

        hdc = new ArrayList<HallDetailedClass>();

        db.collection("TheaterInfo").orderBy("searchedNum", Query.Direction.DESCENDING).limit(5)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    QuerySnapshot querySnapshot = task.getResult();

                    int cntIndex = 0;

                    //각각의 document 응답값을 HallDetailedClass로 받아 버튼에 할당
                    // 버튼 순서와 동일한 순서로 hdc에 넣고, 클릭한 버튼 순서(번호)에 따라 hdc에서 읽어오는 걸로.
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        hdc.add(new HallDetailedClass(document.getId(),
                                document.getString("theaterName") + " " + document.getString("hallName"),
                                document.getString("theaterCode") + "-" + document.getString("hallCode"),
                                document.getString("hallImage"), document.getBoolean("isSeatplan")));

                        // 공연장 이미지 glide, 공연장 이름 textview에 설정
                        if(document.getString("hallImage") == null) {
                            btnList[cntIndex].setBackgroundResource(R.drawable.theater1);
                        }
                        else {
                            Glide.with(getApplicationContext()).load(document.getString("hallImage")).into(btnList[cntIndex]);
                        }

                        //버튼의 이미지 색상을 죽임
                        btnList[cntIndex].setColorFilter(Color.argb(100, 0, 0, 0), android.graphics.PorterDuff.Mode.MULTIPLY);
                        btnList[cntIndex].setClickable(true);
                        textList[cntIndex].setText(document.getString("theaterName") + " " + document.getString("hallName"));

                        cntIndex++;
                    }

                    //각 버튼에 onClickListener 추가
                    for (int i = 0; i < 5; i++) {
                        final int finalI = i;
                        btnList[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HallDetailedClass item = hdc.get(finalI);

                                String documentID = item.getDocumentID();
                                String combinedID = item.getCombined_ID();
                                String combinedName = item.getCombined_name();
                                boolean isSeatplan = item.getIsSeatplan();

                                Intent intent = new Intent(MainActivity.this, HallInfo.class);
                                intent.putExtra("document_ID", documentID);
                                intent.putExtra("Combined_ID", combinedID);
                                intent.putExtra("Combined_Name", combinedName);
                                intent.putExtra("is_Seatplan", isSeatplan);
                                startActivity(intent);
                            }
                        });
                    }

                }
                else {
                    Toast.makeText(MainActivity.this, "정보를 불러오는 데에 실패했습니다. 인터넷 연결을 확인하고 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mainAuth.getCurrentUser();
    }

    @Override
    public void onBackPressed() {
        if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            backpress.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.drawerLayout.openDrawer(Gravity.RIGHT);
                drawer.init();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}