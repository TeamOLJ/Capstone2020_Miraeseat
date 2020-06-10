package com.capstondesign.miraeseat.hall;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.MainActivity;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.TheaterActivity;

import java.util.ArrayList;

public class HallInfo extends AppCompatActivity {

    Button btnSeat;

    ListView listView;
    HallAdapter hallAdapter;

    ArrayList<HallList_item> mData;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_info);

        drawer = new DrawerHandler(this);
        drawer.init();

        setSupportActionBar(drawer.toolbar);
        ViewCompat.setLayoutDirection(drawer.toolbar, ViewCompat.LAYOUT_DIRECTION_RTL);
        getSupportActionBar().setHomeAsUpIndicator(drawer.toolbar_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        btnSeat = (Button) findViewById(R.id.btnSeat);

        listView = (ListView)findViewById(R.id.hall_playlist);

        mData = new ArrayList<HallList_item>();

        //임시데이터
        mData.add(new HallList_item(R.mipmap.ic_launcher, "오페라의 유령","2020/04/04-2020/05/05","타비소 마세메네, 힐러리 라이터..."));
        mData.add(new HallList_item(R.mipmap.ic_launcher, "드라큘라","2020/04/22-2020/06/12","김준수, 류정한, 전동석..."));

        hallAdapter = new HallAdapter(HallInfo.this,mData);
        listView.setAdapter(hallAdapter);

        //hall_name에 공연장 이름 불러오기
        //해당 공연장의 공연 정보 리스트뷰에 불러오기

        btnSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TheaterActivity.class);
                intent.putExtra("hall_name", "hello"); //무슨 오류가 난다... 뭔지 모르겠다
                startActivity(intent);
            }
        });
    }

    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.drawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            finish();
        }
    }
}
