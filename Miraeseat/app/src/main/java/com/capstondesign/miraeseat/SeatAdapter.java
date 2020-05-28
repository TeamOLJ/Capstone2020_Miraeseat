package com.capstondesign.miraeseat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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

            profile.setImageResource(seatList_itemArrayList.get(position).getProfile_image());
            nickName.setText(seatList_itemArrayList.get(position).getNickname());
            image.setImageResource(seatList_itemArrayList.get(position).getReview_image());
            ratingBar.setRating(seatList_itemArrayList.get(position).getRatingbar());
            writing.setText(seatList_itemArrayList.get(position).getReview_writing());
        }

        return convertView;
    }
}
