package com.retrytech.vilo.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemNotificationBinding;
import com.retrytech.vilo.model.notification.Notification;
import com.retrytech.vilo.view.search.FetchUserActivity;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private ArrayList<Notification.Data> mList = new ArrayList<>();


    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        holder.setModel(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Notification.Data> list) {
        mList = (ArrayList<Notification.Data>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<Notification.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }

    public List<Notification.Data> getData() {
        return mList;
    }


    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(Notification.Data notification) {
            binding.setNotification(notification);
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), FetchUserActivity.class);
                intent.putExtra("userid", notification.getSenderUserId());
                binding.getRoot().getContext().startActivity(intent);
            });

        }

    }
}
