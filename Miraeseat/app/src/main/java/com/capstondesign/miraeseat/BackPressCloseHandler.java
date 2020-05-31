package com.capstondesign.miraeseat;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class BackPressCloseHandler {
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) { this.activity = context; }

    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000)
        {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        else
        {
            // 자동로그인 체크여부 확인하여 그에 따른 처리 진행
            if(SaveSharedPreference.getIsAutoLogin(activity.getApplicationContext())) {
                // 아무 것도 안 함
            }
            else {
                FirebaseAuth.getInstance().signOut();
                SaveSharedPreference.setUserNickName(activity.getApplicationContext(), "");
            }
            activity.finish();
            toast.cancel();
        }
    }

    private void showGuide()
    {
        toast = Toast.makeText(activity, "\'뒤로\'버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}