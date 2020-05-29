package com.capstondesign.miraeseat;

import android.app.Activity;
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
            // FirebaseAuth.getInstance().signOut();
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