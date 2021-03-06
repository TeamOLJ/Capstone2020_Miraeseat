package com.capstondesign.miraeseat.hall;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.MainActivity;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.TheaterActivity;
import com.capstondesign.miraeseat.search.HallLocation;
import com.capstondesign.miraeseat.search.PlayClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class HallInfo extends AppCompatActivity implements OnMapReadyCallback {
    static final String TAG = "HallInfo";

    private GoogleMap map;

    FirebaseFirestore db;

    Button btnSeat;

    ListView listView;
    HallAdapter hallAdapter;

    //ArrayList<HallList_item> mData;
    ArrayList<PlayClass> mData;

    DrawerHandler drawer;

    HallLocation hall_locate;

    String seatplanImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hall_info);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        drawer = new DrawerHandler(this);
        drawer.init();

        setSupportActionBar(drawer.toolbar);
        ViewCompat.setLayoutDirection(drawer.toolbar, ViewCompat.LAYOUT_DIRECTION_RTL);
        getSupportActionBar().setHomeAsUpIndicator(drawer.toolbar_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        final String documentID = intent.getStringExtra("document_ID");
        final String combinedID = intent.getStringExtra("Combined_ID");
        final String combinedName = intent.getStringExtra("Combined_Name");
        final boolean isSeatplan = intent.getBooleanExtra("is_Seatplan", false);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            // API ?????? - ????????????ID??? ???????????? ??????, ?????? ?????? ??????
            Thread_hall_details thd = new Thread_hall_details(combinedID.split("-")[0]);
            thd.run();
            thd.join();

            hall_locate = thd.getHallLocate();

            // API ?????? - ????????????+?????????ID??? ???????????? ???????????? ?????? ?????? ?????? ??????
            Thread_plays tp = new Thread_plays(combinedID);
            tp.run();
            tp.join();

            mData = tp.getPalys();

        } catch (InterruptedException e) {
            Log.d("Error(HallInfo):", e.toString());
        }

        TextView titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText(combinedName);
        titleText.setSelected(true);

        btnSeat = (Button) findViewById(R.id.btnSeat);

        listView = (ListView) findViewById(R.id.hall_playlist);

        hallAdapter = new HallAdapter(HallInfo.this, mData);

        View footer = getLayoutInflater().inflate(R.layout.hall_item_footer, null, false);
        listView.addFooterView(footer);
        listView.setAdapter(hallAdapter);


        btnSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isSeatplan) {
                    // ?????????????????? ?????? ??????
                    db.collection("SeatPlanInfo").document(combinedID).get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    seatplanImage = documentSnapshot.getString("seatplan");

                                    Intent intent = new Intent(getApplicationContext(), TheaterActivity.class);
                                    intent.putExtra("combined_name", combinedName);
                                    intent.putExtra("combined_id", combinedID);
                                    intent.putExtra("seatImage", seatplanImage);
                                    startActivity(intent);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Error getting documents: ", e);
                                }
                            });
                }
                else {
                    // ?????????????????? ?????? ??????
                    showNotSupported();
                }
            }
        });

        //???????????? 1 ??????
        db.collection("TheaterInfo").document(documentID).update("searchedNum", FieldValue.increment(1));

    }


    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.drawerLayout.openDrawer(Gravity.RIGHT);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            finish();
        }
    }

    public void showNotSupported() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SORRY! :(");
        builder.setMessage("?????? ???????????? ?????? ??????????????????. ??????????????? ??????????????????.");

        builder.setPositiveButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;

        //????????? API?????? ??????,??????,??????????????? ???????????? ?????? ?????????
        LatLng Point = new LatLng(hall_locate.getLatitude(), hall_locate.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Point);
        markerOptions.title(hall_locate.getTheater_name());
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(Point));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    class Thread_hall_details extends Thread {
        String theater_ID;
        HallLocation hall_location;

        public Thread_hall_details(String theater_id) {
            theater_ID = theater_id;
        }

        @Override
        public void run() {
            hall_location = HallInfoAPI.GetDetails_Hall(theater_ID);
        }

        public HallLocation getHallLocate() {
            return hall_location;
        }
    }

    class Thread_plays extends Thread {
        String combined_ID;
        ArrayList<PlayClass> Plays;

        public Thread_plays(String combined_id) {
            combined_ID = combined_id;
        }

        @Override
        public void run() {
            Plays = HallInfoAPI.Get_Play(combined_ID);
        }

        public ArrayList<PlayClass> getPalys() {
            return Plays;
        }
    }
}
