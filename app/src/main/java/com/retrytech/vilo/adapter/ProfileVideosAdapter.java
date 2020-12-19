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
import com.retrytech.vilo.databinding.ItemVidProfileListBinding;
import com.retrytech.vilo.model.videos.Video;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.video.PlayerActivity;
import com.retrytech.vilo.viewmodel.ProfileVideosViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileVideosAdapter extends RecyclerView.Adapter<ProfileVideosAdapter.ProfileVideoViewHolder> {
    private ArrayList<Video.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;
    private ProfileVideosViewModel videosViewModel;

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }

    public void setVideosViewModel(ProfileVideosViewModel videosViewModel) {
        this.videosViewModel = videosViewModel;
    }

    @NonNull
    @Override
    public ProfileVideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vid_profile_list, parent, false);
        return new ProfileVideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileVideoViewHolder holder, int position) {
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

    public interface OnRecyclerViewItemClick {

        void onItemClick(Video.Data model, int position, ItemVidProfileListBinding binding);

    }


    class ProfileVideoViewHolder extends RecyclerView.ViewHolder {
        ItemVidProfileListBinding binding;

        ProfileVideoViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.executePendingBindings();
            }
        }

        public void setModel(int position) {
            binding.setModel(mList.get(position));

            if (videosViewModel.userId.equals(Global.USER_ID) && videosViewModel.vidType == 0) {
                binding.getRoot().setOnLongClickListener(v -> {
                    onRecyclerViewItemClick.onItemClick(mList.get(position), position, binding);
                    return true;
                });
            }

            binding.getRoot().setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), PlayerActivity.class);
                intent.putExtra("video_list", new Gson().toJson(mList));
                intent.putExtra("position", position);
                intent.putExtra("type", videosViewModel.vidType);
                intent.putExtra("user_id", videosViewModel.userId);
                binding.getRoot().getContext().startActivity(intent);
            });

        }
    }
}
