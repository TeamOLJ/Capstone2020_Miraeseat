package com.capstondesign.miraeseat.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.R;

import java.util.ArrayList;

public class SearchAdapter extends BaseAdapter {
    private static final String TAG = "MyPageAdapter";

    Context mContext;
    ArrayList<HallDetailedClass> hallList;
    LayoutInflater inflater;

    HallDetailedClass oneListItem;


    public SearchAdapter(Context context, ArrayList<HallDetailedClass> list_itemArrayList) {
        this.mContext = context;
        this.hallList = list_itemArrayList;
    }

    // ViewHolder
    private class ViewHolder {
        ImageView hallImage;
        TextView hallName;
    }

    @Override
    public int getCount() {
        return this.hallList.size();
    }

    @Override
    public HallDetailedClass getItem(int position) {
        return hallList.get(position);
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
            convertView = inflater.inflate(R.layout.layout_search_item, parent, false);
            holder = new ViewHolder();

            holder.hallImage = (ImageView) convertView.findViewById(R.id.item_image);
            holder.hallName = (TextView) convertView.findViewById(R.id.item_name);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        oneListItem = hallList.get(position);

        holder.hallName.setText(oneListItem.getCombined_name());
        holder.hallName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        holder.hallName.setMarqueeRepeatLimit(-1);
        holder.hallName.setSelected(true);

        if (oneListItem.getHall_Image() == null) { // 지정된 공연장 이미지가 없을 경우 기본이미지로
            holder.hallImage.setImageResource(R.drawable.logo_color);
        } else {
            Glide.with(mContext).load(oneListItem.getHall_Image()).centerCrop().into(holder.hallImage);
        }

        return convertView;
    }

}
