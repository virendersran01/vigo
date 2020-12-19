package com.retrytech.vilo.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemCommentListBinding;
import com.retrytech.vilo.model.comment.Comment;
import com.retrytech.vilo.utils.Global;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentsViewHolder> {
    private ArrayList<Comment.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;

    public void setListener(OnRecyclerViewItemClick onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }

    @NonNull
    @Override
    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_list, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsViewHolder holder, int position) {
        holder.setModel(mList.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<Comment.Data> list) {
        mList = (ArrayList<Comment.Data>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<Comment.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }

    }

    public List<Comment.Data> getData() {
        return mList;
    }

    public interface OnRecyclerViewItemClick {
        void onCommentItemClick(Comment.Data data, int position, int type);
    }


    class CommentsViewHolder extends RecyclerView.ViewHolder {
        ItemCommentListBinding binding;


        CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);


        }

        public void setModel(Comment.Data comment, int position) {
            binding.setComment(comment);
            if (!TextUtils.isEmpty(Global.USER_ID) && Global.USER_ID.equals(comment.getUserId())) {
                binding.imgDelete.setVisibility(View.VISIBLE);
            }
            binding.imgDelete.setOnClickListener(v -> onRecyclerViewItemClick.onCommentItemClick(comment, position, 1));
            binding.tvUsername.setOnClickListener(view -> onRecyclerViewItemClick.onCommentItemClick(comment, position, 2));
            binding.imgProfile.setOnClickListener(view -> onRecyclerViewItemClick.onCommentItemClick(comment, position, 2));

        }
    }
}
