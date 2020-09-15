package com.capstondesign.miraeseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.search.HallDetailedClass;
import com.capstondesign.miraeseat.search.SearchActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    final String SEARCH_WORD = "search_word";

    // Firebase 인증 변수
    private FirebaseAuth mainAuth;

    BackPressCloseHandler backpress;
    EditText searchText;
    DrawerHandler drawer;

    ArrayList<HallDetailedClass> hdc = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
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

        CreateButton(R.id.view_theater, hdc);

        // Initialize Firebase Auth
        mainAuth = FirebaseAuth.getInstance();
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

    //동적 버튼 만들기
    private void CreateButton(int layout, ArrayList<HallDetailedClass> datas) {
        LinearLayout view_theater = findViewById(layout);
        int height = (int) getResources().getDimension(R.dimen.poster_height);
        int margin = (int) getResources().getDimension(R.dimen.poster_margin);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        params.setMargins(margin, 0, margin, margin);

        for (int i = 0; i < 5; ++i) {
            ImageButton imageButton = new ImageButton(this);
            if(datas==null) {
                imageButton.setImageResource(R.drawable.logo_temp);
            }
            else{
                Glide.with(imageButton.getContext()).load(datas.get(i).getHall_Image()).into(imageButton);
            }

            imageButton.setLayoutParams(params);
            imageButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageButton.setElevation(1);
            }
            imageButton.setOnClickListener(PosterOnClickListner);  //다른 버튼 클릭 리스너
            view_theater.addView(imageButton);
        }
    }

    public View.OnClickListener PosterOnClickListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int selected_item = (int) v.getId();
            Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
//            Intent act_information = new Intent(getApplicationContext(), HallInfo.class);
//            startActivity(act_information);
        }
    };

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