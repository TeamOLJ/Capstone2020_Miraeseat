package com.capstondesign.miraeseat.find;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.capstondesign.miraeseat.DrawerHandler;
import com.capstondesign.miraeseat.R;
import com.google.android.material.tabs.TabLayout;

public class FindInfoPage extends AppCompatActivity {

    TextView titleText;

    FindIDFragment findIDFragment;
    FindPwdFragment findPwdFragment;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_info_page);

        // 호출한 인텐트(LoginPage)로부터 요청페이지가 아이디 찾기인지 비밀번호 찾기인지 판별
        Intent intent = getIntent();
        int selectedTab = intent.getIntExtra("selectedTab", 0);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);

        findIDFragment = new FindIDFragment();
        findPwdFragment = new FindPwdFragment();

        TabLayout tabs = findViewById(R.id.tabFindInfo);

        if(selectedTab == 0) {
            titleText.setText("아이디 찾기");
            // 지정된 인덱스의 탭을 선택해 보여주는 코드
            tabs.getTabAt(0).select();
            // frameContainer 이라는 프레임레이아웃 안의 프래그먼트를 findIDFragment로 대체
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, findIDFragment).commit();
        }

        if(selectedTab == 1) {
            titleText.setText("비밀번호 찾기");
            tabs.getTabAt(1).select();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer,findPwdFragment).commit();
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                // Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = findIDFragment;
                    titleText.setText("아이디 찾기");
                } else if (position == 1) {
                    selected = findPwdFragment;
                    titleText.setText("비밀번호 찾기");
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

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
