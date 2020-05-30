package com.capstondesign.miraeseat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.capstondesign.miraeseat.mypage.MyPage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


// dawerLayout의 로그인 정보를 변경하기 위함.
// FirebaseAuth, FirebaseUser 객체 이용
public class LoginCheckHandler {
    private Context ctx;
    private NavigationView root;
    private View nav_header;
    private TextView login_id;
    private ImageView profile_image;
    private Button nav_left, nav_right;

    // Firebase
    private FirebaseAuth mainAuth;
    private FirebaseUser currentUser;

    public LoginCheckHandler(NavigationView navigationView, Context context) {
        root = navigationView;
        ctx = context;
        //prefId = SaveSharedPreference.getUserName(context);

        mainAuth = FirebaseAuth.getInstance();
        currentUser = mainAuth.getCurrentUser();

        nav_header = root.getHeaderView(0);
        login_id = nav_header.findViewById(R.id.login_id);
        profile_image = nav_header.findViewById(R.id.profile_image);
        nav_left = nav_header.findViewById(R.id.nav_left);
        nav_right = nav_header.findViewById(R.id.nav_right);
    }

    public void loginCheck() {
        // currentUser가 null이 아닌 경우 (로그인 중인 경우)
        if (currentUser != null) {
            login_id.setText("닉네임 뜨는 곳");
            profile_image.setImageResource(R.drawable.logo_temp);

            nav_left.setText("마이페이지");
            nav_left.setOnClickListener(MyPage);

            nav_right.setText("로그아웃");
            nav_right.setOnClickListener(Logout);
        }
        else {
            login_id.setText(R.string.nav_login);
            profile_image.setImageResource(R.drawable.no_result);

            nav_left.setText("회원가입");
            nav_left.setOnClickListener(SignUp);

            nav_right.setText("로그인");
            nav_right.setOnClickListener(Login);
        }
    }

    private View.OnClickListener MyPage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ctx, com.capstondesign.miraeseat.mypage.MyPage.class);
            ctx.startActivity(intent);
        }
    };

    private View.OnClickListener SignUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ctx, SignUpPage.class);
            ctx.startActivity(intent);
        }
    };

    private View.OnClickListener Login = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ctx, LoginPage.class);
            ctx.startActivity(intent);
        }
    };

    private View.OnClickListener Logout = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

            builder.setMessage("로그아웃 하시겠습니까?");

            builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mainAuth.signOut();
                    SaveSharedPreference.setIsAutoLogin(ctx, false);
                    Toast.makeText(ctx, "로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);
                }
            });
            builder.setNegativeButton("취소", null);

            builder.setCancelable(true);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    };

}
