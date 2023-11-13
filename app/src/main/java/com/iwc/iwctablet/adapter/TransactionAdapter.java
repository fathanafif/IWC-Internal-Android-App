package com.iwc.iwctablet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.Transactions;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    Context context;
    ArrayList<Transactions> mTransactionList;
    TransactionAdapter.TransactionItemClickListener mItemListener;

    public TransactionAdapter(Context context, ArrayList<Transactions> list, TransactionItemClickListener itemClickListener) {
        this.context = context;
        this.mTransactionList = list;
        this.mItemListener = itemClickListener;
    }

    public interface TransactionItemClickListener {
        void onItemClick(Transactions transactions);
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int transactionPosition) {
        Transactions transactions = mTransactionList.get(transactionPosition);

        holder.name.setText(transactions.getName());
        holder.dateSaved.setText(transactions.getDate_saved());
        holder.clickable.setOnClickListener(view -> mItemListener.onItemClick(mTransactionList.get(transactionPosition)));

        if (!transactions.getImg_url().equals("")) {
            Glide.with(context)
                    .load(mTransactionList.get(transactionPosition).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .into(holder.transactionImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.png_npt)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(12))
                    .into(holder.transactionImg);
        }
    }

    @Override
    public int getItemCount() {
        return mTransactionList.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {

        TextView name, dateSaved;
        ImageView transactionImg;
        ConstraintLayout clickable;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_transaction_tv);
            dateSaved = itemView.findViewById(R.id.date_saved_transaction_tv);
            transactionImg = itemView.findViewById(R.id.receipt_transaction_iv);
            clickable = itemView.findViewById(R.id.clickable_transaction_item_cl);
        }

    }

}
