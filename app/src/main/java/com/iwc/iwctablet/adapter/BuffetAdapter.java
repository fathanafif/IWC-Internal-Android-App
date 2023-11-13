package com.iwc.iwctablet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.Buffets;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;


public class BuffetAdapter extends RecyclerView.Adapter<BuffetAdapter.BuffetViewHolder> {

    Context context;
    ArrayList<Buffets> mBuffetList;
    BuffetAdapter.BuffetPackItemClickListener mItemListener;

    public BuffetAdapter(Context context, ArrayList<Buffets> list, BuffetPackItemClickListener itemClickListener) {
        this.context = context;
        this.mBuffetList = list;
        this.mItemListener = itemClickListener;
    }

    public interface BuffetPackItemClickListener {
        void onItemClick(int buffetPosition);
    }

    @NonNull
    @Override
    public BuffetAdapter.BuffetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_buffet_packs, parent, false);
        return new BuffetViewHolder(view);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull BuffetAdapter.BuffetViewHolder holder, int buffetPosition) {
        Buffets buffets = mBuffetList.get(buffetPosition);

        int convertedDk = Integer.parseInt(buffets.getBuffet_price_dk());
        int convertedLk = Integer.parseInt(buffets.getBuffet_price_lk());
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String packPriceDk = idr.format(convertedDk);
        String packPriceLk = idr.format(convertedLk);

        holder.name.setText(buffets.getBuffet_name());
        holder.priceDk.setText(packPriceDk);
        holder.priceLk.setText(packPriceLk);
        holder.required.setText(buffets.getBuffet_required_menu());
        holder.choice.setText(buffets.getBuffet_choice_menu());
        holder.packItemLl.setOnClickListener(view -> mItemListener.onItemClick(buffetPosition));

        if (!buffets.getImg_url().equals("")) {
            Glide.with(context)
                    .load(mBuffetList.get(buffetPosition).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .into(holder.packImg);
        } else {
            Glide.with(context)
                    .load(R.drawable.png_npt)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(24))
                    .into(holder.packImg);
        }

        if (!buffets.getBuffet_name().contains("Type")) {
            holder.tagBv.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mBuffetList.size();
    }

    public static class BuffetViewHolder extends RecyclerView.ViewHolder {

        TextView name, priceDk, priceLk, required, choice, tagBv, tagMc;
        LinearLayout packItemLl;
        ImageView packImg;

        public BuffetViewHolder(@NonNull View itemView) {
            super(itemView);

            packItemLl = itemView.findViewById(R.id.pack_item_ll);
            name = itemView.findViewById(R.id.buffet_name_tv);
            tagBv = itemView.findViewById(R.id.tag_bv);
            tagMc = itemView.findViewById(R.id.tag_mc);
            priceDk = itemView.findViewById(R.id.buffet_price_dk_tv);
            priceLk = itemView.findViewById(R.id.buffet_price_lk_tv);
            required = itemView.findViewById(R.id.buffet_required_menu_tv);
            choice = itemView.findViewById(R.id.buffet_choice_menu_tv);
            packImg = itemView.findViewById(R.id.buffet_image_iv);
        }
    }

}
