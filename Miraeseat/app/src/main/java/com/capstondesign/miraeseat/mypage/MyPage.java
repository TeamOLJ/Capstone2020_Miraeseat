package com.capstondesign.miraeseat.mypage;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.EditInfo;

import com.capstondesign.miraeseat.Image;
import com.capstondesign.miraeseat.MainActivity;
import com.capstondesign.miraeseat.EditReview;

import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.Review;
import com.capstondesign.miraeseat.SaveSharedPreference;
import com.capstondesign.miraeseat.UnsubscribePage;
import com.capstondesign.miraeseat.UserClass;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPage extends AppCompatActivity implements MyPageAdapter.ListBtnClickListener {
    private static final String TAG = "MyPage";

    static final int IS_INFO_MODIFIED = 1234;

    FirebaseUser user;
    FirebaseFirestore db;
    String userUID;

    Button mypage_edit;
    Button mypage_withdrawl;
    CircleImageView profile;
    TextView nickname;

    ListView listView;
    MyPageAdapter myPageAdapter;
    RelativeLayout noReviewLayout;
    RelativeLayout loadingLayout;

    Review userReview;

    DrawerHandler drawer;

    ArrayList<mypageList_item> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mypage_edit = (Button) findViewById(R.id.mypage_edit);
        mypage_withdrawl = (Button) findViewById(R.id.mypage_withdrawl);

        profile = (CircleImageView) findViewById(R.id.profilePic);
        nickname = (TextView) findViewById(R.id.nickName);

        listView = (ListView)findViewById(R.id.mypage_list);

        noReviewLayout = (RelativeLayout)findViewById(R.id.no_review_layout);
        loadingLayout = (RelativeLayout)findViewById(R.id.loading_layout);

        initUI();

        //loadReviewData();
        // initUI() 실행이 끝난 후에 loadReviewData()를 실행해야 하는데 자꾸 동시에 돌아가서 실행이 제대로 안 된다...
        // initUI() 안에 집어넣는 걸로 일단은 해결했지만... 더 나은 방안이 있지 않을까?

        //회원정보수정 버튼
        mypage_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditInfo.class);
                startActivityForResult(intent, IS_INFO_MODIFIED);
            }
        });

        //회원탈퇴 버튼
        mypage_withdrawl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UnsubscribePage.class);
                startActivity(intent);
            }
        });


    }

    public void initUI() {
        // Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();

//        // 프로필사진 & 닉네임 데이터베이스에서 읽어오기
//        db.collection("UserInfo").document(userUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                UserClass loginedUser = documentSnapshot.toObject(UserClass.class);
//                nickname.setText(loginedUser.getNick());
//                if(loginedUser.getImagepath() != null) {
//                    Glide.with(getApplicationContext()).load(loginedUser.getImagepath()).into(profile);
//                }
//
//                loadReviewData();
//
//            }
//        });

        nickname.setText(SaveSharedPreference.getUserNickName(getApplicationContext()));
        if(SaveSharedPreference.getProfileImage(getApplicationContext()) != null) {
            Glide.with(getApplicationContext()).load(SaveSharedPreference.getProfileImage(getApplicationContext())).into(profile);
        }

        loadReviewData();
    }

    // 로그인한 계정이 작성한 후기를 DB에서 읽어와 화면에 표시
    public void loadReviewData() {
        mData = new ArrayList<mypageList_item>();

        db.collection("SeatReview").whereEqualTo("ownerUser", userUID).orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {
                        // 쿼리 리턴값이 없음 = 작성한 리뷰가 없음
                        Log.d(TAG, nickname.getText().toString());
                        loadingLayout.setVisibility(View.INVISIBLE);
                        noReviewLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // 리턴값이 있음 = 작성한 리뷰가 있음
                        loadingLayout.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        noReviewLayout.setVisibility(View.INVISIBLE);

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // 각각의 쿼리 반환값을 mData에 추가
                            userReview = document.toObject(Review.class);

                            // 문서 내용에서 필요한 값만 취해서 mypageList_item 객체로 mData에 추가
                            mData.add(new mypageList_item(document.getId(), userReview.getTheaterName(), userReview.getSeatNum(),
                                    userReview.getRating(), userReview.getImagepath(), userReview.getReviewText(), userReview.getReviewDate()));
                        }

                        // mData까지도 문제가 없다...

                        myPageAdapter = new MyPageAdapter(MyPage.this, mData, MyPage.this);
                        listView.setAdapter(myPageAdapter);
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1) {
            initUI();
        }
    }

    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // 어댑터의 버튼 클릭을 처리하기 위해 MyPageAdapter의 ListBtnClickListener를 상속받았음
    // ListBtnClickLister 내부에 선언했던 onListBtnClick 함수를 오버라이드
    @Override
    public void onListBtnClick(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(MyPage.this, view);
        popupMenu.inflate(R.menu.list_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    // 수정 버튼을 누른 경우
                    case R.id.item_modify:
                        Intent intent = new Intent(MyPage.this, EditReview.class);
                        intent.putExtra("documentID", mData.get(position).getDocumentID());
                        intent.putExtra("imagepath", mData.get(position).getImagePath());
                        intent.putExtra("seatNum", mData.get(position).getSeatNum());
                        intent.putExtra("rating", mData.get(position).getSeatRating());
                        intent.putExtra("reviewContext", mData.get(position).getReviewContext());
                        startActivityForResult(intent, 1234);
                        return true;
                    // 삭제 버튼을 누른 경우
                    case R.id.item_delete:
                        showConfirmMsg(position);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void showConfirmMsg(final int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle(null);
        builder.setMessage("후기를 삭제하시겠습니까? 삭제한 후기는 되돌릴 수 없습니다.");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String reviewImagePath = mData.get(position).getImagePath();

                // DB에서 해당 문서를 삭제
                db.collection("SeatReview").document(mData.get(position).getDocumentID())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Storage에서 사진 삭제
                                StorageReference oldPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(reviewImagePath);
                                oldPhotoRef.delete();

                                Toast.makeText(MyPage.this, "후기가 삭제되었습니다.", Toast.LENGTH_LONG).show();

                                // 리스트뷰의 리스트에서도 해당 아이템 삭제
                                mData.remove(position);
                                // 어댑터 새로고침
                                myPageAdapter.notifyDataSetChanged();
                                listView.setAdapter(myPageAdapter);
                            }
                        });

            }
        });
        builder.setNegativeButton("취소", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    // 뒤로가기 버튼(홈버튼)을 누르면 창이 꺼지는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}


