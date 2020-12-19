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
import com.retrytech.vilo.databinding.ItemSoundVideosListBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.view.video.PlayerActivity;

import java.util.ArrayList;
import java.util.List;

public class SoundVideoAdapter extends RecyclerView.Adapter<SoundVideoAdapter.SoundVideoViewHolder> {
    private ArrayList<Video.Data> mList = new ArrayList<>();
    private String soundId;


    @NonNull
    @Override
    public SoundVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sound_videos_list, parent, false);
        return new SoundVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundVideoViewHolder holder, int position) {

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

    public void setSoundId(String soundId) {
        this.soundId = soundId;
    }


    class SoundVideoViewHolder extends RecyclerView.ViewHolder {
        ItemSoundVideosListBinding binding;

        SoundVideoViewHolder(@NonNull View itemView) {
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
                intent.putExtra("type", 4);
                intent.putExtra("sound_id", soundId);
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }
}
