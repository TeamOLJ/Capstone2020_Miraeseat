package com.capstondesign.miraeseat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SeatAdapter extends BaseAdapter {
    Context context;
    ArrayList<seatList_item> seatList_itemArrayList;
    LayoutInflater inflater;

    ImageView profile;
    TextView nickName;
    ImageView image;
    RatingBar ratingBar;
    TextView writing;

    Button btnLike;
    Button btnReport;

    public SeatAdapter(Context context, ArrayList<seatList_item> seatList_itemArrayList) {
        this.context = context;
        this.seatList_itemArrayList = seatList_itemArrayList;
    }

    @Override
    public int getCount() {
        return this.seatList_itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return seatList_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.seat_item, parent, false);

            profile = (ImageView)convertView.findViewById(R.id.re_profilePic);
            nickName = (TextView)convertView.findViewById(R.id.re_nickName);
            image = (ImageView)convertView.findViewById(R.id.re_Image);
            ratingBar = (RatingBar)convertView.findViewById(R.id.re_Ratingbar);
            writing = (TextView)convertView.findViewById(R.id.re_writing);

            btnLike = (Button)convertView.findViewById(R.id.btnLike);
            btnReport = (Button)convertView.findViewById(R.id.btnReport);

            profile.setImageResource(seatList_itemArrayList.get(position).getProfile_image());
            nickName.setText(seatList_itemArrayList.get(position).getNickname());
            image.setImageResource(seatList_itemArrayList.get(position).getReview_image());
            ratingBar.setRating(seatList_itemArrayList.get(position).getRatingbar());
            writing.setText(seatList_itemArrayList.get(position).getReview_writing());


            //이미지 클릭시 원본 이미지
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),Image.class);
                    context.startActivity(intent);
                }
            });

            //좋아요 버튼 클릭
            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //좋아요 1증가
                }
            });

            //신고 버튼 클릭
            btnReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final AlertDialog.Builder report = new AlertDialog.Builder(view.getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog);

                    report.setTitle("신고하기");
                    report.setMessage("신고 사유를 적어주세요.");

                    final EditText txt = new EditText(view.getContext());
                    report.setView(txt);
                    report.setCancelable(false);


                    report.setPositiveButton("신고하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                    report.setNegativeButton("취소하기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Log.v("선택","취소");
                            dialogInterface.cancel();
                        }
                    });

                    final AlertDialog alertDialog = report.create();

                    alertDialog.show();

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.v("선택","신고");

                            String value = txt.getText().toString();


                            Boolean closeDialog = false;

                            if(value.length()<10) {
                                Toast.makeText(context, "신고 내용을 입력해주세요.(10자 이상)", Toast.LENGTH_LONG).show();
                            }
                            else if(value.length()>= 10) {
                                //신고 정보 데이터로 넘기기
                                Toast.makeText(context, "신고가 접수되었습니다.", Toast.LENGTH_LONG).show();
                                closeDialog = true;
                            }

                            if(closeDialog)
                                alertDialog.dismiss();
                        }
                    });

                }
            });

        }

        return convertView;
    }
}
