package com.capstondesign.miraeseat.mypage;

import android.content.DialogInterface;
import android.content.Intent;
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

import com.capstondesign.miraeseat.MainActivity;
import com.capstondesign.miraeseat.EditReview;

import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.Review;
import com.capstondesign.miraeseat.SaveSharedPreference;
import com.capstondesign.miraeseat.UnsubscribePage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

        //?????????????????? ??????
        mypage_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditInfo.class);
                startActivityForResult(intent, IS_INFO_MODIFIED);
            }
        });

        //???????????? ??????
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

        nickname.setText(SaveSharedPreference.getUserNickName(getApplicationContext()));
        if(SaveSharedPreference.getProfileImage(getApplicationContext()) != null) {
            Glide.with(getApplicationContext()).load(SaveSharedPreference.getProfileImage(getApplicationContext())).into(profile);
        }

        loadReviewData();
    }

    // ???????????? ????????? ????????? ????????? DB?????? ????????? ????????? ??????
    public void loadReviewData() {
        mData = new ArrayList<mypageList_item>();

        db.collection("SeatReview").whereEqualTo("ownerUser", userUID).orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot.isEmpty()) {
                        // ?????? ???????????? ?????? = ????????? ????????? ??????
                        loadingLayout.setVisibility(View.INVISIBLE);
                        noReviewLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // ???????????? ?????? = ????????? ????????? ??????
                        loadingLayout.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        noReviewLayout.setVisibility(View.INVISIBLE);

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            // ????????? ?????? ???????????? mData??? ??????
                            userReview = document.toObject(Review.class);

                            // ?????? ???????????? ????????? ?????? ????????? mypageList_item ????????? mData??? ??????
                            mData.add(new mypageList_item(document.getId(), userReview.getTheaterName(), userReview.getSeatNum(),
                                    userReview.getRating(), userReview.getImagepath(), userReview.getReviewText(), userReview.getReviewDate()));
                        }

                        myPageAdapter = new MyPageAdapter(MyPage.this, mData, MyPage.this);
                        listView.setAdapter(myPageAdapter);
                    }
                }
                else {
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

    // ???????????? ?????? ????????? ???????????? ?????? MyPageAdapter??? ListBtnClickListener??? ???????????????
    // ListBtnClickLister ????????? ???????????? onListBtnClick ????????? ???????????????
    @Override
    public void onListBtnClick(View view, final int position) {
        PopupMenu popupMenu = new PopupMenu(MyPage.this, view);
        popupMenu.inflate(R.menu.list_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    // ?????? ????????? ?????? ??????
                    case R.id.item_modify:
                        Intent intent = new Intent(MyPage.this, EditReview.class);
                        intent.putExtra("documentID", mData.get(position).getDocumentID());
                        intent.putExtra("imagepath", mData.get(position).getImagePath());
                        intent.putExtra("seatNum", mData.get(position).getSeatNum());
                        intent.putExtra("rating", mData.get(position).getSeatRating());
                        intent.putExtra("reviewContext", mData.get(position).getReviewContext());
                        startActivityForResult(intent, 1234);
                        return true;
                    // ?????? ????????? ?????? ??????
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
        builder.setMessage("????????? ????????????????????????? ????????? ????????? ????????? ??? ????????????.");

        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String reviewImagePath = mData.get(position).getImagePath();

                // DB?????? ?????? ????????? ??????
                db.collection("SeatReview").document(mData.get(position).getDocumentID())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // ??????????????? ?????? ???????????? Storage?????? ?????? ??????
                                if(reviewImagePath != null) {
                                    StorageReference oldPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(reviewImagePath);
                                    oldPhotoRef.delete();
                                }

                                Toast.makeText(MyPage.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                                // ??????????????? ?????????????????? ?????? ????????? ??????
                                mData.remove(position);
                                // ????????? ????????????
                                myPageAdapter.notifyDataSetChanged();
                                listView.setAdapter(myPageAdapter);
                            }
                        });

            }
        });
        builder.setNegativeButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // ???????????? ??????(?????????)??? ????????? ?????? ????????? ?????????
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


