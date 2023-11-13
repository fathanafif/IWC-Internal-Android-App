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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.BuffetItems;
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

public class BuffetItemInAdapter extends RecyclerView.Adapter<BuffetItemInAdapter.BuffetItemViewHolder> {

    Context context;
    ArrayList<BuffetItems> list;

    public BuffetItemInAdapter(Context context, ArrayList<BuffetItems> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public BuffetItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_order_item, parent, false);
        return new BuffetItemViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull BuffetItemViewHolder holder, int position) {
        BuffetItems buffetItems = list.get(position);

        int converted = Integer.parseInt(buffetItems.getItem_price_dk());
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String buffetItemPrice = idr.format(converted);

        holder.name.setText(buffetItems.getItem_name());
        holder.price.setText(buffetItemPrice);

        holder.addButton.setOnClickListener(v -> {
            addItemToCart(holder.getAdapterPosition());
            Log.d("addButtonCatalogue", String.valueOf(holder.getAdapterPosition()));
        });

        if (!buffetItems.getImg_url().equals("")) {

            Glide.with(context)
                    .load(list.get(position).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new RoundedCorners(36))
                    .into(holder.itemImg);
        } else {
            holder.itemImg.setImageResource(R.drawable.png_npt);
        }

    }

    private void addItemToCart(int adapterPosition) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference cartFd = FirebaseDatabase.getInstance().getReference("carts_temp").child(mUid).push();
        Query dr = FirebaseDatabase.getInstance().getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(list.get(adapterPosition).getItem_name());
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Toast.makeText(context.getApplicationContext(), list.get(adapterPosition).getItem_name() + " is already in cart", Toast.LENGTH_SHORT).show();
                } else {
                    cartFd.child("name").setValue(list.get(adapterPosition).getItem_name());
                    int itemPrice = Integer.parseInt(list.get(adapterPosition).getItem_price_dk());
                    cartFd.child("price").setValue(itemPrice);
                    cartFd.child("qty").setValue(1);
                    cartFd.child("sequence").setValue(Integer.parseInt("2"));
                    cartFd.child("category").setValue("Buffet Item");
                    cartFd.child("type").setValue("Beverage");
                    Toast.makeText(context.getApplicationContext(), list.get(adapterPosition).getItem_name() + " is successfully added to cart", Toast.LENGTH_SHORT).show();
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

    public static class BuffetItemViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        MaterialButton addButton;
        ImageView itemImg;

        public BuffetItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImg = itemView.findViewById(R.id.buffet_item_img);
            name = itemView.findViewById(R.id.buffet_item_name_tv);
            price = itemView.findViewById(R.id.buffet_item_price_tv);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }
}