package com.capstondesign.miraeseat.mypage;

import android.app.Activity;
import android.content.Context;
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

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.EditReview;
import com.capstondesign.miraeseat.R;

import java.util.ArrayList;

public class MyPageAdapter extends BaseAdapter implements PopupMenu.OnMenuItemClickListener {
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


    public MyPageAdapter(Context context, ArrayList<mypageList_item> list_itemArrayList) {
        this.mContext = context;
        this.infoList = list_itemArrayList;
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

            convertView = inflater.inflate(R.layout.mypage_item,parent,false);

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
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedPosition = position;
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    popupMenu.setOnMenuItemClickListener(MyPageAdapter.this);
                    popupMenu.inflate(R.menu.list_menu);
                    popupMenu.show();
                }
            });
        }

        return convertView;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_modify:
//                Toast.makeText(mContext, infoList.get(selectedPosition).getSeatNum(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, EditReview.class);
                intent.putExtra("documentID", infoList.get(selectedPosition).getDocumentID());
                intent.putExtra("imagepath", infoList.get(selectedPosition).getImagePath());
                intent.putExtra("seatNum", infoList.get(selectedPosition).getSeatNum());
                intent.putExtra("rating", infoList.get(selectedPosition).getSeatRating());
                intent.putExtra("reviewContext", infoList.get(selectedPosition).getReviewContext());

                Activity origin = (Activity)mContext;
                origin.startActivityForResult(intent, 1234);
                return true;
            case R.id.item_delete:
                Toast.makeText(mContext, "삭제 버튼", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
