package com.retrytech.vilo.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.retrytech.vilo.R;
import com.retrytech.vilo.databinding.ItemCoinPlansBinding;
import com.retrytech.vilo.model.wallet.CoinPlan;

import java.util.ArrayList;
import java.util.List;

public class CoinPlansAdapter extends RecyclerView.Adapter<CoinPlansAdapter.CoinPlansViewHolder> {
    private ArrayList<CoinPlan.Data> mList = new ArrayList<>();
    private OnRecyclerViewItemClick onRecyclerViewItemClick;

    public void setListener(OnRecyclerViewItemClick onRecyclerViewItemClick) {
        this.onRecyclerViewItemClick = onRecyclerViewItemClick;
    }

    @NonNull
    @Override
    public CoinPlansViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coin_plans, parent, false);
        return new CoinPlansViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinPlansViewHolder holder, int position) {
        holder.setModel(mList.get(position), position);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateData(List<CoinPlan.Data> list) {
        mList = (ArrayList<CoinPlan.Data>) list;
        notifyDataSetChanged();

    }

    public void loadMore(List<CoinPlan.Data> data) {
        for (int i = 0; i < data.size(); i++) {
            mList.add(data.get(i));
            notifyItemInserted(mList.size() - 1);
        }
    }

    public interface OnRecyclerViewItemClick {
        void onBuyButtonClick(CoinPlan.Data data, int position);
    }


    class CoinPlansViewHolder extends RecyclerView.ViewHolder {
        ItemCoinPlansBinding binding;

        CoinPlansViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

        }

        public void setModel(CoinPlan.Data plan, int position) {
            binding.setPlan(plan);
            binding.btnBuy.setOnClickListener(view -> onRecyclerViewItemClick.onBuyButtonClick(plan, position));
        }

    }
}
