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
        String data = intent.getStringExtra(SEARCH_WORD);   //검색어 전달받기

        searchText = findViewById(R.id.searchText);
        searchText.setText(data);   //검색어를 검색창에 넣어두기
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

        // 1. API를 이용해 공연시설명과 ID 목록 획득
        theaterList = thr.getResult();

        // API 검색 결과가 없을 경우
        if(theaterList.size() == 0) {
            progressBar.setVisibility(View.INVISIBLE);
            noResultLayout.setVisibility(View.VISIBLE);
        }
        else { // 있을 경우
            finalList = new ArrayList<HallDetailedClass>();

            // 2. DB를 검색하여 공연시설명+공연장명의 리스트 반환
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
                                        // arrayList의 내용과 각 문서의 내용을 finalList에 추가
                                        finalList.add(new HallDetailedClass(document.getId(),
                                                currentItem.getTheater_name().split("\\(구")[0] + " " + document.getString("hallName"),
                                                currentItem.getTheater_ID() + "-" + document.getString("hallCode"),
                                                document.getString("hallImage"), document.getBoolean("isSeatplan")));
                                    }
                                } else {
                                    Log.d(TAG, "Error reading DB", task.getException());
                                }

                                count++;

                                // theaterList의 모든 항목에 대해 DB 읽기를 마친 경우,
                                if (count == theaterSize) {
                                    searchAdapter = new SearchAdapter(SearchActivity.this, finalList);
                                    gridView.setAdapter(searchAdapter);

                                    if(finalList.size() == 0) {
                                        // DB 검색 결과가 없을 때
                                        progressBar.setVisibility(View.INVISIBLE);
                                        noResultLayout.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        // 프로그래스바를 끄고 검색결과 보여주기
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
           // API를 이용해 공연시설명과 ID 목록 획득
            arrayList = SearchAPI.GetResult_Theater(word);
        }

        public ArrayList<TheaterClass> getResult() {
            return arrayList;
        }
    }


    //검색 버튼 눌렀을 때
    public void onSearchButtonClicked(View v) {
        // 인터넷 연결 확인 먼저
        ConnectivityManager conManager = (ConnectivityManager) SearchActivity.this.getSystemService(CONNECTIVITY_SERVICE);
        if(conManager.getActiveNetworkInfo() == null) {
            Toast.makeText(getApplicationContext(),"인터넷 연결을 확인해주세요.",Toast.LENGTH_LONG).show();
        }
        else {
            String data = searchText.getText().toString();

            progressBar.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.INVISIBLE);
            noResultLayout.setVisibility(View.INVISIBLE);

            initUI(data);
        }
    }

    //엔터키 눌렀을 때 = 검색 버튼 눌렀을 때
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.searchText && actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearchButtonClicked(v);
        }
        return false;
    }


    //로고 버튼 작동
    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    //뒤로가기 버튼 작동
    public void onBackPressed() {
        if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
    }

    //네비게이션 작동
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