package com.capstondesign.miraeseat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
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
    Boolean album;

    int selectedPhotoMenu;

    // Firebase Instance variables
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    CircleImageView edit_photo;

    Button btnCancel;
    Button btnSave;

    Button btnCheckNick;

    private TextInputLayout inputLayoutEmail;
    private TextInputLayout inputLayoutNickname;
    private TextInputLayout inputLayoutCurrentPwd;
    private TextInputLayout inputLayoutNewPwd;
    private TextInputLayout inputLayoutCheckPwd;

    EditText edtEmail;
    EditText edtNickname;
    EditText edtCurrentPwd;
    EditText edtNewPwd;
    EditText edtCheckPwd;

    private Boolean isNickChecked = true;
    private Boolean isNickValid = true;
    private Boolean isPwdValid = false;
    private Boolean isPwdChecked = false;

    String userUID;
    String userEmail;
    String prevNick;
    String prevImagePath;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        edit_photo = (CircleImageView) findViewById(R.id.edit_photo);

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnSave = (Button)findViewById(R.id.btnSave);

        btnCheckNick = (Button)findViewById(R.id.btnCheckNick);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.layoutEmail);
        inputLayoutNickname = (TextInputLayout) findViewById(R.id.layoutNickname);
        inputLayoutCurrentPwd = (TextInputLayout)findViewById(R.id.layoutCurrentPwd);
        inputLayoutNewPwd = (TextInputLayout) findViewById(R.id.layoutNewPwd);
        inputLayoutCheckPwd = (TextInputLayout) findViewById(R.id.layoutCheckPwd);

        edtEmail = inputLayoutEmail.getEditText();
        edtNickname = inputLayoutNickname.getEditText();
        edtCurrentPwd = inputLayoutCurrentPwd.getEditText();
        edtNewPwd = inputLayoutNewPwd.getEditText();
        edtCheckPwd = inputLayoutCheckPwd.getEditText();

        // Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("user_upload_image/"+userUID+"/profile_picture");

        //????????? ????????? ?????? ????????? ????????????
        db.collection("UserInfo").document(userUID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserClass loginedUser = documentSnapshot.toObject(UserClass.class);
                userEmail = loginedUser.getEmail();
                edtEmail.setText(loginedUser.getEmail());
                prevNick = loginedUser.getNick();
                edtNickname.setText(prevNick);
                prevImagePath = loginedUser.getImagepath();
                if(loginedUser.getImagepath() != null) {
                    // edit_photo ??? loginedUser.getImagepath()??? ????????? ?????????
                    Glide.with(getApplicationContext()).load(prevImagePath).into(edit_photo);
                    currentPhoto = loginedUser.getImagepath();
                }
                else {
                }
                finalURI = null;
            }
        });

        //????????? ??????
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
                if(!Pattern.matches("[+???-??????-??????-???a-zA-Z0-9]{2,7}", s.toString())) {
                    isNickValid = false;
                    inputLayoutNickname.setErrorTextAppearance(R.style.InputError_Red);
                    inputLayoutNickname.setError("2~7????????? ??????, ??????, ????????? ????????? ??? ????????????.");
                }
                else {
                    isNickValid = true;
                    inputLayoutNickname.setError(null);
                    inputLayoutNickname.setErrorEnabled(false);
                }
            }
        });

        //???????????? ?????????
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
                    inputLayoutNewPwd.setError("8~20?????? ?????? ????????????, ??????, ??????????????? ????????? ??? ????????????.");
                }
                else {
                    isPwdValid = true;
                    inputLayoutNewPwd.setError(null);
                    inputLayoutNewPwd.setErrorEnabled(false);
                }

                // ???????????? ???????????? ???????????? ??????
                // ???????????? ???????????? ???????????? ?????? ???????????? ????????????!=?????????????????? ??? ??? ???????????? ?????????
                if(edtCheckPwd.getText().toString().length() > 0 && !edtCheckPwd.getText().toString().equals(edtNewPwd.getText().toString())) {
                    isPwdChecked = false;
                    inputLayoutCheckPwd.setError("??????????????? ???????????????.");
                }
            }
        });

        //?????? ???????????? ??????
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
                // ???????????? ???????????? ???????????? ?????????????????? ???????????? ?????? ?????????
                if(!givenPwdCheck.equals(givenPwd)) {
                    isPwdChecked = false;
                    inputLayoutCheckPwd.setError("??????????????? ???????????????.");
                }
                else {
                    isPwdChecked = true;
                    inputLayoutCheckPwd.setError(null);
                    inputLayoutCheckPwd.setErrorEnabled(false);
                }
            }
        });

        // ????????? ???????????? ??????
        btnCheckNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ????????? ?????? ??????
                ConnectivityManager conManager = (ConnectivityManager) EditInfo.this.getSystemService(CONNECTIVITY_SERVICE);
                if(conManager.getActiveNetworkInfo() == null) {
                    Toast.makeText(getApplicationContext(),"????????? ????????? ?????? ??????????????????.",Toast.LENGTH_LONG).show();
                }
                else if (isNickValid) {
                    String newNick = edtNickname.getText().toString();
                    Query existingNicks = db.collection("UserInfo").whereEqualTo("nick", newNick);

                    existingNicks.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                QuerySnapshot querySnapshot = task.getResult();
                                if (querySnapshot.isEmpty()) {
                                    // ????????? ???????????? ?????? ?????? ?????? = ????????? ???????????? ?????? ??????
                                    isNickChecked = true;
                                    inputLayoutNickname.setErrorTextAppearance(R.style.InputError_Green);
                                    inputLayoutNickname.setError("?????? ????????? ??????????????????!");
                                } else {
                                    // ???????????? ?????? ?????? = ?????? ?????? ???????????? ??????
                                    isNickChecked = false;
                                    inputLayoutNickname.setErrorTextAppearance(R.style.InputError_Red);
                                    inputLayoutNickname.setError("?????? ?????? ?????? ??????????????????.");
                                }
                            }
                        }
                    });
                }
            }
        });

        //?????? ??????
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //?????? ??????
        edit_photo.setOnClickListener(clickImageListener);

        //?????? ??????
        btnSave.setOnClickListener(clickSaveListener);
    }

    OnOneOffClickListener clickImageListener = new OnOneOffClickListener() {
        @Override
        public void onOneClick(View v) {
            makeDialog();
        }
    };

    OnOneOffClickListener clickSaveListener = new OnOneOffClickListener() {
        @Override
        public void onOneClick(View v) {
            clickImageListener.disable();

            final String newUserNick = edtNickname.getText().toString();
            final String currentPassword = edtCurrentPwd.getText().toString();
            final String newPassword = edtNewPwd.getText().toString();
            String newPasswordCheck = edtCheckPwd.getText().toString();

            // ???????????? ??????????????? ?????? ??????
            if (!prevNick.equals(newUserNick)) {
                if (newUserNick.getBytes().length <= 0) {
                    reset();
                    clickImageListener.reset();
                    inputLayoutNickname.setError("???????????? ???????????????.");
                    edtNickname.requestFocus();
                } else if (!isNickChecked) {
                    reset();
                    clickImageListener.reset();
                    inputLayoutNickname.setError("????????? ?????? ????????? ????????????.");
                }
            }

            // ????????? ?????? ?????? ??????
            ConnectivityManager conManager = (ConnectivityManager) EditInfo.this.getSystemService(CONNECTIVITY_SERVICE);
            if(conManager.getActiveNetworkInfo() == null) {
                reset();
                clickImageListener.reset();
                Toast.makeText(getApplicationContext(),"????????? ????????? ?????? ??????????????????.",Toast.LENGTH_LONG).show();
            }
            // ??????????????? ???????????? ??????
            else if (currentPassword.getBytes().length > 0 || newPassword.getBytes().length > 0 || newPasswordCheck.getBytes().length > 0) {
                if (currentPassword.getBytes().length <= 0) {
                    reset();
                    clickImageListener.reset();
                    inputLayoutCurrentPwd.setError("?????? ??????????????? ???????????????.");
                    edtCurrentPwd.requestFocus();
                } else if (newPassword.getBytes().length <= 0) {
                    reset();
                    clickImageListener.reset();
                    inputLayoutNewPwd.setError("????????? ??????????????? ???????????????.");
                    edtNewPwd.requestFocus();
                } else if (newPasswordCheck.getBytes().length <= 0) {
                    reset();
                    clickImageListener.reset();
                    inputLayoutCheckPwd.setError("??????????????? ??? ??? ??? ??????????????????.");
                    edtCheckPwd.requestFocus();
                }
                // ????????? ??????
                if (isNickValid && isNickChecked && isPwdValid && isPwdChecked) {
                    // ???????????? ?????? ??????
                    AuthCredential credential = EmailAuthProvider.getCredential(userEmail, currentPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                        }
                                    }
                                });
                                if(finalURI != null) {
                                    // ????????? ??????????????? ?????????
                                    // ????????? ???????????? ?????? ?????? (user_profile_picture/<????????????>)
                                    final StorageReference photoRef = storageRef.child(finalURI.getLastPathSegment());
                                    // Storage??? ?????? ?????????
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
                                                // Storage?????? ?????? ?????? ??????
                                                if(prevImagePath != null) {
                                                    StorageReference oldPhotoRef = storage.getReferenceFromUrl(prevImagePath);
                                                    oldPhotoRef.delete();
                                                }

                                                // ???????????? ????????? ???????????? ??????
                                                Uri downloadUri = task.getResult();
                                                finalURI = null;
                                                currentPhoto = downloadUri.toString();

                                                // ?????? ?????? ????????????(????????????) DB??? ??????
                                                UserClass modifyUser = new UserClass(edtEmail.getText().toString(), newUserNick, downloadUri.toString());
                                                updateDB(modifyUser);
                                            } else {
                                                // Handle failures
                                                reset();
                                                clickImageListener.reset();
                                                Toast.makeText(getApplicationContext(), "????????? ??????????????????. ?????? ??? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                                else {
                                    // ????????? ????????? ????????????
                                    UserClass modifyUser = new UserClass(edtEmail.getText().toString(), newUserNick, currentPhoto);
                                    updateDB(modifyUser);
                                }
                            } else {
                                Log.w(TAG, "User failed to re-authenticate.", task.getException());
                                Toast.makeText(getApplicationContext(), "?????? ????????? ??????????????????. ??????????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
            else { //??????????????? ???????????? ?????? ??????
                if (prevNick.equals(newUserNick) || (isNickValid && isNickChecked)) {
                    if(finalURI != null) {
                        // ????????? ??????????????? ?????????
                        // finalURI??? DStorage??? ??????
                        // ????????? ???????????? ?????? ?????? (user_profile_picture/<????????????>)
                        final StorageReference photoRef = storageRef.child(finalURI.getLastPathSegment());
                        // Storage??? ?????? ?????????
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
                                    // Storage?????? ?????? ?????? ??????
                                    if(prevImagePath != null) {
                                        StorageReference oldPhotoRef = storage.getReferenceFromUrl(prevImagePath);
                                        oldPhotoRef.delete();
                                    }

                                    Uri downloadUri = task.getResult();
                                    finalURI = null;
                                    currentPhoto = downloadUri.toString();

                                    // ?????? ?????? ????????????(????????????) DB??? ??????
                                    UserClass modifyUser = new UserClass(edtEmail.getText().toString(), newUserNick, downloadUri.toString());
                                    updateDB(modifyUser);
                                } else {
                                    // Handle failures
                                    reset();
                                    clickImageListener.reset();
                                    Toast.makeText(getApplicationContext(), "????????? ??????????????????. ?????? ??? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                    else {
                        // ????????? ????????? ????????????
                        UserClass modifyUser = new UserClass(edtEmail.getText().toString(), newUserNick, currentPhoto);
                        updateDB(modifyUser);
                    }
                }
            }
        }
    };

    // ?????????????????? ???????????? ??????
    private void updateDB(final UserClass user) {
        db.collection("UserInfo").document(userUID).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SaveSharedPreference.setUserNickName(getApplicationContext(), user.getNick());
                        SaveSharedPreference.setProfileImage(getApplicationContext(), user.getImagepath());
                        Toast.makeText(getApplicationContext(), "??????????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                        setResult(1);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        clickSaveListener.reset();
                        clickImageListener.reset();
                        Toast.makeText(getApplicationContext(), "????????? ??????????????????. ?????? ??? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //????????? ?????? ????????? ??? ??????
    private void makeDialog(){

        selectedPhotoMenu = 0;

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(EditInfo.this);
        alt_bld.setTitle("????????? ??????").setCancelable(false);

        alt_bld.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                selectedPhotoMenu = whichButton;
            }
        });


        alt_bld.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                clickImageListener.reset();

                if(selectedPhotoMenu == 0 ) {
                    // ????????????
                    getCamera();
                } else if(selectedPhotoMenu == 1) {
                    // ???????????? ??????
                    getAlbum();
                }
            }
        });

        alt_bld.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clickImageListener.reset();
                dialog.cancel();
            }
        });

        checkPermission();

        if(checkPermission()==1) {
            AlertDialog alert = alt_bld.create();
            alert.show();
        }
    }

    //???????????? ??????
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
                    photoURI = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    imageURI = photoURI;

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            } else {
                Toast.makeText(this, "?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    //??????????????? ??????
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

    //????????????,?????? ??????
    private int checkPermission() {
        if((ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)))
        {
            new android.app.AlertDialog.Builder(this).setTitle("??????").setMessage("????????? ????????? ?????????????????????.")
                    .setNeutralButton("??????", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            })
                    .setPositiveButton("??????", null).setCancelable(false).create().show();

            return 0;
        }
        else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);
            return 1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_TAKE_PHOTO:
                if(resultCode == Activity.RESULT_OK){
                    try{
                        album = false; //false????????? :????????????
                        cropImage();

                        finalURI = imageURI;
                        // Firestore?????? ???????????? ????????? ??? ???????????? ????????? ??????????????? exif ????????? ????????? ????????? ?????????
                    }catch(Exception e){
                        Log.e("REQUEST_TAKE_PHOTO",e.toString());
                    }
                }else{
                    Toast.makeText(EditInfo.this,"??????????????? ????????? ??? ?????? ?????? ?????????.",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                album = true;
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try {
                            album = true;
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

                    edit_photo.setImageURI(albumURI);
                    finalURI = albumURI;
                }
                break;
        }
    }

    // ?????? ?????? ??????
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

    //???????????? ?????? ????????????
    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_TAKE_ALBUM);
    }


    //?????? crop??? ??? ????????? ?????? ??????
    public void cropImage() throws IOException {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);

        if(album==false){
            File albumFile = null;
            albumFile = createImageFile();
            albumURI = Uri.fromFile(albumFile);
            cropIntent.putExtra("output",albumURI);
        }
        else if(album==true)
        {
            cropIntent.putExtra("output",albumURI);
        }

        startActivityForResult(cropIntent, REQUEST_IMAGE_CROP);
    }

// ???????????? ?????? ?????? ??????
    private void galleryAddPic(){
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(mCurrentPhotoPath);

        Uri contentURI = Uri.fromFile(file);
        mediaScanIntent.setData(contentURI);
        sendBroadcast(mediaScanIntent);
        Toast.makeText(this,"????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
    }

 //permission??? ?????? ?????? ???????????? ??????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0){
            if(grantResults[0]==0){
                Toast.makeText(this,"????????? ?????? ?????? ??????",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"????????? ?????? ?????? ??????",Toast.LENGTH_SHORT).show();
            }
        }
    }

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
