package com.capstondesign.miraeseat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class UnsubscribePage extends AppCompatActivity {
    private static final String TAG = "UnsubscribePage";

    FirebaseUser user;
    FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    TextView titleText;

    TextInputLayout textLayoutPwd;
    EditText edtUnsubPwd;

    Button btnUnsubscribe;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unsubscribe_page);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        titleText = (TextView) findViewById(R.id.titleText);
        titleText.setText("회원 탈퇴");

        textLayoutPwd = (TextInputLayout)findViewById(R.id.unsubscribePwd);
        btnUnsubscribe = (Button)findViewById(R.id.btnUnsubscribe);

        edtUnsubPwd = textLayoutPwd.getEditText();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        edtUnsubPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textLayoutPwd.setError(null);
                textLayoutPwd.setErrorEnabled(false);
            }
        });

        btnUnsubscribe.setOnClickListener(clickUnsubListener);
    }

    OnOneOffClickListener clickUnsubListener = new OnOneOffClickListener() {
        @Override
        public void onOneClick(View v) {
            String givenPwd = edtUnsubPwd.getText().toString();

            if(givenPwd.getBytes().length <= 0)
            {
                textLayoutPwd.setError("비밀번호를 입력하세요.");
            }
            else
            {
                user = FirebaseAuth.getInstance().getCurrentUser();
                final String userEmail = user.getEmail();
                final String userUID = user.getUid();

                // Get auth credentials from the user for re-authentication.
                AuthCredential credential = EmailAuthProvider.getCredential(userEmail, givenPwd);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "User account deleted.");
                                        SaveSharedPreference.setIsAutoLogin(getApplicationContext(), false);
                                        // 회원 기타 정보 삭제
                                        db.collection("UserInfo").document(userUID).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "User information successfully deleted from DB.");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error deleting document(DB)", e);
                                                    }
                                                });

                                        // 회원의 리뷰 전체 삭제
                                        db.collection("SeatReview").whereEqualTo("ownerUser", userUID).get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            List<String> docIdlist = new ArrayList<>();
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                docIdlist.add(document.getId());
                                                            }
                                                            Log.d(TAG, docIdlist.toString());
                                                            updateFirestore((ArrayList) docIdlist);
                                                        } else {
                                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });

                                        // 회원이 업로드한 이미지 전체 삭제
                                        // storage.getReference().child("user_upload_image/"+userUID).delete();
                                        // 저장소 폴더를 단번에 삭제할 수 있는 방법은 없나?

                                        // 메인화면으로 돌아감
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        // 열려있던 모든 액티비티를 닫고 지정된 액티비티(메인)만 열도록
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                    }
                                    else {
                                        // Log.w(TAG, "User account is not deleted.", task.getException());
                                        reset();
                                        Toast.makeText(getApplicationContext(), "오류가 발생했습니다. 잠시 후 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        else {
                            reset();
                            textLayoutPwd.setError("잘못 된 입력입니다. 비밀번호를 확인하세요.");
                        }
                    }
                });
            }
        }
    };

    void updateFirestore(ArrayList list) {

        // Get a new write batch
        WriteBatch batch = db.batch();

        // Iterate through the list
        for (int k = 0; k < list.size(); k++) {

            // Update each list item
            DocumentReference ref = db.collection("SeatReview").document(list.get(k).toString());
            batch.delete(ref);

        }

        // Commit the batch
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Yay its all done in one go!
            }
        });
    }

    // 뒤로가기 버튼(홈버튼)을 누르면 창이 꺼지는 메소드
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