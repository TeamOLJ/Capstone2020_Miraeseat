package com.capstondesign.miraeseat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.capstondesign.miraeseat.BuildConfig;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import java.io.File;


public class SettingPage extends AppCompatActivity {
    private static final String TAG = "SettingPage";

    DrawerHandler drawer;
    TextView titleText;
    TextView versionText;


    Button btTheme;
    Button btnCash;
    Button btnTOS;

    String versionName = BuildConfig.VERSION_NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("설정");

        btTheme = (Button) findViewById(R.id.btTheme);
        btnCash = (Button) findViewById(R.id.btnCash);
        btnTOS = (Button) findViewById(R.id.btnTOS);


        btTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), themedialog.class);
                startActivity(intent);
            }
        });

        btnCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingPage.this);
                builder.setTitle("캐시 정리");
                builder.setMessage(R.string.cash_text);
                builder.setPositiveButton("지우기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearApplicationData(getApplicationContext());
                        Toast.makeText(getApplicationContext(), "캐시가 지워졌습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });

        btnTOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TermsPage.class);
                startActivity(intent);
            }
        });


        versionText = (TextView) findViewById(R.id.verNum);
        versionText.setText(versionName);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        if(cache != null && cache.isDirectory()){
            deleteDir(cache);
        }
//        File appDir = new File(cache.getParent());
//        if (appDir.exists()) {
//            String[] children = appDir.list();
//            for (String s : children) {
//                if(s.equals("lib") || s.equals("files")) continue;
//                deleteDir(new File(appDir, s));
//                Log.d("test", "File /data/data/" + context.getPackageName() + "/" + s + " DELETED");
//            }
//        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

}