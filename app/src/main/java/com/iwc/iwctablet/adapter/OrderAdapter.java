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
import com.iwc.iwctablet.model.Orders;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    
    Context context;
    ArrayList<Orders> listOrder;
    OrderAdapter.OrderItemClickListener mItemListener;
    
    //    public OrderAdapter(Context context, ArrayList<Orders> listOrder, OrderItemClickListener itemClickListener) {
    public OrderAdapter(Context context, ArrayList<Orders> listOrder, OrderItemClickListener itemClickListener) {
        this.context = context;
        this.listOrder = listOrder;
        this.mItemListener = itemClickListener;
    }
    
    public interface OrderItemClickListener {
        void onItemClick(int orderPosition);
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_list, parent, false);
        return new OrderViewHolder(view);
    }
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int orderPosition) {
        Orders orders = listOrder.get(orderPosition);
        
        holder.orderDate.setText(orders.getCreated_at());
        holder.eventDate.setText(orders.getEvent_date());
        holder.orderId.setText(orders.getId());
        holder.cartCount.setText(orders.getTotal_item() + " items");
        holder.totalPrice.setText("Rp" + orders.getTotal_price());
        
        holder.showDetail.setOnClickListener(view -> mItemListener.onItemClick(orderPosition));
    }
    
    @Override
    public int getItemCount() {
        return listOrder.size();
    }
    
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        
        TextView orderDate, eventDate, cartCount, totalPrice, orderId;
        MaterialButton showDetail;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            
            orderDate = itemView.findViewById(R.id.order_date_value);
            eventDate = itemView.findViewById(R.id.event_date_value);
            orderId = itemView.findViewById(R.id.order_id_value);
            cartCount = itemView.findViewById(R.id.item_count_value);
            totalPrice = itemView.findViewById(R.id.total_price_value);
            showDetail = itemView.findViewById(R.id.show_detail_button);
        }
    }

//    public void orderDetail(@NonNull OrderAdapter.OrderViewHolder holder) {
//        Log.d("valerie", String.valueOf(holder));
//        FirebaseDatabase fd = FirebaseDatabase.getInstance();
//        DatabaseReference dr = fd.getReference().child("orders");
//
//        dr.orderByChild("customer_name").equalTo(holder.name.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()) {
//                    holder.customer_status.setVisibility(View.VISIBLE);
//                } else {
//                    holder.customer_status.setVisibility(View.INVISIBLE);
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) { }
//        });
//    }

}