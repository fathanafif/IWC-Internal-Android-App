package com.iwc.iwctablet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.OrderItems;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class VisibleOrderItemAdapter extends RecyclerView.Adapter<VisibleOrderItemAdapter.OrderItemViewHolder> {

    Context context;
    ArrayList<OrderItems> listItem;

    public VisibleOrderItemAdapter(Context context, ArrayList<OrderItems> listItem) {
        this.context = context;
        this.listItem = listItem;
    }
    
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail_order, parent, false);
        return new OrderItemViewHolder(view);
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int orderItemPosition) {
        OrderItems orderItems = listItem.get(orderItemPosition);
        int subtotalInt = orderItems.getQty() * orderItems.getPrice();
        
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String subTotal = idr.format(subtotalInt);
        String itemPrice = idr.format(orderItems.getPrice());
        String itemQty = idr.format(orderItems.getQty());
        
        holder.no.setText(String.valueOf(orderItemPosition + 1));
        holder.name.setText(orderItems.getName());
        if (orderItems.getType().equals("")) {
            holder.category.setText(orderItems.getCategory());
        } else {
            holder.category.setText(orderItems.getCategory() + " (" + orderItems.getType() + ")");
        }
        holder.qty.setText(itemQty);
        holder.price.setText(itemPrice);
        holder.subtotal.setText(subTotal);
    }
    
    @Override
    public int getItemCount() {
        return listItem.size();
    }
    
    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        
        TextView no, name, qty, price, subtotal, category;
        
        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            no = itemView.findViewById(R.id.vos_number);
            name = itemView.findViewById(R.id.vos_item_name);
            category = itemView.findViewById(R.id.vos_item_category_type);
            qty = itemView.findViewById(R.id.vos_item_qty);
            price = itemView.findViewById(R.id.vos_item_price);
            subtotal = itemView.findViewById(R.id.vos_item_subtotal);
        }
    }

}