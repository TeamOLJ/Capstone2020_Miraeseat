package com.capstondesign.miraeseat;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class seatPage extends AppCompatActivity {

    ListView listView;
    SeatAdapter seatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //배경블러처리
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindow().setAttributes(layoutParams);
        // 레이아웃 설정
        setContentView(R.layout.activity_seat_page);


        // 사이즈조절
        // 1. 디스플레이 화면 사이즈 구하기
        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // 2. 화면 비율 설정
        int width = (int)(dp.getWidth()*0.9);
        int height = (int)(dp.getHeight()*0.8);
        // 3. 현재 화면에 적용
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        this.setFinishOnTouchOutside(false);
    }
}
