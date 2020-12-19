package com.retrytech.vilo.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemSearchUserBinding;
import com.retrytech.vilo.model.user.SearchUser;
import com.retrytech.vilo.utils.Global;
import com.retrytech.vilo.view.search.FetchUserActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.SearchUserViewHolder> {
    private ArrayList<SearchUser.User> mList = new ArrayList<>();

    @NonNull
    @Override
    public SearchUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_user, parent, false);
        return new SearchUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserViewHolder holder, int position) {
        holder.setModel(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<SearchUser.User> list) {
        mList = (ArrayList<SearchUser.User>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<SearchUser.User> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }

    public List<SearchUser.User> getData() {
        return mList;
    }


    static class SearchUserViewHolder extends RecyclerView.ViewHolder {
        ItemSearchUserBinding binding;

        SearchUserViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }

        public void setModel(SearchUser.User user) {
            binding.setUser(user);
            binding.tvDetails.setText(Global.prettyCount(user.getUserFollowerCount()).concat(" Fans  " + Global.prettyCount(user.getUserPostCount()) + " Videos"));
            binding.getRoot().setOnClickListener(v -> {

                Intent intent = new Intent(binding.getRoot().getContext(), FetchUserActivity.class);
                intent.putExtra("userid", user.getUserId());
                binding.getRoot().getContext().startActivity(intent);

            });
        }

    }
}
