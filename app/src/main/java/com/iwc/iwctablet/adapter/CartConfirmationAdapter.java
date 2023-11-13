package com.iwc.iwctablet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.Cart;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class CartConfirmationAdapter extends RecyclerView.Adapter<CartConfirmationAdapter.CartViewHolder> {

    Context context;
    ArrayList<Cart> mCartList;

    public CartConfirmationAdapter(Context context, ArrayList<Cart> mCartList) {
        this.context = context;
        this.mCartList = mCartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_confirmation, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String mUid = mAuth.getUid();

        Cart cart = mCartList.get(position);

        int converted = cart.getPrice();
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String itemPrice = idr.format(converted);

        holder.number.setText(String.valueOf(position + 1));
        holder.name.setText(cart.getName());
        holder.categoryType.setText(cart.getCategory());
        holder.price.setText(itemPrice);
        holder.qty.setText(String.valueOf(cart.getQty()));

        // subtotal
        assert mUid != null;
        Query drSubTotal = fd.getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(cart.getName());
        drSubTotal.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String qty = String.valueOf(dataSnapshot.child("qty").getValue());
                    String price = String.valueOf(dataSnapshot.child("price").getValue());
                    int qtyInteger = Integer.parseInt(qty);
                    int priceInteger = Integer.parseInt(price);
                    int subTotal = qtyInteger * priceInteger;
                    String subTotalPriceFormat = idr.format(subTotal);
                    holder.subtotal.setText(subTotalPriceFormat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView number, name, categoryType, price, qty, subtotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            number = itemView.findViewById(R.id.oc_number);
            name = itemView.findViewById(R.id.oc_item_name);
            categoryType = itemView.findViewById(R.id.oc_item_category_type);
            price = itemView.findViewById(R.id.oc_item_price);
            qty = itemView.findViewById(R.id.oc_item_qty);
            subtotal = itemView.findViewById(R.id.oc_item_subtotal);

        }
    }

}