package com.capstondesign.miraeseat;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.capstondesign.miraeseat.BuildConfig;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;



public class SettingPage extends AppCompatActivity {
    private static final String TAG = "SettingPage";

    DrawerHandler drawer;
    TextView titleText;
    TextView versionText;


    Button btTheme;
    Button btnCash;

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


        btTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), themedialog.class);
                startActivity(intent);
            }
        });


        versionText = (TextView)findViewById(R.id.verNum);
        versionText.setText(versionName);


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
