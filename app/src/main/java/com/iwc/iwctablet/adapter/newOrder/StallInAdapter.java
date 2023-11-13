package com.iwc.iwctablet.adapter.newOrder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.Stalls;
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

public class StallInAdapter extends RecyclerView.Adapter<StallInAdapter.StallViewHolder> {

    Context context;
    ArrayList<Stalls> list;

    public StallInAdapter(Context context, ArrayList<Stalls> list) {
        this.context = context;
        this.list = list;

    }

    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_new_order_stall, parent, false);
        return new StallViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int position) {
        Stalls stalls = list.get(position);

        if (!stalls.getImg_url().equals("")) {
            Glide.with(context)
                    .load(list.get(position).getImg_url())
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

        int converted = Integer.parseInt(stalls.getPrice_dk());
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String stallPrice = idr.format(converted);

        holder.name.setText(stalls.getName());
        switch (stalls.getName()) {
            case "Puding Plus Fla":
                holder.satuan.setText("/ loyang");
                break;
            case "Kambing Guling":
                holder.satuan.setText("/ ekor");
                break;
            case "Tengkleng":
                holder.satuan.setText("/ kuali");
                break;
            default:
                holder.satuan.setText("/ porsi");
                break;
        }
        holder.category.setText("Pondok " + stalls.getCategory());
        holder.price.setText(stallPrice);

        holder.addButton.setOnClickListener(v -> addItemToCart(holder.getAdapterPosition()));

    }

    private void addItemToCart(int adapterPosition) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        DatabaseReference cartFd = FirebaseDatabase.getInstance().getReference("carts_temp").child(mUid).push();
        Query dr = FirebaseDatabase.getInstance().getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(list.get(adapterPosition).getName());
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context.getApplicationContext(), list.get(adapterPosition).getName() + " is already in cart", Toast.LENGTH_SHORT).show();
                } else {
                    cartFd.child("name").setValue(list.get(adapterPosition).getName());
                    int itemPrice = Integer.parseInt(list.get(adapterPosition).getPrice_dk());
                    cartFd.child("price").setValue(itemPrice);
                    cartFd.child("img_url").setValue(list.get(adapterPosition).getImg_url());
                    cartFd.child("qty").setValue(1);
                    cartFd.child("sequence").setValue(Integer.parseInt("3"));
                    cartFd.child("category").setValue("Stall");
                    cartFd.child("type").setValue(list.get(adapterPosition).getType());
                    Toast.makeText(context.getApplicationContext(), list.get(adapterPosition).getName() + " is successfully added to cart", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface StallNewOrderInterface {

    }

    public static class StallViewHolder extends RecyclerView.ViewHolder {
        TextView name, price, category, satuan;
        MaterialButton addButton;
        ImageView stallImg;

        public StallViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.stall_name_tv);
            satuan = itemView.findViewById(R.id.per_porsi_dk_tv);
            category = itemView.findViewById(R.id.stall_category_tv);
            price = itemView.findViewById(R.id.stall_price_dk_tv);
            addButton = itemView.findViewById(R.id.add_button);
            stallImg = itemView.findViewById(R.id.stall_image_iv);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Stalls> filteredList) {
        list = filteredList;
        notifyDataSetChanged();
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private void addItemToCart(StallViewHolder holder, Stalls stalls) {
//        Log.d("angie", String.valueOf(stalls.getName()));
//        Log.d("angie", String.valueOf(stalls.getKey()));
//        DatabaseReference cartFd = FirebaseDatabase.getInstance().getReference("carts_temp").child("stalls").push();
//        cartFd.child("name").setValue(stalls.getName());
//        cartFd.child("price").setValue(stalls.getPrice());
//        cartFd.child("qty").setValue(String.valueOf(1));
//
//        list.remove(holder.getAdapterPosition());
//        notifyItemRemoved(holder.getAdapterPosition());
//    }

}