package com.capstondesign.miraeseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class Image extends AppCompatActivity {

    ImageView img;
    String imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Intent intent = getIntent();
        imagepath = intent.getStringExtra("imagepath");

        img = (ImageView)findViewById(R.id.Img);

        Glide.with(this).load(imagepath).into(img);

    }
}
