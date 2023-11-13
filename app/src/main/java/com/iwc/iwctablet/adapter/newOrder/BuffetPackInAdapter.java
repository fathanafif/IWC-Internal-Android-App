package com.iwc.iwctablet.adapter.newOrder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.Buffets;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Objects;

import carbon.widget.ImageView;

public class BuffetPackInAdapter extends RecyclerView.Adapter<BuffetPackInAdapter.BuffetPackViewHolder> {

    Context context;
    ArrayList<Buffets> list;
    BuffetPackInAdapter.BuffetPackItemClickListener mItemListener;

    public BuffetPackInAdapter(Context context, ArrayList<Buffets> list, BuffetPackInAdapter.BuffetPackItemClickListener itemClickListener) {
        this.context = context;
        this.list = list;
        this.mItemListener = itemClickListener;

    }

    public interface BuffetPackItemClickListener {
        void onItemClick(int buffetPackPosition);
    }

    @NonNull
    @Override
    public BuffetPackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_order_pack, parent, false);
        return new BuffetPackViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull BuffetPackViewHolder holder, int position) {
        Buffets buffets = list.get(position);
        int converted;

        converted = Integer.parseInt(buffets.getBuffet_price_dk());
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String buffetPackPrice = idr.format(converted);

        holder.name.setText(buffets.getBuffet_name());
        holder.description.setText(buffets.getBuffet_required_menu());
        holder.price.setText(buffetPackPrice);

        if (buffets.getBuffet_name().contains("Type")) {
            holder.type.setText("Beverage & Main Course");
        } else {
            holder.type.setText("Main Course");
        }

        holder.addButton.setOnClickListener(v -> {
            addItemToCart(holder.getAdapterPosition());
            Log.d("addButtonCatalogue", String.valueOf(holder.getAdapterPosition()));
        });
        holder.showDetailPack.setOnClickListener(v -> mItemListener.onItemClick(position));

        if (!buffets.getImg_url().equals("")) {
            Glide.with(context)
                    .load(list.get(position).getImg_url())
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
    }

    private void addItemToCart(int adapterPosition) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        DatabaseReference cartFd = FirebaseDatabase.getInstance().getReference("carts_temp").child(mUid).push();
        Query dr = FirebaseDatabase.getInstance().getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(list.get(adapterPosition).getBuffet_name());
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Toast.makeText(context.getApplicationContext(), list.get(adapterPosition).getBuffet_name() + " is already in cart", Toast.LENGTH_SHORT).show();
                } else {
                    int itemPrice = Integer.parseInt(list.get(adapterPosition).getBuffet_price_dk());
                    String sentence = list.get(adapterPosition).getBuffet_name();
                    String keyword = "type";
                    cartFd.child("name").setValue(list.get(adapterPosition).getBuffet_name());
                    cartFd.child("price").setValue(itemPrice);
                    cartFd.child("qty").setValue(1);
                    cartFd.child("sequence").setValue(Integer.parseInt("1"));
                    cartFd.child("category").setValue("Buffet Pack");
                    String imgUrl = list.get(adapterPosition).getImg_url();

                    if(list.get(adapterPosition).getImg_url().equals("")) {
                        cartFd.child("img_url").setValue("no_image");
                    } else {
                        cartFd.child("img_url").setValue(imgUrl);
                    }
                    if (sentence.toLowerCase().contains(keyword.toLowerCase())) {
                        cartFd.child("type").setValue("Main Course & Beverage");
                    } else {
                        cartFd.child("type").setValue("Main Course");
                    }
                    Toast.makeText(context.getApplicationContext(), list.get(adapterPosition).getBuffet_name() + " is successfully added to cart", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class BuffetPackViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, description, type;
        MaterialButton addButton;
        ImageView packImg;
        ConstraintLayout showDetailPack;

        public BuffetPackViewHolder(@NonNull View itemView) {
            super(itemView);
            packImg = itemView.findViewById(R.id.buffet_pack_img);
            name = itemView.findViewById(R.id.buffet_pack_name_tv);
            description = itemView.findViewById(R.id.buffet_pack_description_tv);
            price = itemView.findViewById(R.id.buffet_pack_price_tv);
            addButton = itemView.findViewById(R.id.add_button);
            showDetailPack = itemView.findViewById(R.id.item_buffet_pack_cl);
            type = itemView.findViewById(R.id.pack_type_tv);

        }
    }

}