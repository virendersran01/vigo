package com.retrytech.vilo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemMusicBinding;
import com.retrytech.vilo.databinding.ItemMusicCategoryBinding;
import com.retrytech.vilo.model.music.Musics;

import java.util.ArrayList;
import java.util.List;

public class MusicsCategoryAdapter extends RecyclerView.Adapter<MusicsCategoryAdapter.MusicViewHolder> {
    ArrayList<Musics.Category> mList = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private OnItemMoreClickListener onItemMoreClickListener;

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemMoreClickListener(OnItemMoreClickListener onItemMoreClickListener) {
        this.onItemMoreClickListener = onItemMoreClickListener;
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_category, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.setModel(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Musics.Category> data) {
        mList = (ArrayList<Musics.Category>) data;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(ItemMusicBinding view, int position, Musics.SoundList musics, int type);
    }

    public interface OnItemMoreClickListener {
        void onMoreClick(ArrayList<Musics.SoundList> lists);
    }

    public class MusicViewHolder extends RecyclerView.ViewHolder {
        private ItemMusicCategoryBinding binding;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(int parentPosition) {

            Musics.Category model = mList.get(parentPosition);
            if (model.getSoundList() != null && !model.getSoundList().isEmpty()) {
                ((MusicsListAdapter) mList.get(parentPosition).getAdapter()).setChild(true);
                ((MusicsListAdapter) mList.get(parentPosition).getAdapter()).updateData(model.getSoundList());
                ((MusicsListAdapter) mList.get(parentPosition).getAdapter()).setOnMusicClick(onItemClickListener);
            }
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.findSnapView(binding.rvMusics.getLayoutManager());
            binding.rvMusics.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(binding.rvMusics);
            binding.setOnMoreClick(v -> onItemMoreClickListener.onMoreClick((ArrayList<Musics.SoundList>) model.getSoundList()));
            binding.setModel(model);
        }
    }
}
