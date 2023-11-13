package com.iwc.iwctablet.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iwc.iwctablet.R;
import com.iwc.iwctablet.model.Customers;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    Context context;
    ArrayList<Customers> mCustomerList;
    CustomerAdapter.CustomerItemClickListener mItemListener;

    public CustomerAdapter(Context context, ArrayList<Customers> list, CustomerItemClickListener itemClickListener) {
        this.context = context;
        this.mCustomerList = list;
        this.mItemListener = itemClickListener;
    }

    public interface CustomerItemClickListener {
        void onItemClick(Customers customers);
    }

    @NonNull
    @Override
    public CustomerAdapter.CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.CustomerViewHolder holder, int customerPosition) {
        Customers customers = mCustomerList.get(customerPosition);

        holder.name.setText(customers.getName());
        holder.phone.setText(customers.getPhone());
        if (customers.getEmail().length() == 0) {
            holder.email.setText("-");
        } else {
            holder.email.setText(customers.getEmail());
        }
        holder.address.setText(customers.getAddress() + ", " + customers.getCity());
        holder.customer_id.setText("Customer ID: " + customers.getCustomer_id());
        holder.customer_cl.setOnClickListener(view -> mItemListener.onItemClick(mCustomerList.get(customerPosition)));

//        dealCustomer(holder);
    }

    @Override
    public int getItemCount() {
        return mCustomerList.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, email, address, customer_id, customer_status;
        ConstraintLayout customer_cl;
        ShapeableImageView iconEmail;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            customer_cl = itemView.findViewById(R.id.customer_cl);
            name = itemView.findViewById(R.id.customer_name_tv);
            customer_status = itemView.findViewById(R.id.customer_status_tv);
            customer_id = itemView.findViewById(R.id.customer_id_tv);
            phone = itemView.findViewById(R.id.customer_phone_tv);
            email = itemView.findViewById(R.id.customer_email_tv);
            address = itemView.findViewById(R.id.customer_address_tv);
            iconEmail = itemView.findViewById(R.id.icon_email);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<Customers> filteredList) {
        mCustomerList = filteredList;
        notifyDataSetChanged();
    }

//    public void dealCustomer(@NonNull CustomerViewHolder holder) {
//        FirebaseDatabase fd = FirebaseDatabase.getInstance();
//        DatabaseReference drOrder = fd.getReference().child("orders");
//        String customerId = holder.customer_id.getText().toString().substring(13);
//
//
//        drOrder.orderByChild("customer_id").equalTo(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
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