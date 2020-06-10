package com.capstondesign.miraeseat;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.capstondesign.miraeseat.notice.NoticeListPage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DrawerHandler implements NavigationView.OnNavigationItemSelectedListener {
    private Activity activity;
    public Toolbar toolbar;
    public DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public final int toolbar_image = R.drawable.ic_dehaze_black_24dp;

    LoginCheckHandler LCH;

    // Firebase
    private FirebaseAuth mainAuth;
    private FirebaseUser currentUser;

    public DrawerHandler(Activity activity) {
        this.activity = activity;
        toolbar = activity.findViewById(R.id.toolbar);
        drawerLayout = activity.findViewById(R.id.drawer_layout);
        navigationView = activity.findViewById(R.id.nav_view);

        mainAuth = FirebaseAuth.getInstance();
        currentUser = mainAuth.getCurrentUser();
    }

    public void init() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        LCH = new LoginCheckHandler(drawerLayout, navigationView, activity); //drawerLayout을 activity에 생성할 때마다 로그인 정보를 갱신하기 위해 LoginCheckHandler 사용
        LCH.loginCheck();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_notice) {
            Intent intent = new Intent(activity, NoticeListPage.class);
            activity.startActivity(intent);
        } else if (id == R.id.menu_use) {
            if(currentUser == null) {
                Toast.makeText(activity,"로그인을 먼저 해주세요.",Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(activity, WriteReview.class);
                activity.startActivity(intent);
            }
        } else if (id == R.id.menu_settings) {

        }

        drawerLayout.closeDrawer(Gravity.RIGHT);
        return true;
    }

}
