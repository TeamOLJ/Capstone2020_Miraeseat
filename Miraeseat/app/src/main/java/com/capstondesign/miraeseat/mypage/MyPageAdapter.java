package com.capstondesign.miraeseat.mypage;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.EditReview;
import com.capstondesign.miraeseat.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyPageAdapter extends BaseAdapter implements View.OnClickListener {

    Context mContext;
    ArrayList<mypageList_item> infoList;
    LayoutInflater inflater;

    TextView info;
    RatingBar ratingBar;
    ImageView image;
    TextView content;
    TextView date;
    ImageButton btnMenu;

    mypageList_item oneListItem;
    int selectedPosition;

    // Firebase Instance variables
    private FirebaseFirestore db;

    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의
    public interface ListBtnClickListener {
        void onListBtnClick(View view, int position);
    }

    private ListBtnClickListener listBtnClickListener = null;


    public MyPageAdapter(Context context, ArrayList<mypageList_item> list_itemArrayList, ListBtnClickListener clickListener) {
        this.mContext = context;
        this.infoList = list_itemArrayList;
        this.listBtnClickListener = clickListener;
    }

    @Override
    public int getCount() {
        return this.infoList.size();
    }

    @Override
    public Object getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.mypage_item, parent,false);

            oneListItem = infoList.get(position);

            info = (TextView)convertView.findViewById(R.id.listInfo);
            ratingBar = (RatingBar)convertView.findViewById(R.id.listRatingbar);
            image = (ImageView)convertView.findViewById(R.id.listImage);
            content = (TextView)convertView.findViewById(R.id.listWriting);
            date = (TextView)convertView.findViewById(R.id.listDate);

            info.setText(oneListItem.getTheaterName()+" "+oneListItem.getSeatNum());
            ratingBar.setRating(oneListItem.getSeatRating());
            content.setText(oneListItem.getReviewContext());
            date.setText(oneListItem.getReviewDate());

            Glide.with(mContext).load(oneListItem.getImagePath()).into(image);

            btnMenu = (ImageButton)convertView.findViewById(R.id.listMenu);
            btnMenu.setTag(position);
            // "this" - MyPageAdapter.java의 onClick 함수를 호출함
            btnMenu.setOnClickListener(this);
        }

        return convertView;
    }

    @Override
    public void onClick(View view) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출
        if(listBtnClickListener != null)
            listBtnClickListener.onListBtnClick(view, (Integer) view.getTag());
    }

}
