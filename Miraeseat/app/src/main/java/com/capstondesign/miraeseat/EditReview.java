package com.capstondesign.miraeseat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditReview extends AppCompatActivity {
    private static final String TAG = "EditReview";

    static final int MY_PERMISSION_CAMERA = 1111;
    static final int REQUEST_TAKE_PHOTO = 2222;
    static final int REQUEST_TAKE_ALBUM = 3333;
    static final int REQUEST_IMAGE_CROP = 4444;

    String mCurrentPhotoPath;
    Uri imageURI;
    Uri photoURI, albumURI;
    Uri finalURI = null;
    Boolean album;

    int selectedPhotoMenu;

    // Firebase Instance variables
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    String userUID;
    String documentID;
    String serverImagePath;

    TextView textNoPhoto;
    ImageView image;
    RatingBar ratingBar;

    TextView textSeatName;

    TextInputLayout inputlayoutReview;
    EditText edtReview;

    Button btnSave;
    Button btnCancel;

    float ratedBefore;
    float newRating;
    String imageBefore;
    String newReview;

    boolean isContextChanged = false;
    boolean isPhotoChanged = false;

    DrawerHandler drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);

        drawer = new DrawerHandler(this);
        setSupportActionBar(drawer.toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);

        textNoPhoto = (TextView) findViewById(R.id.textNoPhoto);
        textNoPhoto.setVisibility(View.GONE);

        image = (ImageView) findViewById(R.id.edit_photo);
        ratingBar = (RatingBar) findViewById(R.id.write_rating);

        inputlayoutReview = (TextInputLayout)findViewById(R.id.textlayoutReview);
        edtReview = inputlayoutReview.getEditText();
        edtReview.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        // Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        userUID = user.getUid();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference().child("user_upload_image/"+userUID+"/review_photo");

        // ?????? ?????? ????????? ????????? ????????????
        Intent intent = getIntent();
        documentID = intent.getStringExtra("documentID");

        ratedBefore = intent.getFloatExtra("rating", 0);
        ratingBar.setRating(ratedBefore);
        edtReview.setText(intent.getStringExtra("reviewContext").replace("\\\\n", "\n"));

        serverImagePath = intent.getStringExtra("imagepath");
        imageBefore = serverImagePath;
        if(imageBefore != null)
            Glide.with(EditReview.this).load(imageBefore).into(image);
        else
            textNoPhoto.setVisibility(View.VISIBLE);

        textSeatName = (TextView)findViewById(R.id.textSeatName);
        textSeatName.setText(intent.getStringExtra("seatNum"));

        image.setOnClickListener(clickImageListener);

        edtReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                isContextChanged = true;
            }
        });

        //????????????
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isContextChanged)
                    showEndMsg();
                else
                    finish();
            }
        });

        //????????????
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

            newRating = ratingBar.getRating();
            newReview = edtReview.getText().toString();

            if(newRating != ratedBefore)
                isContextChanged = true;

            // ????????? ?????? ?????? ??????
            ConnectivityManager conManager = (ConnectivityManager) EditReview.this.getSystemService(CONNECTIVITY_SERVICE);
            if(conManager.getActiveNetworkInfo() == null) {
                reset();
                clickImageListener.reset();
                Toast.makeText(getApplicationContext(),"????????? ????????? ?????? ??????????????????.",Toast.LENGTH_LONG).show();
            } else if(!isContextChanged) {
                reset();
                clickImageListener.reset();
                Toast.makeText(getApplicationContext(),"?????? ????????? ????????????.",Toast.LENGTH_LONG).show();
            } else if (newReview.replace("\n", "").getBytes().length <= 10) {
                reset();
                clickImageListener.reset();
                Toast.makeText(getApplicationContext(), "????????? 10?????? ?????? ??????????????? ?????????.", Toast.LENGTH_LONG).show();
            }  else if (ratingBar.getRating() == 0) {
                    reset();
                    clickImageListener.reset();
                    Toast.makeText(getApplicationContext(),"????????? ?????? ?????????.",Toast.LENGTH_LONG).show();
            } else {
                // ????????? ?????? DB??? ????????????
                if(user != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditReview.this);
                    //builder.setTitle(null);

                    if (image.getDrawable()==null)
                        builder.setMessage("?????? ????????? ???????????? ???????????????. ?????? ?????? ????????? ?????????????????????????");
                    else
                        builder.setMessage("????????? ???????????????. ?????????????????????????");

                    builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            uploadImageStorage();
                        }
                    });
                    builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clickSaveListener.reset();
                            clickImageListener.reset();
                        }
                    });

                    builder.setCancelable(true);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }
    };

    private void uploadImageStorage() {
        // ????????? ?????? ???????????? ?????? ??????
        if(isPhotoChanged && finalURI != null) {
            final StorageReference photoRef = storageRef.child(finalURI.getLastPathSegment());

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
                        // ??????????????? ?????? ???????????? Storage?????? ?????? ??????
                        if(serverImagePath != null) {
                            StorageReference oldPhotoRef = storage.getReferenceFromUrl(serverImagePath);
                            oldPhotoRef.delete();
                        }

                        Uri downloadUri = task.getResult();
                        // ?????? ??? ?????? ????????? ???????????? ???????????? DB ??????????????? ???????????? ???, ????????? ??? DB ???????????? ??????????????? ???????????? ??????
                        finalURI = null;
                        isPhotoChanged = false;
                        imageBefore = downloadUri.toString();
                        updateDB(imageBefore);
                    } else {
                        // Handle failures
                        clickSaveListener.reset();
                        clickImageListener.reset();
                        Toast.makeText(getApplicationContext(), "????????? ??????????????????. ?????? ??? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        // ????????? ????????? ??????
        else if(isPhotoChanged && finalURI == null) {
            // ????????? ????????? ?????? ?????? Storage?????? ?????? ??????
            if(serverImagePath != null) {
                StorageReference oldPhotoRef = storage.getReferenceFromUrl(serverImagePath);
                oldPhotoRef.delete();
            }
            updateDB(imageBefore);
        }
        else {
            // ????????? ???????????? ?????? ??????
            updateDB(imageBefore);
        }
    }

    private void updateDB(String imagepath) {
        db.collection("SeatReview").document(documentID)
                .update("imagepath", imagepath,
                        "rating", newRating,
                        "reviewText", newReview.replace("\n", "\\\\n"))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "????????? ?????? ???????????????.", Toast.LENGTH_LONG).show();
                        setResult(1);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document(DB)", e);
                        clickImageListener.reset();
                        clickSaveListener.reset();
                        Toast.makeText(getApplicationContext(), "????????? ??????????????????. ?????? ??? ?????? ??????????????????.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void makeDialog(){

        // default:
        selectedPhotoMenu = 0;

        AlertDialog.Builder alt_bld = new AlertDialog.Builder(EditReview.this);
        alt_bld.setTitle("?????? ????????????").setCancelable(false);

        // ????????? ????????? ... ????????? ...
        if(image.getDrawable()==null) {
            alt_bld.setSingleChoiceItems(R.array.array_photo, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    selectedPhotoMenu = whichButton;
                }
            });

            alt_bld.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    clickImageListener.reset();

                    if (selectedPhotoMenu == 0) {
                        // ????????????
                        getCamera();
                    } else if (selectedPhotoMenu == 1) {
                        // ???????????? ??????
                        getAlbum();
                    }
                }
            });
        }
        else {
            alt_bld.setSingleChoiceItems(R.array.array_photo_ex, 0, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    selectedPhotoMenu = whichButton;
                }
            });

            alt_bld.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    clickImageListener.reset();

                    if (selectedPhotoMenu == 0) {
                        // ????????????
                        getCamera();
                    } else if (selectedPhotoMenu == 1) {
                        // ???????????? ??????
                        getAlbum();
                    } else if (selectedPhotoMenu == 2) {
                        image.setImageDrawable(null);
                        textNoPhoto.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

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
                    photoURI = FileProvider.getUriForFile(EditReview.this, getPackageName(), photoFile);
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
        if((ActivityCompat.shouldShowRequestPermissionRationale(EditReview.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))||
                (ActivityCompat.shouldShowRequestPermissionRationale(EditReview.this,Manifest.permission.CAMERA)))
        {
            new android.app.AlertDialog.Builder(EditReview.this).setTitle("??????").setMessage("????????? ????????? ?????????????????????.")
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

            ActivityCompat.requestPermissions(EditReview.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, MY_PERMISSION_CAMERA);

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

                        textNoPhoto.setVisibility(View.GONE);

                    }catch(Exception e){
                        Log.e("REQUEST_TAKE_PHOTO",e.toString());
                    }
                }else{
//                    Toast.makeText(EditInfo.this,"??????????????? ????????? ??? ?????? ?????? ?????????.",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_TAKE_ALBUM:
                if(resultCode == Activity.RESULT_OK){
                    if(data.getData() != null){
                        try {
                            album =true;
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
                    //?????? ?????? error
                    image.setImageURI(albumURI);
                    finalURI = albumURI;
                    textNoPhoto.setVisibility(View.GONE);
                    isContextChanged = true;
                    isPhotoChanged = true;
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
        Toast.makeText(EditReview.this,"????????? ?????????????????????.",Toast.LENGTH_SHORT).show();
    }

    //permission??? ?????? ?????? ???????????? ??????
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==0){
            if(grantResults[0]==0){
                Toast.makeText(EditReview.this,"????????? ?????? ????????????",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(EditReview.this,"????????? ?????? ?????? ??????",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showEndMsg()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditReview.this);
        builder.setMessage("?????? ????????? ?????????????????????????");

        builder.setPositiveButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("??????", null);

        builder.setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        showEndMsg();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}
