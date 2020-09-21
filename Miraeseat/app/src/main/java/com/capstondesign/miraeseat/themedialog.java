package com.capstondesign.miraeseat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.capstondesign.miraeseat.Theme.ThemeUtil;

public class themedialog extends AppCompatActivity {

    RadioGroup group;
    RadioButton darktheme,lighttheme,defaulttheme;

    String themeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_themedialog);

        DisplayMetrics dm =getApplicationContext().getResources().getDisplayMetrics();
        getWindow().getAttributes().width =(int) (dm.widthPixels * 0.7);
        getWindow().getAttributes().height =(int) (dm.heightPixels * 0.18);


        group = (RadioGroup) findViewById(R.id.themegroup);
        darktheme = (RadioButton) findViewById(R.id.darktheme);
        lighttheme = (RadioButton)findViewById(R.id.lighttheme);
        defaulttheme = (RadioButton)findViewById(R.id.defaulttheme);

        themeName = SaveSharedPreference.getTheme(getApplicationContext());
        if(themeName==null) themeName="default";
        if(themeName.equalsIgnoreCase("dark")){
            darktheme.setChecked(true);
        } else if(themeName.equalsIgnoreCase("light")){
            lighttheme.setChecked(true);
        }else {
            defaulttheme.setChecked(true);
        }

        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.darktheme){
                    ThemeUtil.applyTheme("dark");
                    SaveSharedPreference.setTheme(getApplicationContext(),"dark");
                    finish();

                }
                else if(checkedId==R.id.lighttheme){
                    ThemeUtil.applyTheme("light");
                    SaveSharedPreference.setTheme(getApplicationContext(),"light");
                    finish();
                }
                else if(checkedId==R.id.defaulttheme){
                    ThemeUtil.applyTheme("default");
                    SaveSharedPreference.setTheme(getApplicationContext(),"default");
                    finish();
                }
            }
        });

    }
}
