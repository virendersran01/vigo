package com.retrytech.vilo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemFollowerBinding;
import com.retrytech.vilo.model.follower.Follower;

import java.util.ArrayList;
import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder> {
    private ArrayList<Follower.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }


    @NonNull
    @Override
    public FollowersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_follower, parent, false);
        return new FollowersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersViewHolder holder, int position) {
        holder.setModel(mList.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Follower.Data> list) {
        mList = (ArrayList<Follower.Data>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<Follower.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }

    public interface OnRecyclerViewItemClick {
        void onItemClick(Follower.Data data, int position);

    }


    class FollowersViewHolder extends RecyclerView.ViewHolder {
        ItemFollowerBinding binding;

        FollowersViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(Follower.Data follower, int position) {
            binding.setFollower(follower);
            binding.getRoot().setOnClickListener(v -> onRecyclerViewItemClick.onItemClick(follower, position));
        }

    }
}
