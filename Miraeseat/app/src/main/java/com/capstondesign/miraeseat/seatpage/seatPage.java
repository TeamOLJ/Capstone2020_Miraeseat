package com.capstondesign.miraeseat.seatpage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.capstondesign.miraeseat.UserReport;
import com.capstondesign.miraeseat.WriteReview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

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
    RatingBar avgRating; /*?????? ????????????*/
    TextView avgText; /*??????*/
    TextView cntReview; /*?????? ?????? ????????? ???*/

    ListView listView;
    SeatAdapter seatAdapter;

    RelativeLayout noReviewLayout;
    RelativeLayout loadingLayout;

    int countReview;
    float countRating;

    ArrayList<seatList_item> seatItemData;

    // SeatPage Activity??? ????????? ???, ?????? intent????????? ?????? ??????, ?????? ?????? ?????? extra??? ???????????? ???
    String theaterName;
    String seatNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //??????????????????
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.5f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_seat_page);

        // ???????????????
        Display dp = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        // 2. ?????? ?????? ??????
        int width = (int)(dp.getWidth()*0.9);
        int height = (int)(dp.getHeight()*0.8);
        // 3. ?????? ????????? ??????
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

        // ?????? intent????????? ????????? ???
        Intent intent = getIntent();

        theaterName = intent.getStringExtra("theaterName");
        seatNumber = intent.getStringExtra("seatNumber");
        seat.setText(seatNumber);

        loadReviewData();

        //???????????? ????????? ??????
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                overridePendingTransition(R.anim.no_change,R.anim.translate_down);
            }
        });

        //???????????? ??????
        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ???????????? ?????? ?????? ?????? ?????? ???????????????
                if(mainAuth.getCurrentUser() == null) {
                    loginDialog();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), WriteReview.class);
                    intent.putExtra("theaterName", theaterName);
                    intent.putExtra("selectedSeat", seatNumber);
                    startActivityForResult(intent, 1234);
                }
            }
        });
    }

    //?????? ?????? ??????, ???????????? ???????????? seat??? ?????? ??????, avgRating, avgTtext??? ?????? ??????, cntReview??? ?????? ??? ???
    public void loadReviewData() {
        seatItemData = new ArrayList<seatList_item>();
        seatAdapter = new SeatAdapter(seatPage.this, seatItemData, seatPage.this);
        listView.setAdapter(seatAdapter);

        db.collection("SeatReview").whereEqualTo("theaterName", theaterName).whereEqualTo("seatNum", seatNumber)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot reviewQuerySnapshot = task.getResult();
                    if (reviewQuerySnapshot.isEmpty()) {
                        // ?????? ???????????? ?????? = ????????? ????????? ??????
                        cntReview.setText("?????? ???: 0");
                        loadingLayout.setVisibility(View.INVISIBLE);
                        noReviewLayout.setVisibility(View.VISIBLE);
                        listView.setVisibility(View.INVISIBLE);
                    }
                    else {
                        // ???????????? ?????? = ????????? ????????? ??????
                        loadingLayout.setVisibility(View.INVISIBLE);
                        listView.setVisibility(View.VISIBLE);
                        noReviewLayout.setVisibility(View.INVISIBLE);

                        countReview = 0;
                        countRating = 0;

                        // ?????? ??? ??? ??????(=??????)??? ?????? ?????? ?????? ??????
                        for (final QueryDocumentSnapshot document : reviewQuerySnapshot) {

                            final Review foundReview = document.toObject(Review.class);
                            final String documentID = document.getId();

                            // join??? ?????? ????????? ???????????? ?????? ????????? ??? ??? ??? ??????
                            // ?????? ????????? ???????????? ?????? ????????? ?????? ?????? ?????? ????????? ????????? ?????? ????????? ??????, ????????? ???????????? ??????
                            db.collection("UserInfo").document(foundReview.getOwnerUser()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    UserClass foundUser = documentSnapshot.toObject(UserClass.class);

                                    if(foundUser != null) {
                                        boolean isReviewOwner = false;

                                        if(mainAuth.getCurrentUser() != null) {
                                            isReviewOwner = user.getUid().equals(foundReview.getOwnerUser());
                                        }

                                        // ?????? ???????????? ????????? ?????? ????????? mData??? ??????
                                        seatItemData.add(new seatList_item(documentID, foundUser.getNick(), foundUser.getImagepath(), foundReview.getImagepath(),
                                                foundReview.getRating(), foundReview.getReviewText(), isReviewOwner, foundReview.getTimestamp()));

                                        countReview += 1;

                                        countRating += foundReview.getRating();

                                        //????????? ?????? ?????? ?????? ?????? ??????
                                        Collections.sort(seatItemData);
                                        Collections.reverse(seatItemData);

                                        listView.setAdapter(seatAdapter);
                                        seatAdapter.notifyDataSetChanged();

                                        cntReview.setText("?????? ???: " + countReview);
                                        avgRating.setRating(countRating / countReview);
                                        avgText.setText(new DecimalFormat("##.#").format(countRating / countReview));
                                    }
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
                // ??????
                Intent intent = new Intent(seatPage.this, EditReview.class);
                intent.putExtra("documentID", seatItemData.get(position).getDocumentID());
                intent.putExtra("imagepath", seatItemData.get(position).getReview_image());
                intent.putExtra("seatNum", seatNumber);
                intent.putExtra("rating", seatItemData.get(position).getRatingbar());
                intent.putExtra("reviewContext", seatItemData.get(position).getReview_writing());
                startActivityForResult(intent, 1234);
                return;
            case 200:
                // ??????
                showConfirmMsg(position);
                return;
            case 300:
                // ????????????
                if(mainAuth.getCurrentUser() == null) {
                    Toast.makeText(seatPage.this, "????????? ??? ???????????????.", Toast.LENGTH_LONG).show();
                }
                else
                    reportDialog(view, seatItemData.get(position).getDocumentID());
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
        builder.setMessage("????????? ????????????????????????? ????????? ????????? ????????? ??? ????????????.");

        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final String reviewImagePath = seatItemData.get(position).getReview_image();

                // DB?????? ?????? ????????? ??????
                db.collection("SeatReview").document(seatItemData.get(position).getDocumentID())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // ??????????????? ?????? ???????????? Storage?????? ?????? ??????
                        if(reviewImagePath != null) {
                            StorageReference reviewPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(reviewImagePath);
                            reviewPhotoRef.delete();
                        }

                        Toast.makeText(seatPage.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();

                        // ??????????????? ?????????????????? ?????? ????????? ??????
                        seatItemData.remove(position);
                        // ????????? ????????????
                        loadReviewData();
                    }
                });

            }
        });
        builder.setNegativeButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("????????? ????????? ??? ???????????? ??? ????????????. ????????? ???????????? ?????????????????????????");

        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void reportDialog(View view, final String reviewID) {
        final View dialogView = getLayoutInflater().inflate(R.layout.report_dialog,null);

        final AlertDialog.Builder report = new AlertDialog.Builder(view.getContext(), android.R.style.Theme_DeviceDefault_DayNight);

        report.setTitle("????????????");
        report.setMessage("?????? ????????? ???????????????.");
        report.setView(dialogView);

        report.setPositiveButton("????????????", null);

        report.setNegativeButton("??????", null);


        final AlertDialog alertDialog = report.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)dialogView.findViewById(R.id.report);

                String value = input.getText().toString();

                final Boolean[] closeDialog = {false};

                // ????????? ?????? ?????? ??????
                ConnectivityManager conManager = (ConnectivityManager) seatPage.this.getSystemService(CONNECTIVITY_SERVICE);
                if(conManager.getActiveNetworkInfo() == null) {
                    Toast.makeText(getApplicationContext(),"????????? ????????? ?????? ??????????????????.",Toast.LENGTH_LONG).show();
                }
                if(value.length()<10||value.length()>200) {
                    Toast.makeText(seatPage.this, "?????? ????????? ??????????????????.(10??? ??????, 200??? ??????)", Toast.LENGTH_LONG).show();
                }
                else if(value.length()>= 10) {
                    //?????? ?????? ???????????? ?????????
                    db.collection("UserReport").add(new UserReport(user.getEmail(), reviewID, value, null, false))
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(seatPage.this, "????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                                    alertDialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(seatPage.this, "????????? ??????????????????. ?????? ??? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

    }

    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.no_change,R.anim.translate_down);
    }


}
