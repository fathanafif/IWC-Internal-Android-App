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

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    
    Context context;
    ArrayList<OrderItems> listItem;
    
    public OrderItemAdapter(Context context, ArrayList<OrderItems> listItem) {
        this.context = context;
        this.listItem = listItem;
    }
    
    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_invoice, parent, false);
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
        
        holder.no.setText(String.valueOf(orderItemPosition + 1));
        holder.name.setText(orderItems.getName() + " (" + orderItems.getCategory() + ")");
        holder.qty.setText(String.valueOf(orderItems.getQty()));
        holder.price.setText(itemPrice);
        holder.subtotal.setText(subTotal);
    }
    
    @Override
    public int getItemCount() {
        return listItem.size();
    }
    
    public static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        
        TextView no, name, qty, price, subtotal;
        
        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            
            no = itemView.findViewById(R.id.invoice_item_number_tv);
            name = itemView.findViewById(R.id.invoice_item_name_tv);
            qty = itemView.findViewById(R.id.invoice_item_qty_tv);
            price = itemView.findViewById(R.id.invoice_item_price_tv);
            subtotal = itemView.findViewById(R.id.invoice_item_subtotal_tv);
        }
    }

}