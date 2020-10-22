package com.capstondesign.miraeseat;

import android.content.Intent;
import android.graphics.PointF;
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

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capstondesign.miraeseat.seatpage.seatPage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TheaterActivity extends AppCompatActivity {
    private static final String TAG = "TheaterActivity";

    private int half_width, half_height;
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private float previous_sf;
    static float dX, dY;

    private ViewGroup seatplan_layout;
    private ImageView seatplan;

    FirebaseFirestore db;
    TheaterItem TI;

    String TheaterName;
    String seatPlanImage;
    PointF current_P = new PointF();

    SeatPlanInfo seatInfo;

    FloatingActionButton btnViewReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);

        Intent intent = getIntent();
        TheaterName = intent.getStringExtra("combined_name");
        seatPlanImage = intent.getStringExtra("seatImage");
        final String combinedID = intent.getStringExtra("combined_id");

        db = FirebaseFirestore.getInstance();

        seatplan_layout = (ViewGroup) findViewById(R.id.seatplanLayout);
        seatplan = (ImageView) findViewById(R.id.seatplan);

        btnViewReview = (FloatingActionButton) findViewById(R.id.floatingViewReview);

        TI = new TheaterItem(this, seatplan_layout);

        GetSeatplanInfo(combinedID);

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

    private void GetSeatplanInfo(String combinedID) {
        // DB에서 좌석배치도 정보 읽어오기
        db.collection("SeatPlanInfo").document(combinedID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            //(int seat_width, int seat_height, int margin_left, int margin_top, int margin_row, int margin_col, int max_row, int max_col, ArrayList<Integer> floor_row, Map<Integer, ArrayList<Integer>> row_start_end, ArrayList<Integer> aisle_col)
                            seatInfo = new SeatPlanInfo(documentSnapshot.getDouble("seatWidth").floatValue(), //넓이 비율
                                    documentSnapshot.getDouble("seatHeight").floatValue(),    //높이 비율
                                    documentSnapshot.getDouble("marginLeft").floatValue(), //왼쪽 여백 비율
                                    documentSnapshot.getDouble("marginTop").floatValue(),  //위쪽 여백 비율
                                    (ArrayList<Double>)documentSnapshot.get("marginRow"),  //층 사이 여백 비율(층수-1)
                                    documentSnapshot.getDouble("marginCol").floatValue(),  //구역 사이 여백 비율
                                    (ArrayList<Long>) documentSnapshot.get("floorRow"), //층별 행 수
                                    (Map<String, ArrayList<Long>>) documentSnapshot.get("aisleSeat"),    //층별 구역 수
                                    documentSnapshot.getLong("maxCol").intValue(),  //최대 좌석 수
                                    (Map<String, ArrayList<Long>>) documentSnapshot.get("rowStartEnd"), //행-(좌석 시작 번호, 좌석 끝 번호...) 배열 크기는 2n.
                                    documentSnapshot.getBoolean("_isgy").booleanValue(), //구역이 있는가
                                    documentSnapshot.getBoolean("_isColRepeat").booleanValue()   //
                            );

                            // Map 접근 방식:
                            // (주의사항: Map의 Key는 좌석 행 번호이므로 1부터 시작함)
//                            for(int i = 0; i < seatInfo.getRow_start_end().size(); i++) {
//                                Log.d("rowStartEnd: ", String.valueOf(i+1)+" = "+String.valueOf((ArrayList<Integer>)seatInfo.getRow_start_end().get(String.valueOf(i+1))));
//                                Log.d("rowStartEnd: ", (i+1) + " = ["+ seatInfo.getRow_start_end().get(String.valueOf(i+1)).get(0) + ", " + seatInfo.getRow_start_end().get(String.valueOf(i+1)).get(1) + "]");
//                            }

                            // 좌석배치도 세팅
                            Glide.with(getApplicationContext()).load(seatPlanImage)
                                    .override(seatplan_layout.getWidth(), seatplan_layout.getHeight()).fitCenter()
                                    .listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                            Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
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
                                                    TI.setSize(seatplan.getWidth(), seatplan.getHeight());
                                                    TI.init(seatInfo);
                                                    TI.execute();
                                                }
                                            }, 1000);
                                            return false;
                                        }
                                    })
                                    .into(seatplan);
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@Nonnull Exception e) {
                        Log.e(TAG, "error occured", e);
                        Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                    }
                });

        // 가장 이상적인 방식은 .into까지 완료된 뒤에 seatplan의 크기를 읽어오는 건데... 아무리 찾아도 완료 뒤 callback 함수를 찾을 수가 없었다. 시간이 되면 더 알아보는 걸로.
        // 가로세로 비율을 DB에 저장해놓고 사용하는 방식도 고려해보았으나, 어째서인지 사이즈가 이상하게 변해서 그 방식으로 구현은 실패했다.

    }


    public void onFloatingButtonClicked(View v) {
        finish();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);

        int count = event.getPointerCount();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                dX = seatplan_layout.getX() - event.getRawX();
                dY = seatplan_layout.getY() - event.getRawY();
                return false;

            case MotionEvent.ACTION_MOVE:
                if(count==1) {
                    float tmpX = event.getRawX() + dX;
                    float tmpY = event.getRawY() + dY;
                    //mlayout.getX()에 초기의 화면 좌표값(dX의 getRawX())과 새로운 화면 좌표값(tmpX의 getRawX())의 차, 즉 X좌표 전개 방향을 더해줌.

                    if (Math.abs(tmpX) < half_width * (mScaleFactor - 1) && Math.abs(tmpY) < half_height * (mScaleFactor - 1)) {
                        seatplan_layout.animate().x(tmpX).y(tmpY).setDuration(0).start();
                    } else {
                        if (Math.abs(tmpX) < half_width * (mScaleFactor - 1)) {
                            seatplan_layout.animate().x(tmpX).setDuration(0).start();
                        }

                        if (Math.abs(tmpY) < half_height * (mScaleFactor - 1)) {
                            seatplan_layout.animate().y(tmpY).setDuration(0).start();
                        }
                    }
                    return true;
                }

            case MotionEvent.ACTION_UP:
                return false;

            case MotionEvent.ACTION_POINTER_DOWN:
                current_P.set(seatplan_layout.getX(), seatplan_layout.getY());
                previous_sf = mScaleFactor;
                return true;
        }
        return false;
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            previous_sf = mScaleFactor;
            mScaleFactor *= detector.getScaleFactor();
            mScaleFactor = Math.max(1f, Math.min(mScaleFactor, 5.0f));

            if(previous_sf>mScaleFactor) {
                float tmp = (float)(mScaleFactor-1)/previous_sf;
                seatplan_layout.animate().x(current_P.x*tmp).y(current_P.y*tmp).setDuration(0).start();
            }
            seatplan_layout.setScaleX(mScaleFactor);
            seatplan_layout.setScaleY(mScaleFactor);

            return true;
        }
    }
}