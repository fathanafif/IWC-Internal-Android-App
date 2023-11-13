package com.iwc.iwctablet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.BuffetItems;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import carbon.widget.ImageView;

public class BuffetItemAdapter extends RecyclerView.Adapter<BuffetItemAdapter.BuffetItemViewHolder> {
    
    Context context;
    ArrayList<BuffetItems> mBuffetItemList;
    BuffetItemAdapter.BuffetItemItemClickListener mItemListener;
    
    public BuffetItemAdapter(Context context, ArrayList<BuffetItems> list, BuffetItemItemClickListener itemClickListener) {
        this.context = context;
        this.mBuffetItemList = list;
        this.mItemListener = itemClickListener;
    }
    
    public interface BuffetItemItemClickListener {
        void onItemClick(int buffetItemPosition);
    }
    
    @NonNull
    @Override
    public BuffetItemAdapter.BuffetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buffet_items, parent, false);
        return new BuffetItemViewHolder(view);
    }
    
    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull BuffetItemAdapter.BuffetItemViewHolder holder, int buffetItemPosition) {
        BuffetItems buffetItems = mBuffetItemList.get(buffetItemPosition);
    
        int convertedDk = Integer.parseInt(buffetItems.getItem_price_dk());
        int convertedLk = Integer.parseInt(buffetItems.getItem_price_lk());
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String itemPriceDk = idr.format(convertedDk);
        String itemPriceLk = idr.format(convertedLk);
        
        holder.name.setText(buffetItems.getItem_name());
        holder.price_dk.setText(itemPriceDk);
        holder.price_lk.setText(itemPriceLk);
        holder.type.setText(buffetItems.getItem_type());
        holder.packItemLl.setOnClickListener(view -> mItemListener.onItemClick(buffetItemPosition));

        if (!buffetItems.getImg_url().equals("")) {

            Glide.with(context)
                    .load(mBuffetItemList.get(buffetItemPosition).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(36))
                    .into(holder.itemImg);
        } else {
            holder.itemImg.setImageResource(R.drawable.png_npt);
        }
    }
    
    @Override
    public int getItemCount() {
        return mBuffetItemList.size();
    }
    
    public static class BuffetItemViewHolder extends RecyclerView.ViewHolder {
        
        TextView name, price_dk, price_lk, type;
        LinearLayout packItemLl;
        ImageView itemImg;
        
        public BuffetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            packItemLl = itemView.findViewById(R.id.item_item_ll);
            name = itemView.findViewById(R.id.item_name_tv);
            price_dk = itemView.findViewById(R.id.item_price_dk_tv);
            price_lk = itemView.findViewById(R.id.item_price_lk_tv);
            type = itemView.findViewById(R.id.item_type_tv);
            itemImg = itemView.findViewById(R.id.item_image_iv);
        }
    }
    
}