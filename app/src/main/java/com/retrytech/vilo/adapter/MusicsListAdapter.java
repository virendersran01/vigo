package com.retrytech.vilo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemMusicBinding;
import com.retrytech.vilo.model.music.Musics;
import com.retrytech.vilo.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MusicsListAdapter extends RecyclerView.Adapter<MusicsListAdapter.MusicViewHolder> {
    ArrayList<Musics.SoundList> mList = new ArrayList<>();
    private MusicsCategoryAdapter.OnItemClickListener onMusicClick;
    private boolean isChild = false;

    public void setChild(boolean child) {
        isChild = child;
    }

    public void setOnMusicClick(MusicsCategoryAdapter.OnItemClickListener onMusicClick) {
        this.onMusicClick = onMusicClick;
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.setModel(position);
    }

    @Override
    public int getItemCount() {
        if (isChild) {
            return Math.min(mList.size(), 9);
        }
        return mList.size();
    }

    public void updateData(List<Musics.SoundList> soundList) {

        mList = (ArrayList<Musics.SoundList>) soundList;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mList.remove(mList.get(position));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
    }


    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private ItemMusicBinding binding;
        private SessionManager sessionManager;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                sessionManager = new SessionManager(binding.getRoot().getContext());
            }
        }

        public void setModel(int position) {
            Musics.SoundList model = mList.get(position);
            if (sessionManager != null && sessionManager.getFavouriteMusic() != null) {
                binding.setIsFav(sessionManager.getFavouriteMusic().contains(model.getSoundId()));
            }
            binding.setModel(model);
            binding.getRoot().setOnClickListener(v -> onMusicClick.onItemClick(binding, position, model, 0));
            binding.icFavourite.setOnClickListener(v -> onMusicClick.onItemClick(binding, position, model, 1));
            binding.btnSelect.setOnClickListener(v -> onMusicClick.onItemClick(binding, position, model, 2));
        }
    }
}
