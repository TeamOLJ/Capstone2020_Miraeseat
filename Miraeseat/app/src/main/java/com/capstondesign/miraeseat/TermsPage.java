package com.capstondesign.miraeseat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TermsPage extends AppCompatActivity {

    FirebaseFirestore db;

    DrawerHandler drawer;

    TextView titleText;
    TextView TCText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_page);
        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("서비스 이용약관");

        db = FirebaseFirestore.getInstance();

        TCText = (TextView)findViewById(R.id.termsText);

        // 데이터베이스에서 약관을 읽어와 텍스트에 설정
        db.collection("TermsDB").document("TermsDocument").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // 파이어베이스는 \n 등의 특수표기문자를 지원하지 않으므로, "\\n"로 표기해둔 부분을 모두 newline 기호로 치환하는 식으로 새 줄 구성
                        String TC = documentSnapshot.getString("TermCondition").replace("\\\\n", "\n");
                        TCText.setText(TC);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "약관 정보를 읽어오는 데에 실패했습니다. 인터넷 연결을 확인해주세요.", Toast.LENGTH_LONG).show();
                    }
                });

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

}
