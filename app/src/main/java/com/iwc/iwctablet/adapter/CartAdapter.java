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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    ArrayList<Cart> mCartList;

    public CartAdapter(Context context, ArrayList<Cart> mCartList) {
        this.context = context;
        this.mCartList = mCartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
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

        holder.name.setText(cart.getName());
        holder.category.setText(cart.getCategory());
        holder.price.setText(itemPrice);
        holder.qty.setText(String.valueOf(cart.getQty()));

        Glide.with(context)
                .load(mCartList.get(position).getImg_url())
                .placeholder(R.drawable.png_npt_small)
                .transform(new CenterCrop(), new RoundedCorners(12))
                .into(holder.imgCartItem);

        if (cart.getType() == null) {
            holder.type.setText("-");
        } else {
            holder.type.setText("(" + cart.getType() + ")");
        }

        // subtotal
        assert mUid != null;
        Query drSubTotal = fd.getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(cart.getName());
        drSubTotal.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    int qtyInteger = Integer.parseInt(String.valueOf(dataSnapshot.child("qty").getValue()));
                    int priceInteger = Integer.parseInt(String.valueOf(dataSnapshot.child("price").getValue()));
                    int subTotal = qtyInteger * priceInteger;
                    String subTotalPriceFormat = idr.format(subTotal);
                    holder.subtotal.setText(subTotalPriceFormat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // quantity text watcher
        holder.qty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Query dr = fd.getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(cart.getName());
                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                new CountDownTimer(2000, 2000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        if (holder.qty.length() == 0) {
                                            dataSnapshot.getRef().child("qty").setValue(1);
                                            notifyItemChanged(holder.getAdapterPosition());
                                            holder.qty.clearFocus();
                                        } else if (Integer.parseInt(holder.qty.getText().toString()) < 1) {
                                            dataSnapshot.getRef().child("qty").setValue(Integer.parseInt("1"));
                                            notifyItemChanged(holder.getAdapterPosition());
                                            holder.qty.clearFocus();
                                        } else {
                                            dataSnapshot.getRef().child("qty").setValue(Integer.parseInt(holder.qty.getText().toString()));
                                            holder.qty.clearFocus();
                                        }
                                    }
                                }.start();
                            }
                        } else {
                            Log.d("itemQty", "error");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        holder.addButton.setOnClickListener(v -> {
            Log.d("addButton", "success");
            int newQty = Integer.parseInt(String.valueOf(holder.qty.getText()));
            newQty++;
            Query dr = fd.getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(cart.getName());
            Log.d("addButton", cart.getName());
            int finalQty = newQty;
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Log.d("addButton", String.valueOf(dataSnapshot.child("qty")));
                            dataSnapshot.getRef().child("qty").setValue(finalQty);
                        }
                    } else {
                        Log.d("addButton", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        holder.minButton.setOnClickListener(v -> {
            if (holder.qty.getText().toString().equals("1")) {
                holder.minButton.setEnabled(false);
            } else {
                int newQty = Integer.parseInt(String.valueOf(holder.qty.getText()));
                newQty--;
                Query dr = fd.getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(cart.getName());
                int finalQty = newQty;
                dr.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("qty").setValue(finalQty);
                            }
                        } else {
                            Log.d("minButton", "error");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        holder.removeButton.setOnClickListener(v -> {
            Query dr = fd.getReference().child("carts_temp").child(mUid).orderByChild("name").equalTo(cart.getName());
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                        mCartList.remove(holder.getAdapterPosition());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            fd.getReference().child("carts_temp").child(mUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("getCartData", String.valueOf(snapshot.getChildrenCount()));
                    if (snapshot.exists()) {
                        int total = 0;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String string = String.valueOf(dataSnapshot.child("price").getValue());
                            int value = Integer.parseInt(string);
                            total = total + value;
                        }
                        Log.d("sumPrice", String.valueOf(total));

                    } else {
                        Log.d("sumPrice", "cart is empty");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        });

    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView name, category, type, price, subtotal;
        EditText qty;
        ImageView imgCartItem;
        MaterialButton addButton, minButton, removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name_cart_tv);
            type = itemView.findViewById(R.id.item_type_cart_tv);
            category = itemView.findViewById(R.id.item_category_cart_tv);
            price = itemView.findViewById(R.id.item_price_cart_tv);
            qty = itemView.findViewById(R.id.item_quantity_et);
            subtotal = itemView.findViewById(R.id.item_subtotal_cart_tv);
            imgCartItem = itemView.findViewById(R.id.img_cart_item);

            addButton = itemView.findViewById(R.id.add_button_cart);
            minButton = itemView.findViewById(R.id.minus_button_cart);
            removeButton = itemView.findViewById(R.id.remove_button_cart);
        }
    }

}