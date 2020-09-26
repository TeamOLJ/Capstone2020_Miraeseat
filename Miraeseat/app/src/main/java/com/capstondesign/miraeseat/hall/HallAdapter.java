package com.capstondesign.miraeseat.hall;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstondesign.miraeseat.R;
import com.capstondesign.miraeseat.search.PlayClass;

import java.util.ArrayList;

public class HallAdapter extends BaseAdapter {
    private static final String TAG = "HallAdapter";

    Context context;
    ArrayList<PlayClass> hallList_itemArrayList;
    LayoutInflater inflater;


    public HallAdapter(Context context, ArrayList<PlayClass> hallList_itemArrayList) {
        this.context = context;
        this.hallList_itemArrayList = hallList_itemArrayList;
    }

    private class HallHolder {
        ImageView poster;
        TextView name;
        TextView date;
        TextView info;
    }

    @Override
    public int getCount() {
        return this.hallList_itemArrayList.size();
    }

    @Override
    public PlayClass getItem(int position) {
        return hallList_itemArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HallHolder holder = null;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView==null){
            convertView = inflater.inflate(R.layout.hall_item,parent,false);
            holder = new HallHolder();

            holder.poster = (ImageView)convertView.findViewById(R.id.playImage);
            holder.name = (TextView)convertView.findViewById(R.id.playName);
            holder.date = (TextView)convertView.findViewById(R.id.playDate);
            holder.info = (TextView)convertView.findViewById(R.id.playInfo);

            convertView.setTag(holder);
        }
        else {
            holder = (HallHolder)convertView.getTag();
        }

        holder.name.setText(hallList_itemArrayList.get(position).getPlay_name());
        holder.date.setText(hallList_itemArrayList.get(position).getDate());
        holder.info.setText(hallList_itemArrayList.get(position).getState());

        Glide.with(context).load(hallList_itemArrayList.get(position).getPoster()).into(holder.poster);

        holder.name.setSelected(true);

        return convertView;
    }
}
