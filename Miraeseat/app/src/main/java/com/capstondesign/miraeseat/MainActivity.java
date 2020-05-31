package com.capstondesign.miraeseat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.capstondesign.miraeseat.hall.HallInfo;
import com.capstondesign.miraeseat.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    final int theater_id = 10000;
    final int show_id = 20000;
    final String SEARCH_WORD = "search_word";

    // Firebase 인증 변수
    private FirebaseAuth mainAuth;

    BackPressCloseHandler backpress;
    EditText searchText;
    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        CreateButton(theater_id, R.id.view_theater);
        CreateButton(show_id, R.id.view_show);

        // Initialize Firebase Auth
        mainAuth = FirebaseAuth.getInstance();
    }

    //검색 버튼 눌렀을 때
    public void onSearchButtonClicked(View v) {
        String data = searchText.getText().toString();

        Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
        searchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        searchIntent.putExtra(SEARCH_WORD, data);
        startActivity(searchIntent);

        searchText.setText(null);
    }

    //엔터키 눌렀을 때 = 검색 버튼 눌렀을 때
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.searchText && actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearchButtonClicked(v);
        }
        return false;
    }

    //동적 버튼 만들기
    private void CreateButton(int type, int layout) {
        LinearLayout view_theater = findViewById(layout);    //type에 따라 다른 레이아웃을 선택하도록 바꾸기
        int width = (int) getResources().getDimension(R.dimen.poster_width);
        int height = (int) getResources().getDimension(R.dimen.poster_height);
        int margin = (int) getResources().getDimension(R.dimen.poster_margin);

        LinearLayout.LayoutParams pm = new LinearLayout.LayoutParams(width, height);
        pm.setMargins(margin, margin, margin, margin);

        for (int i = 0; i < 5; ++i) {
            ImageButton imageButton = new ImageButton(this);
            imageButton.setId(type + i);
            imageButton.setId(type + i);
            imageButton.setImageResource(R.drawable.theater1);  //type에 따라 다른 그림이 나오도록 바꾸기
            imageButton.setLayoutParams(pm);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageButton.setOnClickListener(PosterOnClickListner);  //다른 버튼 클릭 리스너
            view_theater.addView(imageButton);
        }
    }

    //이게 맞나?
    public View.OnClickListener PosterOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selected_item = (int) v.getId();
            Intent act_information = new Intent(getApplicationContext(), HallInfo.class);
            startActivity(act_information);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        // 사용자가 로그인 되어 있는지 (null이 아닌지) 확인
        FirebaseUser currentUser = mainAuth.getCurrentUser();
        // 로그인 여부에 따라 처리하는 코드
        // updateUI(currentUser);
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