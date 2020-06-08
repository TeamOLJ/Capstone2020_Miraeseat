package com.capstondesign.miraeseat.mypage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.R;

import java.util.ArrayList;

public class MyPageAdapter extends BaseAdapter {
    Context mContext;
    ArrayList<mypageList_item> infoList;
    LayoutInflater inflater;

    TextView info;
    RatingBar ratingBar;
    ImageView image;
    TextView content;
    TextView date;
    ImageButton btnMenu;


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
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null)
        {

            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.mypage_item,parent,false);

            info = (TextView)convertView.findViewById(R.id.listInfo);
            ratingBar = (RatingBar)convertView.findViewById(R.id.listRatingbar);
            image = (ImageView)convertView.findViewById(R.id.listImage);
            content = (TextView)convertView.findViewById(R.id.listWriting);
            date = (TextView)convertView.findViewById(R.id.listDate);

            info.setText(infoList.get(position).getListInfo());
            ratingBar.setRating(infoList.get(position).getListRatingbar());
            content.setText(infoList.get(position).getListWriting());
            date.setText(infoList.get(position).getListDate());

            Glide.with(mContext).load(infoList.get(position).getListImagePath()).into(image);

            btnMenu = (ImageButton)convertView.findViewById(R.id.listMenu);
            btnMenu.setTag(position);
            btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //메뉴를 불러오고 싶은데 어떻게 해야되는지 모르겠음 ㅠㅠ

                }
            });
        }

        return convertView;
    }
}
