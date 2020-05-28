package com.capstondesign.miraeseat.notice;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.capstondesign.miraeseat.R;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> implements OnNoticeClickListener {
    ArrayList<Notice> items = new ArrayList<Notice>();

    OnNoticeClickListener listener;

    int layoutType = 0;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.single_notice, viewGroup, false);

        return new ViewHolder(itemView, this, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Notice item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Notice item) {
        items.add(item);
    }

    public void setItems(ArrayList<Notice> items) {
        this.items = items;
    }

    public Notice getItem(int position) {
        return items.get(position);
    }

    public void setOnNoticeClickListener(OnNoticeClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout noticeLayout;
        TextView textTitle;
        TextView textDate;

        public ViewHolder(View itemView, final OnNoticeClickListener listener, int layoutType) {
            super(itemView);

            noticeLayout = itemView.findViewById(R.id.noticeLayout);

            textTitle = itemView.findViewById(R.id.textTitle);

            textDate = itemView.findViewById(R.id.textDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(Notice item) {

            textTitle.setText(item.getTitle());
            textDate.setText(item.getDate());
        }
    }
}
