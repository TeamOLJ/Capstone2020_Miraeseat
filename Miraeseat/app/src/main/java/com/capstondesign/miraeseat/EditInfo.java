package com.capstondesign.miraeseat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditInfo extends AppCompatActivity {

    static final int MY_PERMISSION_CAMERA = 1111;
    static final int REQUEST_TAKE_PHOTO = 2222;
    static final int REQUEST_TAKE_ALBUM = 3333;
    static final int REQUEST_IMAGE_CROP = 4444;
    String mCurrentPhotoPath;
    Uri imageURI;
    Uri photoURI, albumURI;
    CircleImageView edit_photo;

    ImageButton btnCancel;
    ImageButton btnSave;

    Button btnCheckNick;

    private TextInputLayout inputLayoutID;
    private TextInputLayout inputLayoutNickname;
    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutPwd;
    private TextInputLayout inputLayoutCheckPwd;

    private Boolean isNickChecked = false;

    private Boolean isNickValid = false;
    private Boolean isPwdValid = false;

    private Boolean isPwdChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        edit_photo = (CircleImageView) findViewById(R.id.edit_photo);

        btnCancel = (ImageButton)findViewById(R.id.edit_cancel);
        btnSave = (ImageButton)findViewById(R.id.edit_save);

        btnCheckNick = (Button)findViewById(R.id.btnCheckNick);

        inputLayoutID = (TextInputLayout) findViewById(R.id.layoutID);
        inputLayoutNickname = (TextInputLayout) findViewById(R.id.layoutNickname);
        inputLayoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        inputLayoutPwd = (TextInputLayout) findViewById(R.id.layoutPwd);
        inputLayoutCheckPwd = (TextInputLayout) findViewById(R.id.layoutCheckPwd);

        final EditText edtID = inputLayoutID.getEditText();
        final EditText edtNickname = inputLayoutNickname.getEditText();
        final EditText edtEmail = inputLayoutEmail.getEditText();
        final EditText edtPwd = inputLayoutPwd.getEditText();
        final EditText edtCheckPwd = inputLayoutCheckPwd.getEditText();

        //이름 닉네임 이메일 데이터 불러오기


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
                    inputLayoutNickname.setError("2~7글자의 한글, 영문, 숫자를 사용할 수 있습니다.");
                }
                else {
                    isNickValid = true;
                    inputLayoutNickname.setError(null);
                    inputLayoutNickname.setErrorEnabled(false);
                }
            }
        });

        edtPwd.addTextChangedListener(new TextWatcher() {
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
                    inputLayoutPwd.setError("8~20자의 영문 대소문자, 숫자, 특수문자를 사용할 수 있습니다.");
                }
                else {
                    isPwdValid = true;
                    inputLayoutPwd.setError(null);
                    inputLayoutPwd.setErrorEnabled(false);
                }

                // 비밀번호 확인란의 오류문구 설정
                // 비밀번호 확인란이 비어있지 않은 상태에서 비밀번호!=비밀번호확인 일 때 오류문구 띄우기
                if(edtCheckPwd.getText().toString().length() > 0 && !edtCheckPwd.getText().toString().equals(edtPwd.getText().toString())) {
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
                String givenPwd = edtPwd.getText().toString();
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

        btnCheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 닉네임이 DB에 이미 존재하는지 확인

                // if 존재하지 않으면 (사용 가능한 닉네임이면):

                isNickChecked = true;
                // inputLayoutNick.setError(null);
                // inputLayoutNick.setErrorEnabled(false);
                // 입력칸 하단에 "사용 가능한 닉네임입니다!" 출력
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

                if (edtNickname.getText().toString().getBytes().length <= 0) {
                    inputLayoutNickname.setError("닉네임을 입력하세요.");
                    edtNickname.requestFocus();
                } else if (edtPwd.getText().toString().getBytes().length <= 0) {
                    inputLayoutPwd.setError("비밀번호를 입력하세요.");
                    edtPwd.requestFocus();
                } else if (edtCheckPwd.getText().toString().getBytes().length <= 0) {
                    inputLayoutCheckPwd.setError("비밀번호를 한 번 더 입력해주세요.");
                    edtCheckPwd.requestFocus();
                } else if (!isNickChecked) {
                    inputLayoutNickname.setError("닉네임 중복 확인을 해주세요.");
                } else {

                     //수정된 정보를 DB에 저장
                    Toast.makeText(getApplicationContext(), "회원정보가 수정되었습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });


    }


//프로필 사진 눌렀을 때 메뉴
    private void makeDialog(){


        AlertDialog.Builder alt_bld = new AlertDialog.Builder(EditInfo.this);
        alt_bld.setTitle("프로필 변경").setCancelable(false).setPositiveButton("사진촬영",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 사진촬영 선택");
                        //사진촬영
                        getCamera();
                    }
                }).setNeutralButton("앨범선택",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        Log.v("알림", "다이얼로그 > 앨범선택 선택");
                        //앨범에서 선택
                        getAlbum();
                    }
                }).setNegativeButton("취소   ",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Log.v("알림", "다이얼로그 > 취소 선택");
                        // 취소 클릭. dialog 닫기.
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
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures");

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

                        edit_photo.setImageURI(imageURI);
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
                }
                break;
        }
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
