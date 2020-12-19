package com.retrytech.vilo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemSearchHashListBinding;
import com.retrytech.vilo.model.Explore;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.search.HashTagActivity;

import java.util.ArrayList;
import java.util.List;

public class ExploreHashTagAdapter extends RecyclerView.Adapter<ExploreHashTagAdapter.ExploreHashTagViewHolder> {
    private ArrayList<Explore.Data> mList = new ArrayList<>();

    @NonNull
    @Override
    public ExploreHashTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_hash_list, parent, false);
        return new ExploreHashTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreHashTagViewHolder holder, int position) {
        holder.setModel(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Explore.Data> list) {
        mList = (ArrayList<Explore.Data>) list;
        notifyDataSetChanged();
    }

    public void loadMore(List<Explore.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }

    public List<Explore.Data> getData() {
        return mList;
    }

    static class ExploreHashTagViewHolder extends RecyclerView.ViewHolder {
        ItemSearchHashListBinding binding;
        ExploreHashTagVideoAdapter adapter = new ExploreHashTagVideoAdapter();


        ExploreHashTagViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            if (binding != null) {
                binding.executePendingBindings();
            }
        }

        public void setModel(Explore.Data explore) {
            binding.setExplore(explore);
            adapter.setHashTag(explore.getHashTagName());
            adapter.setChild(true);
            binding.tvHashVidCount.setText(Global.prettyCount(explore.getHashTagVideosCountl()).concat(" Videos"));
            adapter.updateData(explore.getHashTagVideos());
            binding.setAdapter(adapter);

            binding.loutHead.setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), HashTagActivity.class);
                intent.putExtra("hashtag", explore.getHashTagName());
                binding.getRoot().getContext().startActivity(intent);
            });
        }


    }
}
