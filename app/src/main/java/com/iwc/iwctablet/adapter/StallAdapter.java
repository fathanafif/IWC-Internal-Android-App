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
import com.iwc.iwctablet.model.Stalls;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class StallAdapter extends RecyclerView.Adapter<StallAdapter.StallViewHolder> {

    Context context;
    ArrayList<Stalls> mStallList;
    StallAdapter.StallItemClickListener mItemListener;

    FirebaseStorage mStorage = FirebaseStorage.getInstance();
    StorageReference storageRef = mStorage.getReference().child("stall");

    public StallAdapter(Context context, ArrayList<Stalls> list, StallItemClickListener itemClickListener) {
        this.context = context;
        this.mStallList = list;
        this.mItemListener = itemClickListener;
    }
    
    public interface StallItemClickListener {
        void onItemClick(Stalls stalls);
    }
    
    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stall, parent, false);
        return new StallViewHolder(view);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int stallPosition) {
        Stalls stalls = mStallList.get(stallPosition);


        int convertedDk = Integer.parseInt(stalls.getPrice_dk());
        int convertedLk = Integer.parseInt(stalls.getPrice_lk());
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String stallPriceDk = idr.format(convertedDk);
        String stallPriceLk = idr.format(convertedLk);

        holder.name.setText(stalls.getName());

        switch (stalls.getName()) {
            case "Puding Plus Fla" :
                holder.satuanDk.setText("/ loyang");
                break;
            case "Kambing Guling" :
                holder.satuanDk.setText("/ ekor");
                break;
            case "Tengkleng" :
                holder.satuanDk.setText("/ kuali");
                break;
            default:
                holder.satuanDk.setText("/ porsi");
                break;
        }

        switch (stalls.getName()) {
            case "Puding Plus Fla" :
                holder.satuanLk.setText("/ loyang");
                break;
            case "Kambing Guling" :
                holder.satuanLk.setText("/ ekor");
                break;
            case "Tengkleng" :
                holder.satuanLk.setText("/ kuali");
                break;
            default:
                holder.satuanLk.setText("/ porsi");
                break;
        }


        holder.category.setText("Pondok " + stalls.getCategory());
        holder.type.setText(stalls.getType());
        holder.price_dk.setText(stallPriceDk);
        holder.price_lk.setText(stallPriceLk);
        holder.stallItemCl.setOnClickListener(view -> mItemListener.onItemClick(mStallList.get(stallPosition)));

        if (!stalls.getImg_url().equals("")) {
            Glide.with(context)
                    .load(mStallList.get(stallPosition).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .into(holder.stallImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.png_npt)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .into(holder.stallImg);
        }
    }

    @Override
    public int getItemCount() {
        return mStallList.size();
    }

    public static class StallViewHolder extends RecyclerView.ViewHolder {

        TextView name, price_dk, price_lk, category, type, satuanDk, satuanLk;
        ConstraintLayout stallItemCl;
        ImageView stallImg;
        
        public StallViewHolder(@NonNull View itemView) {
            super(itemView);

            stallItemCl = itemView.findViewById(R.id.stall_item_cl);
            name = itemView.findViewById(R.id.stall_name_tv);
            satuanDk = itemView.findViewById(R.id.per_porsi_dk_tv);
            satuanLk = itemView.findViewById(R.id.per_porsi_lk_tv);
            category = itemView.findViewById(R.id.stall_category_tv);
            type = itemView.findViewById(R.id.stall_type_tv);
            price_dk = itemView.findViewById(R.id.stall_price_dk_tv);
            price_lk = itemView.findViewById(R.id.stall_price_lk_tv);
            stallImg = itemView.findViewById(R.id.stall_image);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Stalls> filteredList) {
        mStallList = filteredList;
        notifyDataSetChanged();
    }
}
