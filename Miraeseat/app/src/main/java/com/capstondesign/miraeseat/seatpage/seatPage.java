package com.capstondesign.miraeseat.seatpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.capstondesign.miraeseat.EditReview;
import com.capstondesign.miraeseat.LoginPage;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.Review;
import com.capstondesign.miraeseat.UserClass;
import com.capstondesign.miraeseat.WriteReview;
import com.capstondesign.miraeseat.mypage.MyPage;
import com.capstondesign.miraeseat.mypage.MyPageAdapter;
import com.capstondesign.miraeseat.mypage.mypageList_item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class seatPage extends AppCompatActivity implements SeatAdapter.ItemBtnClickListener {
    private static final String TAG = "SeatPage";

    // Firebase
    FirebaseUser user;
    FirebaseAuth mainAuth;
    FirebaseFirestore db;
    String userUID = null;

    ImageButton btnCancel;
    Button btnWrite;
    TextView seat;
    RatingBar avgRating; /*평점 레이팅바*/
    TextView avgText; /*평점*/
    TextView cntReview; /*해당 좌석 리뷰글 수*/

    ListView listView;
    SeatAdapter seatAdapter;

    RelativeLayout noReviewLayout;
    RelativeLayout loadingLayout;

    int countReview;
    float countRating;

    ArrayList<seatList_item> seatItemData;

    // SeatPage Activity를 시작할 때, 이전 intent로부터 극장 이름, 좌석 위치 값을 extra로 받아와야 함
    String theaterName;
    String seatNumber;

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

        // Firebase
        db = FirebaseFirestore.getInstance();
        mainAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        btnCancel = (ImageButton)findViewById(R.id.btnSlide);
        btnWrite = (Button)findViewById(R.id.btnWrite);

        seat = (TextView)findViewById(R.id.seatNum);
        avgRating = (RatingBar)findViewById(R.id.avgRating);
        avgText = (TextView)findViewById(R.id.avgText);
        cntReview = (TextView)findViewById(R.id.cntReview);

        listView = (ListView)findViewById(R.id.seat_list);

        noReviewLayout = (RelativeLayout)findViewById(R.id.no_review_layout);
        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);

        // 이전 intent로부터 받아온 값
        Intent intent = getIntent();

        theaterName = intent.getStringExtra("theaterName");
        seatNumber = intent.getStringExtra("seatNumber");
        seat.setText(seatNumber);

        loadReviewData();

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
                // 로그인을 아직 하지 않은 경우 로그인부터
                if(mainAuth.getCurrentUser() == null) {
                    loginDialog();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), WriteReview.class);
                    intent.putExtra("selectedSeat", seatNumber);
                    startActivityForResult(intent, 1234);
                }
            }
        });
    }

    //해당 좌석 정보, 리뷰글들 불러오기 seat에 좌석 정보, avgRating, avgTtext에 평균 평점, cntReview에 리뷰 글 수
    public void loadReviewData() {
        seatItemData = new ArrayList<seatList_item>();
        seatAdapter = new SeatAdapter(seatPage.this, seatItemData, seatPage.this);
        listView.setAdapter(seatAdapter);

        db.collection("SeatReview").whereEqualTo("theaterName", theaterName).whereEqualTo("seatNum", seatNumber)
                .orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot reviewQuerySnapshot = task.getResult();
                    if (reviewQuerySnapshot.isEmpty()) {
                        // 쿼리 리턴값이 없음 = 작성한 리뷰가 없음
                        cntReview.setText("후기 수: 0");
                        loadingLayout.setVisibility(View.INVISIBLE);
                        noReviewLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // 리턴값이 있음 = 작성한 리뷰가 있음
                        loadingLayout.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        noReviewLayout.setVisibility(View.INVISIBLE);

                        countReview = 0;
                        countRating = 0;

                        // 리턴 된 각 문서(=리뷰)에 대해 아래 코드 실행
                        for (final QueryDocumentSnapshot document : reviewQuerySnapshot) {

                            final Review foundReview = document.toObject(Review.class);
                            final String documentID = document.getId();

                            // join과 같은 기능을 수행하기 위해 쿼리를 한 번 더 실행
                            // 유저 정보를 가져오는 데에 실패한 경우 해당 리뷰 정보에 오류가 있는 것으로 판단, 화면에 출력하지 않음
                            db.collection("UserInfo").document(foundReview.getOwnerUser()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserClass foundUser = documentSnapshot.toObject(UserClass.class);

                                    boolean isReviewOwner = false;

                                    if(mainAuth.getCurrentUser() != null) {
                                        isReviewOwner = user.getUid().equals(foundReview.getOwnerUser());
                                    }
                                    // 문서 내용에서 필요한 값만 취하여 mData에 추가
                                    seatItemData.add(new seatList_item(documentID, foundUser.getNick(), foundUser.getImagepath(), foundReview.getImagepath(),
                                            foundReview.getRating(), foundReview.getReviewText(), isReviewOwner));

                                    countReview += 1;

                                    countRating += foundReview.getRating();

                                    seatAdapter.notifyDataSetChanged();
                                    listView.setAdapter(seatAdapter);

                                    cntReview.setText("후기 수: "+countReview);
                                    avgRating.setRating(countRating/countReview);
                                    avgText.setText(new DecimalFormat("##.#").format(countRating/countReview));
                                }
                            });
                        }
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    @Override
    public void onItemBtnClick(View view, int position, int whichBtn) {
        switch (whichBtn) {
            case 100:
                // 수정
                Intent intent = new Intent(seatPage.this, EditReview.class);
                intent.putExtra("documentID", seatItemData.get(position).getDocumentID());
                intent.putExtra("imagepath", seatItemData.get(position).getReview_image());
                intent.putExtra("seatNum", seatNumber);
                intent.putExtra("rating", seatItemData.get(position).getRatingbar());
                intent.putExtra("reviewContext", seatItemData.get(position).getReview_writing());
                startActivityForResult(intent, 1234);
                return;
            case 200:
                // 삭제
                showConfirmMsg(position);
                return;
            case 300:
                // 좋아요
                Log.d(TAG, "좋아요를 눌렀음");
                // 해당 리뷰의 좋아요 값 1 증가... 같은 유저가 반복해서 좋아요를 누를 수 없게 하려면 어떻게 해야 하지??
                return;
            case 400:
                // 신고하기
                if(mainAuth.getCurrentUser() == null) {
                    Toast.makeText(seatPage.this, "로그인 후 가능합니다.", Toast.LENGTH_LONG).show();
                }
                else
                    reportDialog(view);
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            loadReviewData();
        }
    }

    private void showConfirmMsg(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("후기를 삭제하시겠습니까? 삭제한 후기는 되돌릴 수 없습니다.");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String reviewImagePath = seatItemData.get(position).getReview_image();

                // DB에서 해당 문서를 삭제
                db.collection("SeatReview").document(seatItemData.get(position).getDocumentID())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Storage에서 사진 삭제
                        StorageReference reviewPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(reviewImagePath);
                        reviewPhotoRef.delete();

                        Toast.makeText(seatPage.this, "후기가 삭제되었습니다.", Toast.LENGTH_LONG).show();

                        // 리스트뷰의 리스트에서도 해당 아이템 삭제
                        seatItemData.remove(position);
                        // 어댑터 새로고침
                        loadReviewData();
                    }
                });

            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("후기는 로그인 후 작성하실 수 있습니다. 로그인 페이지로 이동하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void reportDialog(View view) {
        AlertDialog.Builder report = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Light_Dialog);

        report.setTitle("신고하기");
        report.setMessage("신고 사유를 적어주세요.");

        final EditText txt = new EditText(view.getContext());
        report.setView(txt);
        report.setCancelable(false);

        report.setPositiveButton("신고하기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        report.setNegativeButton("취소", null);

        final AlertDialog alertDialog = report.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("선택","신고");

                String value = txt.getText().toString();

                Boolean closeDialog = false;

                if(value.length()<10) {
                    Toast.makeText(seatPage.this, "신고 내용을 입력해주세요.(10자 이상)", Toast.LENGTH_LONG).show();
                }
                else if(value.length()>= 10) {
                    //신고 정보 데이터로 넘기기
                    Toast.makeText(seatPage.this, "신고가 접수되었습니다.", Toast.LENGTH_LONG).show();
                    closeDialog = true;
                }

                if(closeDialog)
                    alertDialog.dismiss();
            }
        });
    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_change,R.anim.translate_down);
    }


}
