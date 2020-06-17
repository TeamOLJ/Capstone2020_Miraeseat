package com.capstondesign.miraeseat.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.FragmentManager;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.MainActivity;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.search.SearchFragment;

public class SearchActivity extends AppCompatActivity implements TextView.OnEditorActionListener {
    final String SEARCH_WORD = "search_word";
    DrawerHandler drawer;
    EditText searchText;
    FragmentManager fm;
    SearchFragment sf;
    Bundle bundle;
    InputMethodManager imm;

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

        fm = getSupportFragmentManager();
        sf = new SearchFragment();
        bundle = new Bundle();
        bundle.putString(SEARCH_WORD, data);
        sf.setArguments(bundle);    //fragment에 bundle(검색 key, 검색 value) 넘겨주기
        fm.beginTransaction().add(R.id.searchFragment, sf).commit();    //초기 검색 결과창

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        /*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, SearchFragment.newInstance())
                    .commitNow();
        }
         */
    }

    //검색 버튼 눌렀을 때
    public void onSearchButtonClicked(View v) {
        imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);

        String data = searchText.getText().toString();
        sf = new SearchFragment();
        bundle = new Bundle();
        bundle.putString(SEARCH_WORD, data);
        sf.setArguments(bundle);
        fm.beginTransaction().replace(R.id.searchFragment, sf).commitNow();
    }

    //엔터키 눌렀을 때 = 검색 버튼 눌렀을 때
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v.getId() == R.id.searchText && actionId == EditorInfo.IME_ACTION_SEARCH) {
            onSearchButtonClicked(v);
        }
        return false;
    }

    public void onLogoButtonClicked(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }

    public void onBackPressed() {
        if (drawer.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawer.drawerLayout.closeDrawer(Gravity.RIGHT);
        } else {
            super.onBackPressed();
        }
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
}