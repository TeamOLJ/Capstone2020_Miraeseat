package com.capstondesign.miraeseat;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capstondesign.miraeseat.seatpage.seatPage;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;


public class TheaterActivity extends AppCompatActivity {
    private static final String TAG = "TheaterActivity";

    private int half_width, half_height;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    float dX, dY;

    private ViewGroup seatplan_layout;
    private ImageView seatplan;

    private ImageView imageView;

    FirebaseFirestore db;
    TheaterItem_try TI; //TheaterItem -> TheaterItem_try

    String TheaterName;
    String seatPlanImage;
    double widthPerHeight;

    FloatingActionButton btnViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        Intent intent = getIntent();
        TheaterName = intent.getStringExtra("combined_name");
        seatPlanImage = intent.getStringExtra("seatImage");

        seatplan_layout = (ViewGroup) findViewById(R.id.seatplanLayout);
        seatplan = (ImageView) findViewById(R.id.seatplan);

        btnViewReview = (FloatingActionButton) findViewById(R.id.floatingViewReview);

        GetSeatplanImage();

        TI = new TheaterItem_try(this, seatplan_layout);   //TheaterItem -> TheaterItem_try

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
        }
    }

    private void GetSeatplanImage() {
        // 좌석배치도 세팅

        Glide.with(getApplicationContext()).load(seatPlanImage)
                .override(seatplan_layout.getWidth(), seatplan_layout.getHeight()).fitCenter()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // 의도적으로 딜레이를 주어 이미지가 seatplan에 설정된 뒤에 그 이미지뷰의 크기를 읽을 수 있게 한 방식:
                        new Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                TI.getSize(seatplan.getWidth(), seatplan.getHeight());
                                TI.init();
                                TI.execute();
                            }
                        }, 1000);
                        return false;
                    }
                })
                .into(seatplan);

        // 가장 이상적인 방식은 .into까지 완료된 뒤에 seatplan의 크기를 읽어오는 건데... 아무리 찾아도 완료 뒤 callback 함수를 찾을 수가 없었다. 시간이 되면 더 알아보는 걸로.
        // 가로세로 비율을 DB에 저장해놓고 사용하는 방식도 고려해보았으나, 어째서인지 사이즈가 이상하게 변해서 그 방식으로 구현은 실패했다.

    }


    public void onFloatingButtonClicked(View v) {
        finish();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mScaleGestureDetector.onTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dX = seatplan_layout.getX() - ev.getRawX();
                dY = seatplan_layout.getY() - ev.getRawY();
                Log.d("this", "DOWN");
                break;


            case MotionEvent.ACTION_MOVE:
                float tmpX = ev.getRawX() + dX;
                float tmpY = ev.getRawY() + dY;
                //mlayout.getX()에 초기의 화면 좌표값(dX의 getRawX())과 새로운 화면 좌표값(tmpX의 getRawX())의 차, 즉 X좌표 전개 방향을 더해줌.

                if (Math.abs(tmpX) < half_width * (mScaleFactor - 1) && Math.abs(tmpY) < half_height * (mScaleFactor - 1)) {
                    seatplan_layout.animate().x(tmpX).y(tmpY).setDuration(0).start();
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.d("this", "UP");
                super.dispatchTouchEvent(ev);
        }
        return true;
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        mScaleGestureDetector.onTouchEvent(event);
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.d("this", "DOWN");
//                dX = seatplan_layout.getX() - event.getRawX();
//                dY = seatplan_layout.getY() - event.getRawY();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                float tmpX = event.getRawX() + dX;
//                float tmpY = event.getRawY() + dY;
//                //mlayout.getX()에 초기의 화면 좌표값(dX의 getRawX())과 새로운 화면 좌표값(tmpX의 getRawX())의 차, 즉 X좌표 전개 방향을 더해줌.
//
//                if (Math.abs(tmpX) < half_width * (mScaleFactor - 1) && Math.abs(tmpY) < half_height * (mScaleFactor - 1)) {
//                    seatplan_layout.animate().x(tmpX).y(tmpY).setDuration(0).start();
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//                Log.d("this", "UP");
//                return false;
//        }
//        return true;
//    }

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