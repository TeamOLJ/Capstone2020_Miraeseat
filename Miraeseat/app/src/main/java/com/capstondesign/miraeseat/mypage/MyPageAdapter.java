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

public class MyPageAdapter extends BaseAdapter implements View.OnClickListener {
    private static final String TAG = "MyPageAdapter";

    Context mContext;
    ArrayList<mypageList_item> infoList;
    LayoutInflater inflater;

    mypageList_item oneListItem;

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

    // ViewHolder
    private class ViewHolder {
        TextView reviewInfo;
        RatingBar reviewRating;
        ImageView reviewImage;
        TextView reviewContent;
        TextView reviewDate;
        ImageButton btnMenu;
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

        ViewHolder holder = null;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null) {
            convertView = inflater.inflate(R.layout.mypage_item, parent, false);
            holder = new ViewHolder();

            holder.reviewInfo = (TextView) convertView.findViewById(R.id.listInfo);
            holder.reviewRating = (RatingBar) convertView.findViewById(R.id.listRatingbar);
            holder.reviewImage = (ImageView) convertView.findViewById(R.id.listImage);
            holder.reviewContent = (TextView) convertView.findViewById(R.id.listWriting);
            holder.reviewDate = (TextView) convertView.findViewById(R.id.listDate);
            holder.btnMenu = (ImageButton) convertView.findViewById(R.id.listMenu);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }


        oneListItem = infoList.get(position);

        holder.reviewInfo.setText(oneListItem.getTheaterName()+" "+oneListItem.getSeatNum());
        holder.reviewRating.setRating(oneListItem.getSeatRating());
        holder.reviewContent.setText(oneListItem.getReviewContext());
        holder.reviewDate.setText(oneListItem.getReviewDate());

        Glide.with(mContext).load(oneListItem.getImagePath()).into(holder.reviewImage);


        holder.btnMenu.setTag(position);

        // "this" - MyPageAdapter.java의 onClick 함수를 호출함
        holder.btnMenu.setOnClickListener(this);

        return convertView;
    }

    @Override
    public void onClick(View view) {
        // ListBtnClickListener(MainActivity)의 onListBtnClick() 함수 호출
        if(listBtnClickListener != null)
            listBtnClickListener.onListBtnClick(view, (Integer) view.getTag());
    }

}
