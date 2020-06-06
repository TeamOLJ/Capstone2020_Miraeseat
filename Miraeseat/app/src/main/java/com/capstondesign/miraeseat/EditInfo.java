package com.capstondesign.miraeseat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditInfo extends AppCompatActivity {
    private static final String TAG = "EditInfo";

    static final int MY_PERMISSION_CAMERA = 1111;
    static final int REQUEST_TAKE_PHOTO = 2222;
    static final int REQUEST_TAKE_ALBUM = 3333;
    static final int REQUEST_IMAGE_CROP = 4444;

    String mCurrentPhotoPath;
    Uri imageURI;
    Uri photoURI, albumURI, finalURI;
    String currentPhoto;

    int selectedPhotoMenu;

    // Firebase Instance variables
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    CircleImageView edit_photo;

    ImageButton btnCancel;
    ImageButton btnSave;

    Button btnCheckNick;

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutNickname;
    private TextInputLayout inputLayoutCurrentPwd;
    private TextInputLayout inputLayoutNewPwd;
    private TextInputLayout inputLayoutCheckPwd;

    private Boolean isNickChecked = true;
    private Boolean isNickValid = true;
    private Boolean isPwdValid = false;
    private Boolean isPwdChecked = false;

    String userUID;
    String userEmail;
    String prevNick;

    Bitmap originalBitmap;
    Bitmap rotatedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        edit_photo = (CircleImageView) findViewById(R.id.edit_photo);

        btnCancel = (ImageButton)findViewById(R.id.edit_cancel);
        btnSave = (ImageButton)findViewById(R.id.edit_save);

        btnCheckNick = (Button)findViewById(R.id.btnCheckNick);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        inputLayoutNickname = (TextInputLayout) findViewById(R.id.layoutNickname);
        inputLayoutCurrentPwd = (TextInputLayout)findViewById(R.id.layoutCurrentPwd);
        inputLayoutNewPwd = (TextInputLayout) findViewById(R.id.layoutNewPwd);
        inputLayoutCheckPwd = (TextInputLayout) findViewById(R.id.layoutCheckPwd);

        final EditText edtEmail = inputLayoutEmail.getEditText();
        final EditText edtNickname = inputLayoutNickname.getEditText();
        final EditText edtCurrentPwd = inputLayoutCurrentPwd.getEditText();
        final EditText edtNewPwd = inputLayoutNewPwd.getEditText();
        final EditText edtCheckPwd = inputLayoutCheckPwd.getEditText();

        // Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("user_review_photo");

        //닉네임 이메일 사진 데이터 불러오기
        db.collection("UserInfo").document(userUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserClass loginedUser = documentSnapshot.toObject(UserClass.class);
                userEmail = loginedUser.getEmail();
                edtEmail.setText(loginedUser.getEmail());
                Log.d(TAG, "loginedUser.getEmail():"+loginedUser.getEmail());
                prevNick = loginedUser.getNick();
                edtNickname.setText(prevNick);
                if(loginedUser.getImagepath() != null) {
                    // edit_photo 에 loginedUser.getImagepath()의 이미지 보이게
                    Glide.with(getApplicationContext()).load(loginedUser.getImagepath()).into(edit_photo);
                    currentPhoto = loginedUser.getImagepath();
                }
                else {
                }
                finalURI = null;
            }
        });

        //닉네임 설정
        edtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isNickChecked = false;
                if(!Pattern.matches("[+ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]{2,7}", s.toString())) {
                    isNickValid = false;
                    inputLayoutNickname.setErrorTextAppearance(R.style.InputError_Red);
                    inputLayoutNickname.setError("2~7글자의 한글, 영문, 숫자를 사용할 수 있습니다.");
                }
                else {
                    isNickValid = true;
                    inputLayoutNickname.setError(null);
                    inputLayoutNickname.setErrorEnabled(false);
                }
            }
        });

        edtNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isPwdChecked = false;

                if(!Pattern.matches("[+a-zA-Z0-9[`~!@#$%^&*()-_=,./<>?;:\"'{}\\[\\][+[|]]]]{8,20}", s.toString())) {
                    isPwdValid = false;
                    inputLayoutNewPwd.setError("8~20자의 영문 대소문자, 숫자, 특수문자를 사용할 수 있습니다.");
                }
                else {
                    isPwdValid = true;
                    inputLayoutNewPwd.setError(null);
                    inputLayoutNewPwd.setErrorEnabled(false);
                }

                // 비밀번호 확인란의 오류문구 설정
                // 비밀번호 확인란이 비어있지 않은 상태에서 비밀번호!=비밀번호확인 일 때 오류문구 띄우기
                if(edtCheckPwd.getText().toString().length() > 0 && !edtCheckPwd.getText().toString().equals(edtNewPwd.getText().toString())) {
                    isPwdChecked = false;
                    inputLayoutCheckPwd.setError("비밀번호를 확인하세요.");
                }
            }
        });

        edtCheckPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String givenPwd = edtNewPwd.getText().toString();
                String givenPwdCheck = edtCheckPwd.getText().toString();
                // 비밀번호 확인란의 입력값과 비밀번호란의 입력값이 같지 않으면
                if(!givenPwdCheck.equals(givenPwd)) {
                    isPwdChecked = false;
                    inputLayoutCheckPwd.setError("비밀번호를 확인하세요.");
                }
                else {
                    isPwdChecked = true;
                    inputLayoutCheckPwd.setError(null);
                    inputLayoutCheckPwd.setErrorEnabled(false);
                }
            }
        });

        // 닉네임 중복여부 확인
        btnCheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNickValid) {
                    Query existingNicks = db.collection("UserInfo").whereEqualTo("nick", edtNickname.getText().toString());

                    existingNicks.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot.isEmpty()) {
                                    // 쿼리가 리턴하는 값이 없는 경우 = 중복된 닉네임이 아닌 경우
                                    isNickChecked = true;
                                    inputLayoutNickname.setErrorTextAppearance(R.style.InputError_Green);
                                    inputLayoutNickname.setError("사용 가능한 닉네임입니다!");
                                } else {
                                    // 리턴값이 있는 경우 = 사용 중인 닉네임인 경우
                                    isNickChecked = false;
                                    inputLayoutNickname.setErrorTextAppearance(R.style.InputError_Red);
                                    inputLayoutNickname.setError("이미 사용 중인 닉네임입니다.");
                                }
                            }
                        }
                    });
                }
            }
        });

        //취소 버튼
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //프사 변경
        edit_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDialog();
            }
        });


        //저장 버튼
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 닉네임에 수정사항이 있는 경우
                if (!prevNick.equals(edtNickname.getText().toString())) {
                    Log.d(TAG, "prevNick: " + prevNick);
                    Log.d(TAG, "edtNickname.getText().toString(): " + edtNickname.getText().toString());
                    if (edtNickname.getText().toString().getBytes().length <= 0) {
                        inputLayoutNickname.setError("닉네임을 입력하세요.");
                        edtNickname.requestFocus();
                    } else if (!isNickChecked) {
                        inputLayoutNickname.setError("닉네임 중복 확인을 해주세요.");
                    }
                }

                // 비밀번호를 변경하는 경우
                if (edtCurrentPwd.getText().toString().getBytes().length > 0 || edtNewPwd.getText().toString().getBytes().length > 0 || edtCheckPwd.getText().toString().getBytes().length > 0) {
                    if (edtCurrentPwd.getText().toString().getBytes().length <= 0) {
                        inputLayoutCurrentPwd.setError("현재 비밀번호를 입력하세요.");
                        edtCurrentPwd.requestFocus();
                    } else if (edtNewPwd.getText().toString().getBytes().length <= 0) {
                        inputLayoutNewPwd.setError("변경할 비밀번호를 입력하세요.");
                        edtNewPwd.requestFocus();
                    } else if (edtCheckPwd.getText().toString().getBytes().length <= 0) {
                        inputLayoutCheckPwd.setError("비밀번호를 한 번 더 입력해주세요.");
                        edtCheckPwd.requestFocus();
                    }
                    // 유효성 검사
                    if (isNickValid && isNickChecked && isPwdValid && isPwdChecked) {
                        // 비밀번호 변경 루틴
                        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, edtCurrentPwd.getText().toString());
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "User re-authenticated.");
                                    user.updatePassword(edtNewPwd.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated.");
                                            }
                                        }
                                    });
                                    if(finalURI != null) {
                                        // 사진에 변경사항이 있다면
                                        // 사진을 가리키는 참조 생성 (user_review_photo/<파일이름>)
                                        final StorageReference photoRef = storageRef.child(finalURI.getLastPathSegment());
                                        // Storage에 사진 업로드
                                        photoRef.putFile(finalURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                            @Override
                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                if (!task.isSuccessful()) {
                                                    throw task.getException();
                                                }

                                                // Continue with the task to get the download URL
                                                return photoRef.getDownloadUrl();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Uri> task) {
                                                if (task.isSuccessful()) {
                                                    // 업로드한 사진을 가리키는 주소
                                                    Uri downloadUri = task.getResult();

                                                    // 이후 전체 회원정보(변경사항) DB에 반영
                                                    UserClass modifyUser = new UserClass(edtEmail.getText().toString(), edtNickname.getText().toString(), downloadUri.toString());
                                                    updateDB(modifyUser);
                                                } else {
                                                    // Handle failures
                                                    // ...
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        // 사진은 바꾸지 않는다면
                                        UserClass modifyUser = new UserClass(edtEmail.getText().toString(), edtNickname.getText().toString(), currentPhoto);
                                        updateDB(modifyUser);
                                    }
                                } else {
                                    Log.w(TAG, "User failed to re-authenticate.", task.getException());
                                    Toast.makeText(getApplicationContext(), "회원 인증에 실패했습니다. 비밀번호를 다시 확인하세요.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                else { //비밀번호는 변경하지 않는 경우
                    if (prevNick.equals(edtNickname.getText().toString()) || (isNickValid && isNickChecked)) {
                        if(finalURI != null) {
                            // 사진에 변경사항이 있다면
                            // finalURI를 DStorage에 저장
                            // 사진을 가리키는 참조 생성 (user_review_photo/<파일이름>)
                            final StorageReference photoRef = storageRef.child(finalURI.getLastPathSegment());
                            // Storage에 사진 업로드
                            photoRef.putFile(finalURI).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return photoRef.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();

                                        // 이후 전체 회원정보(변경사항) DB에 반영
                                        UserClass modifyUser = new UserClass(edtEmail.getText().toString(), edtNickname.getText().toString(), downloadUri.toString());
                                        updateDB(modifyUser);
                                    } else {
                                        // Handle failures
                                        // ...
                                    }
                                }
                            });
                        }
                        else {
                            // 사진은 바꾸지 않는다면
                            UserClass modifyUser = new UserClass(edtEmail.getText().toString(), edtNickname.getText().toString(), currentPhoto);
                            updateDB(modifyUser);
                        }
                    }
                }
            }
        });
    }

    // 데이터베이스 업데이트 함수
    private void updateDB(final UserClass user) {
        db.collection("UserInfo").document(userUID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Modified Info successfully written to DB.");
                        SaveSharedPreference.setUserNickName(getApplicationContext(), user.getNick());
                        Toast.makeText(getApplicationContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_LONG).show();
                        setResult(1);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error modifying document(DB)", e);
                    }
                });
    }


    //프로필 사진 눌렀을 때 메뉴
    private void makeDialog(){

        // default:
        selectedPhotoMenu = 0;

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(EditInfo.this);
        alt_bld.setTitle("프로필 변경").setCancelable(false);

        alt_bld.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                selectedPhotoMenu = whichButton;
            }
        });

        alt_bld.setPositiveButton("선택", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(selectedPhotoMenu == 0 ) {
                    // 사진촬영
                    Log.v("알림", "다이얼로그 > 사진촬영 선택");
                    getCamera();
                } else if(selectedPhotoMenu == 1) {
                    // 앨범에서 선택
                    Log.v("알림", "다이얼로그 > 앨범선택 선택");
                    getAlbum();
                }
            }
        });

        alt_bld.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.v("알림", "다이얼로그 > 취소 선택");
                dialog.cancel();
            }
        });

        checkPermission();

        if(checkPermission()==1) {
            AlertDialog alert = alt_bld.create();
            alert.show();
        }
    }

    //사진촬영 함수
    private void getCamera() {

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    Uri providerUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageURI = providerUri;

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerUri);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } else {
                Toast.makeText(this, "접근 불가능 합니다", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    //이미지저장 함수
    private File createImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + ".jpg";
        File imageFile = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "Miraeseat");

        if(!storageDir.exists()){
            storageDir.mkdirs();
        }
        imageFile = new File(storageDir, imageFileName);
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    //사진촬영,앨범 권한
    private int checkPermission() {
       // if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if((ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                    (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)))
            {
                new android.app.AlertDialog.Builder(this).setTitle("알림").setMessage("저장소 권한이 거부되었습니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                })
                        .setPositiveButton("확인", null).setCancelable(false).create().show();

                return 0;
            }
            else{

                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);

                return 1;
            }


       /* }
        else
        {
            return 1;
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try{
                        Log.i("REQUEST_TAKE_PHOTO","OK!!!!!!");
                        galleryAddPic();

                        finalURI = imageURI;

                        // imageURI를 exif 정보에 따라 회전처리 한 후 edit_photo에 set 해줘야 함
                        originalBitmap = decodeSampledBitmapFromResource(new File(mCurrentPhotoPath), edit_photo.getWidth(), edit_photo.getHeight());

                        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                        switch(orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(originalBitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(originalBitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(originalBitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                rotatedBitmap = originalBitmap;
                        }

                        edit_photo.setImageBitmap(rotatedBitmap);

                    }catch(Exception e){
                        Log.e("REQUEST_TAKE_PHOTO",e.toString());
                    }
                }else{
//                    Toast.makeText(EditInfo.this,"저장공간에 접근할 수 없는 기기 입니다.",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try {
                            File albumFile = null;
                            albumFile = createImageFile();
                            photoURI = data.getData();
                            albumURI = Uri.fromFile(albumFile);
                            cropImage();
                        } catch (IOException e) {
                            Log.e("TAKE_ALBUM_SINLE_ERROR",e.toString());
                        }
                    }
                }
                break;
            case REQUEST_IMAGE_CROP:
                if(resultCode == Activity.RESULT_OK){
                    galleryAddPic();
                    //사진 변환 error
                    edit_photo.setImageURI(albumURI);
                    finalURI = albumURI;
                }
                break;
        }
    }

    // 사진 회전 함수
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public static Bitmap decodeSampledBitmapFromResource(File res, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res.getAbsolutePath(),options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(res.getAbsolutePath(),options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //앨범에서 사진 가져오기
    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }


    //사진 crop할 수 있도록 하는 함수
    public void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);
        cropIntent.putExtra("output",albumURI);
        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

// 갤러리에 사진 추가 함수
    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);
        Uri contentURI = Uri.fromFile(file);
        mediaScanIntent.setData(contentURI);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"앨범에 저장되었습니다.",Toast.LENGTH_SHORT).show();
    }

 //permission에 대한 승인 완료확인 코드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0){
            if(grantResults[0]==0){
                Toast.makeText(this,"카메라 권한 승인완료",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"카메라 권한 승인 거절",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
