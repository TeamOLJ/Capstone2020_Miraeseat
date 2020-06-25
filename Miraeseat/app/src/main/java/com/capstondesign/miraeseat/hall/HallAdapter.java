package com.capstondesign.miraeseat.hall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstondesign.miraeseat.R;

import java.util.ArrayList;

public class HallAdapter extends BaseAdapter {
    private static final String TAG = "HallAdapter";

    Context context;
    ArrayList<HallList_item> hallList_itemArrayList;
    LayoutInflater inflater;

    HallList_item oneListItem;


    public HallAdapter(Context context, ArrayList<HallList_item> hallList_itemArrayList) {
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
    public Object getItem(int position) {
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

            holder.name.setText(hallList_itemArrayList.get(position).getPlayName());
            holder.date.setText(hallList_itemArrayList.get(position).getPlayDate());
            holder.poster.setImageResource(hallList_itemArrayList.get(position).getPoster_image());
            holder.info.setText(hallList_itemArrayList.get(position).getPlayInfo());

            convertView.setTag(holder);
        }
        else {
            holder = (HallHolder)convertView.getTag();
        }


        return convertView;
    }
}
