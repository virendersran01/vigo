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
import com.retrytech.vilo.databinding.ItemSearchVideosBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.video.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.HashTagVideoViewHolder> {
    private ArrayList<Video.Data> mList = new ArrayList<>();
    private boolean isHashTag = false;
    private String word = "";


    @NonNull
    @Override
    public HashTagVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_videos, parent, false);
        return new HashTagVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HashTagVideoViewHolder holder, int position) {
        holder.setModel(position);

    }

    @Override
    public int getItemCount() {
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

    public List<Video.Data> getData() {
        return mList;
    }

    public void setHashTag(boolean hashTag) {
        isHashTag = hashTag;
    }

    public void setWord(String hashtag) {
        word = hashtag;
    }


    class HashTagVideoViewHolder extends RecyclerView.ViewHolder {
        ItemSearchVideosBinding binding;

        HashTagVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.executePendingBindings();
            }
        }

        public void setModel(int position) {
            binding.setModel(mList.get(position));
            binding.tvLikeCount.setText(Global.prettyCount(Integer.parseInt(mList.get(position).getPostLikesCount())));
            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), PlayerActivity.class);
                intent.putExtra("video_list", new Gson().toJson(mList));
                intent.putExtra("position", position);
                if (isHashTag) {
                    intent.putExtra("type", 2);
                    intent.putExtra("hash_tag", word);
                } else {
                    intent.putExtra("type", 3);
                    intent.putExtra("keyword", word);
                }
                binding.getRoot().getContext().startActivity(intent);
            });
        }

    }
}
