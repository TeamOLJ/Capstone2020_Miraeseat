package com.capstondesign.miraeseat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class seatPage extends AppCompatActivity {

    ImageButton btnCancel;
    Button btnWrite;
    TextView seat;
    RatingBar avgRating; /*평점 레이팅바*/
    TextView avgText; /*평점*/
    TextView cntReview; /*해당 좌석 리뷰글 수*/

    ListView listView;
    SeatAdapter seatAdapter;

    ArrayList<seatList_item> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //배경블러처리
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_seat_page);

        // 사이즈조절
        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // 2. 화면 비율 설정
        int width = (int)(dp.getWidth()*0.9);
        int height = (int)(dp.getHeight()*0.8);
        // 3. 현재 화면에 적용
        getWindow().getAttributes().width = width;
        getWindow().getAttributes().height = height;

        this.setFinishOnTouchOutside(false);

        btnCancel = (ImageButton)findViewById(R.id.btnSlide);
        btnWrite = (Button)findViewById(R.id.btnWrite);

        seat = (TextView)findViewById(R.id.seatNum);
        avgRating = (RatingBar)findViewById(R.id.avgRating);
        avgText = (TextView)findViewById(R.id.avgText);
        cntReview = (TextView)findViewById(R.id.cntReview);

        listView = (ListView)findViewById(R.id.seat_list);

        mData = new ArrayList<seatList_item>();

        mData.add(new seatList_item("홍길동",R.mipmap.ic_launcher_round,R.drawable.example,(float)4.5,"사운드 빵빵\n시야 정말 잘보여요"));
        mData.add(new seatList_item("홍길동",R.mipmap.ic_launcher_round,R.mipmap.ic_launcher,(float)4.5,"사운드 빵빵\n시야 정말 잘보여요"));

        seatAdapter = new SeatAdapter(seatPage.this,mData);
        listView.setAdapter(seatAdapter);

        //해당 좌석 정보, 리뷰글들 불러오기 seat에 좌석 정보, avgRating, avgTtext에 평균 평점, cntReview에 리뷰 글 수



        //좌석정보 내리기 버튼
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.no_change,R.anim.translate_down);
            }
        });

        //리뷰쓰기 버튼
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), WriteReview.class);
                startActivity(intent);
            }
        });
    }
}
