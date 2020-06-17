package com.capstondesign.miraeseat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.seatpage.seatPage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;


public class TheaterActivity extends AppCompatActivity {
    private int half_width, half_height;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ViewGroup seatplan_layout;
    private ImageView seatplan;
    float dX, dY;
    private ImageView imageView;

    FirebaseFirestore db;
    TheaterItem TI;

    FloatingActionButton btnViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        final String TheaterName = getIntent().getStringExtra("hall_name");

        seatplan_layout = (ViewGroup) findViewById(R.id.seatplanLayout);
        seatplan = (ImageView) findViewById(R.id.seatplan);

        btnViewReview = (FloatingActionButton) findViewById(R.id.floatingViewReview);

        GetSeatplanImage(TheaterName);

        TI = new TheaterItem(this, seatplan_layout);

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        btnViewReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String seatNum = TI.getSelectedSeat();

                if(seatNum == null) {
                    Toast.makeText(TheaterActivity.this, "좌석을 선택하세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(TheaterActivity.this, seatPage.class);
                    intent.putExtra("theaterName", TheaterName);
                    intent.putExtra("seatNumber", TI.getSelectedSeat());
                    startActivity(intent);
                    overridePendingTransition(R.anim.translate_up, R.anim.no_change);
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus == true) {
            half_width = seatplan_layout.getWidth() / 2;
            half_height = seatplan_layout.getHeight() / 2;


            //onCreate 때는 seatplan의 크기 측정 불가. 화면에 이미지가 생성된 후 측정 가능.
            TI.getSize(seatplan.getWidth(), seatplan.getHeight());
            TI.init();
            TI.execute();
        }

    }

    private void GetSeatplanImage(String TheaterName) {
        //DB에서 공연장 이름으로 이미지 받아오기

        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/capstone2020-e540d.appspot.com/o/theater_seat_plan%2Fcharlotte.jpg?alt=media&token=80453ebf-d7c5-4274-83c8-beba34bc5caa").into(seatplan);
    }


    public void onFloatingButtonClicked(View v) {
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = seatplan_layout.getX() - event.getRawX();
                dY = seatplan_layout.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float tmpX = event.getRawX() + dX;
                float tmpY = event.getRawY() + dY;
                //mlayout.getX()에 초기의 화면 좌표값(dX의 getRawX())과 새로운 화면 좌표값(tmpX의 getRawX())의 차, 즉 X좌표 전개 방향을 더해줌.

                if (Math.abs(tmpX) < half_width * (mScaleFactor - 1) && Math.abs(tmpY) < half_height * (mScaleFactor - 1)) {
                    seatplan_layout.animate().x(tmpX).y(tmpY).setDuration(0).start();
                }
                break;

            default:
                return false;
        }
        return true;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1f,
                    Math.min(mScaleFactor, 5.0f));
            seatplan_layout.setScaleX(mScaleFactor);
            seatplan_layout.setScaleY(mScaleFactor);

            return true;
        }
    }
}