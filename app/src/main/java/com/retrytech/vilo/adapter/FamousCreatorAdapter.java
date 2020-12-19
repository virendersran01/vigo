package com.retrytech.vilo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemFamousCreatorBinding;
import com.retrytech.vilo.model.videos.Video;

import java.util.ArrayList;
import java.util.List;

public class FamousCreatorAdapter extends RecyclerView.Adapter<FamousCreatorAdapter.VideoFullViewHolder> {
    private ArrayList<Video.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;

    public List<Video.Data> getData() {
        return mList;
    }

    public void setOnRecyclerViewItemClick(OnRecyclerViewItemClick onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }


    @NonNull
    @Override
    public VideoFullViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_famous_creator, parent, false);
        return new VideoFullViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFullViewHolder holder, int position) {
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

    public interface OnRecyclerViewItemClick {

        void onItemClick(Video.Data model, int position, ItemFamousCreatorBinding binding, int type);

    }


    class VideoFullViewHolder extends RecyclerView.ViewHolder {
        ItemFamousCreatorBinding binding;


        VideoFullViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }

        public void setModel(int position) {
            if (position == 0) {
                onRecyclerViewItemClick.onItemClick(mList.get(position), position, binding, 1);
            }
            binding.getRoot().setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    // run scale animation and make it bigger
                    Animation anim = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.scale_in_tv);
                    binding.getRoot().startAnimation(anim);
                    anim.setFillAfter(true);
                } else {
                    // run scale animation and make it smaller
                    Animation anim = AnimationUtils.loadAnimation(binding.getRoot().getContext(), R.anim.scale_out_tv);
                    binding.getRoot().startAnimation(anim);
                    anim.setFillAfter(true);
                }
            });
            binding.setModel(mList.get(position));
            binding.btnFollow.setOnClickListener(view -> onRecyclerViewItemClick.onItemClick(mList.get(position), position, binding, 0));
        }
    }


}
