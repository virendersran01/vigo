package com.retrytech.vilo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemHashtagVideoBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.view.video.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class ExploreHashTagVideoAdapter extends RecyclerView.Adapter<ExploreHashTagVideoAdapter.ExploreHashTagVideoViewHolder> {
    protected ArrayList<Video.Data> mList = new ArrayList<>();
    private String hashTag = "";
    private boolean isChild = false;

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setChild(boolean child) {
        isChild = child;
    }


    @NonNull
    @Override
    public ExploreHashTagVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hashtag_video, parent, false);
        return new ExploreHashTagVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreHashTagVideoViewHolder holder, int position) {
        holder.setModel(position);

    }

    @Override
    public int getItemCount() {
        if (isChild) {
            return Math.min(mList.size(), 10);
        }
        return mList.size();
    }

    public void updateData(List<Video.Data> list) {
        mList = (ArrayList<Video.Data>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<Video.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }


    class ExploreHashTagVideoViewHolder extends RecyclerView.ViewHolder {
        ItemHashtagVideoBinding binding;

        ExploreHashTagVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.executePendingBindings();
            }
        }

        public void setModel(int position) {
            binding.setModel(mList.get(position));
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), PlayerActivity.class);
                intent.putExtra("video_list", new Gson().toJson(mList));
                intent.putExtra("position", position);
                intent.putExtra("type", 2);
                intent.putExtra("hash_tag", hashTag);
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }
}
