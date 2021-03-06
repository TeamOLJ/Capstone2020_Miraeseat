package com.capstondesign.miraeseat.seatpage;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.Image;
import com.capstondesign.miraeseat.R;

import java.util.ArrayList;

public class SeatAdapter extends BaseAdapter {
    private static final String TAG = "SeatAdapter";

    Context context;
    ArrayList<seatList_item> seatList_itemArrayList;
    LayoutInflater inflater;

    // 버튼 클릭 이벤트를 위한 Listener 인터페이스 정의
    public interface ItemBtnClickListener {
        void onItemBtnClick(View view, int position, int whichBtn);
        // whichBtn: 100 - 수정, 200 - 삭제, 300 - 신고
    }

    private ItemBtnClickListener itemBtnClickListener = null;

    public SeatAdapter(Context context, ArrayList<seatList_item> seatList_itemArrayList, ItemBtnClickListener itemBtnClickListener) {
        this.context = context;
        this.seatList_itemArrayList = seatList_itemArrayList;
        this.itemBtnClickListener = itemBtnClickListener;
    }

    // ViewHolder
    private class SeatHolder {
        ImageView profile;
        TextView nickName;
        ImageView image;
        RatingBar ratingBar;
        TextView writing;

        Button btnLike;
        Button btnReport;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        SeatHolder holder = null;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.seat_item, parent, false);
            holder = new SeatHolder();

            holder.profile = (ImageView)convertView.findViewById(R.id.re_profilePic);
            holder.nickName = (TextView)convertView.findViewById(R.id.re_nickName);
            holder.image = (ImageView)convertView.findViewById(R.id.re_Image);
            holder.ratingBar = (RatingBar)convertView.findViewById(R.id.re_Ratingbar);
            holder.writing = (TextView)convertView.findViewById(R.id.re_writing);

            holder.btnLike = (Button)convertView.findViewById(R.id.btnLike);
            holder.btnReport = (Button)convertView.findViewById(R.id.btnReport);

            convertView.setTag(holder);
        }
        else {
            holder = (SeatHolder)convertView.getTag();
        }


        holder.nickName.setText(seatList_itemArrayList.get(position).getNickname());
        holder.ratingBar.setRating(seatList_itemArrayList.get(position).getRatingbar());
        holder.writing.setText(seatList_itemArrayList.get(position).getReview_writing().replace("\\\\n", "\n"));

        Glide.with(context).load(seatList_itemArrayList.get(position).getProfile_image()).into(holder.profile);

        // 사진의 유무에 따라 텍스트뷰의 마진 변경
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)holder.writing.getLayoutParams();

        if(seatList_itemArrayList.get(position).getReview_image() != null) {
            Glide.with(context).load(seatList_itemArrayList.get(position).getReview_image()).into(holder.image);
            holder.image.setVisibility(View.VISIBLE);
            params.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.review_withimage_bottom);
            holder.writing.setLayoutParams(params);
        }
        else {
            // 사진이 없을 경우 이미지뷰의 Visibility를 GONE으로 설정 - 불필요한 빈공간 보이지 않게
            holder.image.setVisibility(View.GONE);
            params.bottomMargin = context.getResources().getDimensionPixelSize(R.dimen.review_noimage_bottom);
            holder.writing.setLayoutParams(params);
        }


        //이미지 클릭시 원본 이미지
        holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), Image.class);
                    intent.putExtra("imagepath", seatList_itemArrayList.get(position).getReview_image());
                    context.startActivity(intent);
            }
        });

        holder.btnLike.setTag(position);
        holder.btnReport.setTag(position);

        // 현재 사용자가 후기 작성자인지 아닌지에 따라 버튼 변경
        if(seatList_itemArrayList.get(position).getIsOwner()) {
            holder.btnLike.setText("수정");
            holder.btnLike.setOnClickListener(ClickEdit);
            holder.btnLike.setVisibility(View.VISIBLE);

            holder.btnReport.setText("삭제");
            holder.btnReport.setOnClickListener(ClickDelete);
        }
        else {
            holder.btnLike.setVisibility(View.GONE);

            holder.btnReport.setText("신고하기");
            holder.btnReport.setOnClickListener(ClickReport);
        }

        return convertView;
    }

    // whichBtn: 100 - 수정, 200 - 삭제, 300 - 좋아요, 400 - 신고
    private View.OnClickListener ClickEdit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(itemBtnClickListener != null) {
                itemBtnClickListener.onItemBtnClick(v, (int) v.getTag(), 100);
            }
        }
    };

    private View.OnClickListener ClickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(itemBtnClickListener != null) {
                itemBtnClickListener.onItemBtnClick(v, (int) v.getTag(), 200);
            }
        }
    };

    private View.OnClickListener ClickReport = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(itemBtnClickListener != null) {
                itemBtnClickListener.onItemBtnClick(v, (int) v.getTag(), 300);
            }
        }
    };
}
