package com.capstondesign.miraeseat.search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.TheaterActivity;
import com.capstondesign.miraeseat.seatPage;

public class temp extends AppCompatActivity {

    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        btn = (Button)findViewById(R.id.btnTemp);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), seatPage.class);

                startActivityForResult(intent,1);

                overridePendingTransition(R.anim.translate_up,R.anim.no_change);

            }
        });
    }
}
