package com.capstondesign.miraeseat.hall;

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
import com.capstondesign.miraeseat.search.HallClass;
import com.capstondesign.miraeseat.search.PlayClass;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class HallInfo extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    Button btnSeat;

    ListView listView;
    HallAdapter hallAdapter;

    //ArrayList<HallList_item> mData;
    ArrayList<PlayClass> mData;


    DrawerHandler drawer;

    HallClass hall;

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

        Intent intent = getIntent();
        String id = intent.getStringExtra("hall id");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Thread_hall_details thd = new Thread_hall_details(id);
            thd.run();
            thd.join();

            hall = thd.getHall();

            Thread_plays tp = new Thread_plays(hall.getName());
            tp.run();
            tp.join();

            mData = tp.getPalys();

        } catch (InterruptedException e) {
            Log.d("Error(HallInfo):", e.toString());
        }


        TextView titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText(hall.getName());
        titleText.setSelected(true);

        btnSeat = (Button) findViewById(R.id.btnSeat);

        listView = (ListView) findViewById(R.id.hall_playlist);

        hallAdapter = new HallAdapter(HallInfo.this, mData);
        listView.setAdapter(hallAdapter);


        btnSeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TheaterActivity.class);
                intent.putExtra("hall name", hall.getName());
                startActivity(intent);
            }
        });
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

    public void onMapReady(final GoogleMap googleMap) {

        map = googleMap;

        //공연장 API에서 위도,경도,공연장이름 불러와서 맵에 띄우기

        LatLng Point = new LatLng(hall.getLatitude(), hall.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(Point);
        markerOptions.title(hall.getName());
        //markerOptions.snippet("한국의 수도");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(Point));
        map.animateCamera(CameraUpdateFactory.zoomTo(12));
    }

    class Thread_hall_details extends Thread {
        String ID;
        HallClass Hall;

        public Thread_hall_details(String id) {
            ID = id;
        }

        @Override
        public void run() {
            Hall = HallInfoAPI.GetDetails_Hall(ID);
        }

        public HallClass getHall() {
            return Hall;
        }
    }

    class Thread_plays extends Thread {
        String Name;
        ArrayList<PlayClass> Plays;

        public Thread_plays(String name) {
            Name = name;
        }

        @Override
        public void run() {
            Plays = HallInfoAPI.Get_Play(Name);
        }

        public ArrayList<PlayClass> getPalys() {
            return Plays;
        }
    }
}
