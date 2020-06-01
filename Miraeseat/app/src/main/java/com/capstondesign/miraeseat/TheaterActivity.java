package com.capstondesign.miraeseat;


import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;


public class TheaterActivity extends AppCompatActivity {
    private int half_width, half_height;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ViewGroup mlayout;
    float dX, dY;
    private ImageView imageView;

    DrawerHandler drawer;
    FirebaseFirestore db;
    SeatplanItem spi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        final String TheaterName = getIntent().getStringExtra("hall_name");
        GetSeatplanImage(TheaterName);

        spi = new SeatplanItem(this, (ViewGroup)findViewById(R.id.seatplanLayout));
        spi.init();
        spi.execute();

        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }


    private void GetSeatplanImage(String name) {//DB에서 공연장 이름으로 이미지 받아오기
        imageView = (ImageView)findViewById(R.id.seatplan);

        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/capstone2020-e540d.appspot.com/o/theater_seat_plan%2Fcharlotte90.jpg?alt=media&token=c58ec0c9-de25-4fc4-b001-d16c6aca986a").into(imageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mlayout = (ViewGroup)findViewById(R.id.seatplanLayout);
        half_width = mlayout.getWidth()/2;
        half_height = mlayout.getHeight()/2;
    }

    public void onFloatingButtonClicked(View v) {
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = mlayout.getX()-event.getRawX();
                dY = mlayout.getY()-event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:
                float tmpX = event.getRawX() + dX;
                float tmpY = event.getRawY() + dY;
                //mlayout.getX()에 초기의 화면 좌표값(dX의 getRawX())과 새로운 화면 좌표값(tmpX의 getRawX())의 차, 즉 X좌표 전개 방향을 더해줌.

                if(Math.abs(tmpX) < half_width*(mScaleFactor-1) && Math.abs(tmpY) < half_height*(mScaleFactor-1)) {
                    mlayout.animate().x(tmpX).y(tmpY).setDuration(0).start();
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
            mlayout.setScaleX(mScaleFactor);
            mlayout.setScaleY(mScaleFactor);

            return true;
        }
    }
}