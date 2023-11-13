package com.iwc.iwctablet.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iwc.iwctablet.MainActivity;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.adapter.CustomerAdapter;
import com.iwc.iwctablet.adapter.OrderAdapter;
import com.iwc.iwctablet.adapter.OrderItemAdapter;
import com.iwc.iwctablet.adapter.TransactionAdapter;
import com.iwc.iwctablet.adapter.VisibleOrderItemAdapter;
import com.iwc.iwctablet.model.Customers;
import com.iwc.iwctablet.model.OrderItems;
import com.iwc.iwctablet.model.Orders;
import com.iwc.iwctablet.model.Transactions;
import com.iwc.iwctablet.utility.TimeUtility;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CustomerListFragment extends Fragment {

    //init
    FirebaseDatabase fd;
    DatabaseReference drCustomer, drOrder, drTransaction;
    StorageReference sr;
    CustomerAdapter customerAdapter;
    OrderAdapter orderAdapter;
    OrderItemAdapter orderItemAdapter;
    VisibleOrderItemAdapter visibleOrderItemAdapter;
    TransactionAdapter transactionAdapter;
    ArrayList<Customers> listCustomer;
    ArrayList<Orders> listOrder;
    ArrayList<OrderItems> listItemOrder;
    ArrayList<Transactions> listTransaction;
    RecyclerView customerRecyclerView, orderRecyclerView, orderItemRecyclerView, visibleOrderItemRecyclerView, transactionRecyclerView;
    MaterialButton buttonNewCustomer, buttonBack, clearSearch, buttonCopyPhone, buttonCopyEmail, buttonExpandOrderedItems, buttonReloadData, buttonShowInvoice, buttonShowPayment, buttonClosePayment, buttonAddNewTransaction;
    ImageView popUpImageTransaction;
    EditText nameInput, phoneInput, emailInput, addressInput, cityInput;
    LinearLayout mainContentLl, nameLl, phoneLl, emailLl, addressLl, cityLl, detailCustomerLl, orderDetailLl, newCustomerLl, eventInfoLl, defaultRightSheetLl;
    ConstraintLayout searchCustomerCl, newCustomerFooter, popUpUpdatePaymentCl, popUpInvoiceViewCl;
    CalendarView eventDateCv;
    TextView noCustomerTv;
    TextView customerListHeaderText, headerText, cusNameCd, cusIdCd, cusPhoneCd, cusEmailCd, cusAddressCd;
    MaterialButton buttonSaveCustomer, buttonResetForm, buttonCheck, buttonOpenPdf;
    TextInputEditText searchCustomer;
    ShimmerFrameLayout shimmerFrameLayout;
    ProgressBar invoicePb;
    AutoCompleteTextView transactionName;

    //init order specs
    TextView osName, osAddress, osPhone, osEventDate, osEventTime, osEventLocation, osGuests, osTheme, osCarpet, osDp1, osDp2, osDp3, osDp4, osCs, osId, osOrderDate, osTotalPrice, osOrderNote;
    TextView vosEventDate, vosEventTime, vosEventLocation, vosGuests, vosTheme, vosCarpet, vosDp1, vosDp2, vosDp3, vosDp4, vosDp1Date, vosDp2Date, vosDp3Date, vosDp4Date, vosCs, vosId, vosOrderDate, vosTotalPrice, vosOrderNote;
    LinearLayout orderSpecificationLayout, invoiceLl;
    LinearLayout orderSpecificationLl;

    boolean isNameRegistered, isPhoneRegistered, isEmailRegistered, generateStatus, expandStatus;
    String customerIdentifier, customerNameForTv, customerPhoneForTv, customerEmailForTv, customerAddressForTv, customerCityForTv, uploadedTransactionImgUrl, universalOrderId;
    File invoice;
    int orderPositionRealTime;
    Uri transactionImgUri;


    public CustomerListFragment() {
        // Required empty public constructor
    }

    private void viewInitialize(View view) {
        shimmerFrameLayout = view.findViewById(R.id.shimmer_customer_list);
        searchCustomer = view.findViewById(R.id.search_customer);
        customerRecyclerView = view.findViewById(R.id.customer_rv);
        orderRecyclerView = view.findViewById(R.id.order_list_rv);
        orderItemRecyclerView = view.findViewById(R.id.invoice_item_rv);
        visibleOrderItemRecyclerView = view.findViewById(R.id.ordered_item_rv);
        clearSearch = view.findViewById(R.id.clear_search);
        noCustomerTv = view.findViewById(R.id.no_customer_tv);
        buttonNewCustomer = view.findViewById(R.id.button_new_customer);
        buttonShowInvoice = view.findViewById(R.id.button_show_invoice);
        buttonBack = view.findViewById(R.id.back_button);
        headerText = view.findViewById(R.id.header_text);
        searchCustomerCl = view.findViewById(R.id.search_customer_cl);
        customerListHeaderText = view.findViewById(R.id.title_customer_list);

        newCustomerFooter = view.findViewById(R.id.right_sheet_footer);
        detailCustomerLl = view.findViewById(R.id.customer_detail_ll);
        newCustomerLl = view.findViewById(R.id.new_customer_ll);

        //new customer
        nameInput = view.findViewById(R.id.input_name);
        phoneInput = view.findViewById(R.id.input_phone);
        emailInput = view.findViewById(R.id.input_email);
        addressInput = view.findViewById(R.id.input_address);
        cityInput = view.findViewById(R.id.input_city);
        buttonResetForm = view.findViewById(R.id.clear_fields_button);
        buttonSaveCustomer = view.findViewById(R.id.save_customer_button);
        buttonCheck = view.findViewById(R.id.check_item_button);
        mainContentLl = view.findViewById(R.id.main_content_ll);
        nameLl = view.findViewById(R.id.name_set_ll);
        phoneLl = view.findViewById(R.id.phone_set_ll);
        emailLl = view.findViewById(R.id.email_set_ll);
        addressLl = view.findViewById(R.id.address_set_ll);
        cityLl = view.findViewById(R.id.city_set_ll);
        cusNameCd = view.findViewById(R.id.name_cd_tv);
        cusIdCd = view.findViewById(R.id.customer_id_tv);
        cusPhoneCd = view.findViewById(R.id.phone_cd_tv);
        cusEmailCd = view.findViewById(R.id.email_cd_tv);
        cusAddressCd = view.findViewById(R.id.address_cd_tv);
        buttonCopyPhone = view.findViewById(R.id.copy_phone_button);
        buttonCopyEmail = view.findViewById(R.id.copy_email_button);

        // order detail section
        orderSpecificationLayout = view.findViewById(R.id.order_specification_ll);
        defaultRightSheetLl = view.findViewById(R.id.default_sheet_ll);
        transactionRecyclerView = view.findViewById(R.id.transaction_rv);
        popUpUpdatePaymentCl = view.findViewById(R.id.update_payment_mother_cl);
        buttonShowPayment = view.findViewById(R.id.show_payment_history_button);
        buttonClosePayment = view.findViewById(R.id.button_close_payment_history);
        popUpImageTransaction = view.findViewById(R.id.pop_up_receipt_transaction_iv);
        popUpInvoiceViewCl = view.findViewById(R.id.invoice_view_mother_cl);
        buttonAddNewTransaction = view.findViewById(R.id.add_new_transaction_button);
        transactionName = view.findViewById(R.id.new_transaction_name_actv);
        eventDateCv = view.findViewById(R.id.event_date_cv);
        invoicePb = view.findViewById(R.id.invoice_pb);
        eventInfoLl = view.findViewById(R.id.event_info_ll);
        buttonReloadData = view.findViewById(R.id.button_reload_detail_order);
        buttonExpandOrderedItems = view.findViewById(R.id.expand_ordered_items_button);
        buttonOpenPdf = view.findViewById(R.id.open_file_button);
        orderDetailLl = view.findViewById(R.id.order_detail_ll);
        invoiceLl = view.findViewById(R.id.invoice_layout_ll);
        osTotalPrice = view.findViewById(R.id.invoice_total_price_tv);
        osName = view.findViewById(R.id.os_customer_name);
        osAddress = view.findViewById(R.id.os_customer_address);
        osPhone = view.findViewById(R.id.os_customer_phone);
        osEventDate = view.findViewById(R.id.os_event_date);
        osEventTime = view.findViewById(R.id.os_event_time);
        osEventLocation = view.findViewById(R.id.os_event_location);
        osGuests = view.findViewById(R.id.os_guests);
        osTheme = view.findViewById(R.id.os_theme);
        osCarpet = view.findViewById(R.id.os_carpet);
        osDp1 = view.findViewById(R.id.os_dp_1_date);
        osDp2 = view.findViewById(R.id.os_dp_2_date);
        osDp3 = view.findViewById(R.id.os_dp_3_date);
        osDp4 = view.findViewById(R.id.os_dp_4_date);
        osId = view.findViewById(R.id.os_order_id);
        osOrderDate = view.findViewById(R.id.os_order_date);
        osOrderNote = view.findViewById(R.id.os_order_note);
        osCs = view.findViewById(R.id.os_cs);

        vosTotalPrice = view.findViewById(R.id.vos_total_price);
//        vosName = view.findViewById(R.id.vos_customer_name);
//        vosIdCustomer = view.findViewById(R.id.vos_customer_id);
//        vosAddress = view.findViewById(R.id.vos_customer_address);
//        vosPhone = view.findViewById(R.id.vos_customer_phone_email);
        vosEventDate = view.findViewById(R.id.vos_event_date);
        vosEventTime = view.findViewById(R.id.vos_event_time);
        vosEventLocation = view.findViewById(R.id.vos_event_location);
        vosGuests = view.findViewById(R.id.vos_guests);
        vosTheme = view.findViewById(R.id.vos_theme);
        vosCarpet = view.findViewById(R.id.vos_carpet_heading);
        vosDp1 = view.findViewById(R.id.vos_dp_1_amount);
        vosDp2 = view.findViewById(R.id.vos_dp_2_amount);
        vosDp3 = view.findViewById(R.id.vos_dp_3_amount);
        vosDp4 = view.findViewById(R.id.vos_dp_4_amount);
        vosDp1Date = view.findViewById(R.id.vos_dp_1_date);
        vosDp2Date = view.findViewById(R.id.vos_dp_2_date);
        vosDp3Date = view.findViewById(R.id.vos_dp_3_date);
        vosDp4Date = view.findViewById(R.id.vos_dp_4_date);
        vosId = view.findViewById(R.id.vos_order_id);
        vosOrderDate = view.findViewById(R.id.vos_order_date);
        vosOrderNote = view.findViewById(R.id.vos_order_note);
        vosCs = view.findViewById(R.id.vos_customer_service);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);
        viewInitialize(view);

        fd = FirebaseDatabase.getInstance();
        drCustomer = fd.getReference("customers");
        drOrder = fd.getReference("orders");
        drTransaction = fd.getReference("transactions");
        customerRecyclerView.setHasFixedSize(true);
        customerRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        orderRecyclerView.setHasFixedSize(true);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        clearSearch.setVisibility(View.GONE);
        buttonBack.setVisibility(View.GONE);
        buttonShowInvoice.setVisibility(View.GONE);
        buttonReloadData.setVisibility(View.GONE);

        getCustomers();

        searchCustomer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(searchCustomer.getText()).toString().equals("")) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                customerFilter(s.toString());
            }
        });
        buttonNewCustomer.setOnClickListener(v -> newCustomer());
        clearSearch.setOnClickListener(v -> {
            searchCustomer.setText("");
            clearSearch.setVisibility(View.GONE);
        });
        searchCustomer.clearFocus();

        buttonCopyPhone.setOnClickListener(v -> copyPhone());
        buttonCopyEmail.setOnClickListener(v -> copyEmail());
        newCustomerLl.setVisibility(View.GONE);

        return view;
    }

    private void getCustomers() {
        orderRecyclerView.setVisibility(View.GONE);
        customerRecyclerView.setVisibility(View.GONE);
        shimmerFrameLayout.startShimmer();
        drCustomer.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listCustomer = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Customers customers = dataSnapshot.getValue(Customers.class);
                        listCustomer.add(customers);
                    }
//                    customerAdapter.notifyDataSetChanged();
                    customerAdapter = new CustomerAdapter(requireActivity(), listCustomer, customers -> getCustomerBio(customers.getCustomer_id()));
                    customerRecyclerView.setAdapter(customerAdapter);
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
                    customerRecyclerView.setLayoutManager(gridLayoutManager);
                    final Handler showCustomerRvPostDelayed = new Handler();
                    showCustomerRvPostDelayed.postDelayed(() -> {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        customerRecyclerView.setVisibility(View.VISIBLE);
                    }, 3000);
                } else {
                    shimmerFrameLayout.setVisibility(View.GONE);
                    noCustomerTv.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getCustomerBio(String customerId) {
        customerIdentifier = customerId;
        customerListHeaderText.setVisibility(View.GONE);
        hideKeyboard();
//        buttonShare.setVisibility(View.GONE);
        buttonReloadData.setVisibility(View.GONE);
        orderDetailLl.setVisibility(View.GONE);
        mainContentLl.setVisibility(View.GONE);
        searchCustomerCl.setVisibility(View.GONE);
        buttonNewCustomer.setVisibility(View.GONE);
        headerText.setVisibility(View.VISIBLE);
        buttonBack.setVisibility(View.VISIBLE);
        detailCustomerLl.setVisibility(View.VISIBLE);
        headerText.setText("Customer Details");
        buttonBack.setOnClickListener(v -> {
            customerListHeaderText.setVisibility(View.VISIBLE);
            buttonBack.setVisibility(View.GONE);
            buttonShowInvoice.setVisibility(View.GONE);
            mainContentLl.setVisibility(View.VISIBLE);
            searchCustomerCl.setVisibility(View.VISIBLE);
            buttonNewCustomer.setVisibility(View.VISIBLE);
        });

        // get customer bio from database
        drCustomer.orderByChild("customer_id").equalTo(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        customerNameForTv = Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        customerPhoneForTv = Objects.requireNonNull(dataSnapshot.child("phone").getValue()).toString();
                        customerEmailForTv = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                        customerAddressForTv = Objects.requireNonNull(dataSnapshot.child("address").getValue()).toString();
                        customerCityForTv = Objects.requireNonNull(dataSnapshot.child("city").getValue()).toString();
                        customerIdentifier = Objects.requireNonNull(dataSnapshot.child("customer_id").getValue()).toString();

                        cusNameCd.setText(customerNameForTv);
                        cusIdCd.setText("Customer ID: " + customerIdentifier);
                        cusPhoneCd.setText(customerPhoneForTv);
                        if (customerEmailForTv.length() == 0) {
                            cusEmailCd.setText("-");
                            buttonCopyEmail.setVisibility(View.GONE);
                        } else {
                            cusEmailCd.setText(customerEmailForTv);
                            buttonCopyEmail.setVisibility(View.VISIBLE);
                        }
                        cusAddressCd.setText(customerAddressForTv + ", " + customerCityForTv);
                    }
                    // get customer's order list
                    getCustomerOrderList();
                } else {
                    cusIdCd.setText("");
                    cusPhoneCd.setText("");
                    cusEmailCd.setText("");
                    cusAddressCd.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCustomerOrderList() {
        TextView noOrderTv = requireView().findViewById(R.id.no_order_history);
        noOrderTv.setVisibility(View.GONE);
        drOrder.orderByChild("customer_id").equalTo(customerIdentifier).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listOrder = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Orders orders = dataSnapshot.getValue(Orders.class);
                        listOrder.add(orders);
                    }
                    orderRecyclerView.setVisibility(View.VISIBLE);
                    orderAdapter = new OrderAdapter(requireActivity(), listOrder, orderPosition -> getOrderDetail(orderPosition));
                    GridLayoutManager orderGlm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                    orderRecyclerView.setLayoutManager(orderGlm);
                    orderRecyclerView.setAdapter(orderAdapter);
                    orderAdapter.notifyDataSetChanged();
                } else {
                    orderRecyclerView.setVisibility(View.GONE);
                    noOrderTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getOrderDetail(int orderPosition) {
        expandStatus = false;
        defaultRightSheetLl.setVisibility(View.VISIBLE);
        invoicePb.setVisibility(View.GONE);
        buttonReloadData.setVisibility(View.VISIBLE);
        String str = customerNameForTv.toLowerCase();
        String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
        buttonExpandOrderedItems.setOnClickListener(v -> {
            if (!expandStatus) {
                eventInfoLl.setVisibility(View.GONE);
                expandStatus = true;
                buttonExpandOrderedItems.setRotation(-90);
            } else {
                eventInfoLl.setVisibility(View.VISIBLE);
                expandStatus = false;
                buttonExpandOrderedItems.setRotation(90);
            }
        });
        buttonReloadData.setOnClickListener(v -> {
            getOrderDetail(orderPosition);
            Toast.makeText(requireContext(), "Data was reloaded.", Toast.LENGTH_SHORT).show();
        });
        buttonShowPayment.setOnClickListener(v -> defaultRightSheetLl.setVisibility(View.GONE));
        buttonClosePayment.setOnClickListener(v -> defaultRightSheetLl.setVisibility(View.VISIBLE));

        headerText.setText( cap + "'s Order Details");
        newCustomerLl.setVisibility(View.GONE);
        detailCustomerLl.setVisibility(View.VISIBLE);
        buttonShowInvoice.setVisibility(View.VISIBLE);
        orderDetailLl.setVisibility(View.VISIBLE);

        buttonBack.setOnClickListener(v -> {
            buttonShowInvoice.setVisibility(View.GONE);
            getCustomerBio(customerIdentifier);
        });

        drCustomer.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getOrderSpecInfo(orderPosition);
                getOrderItem(orderPosition);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        drTransaction.child(listOrder.get(orderPosition).getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listTransaction = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Transactions transactions = dataSnapshot.getValue(Transactions.class);
                        listTransaction.add(transactions);
                    }
                    transactionAdapter = new TransactionAdapter(requireActivity(), listTransaction, transactions  -> getTransactionInfo(transactions.getImg_url()));
                    transactionRecyclerView.setAdapter(transactionAdapter);
                    GridLayoutManager transactionGlm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                    transactionRecyclerView.setLayoutManager(transactionGlm);
                } else {
                    Log.d("valerie", "failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonShowInvoice.setOnClickListener(v -> showInvoice(orderPosition));

        updateTransaction();

        Handler test = new Handler();
        test.postDelayed(() -> {
            String valerie = osEventDate.getText().toString();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat f = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date d = f.parse(valerie);
                long milli = d.getTime();
                eventDateCv.setDate(milli);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("valerie", valerie);
        }, 1200);

        buttonOpenPdf.setVisibility(View.INVISIBLE);

//        final Handler handler = new Handler();
//        handler.postDelayed(() -> {
//            createInvoicePdf(orderPosition);
//            if(!generateStatus) {
//                Toast.makeText(requireContext(), "Generate failed", Toast.LENGTH_SHORT).show();
//            } else {
//                invoicePb.setVisibility(View.GONE);
//                buttonOpenPdf.setVisibility(View.VISIBLE);
//                invoiceLl.setVisibility(View.VISIBLE);
//
//                buttonOpenPdf.setOnClickListener(v -> {
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(Uri.fromFile(invoice), "application/pdf");
//                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    startActivity(intent);
//                });
//            }
//            buttonOpenFile.setOnClickListener(v -> {
//                Intent target = new Intent(Intent.ACTION_VIEW);
////            target.addCategory(Intent.CATEGORY_OPENABLE);
//                target.setDataAndType(Uri.fromFile(invoice), "application/pdf");
//                target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                Intent intent = Intent.createChooser(target, "Open File");
//                try {
//                    startActivity(intent);
//                } catch (ActivityNotFoundException e) {
//                    Log.d("valerie", "Can't open file,");
//                }
//            });

//        }, 1000);

    }

    @SuppressLint("SetTextI18n")
    // invoice preview
    private void getOrderSpecInfo(int orderPosition) {
        // vos -> order specification (pdf preview)
        osName.setText(customerNameForTv);
        if (customerEmailForTv.length() == 0) {
            osPhone.setText(customerPhoneForTv + " / -");
        } else {
            osPhone.setText(customerPhoneForTv + " / " + customerEmailForTv);
        }
        osAddress.setText(customerAddressForTv + ", " + customerCityForTv);
        osId.setText(listOrder.get(orderPosition).getId());
        osOrderDate.setText(listOrder.get(orderPosition).getCreated_at());
        osCs.setText(listOrder.get(orderPosition).getCustomer_service());
        osEventDate.setText(listOrder.get(orderPosition).getEvent_date());
        osEventTime.setText(listOrder.get(orderPosition).getTime_start() + " s/d " + listOrder.get(orderPosition).getTime_end());
        osEventLocation.setText(listOrder.get(orderPosition).getEvent_location());
        osGuests.setText(listOrder.get(orderPosition).getNumber_of_guests() + " tamu undangan");
        osTheme.setText(listOrder.get(orderPosition).getTheme());
        osCarpet.setText(listOrder.get(orderPosition).getCarpet_heading());
        osDp1.setText(listOrder.get(orderPosition).getDp_1_amount() + " / " + listOrder.get(orderPosition).getDp_1_date());
        osDp2.setText(listOrder.get(orderPosition).getDp_2_amount() + " / " + listOrder.get(orderPosition).getDp_2_date());
        osDp3.setText(listOrder.get(orderPosition).getDp_3_amount() + " / " + listOrder.get(orderPosition).getDp_3_date());
        osDp4.setText(listOrder.get(orderPosition).getDp_4_amount() + " / " + listOrder.get(orderPosition).getDp_4_date());
        osTotalPrice.setText("Rp" + listOrder.get(orderPosition).getTotal_price());
        osOrderNote.setText(listOrder.get(orderPosition).getOrder_note());

        if (listOrder.get(orderPosition).getDp_1_amount().equals("") || listOrder.get(orderPosition).getDp_1_date().equals("")) {
            osDp1.setText("-");
        } else {
            osDp1.setText(listOrder.get(orderPosition).getDp_1_amount() + " / " + listOrder.get(orderPosition).getDp_1_date());
        }
        if (listOrder.get(orderPosition).getDp_2_amount().equals("") || listOrder.get(orderPosition).getDp_2_date().equals("")) {
            osDp2.setText("-");
        } else {
            osDp2.setText(listOrder.get(orderPosition).getDp_2_amount() + " / " + listOrder.get(orderPosition).getDp_2_date());
        }
        if (listOrder.get(orderPosition).getDp_3_amount().equals("") || listOrder.get(orderPosition).getDp_3_date().equals("")) {
            osDp3.setText("-");
        } else {
            osDp3.setText(listOrder.get(orderPosition).getDp_3_amount() + " / " + listOrder.get(orderPosition).getDp_3_date());
        }
        if (listOrder.get(orderPosition).getDp_4_amount().equals("") || listOrder.get(orderPosition).getDp_4_date().equals("")) {
            osDp4.setText("-");
        } else {
            osDp4.setText(listOrder.get(orderPosition).getDp_4_amount() + " / " + listOrder.get(orderPosition).getDp_4_date());
        }

        // vos -> visible order specification (not pdf preview)
//        vosName.setText(customerNameForTv);
//        vosIdCustomer.setText("(ID: " + customerIdentifier + ")");
//        if (customerEmailForTv.length() == 0) {
//            vosPhone.setText(customerPhoneForTv + " / -");
//        } else {
//            vosPhone.setText(customerPhoneForTv + " / " + customerEmailForTv);
//        }
//        vosAddress.setText(customerAddressForTv + ", " + customerCityForTv);

        vosId.setText(listOrder.get(orderPosition).getId());
        vosOrderDate.setText(listOrder.get(orderPosition).getCreated_at());
        vosCs.setText(listOrder.get(orderPosition).getCustomer_service());
        vosEventDate.setText(listOrder.get(orderPosition).getEvent_date());
        vosEventTime.setText(listOrder.get(orderPosition).getTime_start() + " s/d " + listOrder.get(orderPosition).getTime_end());
        vosEventLocation.setText(listOrder.get(orderPosition).getEvent_location());
        vosGuests.setText(listOrder.get(orderPosition).getNumber_of_guests() + " tamu udangan");
        vosTheme.setText(listOrder.get(orderPosition).getTheme());
        vosCarpet.setText(listOrder.get(orderPosition).getCarpet_heading());
        vosOrderNote.setText(listOrder.get(orderPosition).getOrder_note());

        if (listOrder.get(orderPosition).getDp_1_amount().length() == 0) {
            vosDp1.setText("-");
        } else {
            vosDp1.setText(listOrder.get(orderPosition).getDp_1_amount());
        }
        if (listOrder.get(orderPosition).getDp_1_date().length() == 0) {
            vosDp1Date.setText("-");
        } else {
            vosDp1Date.setText(listOrder.get(orderPosition).getDp_1_date());
        }

        if (listOrder.get(orderPosition).getDp_2_amount().length() == 0) {
            vosDp2.setText("-");
        } else {
            vosDp2.setText(listOrder.get(orderPosition).getDp_2_amount());
        }
        if (listOrder.get(orderPosition).getDp_2_date().length() == 0) {
            vosDp2Date.setText("-");
        } else {
            vosDp2Date.setText(listOrder.get(orderPosition).getDp_2_date());
        }

        if (listOrder.get(orderPosition).getDp_3_amount().length() == 0) {
            vosDp3.setText("-");
        } else {
            vosDp3.setText(listOrder.get(orderPosition).getDp_3_amount());
        }
        if (listOrder.get(orderPosition).getDp_3_date().length() == 0) {
            vosDp3Date.setText("-");
        } else {
            vosDp3Date.setText(listOrder.get(orderPosition).getDp_3_date());
        }

        if (listOrder.get(orderPosition).getDp_4_amount().length() == 0) {
            vosDp4.setText("-");
        } else {
            vosDp4.setText(listOrder.get(orderPosition).getDp_4_amount());
        }
        if (listOrder.get(orderPosition).getDp_4_date().length() == 0) {
            vosDp4Date.setText("-");
        } else {
            vosDp4Date.setText(listOrder.get(orderPosition).getDp_4_date());
        }

        vosTotalPrice.setText("Rp" + listOrder.get(orderPosition).getTotal_price());

//        buttonShare.setOnClickListener(v -> {

//            String nameForContact = osName.getText().toString() +" (IWC Customer " + customerIdentifier + ")";
//
//            ArrayList<ContentProviderOperation> ops = new ArrayList<>();
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
//                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
//                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
//                    .build());
//
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, nameForContact).build());
//
//            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, customerPhoneForTv)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
//                    .build());
//
//            // Asking the Contact provider to create a new contact
//            try {
//                requireActivity().getApplicationContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//            } catch (Exception e) {
//                e.printStackTrace();
//                Toast.makeText(requireActivity().getApplicationContext(), "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }

//
//            String phoneStr = customerPhoneForTv;
//            String replacement = "62";
//            String result = replacement + phoneStr.substring(1);
//
//            if (!invoice.exists()) {
//                Log.d("valerie", "gagal bos");
//            }
//            Intent sendIntent = new Intent(Intent.ACTION_SEND); //initalize
//            sendIntent.setType("application/pdf"); //type
//            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + invoice)); //target file
//            sendIntent.putExtra("jid", result + "@s.whatsapp.net"); //phone number without "+" prefix
//            sendIntent.setPackage("com.whatsapp"); //provider
//            startActivity(sendIntent);


//            final Handler handler = new Handler();
//            handler.postDelayed(() -> {
//                String phoneStr = customerPhoneForTv;
//                String replacement = "62";
//                String result = replacement + phoneStr.substring(1);
//
//                Intent sendIntent = new Intent(Intent.ACTION_SEND); //initialize
//                sendIntent.putExtra("jid", result + "@s.whatsapp.net"); //phone number without "+" prefix
//                sendIntent.setType("application/pdf"); //type
//                sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + invoice)); //target file
//                sendIntent.setPackage("com.whatsapp");
//
//                // Give your message here
//                sendIntent.putExtra(Intent.EXTRA_TEXT,"message");
//
//                // Checking whether Whatsapp
//                // is installed or not
////            if (sendIntent
////                    .resolveActivity(requireContext().getPackageManager()) == null) {
////                Toast.makeText(requireContext(), "Please install whatsapp first.", Toast.LENGTH_SHORT).show();
////                return;
////            }
//
//                // Starting Whatsapp
//                startActivity(sendIntent);
//            }, 10000);

//            Log.d("valerie", "handle share temporary");
//        });

    }

    private void getOrderItem(int orderPosition) {
        drOrder.orderByChild("id").equalTo(listOrder.get(orderPosition).getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            ds.child("items").getRef().orderByChild("sequence").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        listItemOrder = new ArrayList<>();
                                        for (DataSnapshot dsItemOrder : snapshot.getChildren()) {
                                            OrderItems orderItems = dsItemOrder.getValue(OrderItems.class);
                                            listItemOrder.add(orderItems);
                                        }
                                        orderItemAdapter = new OrderItemAdapter(requireActivity(), listItemOrder);
                                        orderItemRecyclerView.setAdapter(orderItemAdapter);
                                        GridLayoutManager itemGlm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                                        orderItemRecyclerView.setLayoutManager(itemGlm);

                                        visibleOrderItemAdapter = new VisibleOrderItemAdapter(requireActivity(), listItemOrder);
                                        visibleOrderItemRecyclerView.setAdapter(visibleOrderItemAdapter);
                                        GridLayoutManager visibleItemGlm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                                        visibleOrderItemRecyclerView.setLayoutManager(visibleItemGlm);
                                    } else {
                                        Log.d("valerie", "failed");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                } else {
                    Log.d("valerie", "failed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showInvoice(int orderPosition) {
        Log.d("valerie", "works");
        popUpInvoiceViewCl.setVisibility(View.VISIBLE);
        getOrderSpecInfo(orderPosition);
        ((MainActivity) requireActivity()).setNavDarkTransparent();
        popUpInvoiceViewCl.setOnClickListener(v -> {
            popUpInvoiceViewCl.setVisibility(View.GONE);
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        });
    }

    private void createInvoicePdf(int orderPosition) {

    }

    @SuppressLint("SetTextI18n")
    private void newCustomer() {
        hideKeyboard();
        buttonCheck.setVisibility(View.GONE);
        mainContentLl.setVisibility(View.GONE);
        orderDetailLl.setVisibility(View.GONE);
        searchCustomerCl.setVisibility(View.GONE);
        detailCustomerLl.setVisibility(View.GONE);
        buttonNewCustomer.setVisibility(View.GONE);
        buttonBack.setVisibility(View.VISIBLE);
        customerListHeaderText.setVisibility(View.GONE);
        headerText.setVisibility(View.VISIBLE);
        newCustomerLl.setVisibility(View.VISIBLE);

        headerText.setText("New Customer");
        formHandler();
        checkAllAvailability();
        buttonCheckOnClickedListener();
        buttonBack.setOnClickListener(v -> {
            searchCustomerCl.setVisibility(View.VISIBLE);
            buttonBack.setVisibility(View.GONE);
            newCustomerLl.setVisibility(View.GONE);
            customerListHeaderText.setVisibility(View.VISIBLE);
            mainContentLl.setVisibility(View.VISIBLE);
            buttonNewCustomer.setVisibility(View.VISIBLE);
        });
        buttonSaveCustomer.setOnClickListener(v -> {
            // checking for null fields
            if (!checkAllFields()) {
                Toast.makeText(requireActivity().getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
            } else {
                // checking for redundant data (name, phone, or email)
                if (!isNameRegistered) {
                    if (!isPhoneRegistered) {
                        if (!isEmailRegistered) {
                            saveCustomer();
                        } else {
                            Toast.makeText(requireActivity().getApplicationContext(), "Email is registered. Please use another email.", Toast.LENGTH_SHORT).show();
                            emailInput.setText("");
                            emailInput.requestFocus();
                        }
                    } else {
                        Toast.makeText(requireActivity().getApplicationContext(), "Phone number is registered. Please use another phone number.", Toast.LENGTH_SHORT).show();
                        phoneInput.setText("");
                        phoneInput.requestFocus();
                    }
                } else {
                    Toast.makeText(requireActivity().getApplicationContext(), "Name is registered. Please use another name.", Toast.LENGTH_SHORT).show();
                    nameInput.setText("");
                    nameInput.requestFocus();
                }
            }
        });
        buttonResetForm.setOnClickListener(v -> clearForm());

        defaultForm();

    }

    private void updateTransaction() {
        ImageView uploadReceiptButton = requireView().findViewById(R.id.upload_receipt_button);
        TextView datePopUp = requireView().findViewById(R.id.date_update_transaction_tv);

        ArrayAdapter<CharSequence> transactionNameAdapter = ArrayAdapter.createFromResource(requireActivity().getApplicationContext(), R.array.transaction_name, R.layout.item_transaction_name);
        transactionName.setAdapter(transactionNameAdapter);

        uploadReceiptButton.setOnClickListener(v -> selectImage());

        buttonAddNewTransaction.setOnClickListener(v -> {
            MaterialButton storeButton = requireView().findViewById(R.id.store_receipt_button);
            popUpUpdatePaymentCl.setVisibility(View.VISIBLE);
            ((MainActivity) requireActivity()).setNavDarkTransparent();
            datePopUp.setText(new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date()));

            popUpUpdatePaymentCl.setOnClickListener(v1 -> {
                popUpUpdatePaymentCl.setVisibility(View.GONE);
                ((MainActivity) requireActivity()).setNavNotDarkTransparent();
            });

            storeButton.setOnClickListener(v2 -> storeTransactionToDatabase());
        });
    }

    private void getTransactionInfo(String transactionImg) {
        LinearLayout viewReceiptLl = requireView().findViewById(R.id.view_receipt_ll);
        LinearLayout newTransactionLl = requireView().findViewById(R.id.new_transaction_ll);
        ImageView receiptView = requireView().findViewById(R.id.view_receipt_iv);

        Log.d("valerie", transactionImg);
        popUpUpdatePaymentCl.setVisibility(View.VISIBLE);
        ((MainActivity) requireActivity()).setNavDarkTransparent();
        viewReceiptLl.setVisibility(View.VISIBLE);

        popUpUpdatePaymentCl.setOnClickListener(v -> {
            viewReceiptLl.setVisibility(View.GONE);
            popUpUpdatePaymentCl.setVisibility(View.GONE);
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        });

        Glide.with(requireContext())
                .load(transactionImg)
                .placeholder(R.drawable.png_npt)
                .transform(new FitCenter(), new RoundedCorners(12))
                .into(receiptView);

    }

    private void storeTransactionToDatabase() {
        uploadedTransactionImgUrl = "";
        String orderId = osId.getText().toString();
        if (transactionImgUri != null) {
            if (transactionName.getText().length() != 0) {
                drTransaction.child(orderId).orderByChild("name").equalTo(transactionName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(requireActivity(), transactionName.getText() +" is already exist.", Toast.LENGTH_SHORT).show();
                        } else {
                            sr = FirebaseStorage.getInstance().getReference().child("iwc_app/transactions/" + orderId + "/" + transactionName.getText().toString() + "." + getFileExtension(transactionImgUri));
                            sr.putFile(transactionImgUri).addOnSuccessListener(taskSnapshot -> sr.getDownloadUrl().addOnSuccessListener(uri -> {
                                uploadedTransactionImgUrl = uri.toString();
                                Transactions transactions = new Transactions();
                                transactions.setImg_url(uploadedTransactionImgUrl);
                                transactions.setDate_saved(new SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(new Date()));
                                transactions.setName(transactionName.getText().toString());
                                drTransaction.child(orderId).push().setValue(transactions).addOnCompleteListener(task -> {
                                    Log.d("valerie", "push successful");
                                });
                                popUpImageTransaction.setImageURI(null);
                                popUpUpdatePaymentCl.setVisibility(View.GONE);
                                ((MainActivity) requireActivity()).setNavNotDarkTransparent();
                            }));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else { Toast.makeText(requireActivity(), "Please fill the name of transaction.", Toast.LENGTH_SHORT).show(); }
        } else { Toast.makeText(requireActivity(), "Please select transaction recipt image.", Toast.LENGTH_SHORT).show(); }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch("image/*");
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        transactionImgUri = uri;
        popUpImageTransaction.setImageURI(transactionImgUri);
    });





































































































// =================================================================================================


    @SuppressLint("NotifyDataSetChanged")
    private void customerFilter(String text) {
        ArrayList<Customers> filteredCustomerList = new ArrayList<>();
        for (Customers item : listCustomer) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredCustomerList.add(item);
            }
            customerAdapter.filterList(filteredCustomerList);
            customerAdapter.notifyDataSetChanged();
        }
    }

    private void checkAllAvailability() {
        phoneInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPhoneAvailability();
            }
        });
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEmailAvailability();
            }
        });
    }

    private void clearForm() {
        nameInput.setText("");
        phoneInput.setText("");
        emailInput.setText("");
        addressInput.setText("");
        cityInput.setText("");
        defaultForm();
    }

    private boolean checkAllFields() {
        String email = emailInput.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (nameInput.length() == 0) {
            return false;
        }
        if (phoneInput.length() <= 10) {
            return false;
        }
        if (emailInput.length() != 0) {
            if (!email.matches(emailPattern)) {
                return false;
            }
        }

        if (addressInput.length() == 0) {
            return false;
        }
        return cityInput.length() != 0;
    }

    private void checkPhoneAvailability() {
        String phone = String.valueOf(phoneInput.getText());
//        Log.d("valerie", phone);

        drCustomer.orderByChild("phone").equalTo(phone).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isPhoneRegistered = snapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkEmailAvailability() {
        String email = String.valueOf(emailInput.getText());
//        Log.d("valerie", email);

        drCustomer.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (email.length() == 0) {
                    isEmailRegistered = false;
                } else {
                    isEmailRegistered = snapshot.exists();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void saveCustomer() {
        Customers customers = new Customers();
//        String idOnly = RandomIDGenerator.randomString(RandomIDGenerator.CHARSET_AZ_09, 4);
        long longIdOnly = System.currentTimeMillis() / 100000;
        String customerId = String.valueOf(longIdOnly);
        String timeCreatedAt = new TimeUtility().getCustomerAddedTime() + ", " + new TimeUtility().getTodayDate();
        customers.setName(Objects.requireNonNull(nameInput.getText()).toString().toUpperCase());
        customers.setPhone(Objects.requireNonNull(phoneInput.getText()).toString());
        customers.setEmail(Objects.requireNonNull(emailInput.getText()).toString());
        customers.setAddress(Objects.requireNonNull(addressInput.getText()).toString().toLowerCase());
        customers.setCity(Objects.requireNonNull(cityInput.getText()).toString().toUpperCase());
        customers.setCreated_at(timeCreatedAt);
        customers.setCustomer_id(customerId);

        drCustomer.push().setValue(customers);
        noCustomerTv.setVisibility(View.GONE);
        clearForm();
        Toast.makeText(requireContext().getApplicationContext(), "Customer data was successfully saved to database.", Toast.LENGTH_SHORT).show();

        hideKeyboard();
        nameInput.clearFocus();
        phoneLl.clearFocus();
        emailLl.clearFocus();
        addressLl.clearFocus();
        cityLl.clearFocus();
        nameLl.setVisibility(View.VISIBLE);
        phoneLl.setVisibility(View.VISIBLE);
        emailLl.setVisibility(View.VISIBLE);
        addressLl.setVisibility(View.VISIBLE);
        cityLl.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.GONE);

        listCustomer = new ArrayList<>();
        customerAdapter.notifyDataSetChanged();
        mainContentLl.setVisibility(View.VISIBLE);
        newCustomerLl.setVisibility(View.GONE);
        headerText.setVisibility(View.GONE);
        buttonBack.setVisibility(View.GONE);
        searchCustomerCl.setVisibility(View.VISIBLE);
        buttonNewCustomer.setVisibility(View.VISIBLE);
    }

    private void formHandler() {
        nameInput.setOnFocusChangeListener((v, hasFocus) -> {
            phoneLl.setVisibility(View.GONE);
            emailLl.setVisibility(View.GONE);
            addressLl.setVisibility(View.GONE);
            cityLl.setVisibility(View.GONE);
            buttonCheck.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(v1 -> showCustomerList());
        });
        phoneInput.setOnFocusChangeListener((v, hasFocus) -> {
            emailLl.setVisibility(View.GONE);
            addressLl.setVisibility(View.GONE);
            cityLl.setVisibility(View.GONE);
            nameLl.setVisibility(View.GONE);
            buttonCheck.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(v1 -> showCustomerList());
        });
        emailInput.setOnFocusChangeListener((v, hasFocus) -> {
            addressLl.setVisibility(View.GONE);
            cityLl.setVisibility(View.GONE);
            nameLl.setVisibility(View.GONE);
            phoneLl.setVisibility(View.GONE);
            buttonCheck.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(v1 -> showCustomerList());
        });
        addressInput.setOnFocusChangeListener((v, hasFocus) -> {
            cityLl.setVisibility(View.GONE);
            nameLl.setVisibility(View.GONE);
            phoneLl.setVisibility(View.GONE);
            emailLl.setVisibility(View.GONE);
            buttonCheck.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(v1 -> showCustomerList());
        });
        cityInput.setOnFocusChangeListener((v, hasFocus) -> {
            nameLl.setVisibility(View.GONE);
            phoneLl.setVisibility(View.GONE);
            emailLl.setVisibility(View.GONE);
            addressLl.setVisibility(View.GONE);
            buttonCheck.setVisibility(View.VISIBLE);
            buttonBack.setOnClickListener(v1 -> showCustomerList());
        });
    }

    private void buttonCheckOnClickedListener() {
        buttonCheck.setOnClickListener(v -> {
            InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            defaultForm();
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showCustomerList() {
        hideKeyboard();
        detailCustomerLl.setVisibility(View.GONE);
        newCustomerLl.setVisibility(View.GONE);
        buttonBack.setVisibility(View.GONE);
        mainContentLl.setVisibility(View.VISIBLE);
        buttonNewCustomer.setVisibility(View.VISIBLE);
        searchCustomerCl.setVisibility(View.VISIBLE);
    }

    private void defaultForm() {
        nameInput.clearFocus();
        phoneInput.clearFocus();
        emailInput.clearFocus();
        addressInput.clearFocus();
        cityInput.clearFocus();
        nameLl.setVisibility(View.VISIBLE);
        phoneLl.setVisibility(View.VISIBLE);
        emailLl.setVisibility(View.VISIBLE);
        addressLl.setVisibility(View.VISIBLE);
        cityLl.setVisibility(View.VISIBLE);
        buttonCheck.setVisibility(View.GONE);
    }

    private void copyPhone() {
        ClipboardManager phoneCM = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData phoneClip = ClipData.newPlainText("String", customerPhoneForTv);
        phoneCM.setPrimaryClip(phoneClip);
        phoneClip.getDescription();
        Toast.makeText(requireContext(), "Phone number copied successfully", Toast.LENGTH_SHORT).show();
    }

    private void copyEmail() {
        ClipboardManager emailCM = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData emailClip = ClipData.newPlainText("String", customerEmailForTv);
        emailCM.setPrimaryClip(emailClip);
        emailClip.getDescription();
        Toast.makeText(requireContext(), "Email copied successfully", Toast.LENGTH_SHORT).show();
    }


}