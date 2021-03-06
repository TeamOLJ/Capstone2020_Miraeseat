package com.capstondesign.miraeseat.search;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.MainActivity;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.hall.HallInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    static final String TAG = "SearchActivity";

    final String SEARCH_WORD = "search_word";

    FirebaseFirestore db;

    DrawerHandler drawer;

    ProgressBar progressBar;
    RelativeLayout noResultLayout;

    EditText searchText;
    GridView gridView;
    SearchAdapter searchAdapter;

    ArrayList<TheaterClass> theaterList;
    ArrayList<HallDetailedClass> finalList;

    int theaterSize;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        drawer = new DrawerHandler(this);
        drawer.init();

        setSupportActionBar(drawer.toolbar);
        ViewCompat.setLayoutDirection(drawer.toolbar, ViewCompat.LAYOUT_DIRECTION_RTL);

        getSupportActionBar().setHomeAsUpIndicator(drawer.toolbar_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent intent = getIntent();
        String data = intent.getStringExtra(SEARCH_WORD);   //????????? ????????????

        searchText = findViewById(R.id.searchText);
        searchText.setText(data);   //???????????? ???????????? ????????????
        searchText.setOnEditorActionListener(this);

        progressBar = (ProgressBar)findViewById(R.id.searchHallProgr);
        progressBar.setVisibility(View.VISIBLE);

        gridView = (GridView) findViewById(R.id.searched_grid);
        gridView.setVisibility(View.INVISIBLE);

        noResultLayout = (RelativeLayout)findViewById(R.id.no_search_layout);
        noResultLayout.setVisibility(View.INVISIBLE);

        initUI(data);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HallDetailedClass item = searchAdapter.getItem(position);

                String documentID = item.getDocumentID();
                String combinedID = item.getCombined_ID();
                String combinedName = item.getCombined_name();
                boolean isSeatplan = item.getIsSeatplan();

                Intent intent = new Intent(SearchActivity.this, HallInfo.class);
                intent.putExtra("document_ID", documentID);
                intent.putExtra("Combined_ID", combinedID);
                intent.putExtra("Combined_Name", combinedName);
                intent.putExtra("is_Seatplan", isSeatplan);
                startActivity(intent);
            }
        });
    }

    public void initUI(String word) {

        db = FirebaseFirestore.getInstance();

        Thread_hall_result thr = new Thread_hall_result(word);

        try {
            thr.start();
            thr.join();
        } catch (InterruptedException e) {
            Log.d("Error(SearchFragment):", e.toString());
        }

        // 1. API??? ????????? ?????????????????? ID ?????? ??????
        theaterList = thr.getResult();

        // API ?????? ????????? ?????? ??????
        if(theaterList.size() == 0) {
            progressBar.setVisibility(View.INVISIBLE);
            noResultLayout.setVisibility(View.VISIBLE);
        }
        else { // ?????? ??????
            finalList = new ArrayList<HallDetailedClass>();

            // 2. DB??? ???????????? ???????????????+??????????????? ????????? ??????
            theaterSize = theaterList.size();
            count = 0;

            for (int i = 0; i < theaterSize; i++) {

                final TheaterClass currentItem = theaterList.get(i);

                db.collection("TheaterInfo").whereEqualTo("theaterCode", currentItem.getTheater_ID()).orderBy("hallCode").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    QuerySnapshot querySnapshot = task.getResult();

                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        // arrayList??? ????????? ??? ????????? ????????? finalList??? ??????
                                        finalList.add(new HallDetailedClass(document.getId(),
                                                currentItem.getTheater_name().split("\\(???")[0] + " " + document.getString("hallName"),
                                                currentItem.getTheater_ID() + "-" + document.getString("hallCode"),
                                                document.getString("theaterImage"), document.getBoolean("isSeatplan")));
                                    }
                                } else {
                                    Log.d(TAG, "Error reading DB", task.getException());
                                }

                                count++;

                                // theaterList??? ?????? ????????? ?????? DB ????????? ?????? ??????,
                                if (count == theaterSize) {
                                    searchAdapter = new SearchAdapter(SearchActivity.this, finalList);
                                    gridView.setAdapter(searchAdapter);

                                    if(finalList.size() == 0) {
                                        // DB ?????? ????????? ?????? ???
                                        progressBar.setVisibility(View.INVISIBLE);
                                        noResultLayout.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        // ????????????????????? ?????? ???????????? ????????????
                                        progressBar.setVisibility(View.INVISIBLE);
                                        gridView.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        });
            }
        }

    }

    class Thread_hall_result extends Thread {
        ArrayList<TheaterClass> arrayList;
        String word;

        public Thread_hall_result(String word) {
            this.word = word;
            arrayList = new ArrayList<TheaterClass>();
        }

        @Override
        public void run() {
           // API??? ????????? ?????????????????? ID ?????? ??????
            arrayList = SearchAPI.GetResult_Theater(word);
        }

        public ArrayList<TheaterClass> getResult() {
            return arrayList;
        }
    }


    //?????? ?????? ????????? ???
    public void onSearchButtonClicked(View v) {
        // ????????? ?????? ?????? ??????
        ConnectivityManager conManager = (ConnectivityManager) SearchActivity.this.getSystemService(CONNECTIVITY_SERVICE);
        if(conManager.getActiveNetworkInfo() == null) {
            Toast.makeText(getApplicationContext(),"????????? ????????? ??????????????????.",Toast.LENGTH_LONG).show();
        }
        else {
            String data = searchText.getText().toString();

            progressBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.INVISIBLE);
            noResultLayout.setVisibility(View.INVISIBLE);

            initUI(data);
        }
    }

    //????????? ????????? ??? = ?????? ?????? ????????? ???
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.searchText && actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearchButtonClicked(v);
        }
        return false;
    }


    //?????? ?????? ??????
    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    //???????????? ?????? ??????
    public void onBackPressed() {
        if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }

    //??????????????? ??????
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
}