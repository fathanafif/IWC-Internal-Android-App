package com.iwc.iwctablet.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iwc.iwctablet.MainActivity;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.adapter.CartAdapter;
import com.iwc.iwctablet.adapter.CartConfirmationAdapter;
import com.iwc.iwctablet.adapter.newOrder.BuffetItemInAdapter;
import com.iwc.iwctablet.adapter.newOrder.BuffetItemOutAdapter;
import com.iwc.iwctablet.adapter.newOrder.BuffetPackInAdapter;
import com.iwc.iwctablet.adapter.newOrder.BuffetPackOutAdapter;
import com.iwc.iwctablet.adapter.newOrder.StallInAdapter;
import com.iwc.iwctablet.adapter.newOrder.StallOutAdapter;
import com.iwc.iwctablet.model.Additional;
import com.iwc.iwctablet.model.BuffetItemAdditional;
import com.iwc.iwctablet.model.BuffetItems;
import com.iwc.iwctablet.model.Buffets;
import com.iwc.iwctablet.model.Cart;
import com.iwc.iwctablet.model.Stalls;
import com.iwc.iwctablet.utility.MoneyTextWatcher;
import com.iwc.iwctablet.utility.RandomIDGenerator;
import com.iwc.iwctablet.utility.TimeUtility;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class OrderNewFragment extends Fragment implements StallInAdapter.StallNewOrderInterface {

    FirebaseDatabase fd;

    DatabaseReference drCustomerName, drBuffetPack, drBuffetItem, drStall, drCart, drAdditional, drOrder, drTown;
    ConstraintLayout noItemCl, cartCl, orderFormCl, calculatorCl, additionalCl, orderNoteCl, searchStallCl, packDetailCl, showCatalogButtonConfirmationCl;
    LinearLayout motherDialog, filterStallMotherLl, filterStallLl;

    BuffetPackInAdapter buffetPackInAdapter;
    BuffetPackOutAdapter buffetPackOutAdapter;
    BuffetItemInAdapter buffetItemInAdapter;
    BuffetItemOutAdapter buffetItemOutAdapter;
    StallInAdapter stallInAdapter;
    StallOutAdapter stallOutAdapter;

    CartAdapter cartAdapter;
    CartConfirmationAdapter cartConfirmationAdapter;
    Stalls stalls;
    Buffets buffetPacks;
    BuffetItems buffetItems;
    ArrayList<Stalls> mStallList;
    ArrayList<Buffets> mBuffetPackList;
    ArrayList<BuffetItems> mBuffetItemList;

    Cart cart;
    ArrayList<Cart> mCartList;
    ShimmerFrameLayout shimmerCatalog, shimmerCart;
    TextInputEditText searchStallInput;
    MaterialAutoCompleteTextView customerName;
    RecyclerView buffetPackRv, buffetItemRv, stallRv, cartRv, cartConfirmationRv;
    TextView nogAmount, currentBvLabelTv, currentMcLabelTv, currentBvTv, currentMcTv;
    EditText eventLocation, guestNumber, theme, carpetHeading, dp1Amount, dp2Amount, dp3Amount, dp4Amount, nameAdditionalEt, qtyAdditionalEt, priceAdditionalEt;
    LinearLayout catalogueLl, dialogLl;
    RadioGroup townRg;
    MaterialRadioButton inTown, outOfTown;
    TextView headerText, eventDate, startTime, endTime, dp1Date, dp2Date, dp3Date, dp4Date, totalPriceTv, tabBuffetPack, tabBuffetItem, tabStall, cartCountTv;
    MaterialButton buttonClearDp, buttonMore, buttonShowCatalog, buttonCart, buttonBookOrder, buttonToCustomer, buttonCalculator, buttonAddNote, buttonCloseCalculator, buttonOpenAdditional, buttonCloseAdditional, buttonAddAdditionalToCart, buttonClearForm, buttonClearSearchStall, buttonFilterStall;

    private FirebaseAuth mAuth;
    boolean isAllFieldChecked = false;
    boolean isCustomerRegistered = false;
    String customerId, totalPrice, cartCount, mvBvQtyDr;
    String nameAdditional, priceAdditional, qtyAdditional, categoryAdditional, typeAdditional, orderNote;
    int priceAdditionalInt, qtyAdditionalInt, sequenceAdditionalInt, sequenceAdditional;
    int hour, minute, town;
    int totalCurrentMcPack, totalCurrentBvPack, totalCurrentMcStall, totalCurrentBvItem;
    int mcBvCount, mcCount, bvCount, mcTotalCount;
    Date rawEventDate;

    public OrderNewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_with_cart, container, false);

        mAuth = FirebaseAuth.getInstance();
        String mUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        fd = FirebaseDatabase.getInstance();

        drBuffetPack = fd.getReference("buffets").child("packs");
        drBuffetItem = fd.getReference("buffets").child("items");
        drCart = fd.getReference("carts_temp").child(mUid);
        drAdditional = fd.getReference("additional_temp").child(mUid);
        drCustomerName = fd.getReference("customers");
        drStall = fd.getReference("stalls");
        drOrder = fd.getReference("orders");
        drTown = fd.getReference("settings").child("town").child(mUid);

        getTown();

        buttonClearSearchStall = view.findViewById(R.id.clear_search_stall_button);
        showCatalogButtonConfirmationCl = view.findViewById(R.id.button_show_catalog_cl);
        buttonShowCatalog = view.findViewById(R.id.button_show_catalog);
        buffetPackRv = view.findViewById(R.id.catalogue_buffet_pack_new_order);
        buffetItemRv = view.findViewById(R.id.catalogue_buffet_item_new_order);
        filterStallMotherLl = view.findViewById(R.id.filter_stall_mother_ll);
        searchStallCl = view.findViewById(R.id.search_stall_new_order_cl);
        searchStallInput = view.findViewById(R.id.search_stall_new_order);
        buttonFilterStall = view.findViewById(R.id.filter_stall_button);
        buttonClearForm = view.findViewById(R.id.button_clear_all_form);
        shimmerCatalog = view.findViewById(R.id.shimmer_new_order);
        shimmerCart = view.findViewById(R.id.shimmer_cart);
        stallRv = view.findViewById(R.id.catalogue_stall_new_order);
        filterStallLl = view.findViewById(R.id.filter_stall_ll);
        catalogueLl = view.findViewById(R.id.catalogue_ll);
        buttonMore = view.findViewById(R.id.more_button);
        cartCl = view.findViewById(R.id.cart_cl);
        cartRv = view.findViewById(R.id.cart_rv);

        // note
        buttonAddNote = view.findViewById(R.id.add_note_button);
        orderNoteCl = view.findViewById(R.id.order_note_cl);

        // additional
        buttonAddAdditionalToCart = view.findViewById(R.id.add_additional_to_cart_button);
        buttonCloseAdditional = view.findViewById(R.id.close_additional_button);
        buttonOpenAdditional = view.findViewById(R.id.open_additional_button);
        priceAdditionalEt = view.findViewById(R.id.input_price_additional);
        nameAdditionalEt = view.findViewById(R.id.input_name_additional);
        qtyAdditionalEt = view.findViewById(R.id.input_qty_additional);
        additionalCl = view.findViewById(R.id.additional_cl);

        // calculator
        buttonCloseCalculator = view.findViewById(R.id.close_calculator_button);
        currentBvLabelTv = view.findViewById(R.id.current_bv_label_tv);
        currentMcLabelTv = view.findViewById(R.id.current_mc_label_tv);
        buttonCalculator = view.findViewById(R.id.calculator_button);
        calculatorCl = view.findViewById(R.id.calculator_cl);
        currentBvTv = view.findViewById(R.id.current_bv_tv);
        currentMcTv = view.findViewById(R.id.current_mc_tv);
        nogAmount = view.findViewById(R.id.nog_amount);

        totalPriceTv = view.findViewById(R.id.total_price_tv);
        cartCountTv = view.findViewById(R.id.cart_count_tv);
        orderFormCl = view.findViewById(R.id.order_form_cl);
        headerText = view.findViewById(R.id.header_text);
        noItemCl = view.findViewById(R.id.no_item_cl);

        // tabs
        tabBuffetPack = view.findViewById(R.id.tab_buffet_packs);
        tabBuffetItem = view.findViewById(R.id.tab_buffet_items);
        tabStall = view.findViewById(R.id.tab_stalls);

        // customer's data form
        eventLocation = view.findViewById(R.id.input_event_location);
        buttonClearDp = view.findViewById(R.id.button_clear_all_dp);
        townRg = view.findViewById(R.id.event_town);
        inTown = view.findViewById(R.id.in_town_rg_child);
        outOfTown = view.findViewById(R.id.outf_town_rg_child);
        startTime = view.findViewById(R.id.input_event_time_start);
        endTime = view.findViewById(R.id.input_event_time_end);
        eventDate = view.findViewById(R.id.input_event_date);
        carpetHeading = view.findViewById(R.id.input_carpet);
        guestNumber = view.findViewById(R.id.input_guests);
        customerName = view.findViewById(R.id.input_name);
        dp1Date = view.findViewById(R.id.input_dp_1_date);
        dp2Date = view.findViewById(R.id.input_dp_2_date);
        dp3Date = view.findViewById(R.id.input_dp_3_date);
        dp4Date = view.findViewById(R.id.input_dp_4_date);
        dp1Amount = view.findViewById(R.id.input_dp_1);
        dp2Amount = view.findViewById(R.id.input_dp_2);
        dp3Amount = view.findViewById(R.id.input_dp_3);
        dp4Amount = view.findViewById(R.id.input_dp_4);
        theme = view.findViewById(R.id.input_theme);

        buttonBookOrder = view.findViewById(R.id.book_order_button);
        buttonCart = view.findViewById(R.id.cart_button);

        // dialog order confirmation
        motherDialog = view.findViewById(R.id.mother_order_confirmation_ll);
        buttonToCustomer = view.findViewById(R.id.to_customer_page_button);
        packDetailCl = view.findViewById(R.id.pack_detail_cl);
        dialogLl = view.findViewById(R.id.dialog_ll);

        packDetailCl.setVisibility(View.GONE);
        catalogueLl.setVisibility(View.INVISIBLE);
        motherDialog.setVisibility(View.GONE);
        cartCl.setVisibility(View.GONE);
        orderFormCl.setVisibility(View.INVISIBLE);
        filterStallMotherLl.setVisibility(View.GONE);

        dp1Amount.addTextChangedListener(new MoneyTextWatcher(dp1Amount));
        dp2Amount.addTextChangedListener(new MoneyTextWatcher(dp2Amount));
        dp3Amount.addTextChangedListener(new MoneyTextWatcher(dp3Amount));
        dp4Amount.addTextChangedListener(new MoneyTextWatcher(dp4Amount));

        buttonClearForm.setOnClickListener(v -> clearAllForm());
        buttonClearDp.setOnClickListener(v -> clearAllDp());
        buttonCart.setOnClickListener(v -> openCart());
        buttonShowCatalog.setOnClickListener(v -> openCatalog());


        final Handler test = new Handler();
        test.postDelayed(() -> {
            if (town == 2) {
                outOfTown.setChecked(true);
            } else {
                inTown.setChecked(true);
            }
            orderFormCl.setVisibility(View.VISIBLE);
            ConstraintLayout buttonShowCatalogCl = requireView().findViewById(R.id.button_show_catalog_cl);
            buttonShowCatalogCl.setVisibility(View.VISIBLE);
        }, 200);

        // customer name suggestion list
        drCustomerName.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ArrayList<String> namesPlusAddress = new ArrayList<>();
                    ArrayList<String> names = new ArrayList<>();
                    ArrayList<String> customer_ids = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String address = dataSnapshot.child("address").getValue(String.class);
                        String customer_id = dataSnapshot.child("customer_id").getValue(String.class);
                        namesPlusAddress.add(name + " - " + address);
                        names.add(name);
                        customer_ids.add(customer_id);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_customer_name_new_order, R.id.customer_name_for_new_order_tv, namesPlusAddress);
                    customerName.setAdapter(adapter);
                    customerName.setOnItemClickListener((adapterView, view1, i, l) -> {
                        String selected = (String) adapterView.getItemAtPosition(i);
                        int position = namesPlusAddress.indexOf(selected);
                        customerName.setText(names.get(position));
                        customerId = customer_ids.get(position);
                    });

                } else {
                    Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });

        shimmerCatalog.startShimmer();

        getCartData();
        getCartItemCount();
        sumPrice();
        tabManager();

        sumCartMcBv();
        sumCartBv();
        sumCartMc();

        buttonOpenAdditional.setOnClickListener(v -> additional());
        buttonCalculator.setOnClickListener(v -> calculator());
        buttonAddNote.setOnClickListener(v -> orderNote());

        startTime.setOnClickListener(v -> openStartTimePicker());
        eventDate.setOnClickListener(v -> eventDatePicker());
        eventDate.setHint(new TimeUtility().getTodayDate());
        endTime.setOnClickListener(v -> openEndTimePicker());

        //customer name checking must be before checking method
        checkCustomerName();
        // order sequence
        buttonBookOrder.setOnClickListener(v -> bookVerification());

        dp1Date.setOnClickListener(v -> dp1DatePicker());
        dp2Date.setOnClickListener(v -> dp2DatePicker());
        dp3Date.setOnClickListener(v -> dp3DatePicker());
        dp4Date.setOnClickListener(v -> dp4DatePicker());

        townRg.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.in_town_rg_child:
                    if (town != 1) {
                        clearCart();
                    }
                    town = 1;
                    drTown.setValue(1);
                    showCatalogButtonConfirmationCl.setVisibility(View.VISIBLE);
                    startShowShimmerCatalog();
                    buttonShowCatalog.setText(R.string.show_catalog_dalam_kota);
                    break;
                case R.id.outf_town_rg_child:
                    if (town != 2) {
                        clearCart();
                    }
                    town = 2;
                    drTown.setValue(2);
                    showCatalogButtonConfirmationCl.setVisibility(View.VISIBLE);
                    startShowShimmerCatalog();
                    buttonShowCatalog.setText(R.string.show_catalog_luar_kota);
                    break;
            }
        });

        ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        return view;
    }

    public void getBuffetPackData() {
        if (town == 1) {
            buffetPackRv.setVisibility(View.GONE);
            drBuffetPack.orderByChild("buffet_name").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mBuffetPackList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            buffetPacks = dataSnapshot.getValue(Buffets.class);
                            assert buffetPacks != null;
                            buffetPacks.setKey(dataSnapshot.getKey());
                            mBuffetPackList.add(buffetPacks);
                        }
                        buffetPackInAdapter = new BuffetPackInAdapter(requireActivity(), mBuffetPackList, buffetPackPosition -> showPackDetail(buffetPackPosition));
                        buffetPackRv.setAdapter(buffetPackInAdapter);
                        Handler delayedShow = new Handler();
                        delayedShow.postDelayed(() -> stopHideShimmer(), 3000);
                    } else {
                        Log.d("valerie", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            drBuffetPack.orderByChild("buffet_name").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mBuffetPackList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            buffetPacks = dataSnapshot.getValue(Buffets.class);
                            assert buffetPacks != null;
                            buffetPacks.setKey(dataSnapshot.getKey());
                            mBuffetPackList.add(buffetPacks);
                        }
                        buffetPackOutAdapter = new BuffetPackOutAdapter(requireActivity(), mBuffetPackList, buffetPackPosition -> showPackDetail(buffetPackPosition));
                        buffetPackRv.setAdapter(buffetPackOutAdapter);
                        Handler delayedShow = new Handler();
                        delayedShow.postDelayed(() -> stopHideShimmer(), 3000);
                    } else {
                        Log.d("valerie", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void showPackDetail(int buffetPackPosition) {
        packDetailCl.setVisibility(View.VISIBLE);

        TextView nameTv, priceTv, requiredTv, choiceTv;
        ShapeableImageView packImg;
        MaterialButton buttonClosePackDetail = requireView().findViewById(R.id.close_pack_detail_button);

        packImg = requireView().findViewById(R.id.pop_up_pack_image);
        choiceTv = requireView().findViewById(R.id.pop_up_choice_menu_tv);
        requiredTv = requireView().findViewById(R.id.pop_up_required_menu_tv);
        nameTv = requireView().findViewById(R.id.pop_up_name_tv);
        priceTv = requireView().findViewById(R.id.pop_up_price_dk_tv);
        buttonClosePackDetail.setOnClickListener(v -> packDetailCl.setVisibility(View.GONE));

        int converted = Integer.parseInt(mBuffetPackList.get(buffetPackPosition).getBuffet_price_dk());
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

        if (!mBuffetPackList.get(buffetPackPosition).getImg_url().equals("")) {
            Glide.with(requireContext())
                    .load(mBuffetPackList.get(buffetPackPosition).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(24, 0, 0, 0))
                    .into(packImg);
        } else {
            Glide.with(requireContext())
                    .load(R.drawable.png_npt)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(24, 0, 0, 0))
                    .into(packImg);
        }

        choiceTv.setText(mBuffetPackList.get(buffetPackPosition).getBuffet_choice_menu());
        requiredTv.setText(mBuffetPackList.get(buffetPackPosition).getBuffet_required_menu());
        nameTv.setText(mBuffetPackList.get(buffetPackPosition).getBuffet_name());
        priceTv.setText(buffetPackPrice);
    }

    public void getBuffetItemData() {
        if (town == 1) {
            drBuffetItem.orderByChild("item_name").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mBuffetItemList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            buffetItems = dataSnapshot.getValue(BuffetItems.class);
                            assert buffetItems != null;
                            buffetItems.setKey(dataSnapshot.getKey());
                            mBuffetItemList.add(buffetItems);
                            buffetItemInAdapter = new BuffetItemInAdapter(requireActivity(), mBuffetItemList);
                            buffetItemRv.setAdapter(buffetItemInAdapter);
                        }
                    } else {
                        Log.d("valerie", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else {
            drBuffetItem.orderByChild("item_name").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mBuffetItemList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            buffetItems = dataSnapshot.getValue(BuffetItems.class);
                            assert buffetItems != null;
                            buffetItems.setKey(dataSnapshot.getKey());
                            mBuffetItemList.add(buffetItems);
                            buffetItemOutAdapter = new BuffetItemOutAdapter(requireActivity(), mBuffetItemList);
                            buffetItemRv.setAdapter(buffetItemOutAdapter);
                        }
                    } else {
                        Log.d("valerie", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    public void getStallData() {
        if (town == 1) {
            drStall.orderByChild("name").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mStallList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            stalls = dataSnapshot.getValue(Stalls.class);
                            assert stalls != null;
                            stalls.setKey(dataSnapshot.getKey());
                            mStallList.add(stalls);
                            stallInAdapter = new StallInAdapter(requireActivity(), mStallList);
                            stallRv.setAdapter(stallInAdapter);
                            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false);
                            stallRv.setLayoutManager(gridLayoutManager);
                        }
                    } else {
                        Log.d("valerie", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            drStall.orderByChild("name").addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        mStallList = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            stalls = dataSnapshot.getValue(Stalls.class);
                            assert stalls != null;
                            stalls.setKey(dataSnapshot.getKey());
                            mStallList.add(stalls);
                            stallOutAdapter = new StallOutAdapter(requireActivity(), mStallList);
                            stallRv.setAdapter(stallOutAdapter);
                        }
                    } else {
                        Log.d("valerie", "error");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    public void getCartData() {
        drCart.orderByChild("sequence").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    mCartList = new ArrayList<>();
                    noItemCl.setVisibility(View.GONE);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        cart = dataSnapshot.getValue(Cart.class);
                        assert cart != null;
                        mCartList.add(cart);
                        cartAdapter = new CartAdapter(getContext(), mCartList);
                        cartRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                        cartRv.setAdapter((cartAdapter));
                    }
                } else {
                    noItemCl.setVisibility(View.VISIBLE);
                }
                final Handler showCartRvPostDelayed = new Handler();
                showCartRvPostDelayed.postDelayed(() -> stopHideShimmerCart(), 2000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void stopHideShimmer() {
        shimmerCatalog.stopShimmer();
        shimmerCatalog.setVisibility(View.GONE);
        buffetPackRv.setVisibility(View.VISIBLE);
    }

    private void startShowShimmerCatalog() {
        buffetPackRv.setVisibility(View.GONE);
        shimmerCatalog.startShimmer();
        shimmerCatalog.setVisibility(View.VISIBLE);
    }
    public void stopHideShimmerCart() {
        shimmerCart.stopShimmer();
        shimmerCart.setVisibility(View.GONE);
        cartRv.setVisibility(View.VISIBLE);
    }

    private boolean checkAllFields() {
        if (customerName.getText().length() == 0) {
            motherDialog.setVisibility(View.GONE);
            Toast.makeText(requireContext().getApplicationContext(), "Select a registered customer name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventDate.length() == 0) {
            motherDialog.setVisibility(View.GONE);
            Toast.makeText(requireContext().getApplicationContext(), "Event Date is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (startTime.length() == 0) {
            motherDialog.setVisibility(View.GONE);
            Toast.makeText(requireContext().getApplicationContext(), "Start Time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endTime.length() == 0) {
            motherDialog.setVisibility(View.GONE);
            Toast.makeText(requireContext().getApplicationContext(), "End Time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (guestNumber.length() == 0) {
            motherDialog.setVisibility(View.GONE);
            Toast.makeText(requireContext().getApplicationContext(), "Number of Guests is required", Toast.LENGTH_SHORT).show();
            return false;
        } else if (eventLocation.length() == 0) {
            motherDialog.setVisibility(View.GONE);
            Toast.makeText(requireContext().getApplicationContext(), "Event Location is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void checkCustomerName() {
        customerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String checkCustomerName = String.valueOf(customerName.getText());
                drCustomerName.orderByChild("name").equalTo(checkCustomerName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            isCustomerRegistered = true;
                            customerName.clearFocus();
                        } else {
                            isCustomerRegistered = false;
                            customerName.hasFocus();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bookVerification() {
        isAllFieldChecked = checkAllFields();
//        successOrderLl.setVisibility(View.GONE);

        if (isAllFieldChecked) { // when all fields are filled
            if (isCustomerRegistered) { // when customer name is registered
                MaterialButton buttonBack = requireView().findViewById(R.id.close_order_confirmation_button);
                MaterialButton buttonConfirm = requireView().findViewById(R.id.confirm_order_button);
                ConstraintLayout successCl = requireView().findViewById(R.id.successful_order_cl);
                LinearLayout confirmOrderLl = requireView().findViewById(R.id.confirmation_order_ll);
                DatabaseReference drCustomer = fd.getReference().child("customers");

                drCustomer.orderByChild("customer_id").equalTo(customerId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                                TextView ocName, ocPhoneEmail, ocAddressCity, ocEventDate, ocEventLocation, ocTime, ocGuests, ocTheme, ocCarpetHeading, ocDp1Amount, ocDp1Date, ocDp2Amount, ocDp2Date, ocDp3Amount, ocDp3Date, ocDp4Amount, ocDp4Date, ocOrderNote, ocTotalPrice;

                                ocName = requireView().findViewById(R.id.oc_name);
                                ocPhoneEmail = requireView().findViewById(R.id.oc_phone_email);
                                ocAddressCity = requireView().findViewById(R.id.oc_address_city);
                                ocEventDate = requireView().findViewById(R.id.oc_event_date);
                                ocTime = requireView().findViewById(R.id.oc_event_time);
                                ocEventLocation = requireView().findViewById(R.id.oc_event_location);
                                ocGuests = requireView().findViewById(R.id.oc_guests);
                                ocTheme = requireView().findViewById(R.id.oc_theme);
                                ocCarpetHeading = requireView().findViewById(R.id.oc_carpet_heading);
                                ocDp1Amount = requireView().findViewById(R.id.oc_dp_1_amount);
                                ocDp2Amount = requireView().findViewById(R.id.oc_dp_2_amount);
                                ocDp3Amount = requireView().findViewById(R.id.oc_dp_3_amount);
                                ocDp4Amount = requireView().findViewById(R.id.oc_dp_4_amount);
                                ocDp1Date = requireView().findViewById(R.id.oc_dp_1_date);
                                ocDp2Date = requireView().findViewById(R.id.oc_dp_2_date);
                                ocDp3Date = requireView().findViewById(R.id.oc_dp_3_date);
                                ocDp4Date = requireView().findViewById(R.id.oc_dp_4_date);
                                ocOrderNote = requireView().findViewById(R.id.oc_order_note);
                                ocTotalPrice = requireView().findViewById(R.id.oc_total_price);

                                ocName.setText(customerName.getText() + " (Customer ID: " + customerId + ")");

                                if (Objects.equals(dataSnapshot.child("email").getValue(), "")) {
                                    ocPhoneEmail.setText(dataSnapshot.child("phone").getValue() + " / -");
                                } else {
                                    ocPhoneEmail.setText(dataSnapshot.child("phone").getValue() + " / " + dataSnapshot.child("email").getValue());
                                }
                                ocAddressCity.setText(dataSnapshot.child("address").getValue() + ", " + dataSnapshot.child("city").getValue());
                                ocEventDate.setText(eventDate.getText());
                                ocTime.setText(startTime.getText() + " s/d " + endTime.getText());
                                ocEventLocation.setText(eventLocation.getText());
                                ocTheme.setText(theme.getText());
                                ocGuests.setText(guestNumber.getText());
                                ocCarpetHeading.setText(carpetHeading.getText());

                                if (dp1Amount.getText().length() < 1) { ocDp1Amount.setText("-");} else { ocDp1Amount.setText(dp1Amount.getText()); }
                                if (dp2Amount.getText().length() < 1) { ocDp2Amount.setText("-");} else { ocDp2Amount.setText(dp2Amount.getText()); }
                                if (dp3Amount.getText().length() < 1) { ocDp3Amount.setText("-");} else { ocDp3Amount.setText(dp3Amount.getText()); }
                                if (dp4Amount.getText().length() < 1) { ocDp4Amount.setText("-");} else { ocDp4Amount.setText(dp4Amount.getText()); }

                                if (dp1Date.getText().length() < 1) { ocDp1Date.setText("-"); } else { ocDp1Date.setText(dp1Date.getText()); }
                                if (dp2Date.getText().length() < 1) { ocDp2Date.setText("-"); } else { ocDp2Date.setText(dp2Date.getText()); }
                                if (dp3Date.getText().length() < 1) { ocDp3Date.setText("-"); } else { ocDp3Date.setText(dp3Date.getText()); }
                                if (dp4Date.getText().length() < 1) { ocDp4Date.setText("-"); } else { ocDp4Date.setText(dp4Date.getText()); }

                                ocOrderNote.setText(orderNote);
                                ocTotalPrice.setText("Rp." + totalPrice);

                                motherDialog.setVisibility(View.VISIBLE);
                                buttonBack.setOnClickListener(v -> motherDialog.setVisibility(View.GONE));
                            }
                        } else {
                            Log.d("valerie", "failed 6**");
                        }

                        String mAuthEmail = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
                        assert mAuthEmail != null;

                        cartConfirmationRv = requireView().findViewById(R.id.order_confirmation_rv);

                        drCart.orderByChild("sequence").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Log.d("getCartData", String.valueOf(snapshot.getChildrenCount()));
                                if (snapshot.exists()) {
                                    noItemCl.setVisibility(View.GONE);
                                    mCartList = new ArrayList<>();
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        cart = dataSnapshot.getValue(Cart.class);
                                        assert cart != null;
                                        mCartList.add(cart);
                                        cartConfirmationAdapter = new CartConfirmationAdapter(getContext(), mCartList);
                                        cartConfirmationRv.setLayoutManager(new LinearLayoutManager(getActivity()));
                                        cartConfirmationRv.setAdapter((cartConfirmationAdapter));
                                    }
                                } else {
                                    noItemCl.setVisibility(View.VISIBLE);
                                    Log.d("getCartData", "cart is empty");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                buttonConfirm.setOnClickListener(v -> {
                    bookOrder();
                    confirmOrderLl.setVisibility(View.GONE);
                    successCl.setVisibility(View.VISIBLE);

                });

            } else { //when customer name is not registered
                Toast.makeText(requireActivity().getApplicationContext(), "Customer is not registered yet, please select registered customer or add new one.", Toast.LENGTH_LONG).show();
                ScrollView orderFormSv = requireView().findViewById(R.id.order_form_sv);
                orderFormSv.scrollTo(0, 0);
                openForm();
            }
        } else {
            openForm();
        }
    }

    public void bookOrder() {
        String orderId = RandomIDGenerator.randomString(RandomIDGenerator.CHARSET_AZ_09, 8);
        DatabaseReference drOrder = fd.getReference().child("orders").push();
        drOrder.child("id").setValue(orderId);
        drOrder.child("customer_id").setValue(customerId);
        drOrder.child("customer_name").setValue(customerName.getText().toString());
        drOrder.child("event_date").setValue(eventDate.getText().toString());
        drOrder.child("raw_event_date").setValue(rawEventDate);
        drOrder.child("time_start").setValue(startTime.getText().toString());
        drOrder.child("time_end").setValue(endTime.getText().toString());
        drOrder.child("event_location").setValue(eventLocation.getText().toString());

        drOrder.child("number_of_guests").setValue(guestNumber.getText().toString());
        drOrder.child("theme").setValue(theme.getText().toString());
        drOrder.child("carpet_heading").setValue(carpetHeading.getText().toString());

        drOrder.child("dp_1_amount").setValue(dp1Amount.getText().toString());
        drOrder.child("dp_2_amount").setValue(dp2Amount.getText().toString());
        drOrder.child("dp_3_amount").setValue(dp3Amount.getText().toString());
        drOrder.child("dp_4_amount").setValue(dp4Amount.getText().toString());

        drOrder.child("dp_1_date").setValue(dp1Date.getText().toString());
        drOrder.child("dp_2_date").setValue(dp2Date.getText().toString());
        drOrder.child("dp_3_date").setValue(dp3Date.getText().toString());
        drOrder.child("dp_4_date").setValue(dp4Date.getText().toString());

        drOrder.child("total_price").setValue(totalPrice);
        drOrder.child("total_item").setValue(cartCount);
        drOrder.child("created_at").setValue(new TimeUtility().getOrderTime() + ", " + new TimeUtility().getTodayDate());
        Date rawDate = new Date();
        drOrder.child("raw_created_at").setValue(rawDate);
        drOrder.child("customer_service").setValue(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail());
        drOrder.child("order_note").setValue(orderNote);

        drCart.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String cartKey = dataSnapshot.getKey();
                    String itemName = dataSnapshot.child("name").getValue(String.class);
                    String itemType = dataSnapshot.child("type").getValue(String.class);
                    String itemCategory = dataSnapshot.child("category").getValue(String.class);
                    int price = Integer.parseInt(String.valueOf(dataSnapshot.child("price").getValue()));
                    int qty = Integer.parseInt(String.valueOf(dataSnapshot.child("qty").getValue()));
                    int sequence = Integer.parseInt(String.valueOf(dataSnapshot.child("sequence").getValue()));
                    assert cartKey != null;
                    drOrder.child("items").child(cartKey).child("name").setValue(itemName);
                    drOrder.child("items").child(cartKey).child("type").setValue(itemType);
                    drOrder.child("items").child(cartKey).child("category").setValue(itemCategory);
                    drOrder.child("items").child(cartKey).child("price").setValue(price);
                    drOrder.child("items").child(cartKey).child("qty").setValue(qty);
                    drOrder.child("items").child(cartKey).child("sequence").setValue(sequence);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonToCustomer.setOnClickListener(v -> {
            customerName.setText("");
            eventDate.setText("");
            startTime.setText("");
            endTime.setText("");
            guestNumber.setText("");
            eventLocation.setText("");
            theme.setText("");
            carpetHeading.setText("");
            dp1Amount.setText("");
            dp2Amount.setText("");
            dp3Amount.setText("");
            dp1Date.setText("");
            dp2Date.setText("");
            dp3Date.setText("");
            clearCart();
            drTown.setValue(1);
            Intent i = new Intent(requireActivity(), MainActivity.class);
            startActivity(i);
        });
    }

    private void getCartItemCount() {
        drCart.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cartCount = String.valueOf(snapshot.getChildrenCount());
                    cartCountTv.setText(cartCount);
                } else {
                    cartCountTv.setText("0");
                    cartCount = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void additional() {
        additionalCl.setVisibility(View.VISIBLE);
        ((MainActivity) requireActivity()).setNavDarkTransparent();

        buttonAddAdditionalToCart.setOnClickListener(v -> {

            if (nameAdditionalEt.length() != 0) {
                if (qtyAdditionalEt.length() != 0) {
                    if (priceAdditionalEt.length() != 0) {
                        String noUpperCaseName = nameAdditionalEt.getText().toString();
                        nameAdditional = noUpperCaseName.substring(0, 1).toUpperCase() + noUpperCaseName.substring(1);
                        priceAdditional = priceAdditionalEt.getText().toString();
                        qtyAdditional = qtyAdditionalEt.getText().toString();
                        categoryAdditional = "Additional";
                        sequenceAdditional = 4;
                        typeAdditional = "...";

                        priceAdditionalInt = Integer.parseInt(priceAdditional);
                        qtyAdditionalInt = Integer.parseInt(qtyAdditional);
                        sequenceAdditionalInt = sequenceAdditional;

                        additionalCl.setVisibility(View.GONE);

                        Additional additional = new Additional();

                        additional.setName(nameAdditional);
                        additional.setPrice(priceAdditionalInt);
                        additional.setQty(qtyAdditionalInt);
                        additional.setSequence(sequenceAdditional);
                        additional.setType(typeAdditional);
                        additional.setCategory(categoryAdditional);

                        drCart.push().setValue(additional);

                        nameAdditionalEt.setText("");
                        qtyAdditionalEt.setText("");
                        priceAdditionalEt.setText("");

                        ((MainActivity) requireActivity()).setNavNotDarkTransparent();
                        getCartItemCount();
                        openCart();
                        getCartData();

                    } else {
                        Toast.makeText(requireContext(), "Price is required", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Quantity is required", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Name is required", Toast.LENGTH_SHORT).show();
            }

            drCart.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Log.d("valerie", String.valueOf(snapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        buttonCloseAdditional.setOnClickListener(v -> {
            hideKeyboard();
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
            additionalCl.setVisibility(View.GONE);
        });
    }
    private void orderNote() {
        EditText noteEt = requireView().findViewById(R.id.input_order_note);
        TextView charCount = requireView().findViewById(R.id.char_count_down);
        MaterialButton buttonCloseAddNoteCl = requireView().findViewById(R.id.close_note_button);
        MaterialButton buttonSaveNote = requireView().findViewById(R.id.save_order_note_button);

        ((MainActivity) requireActivity()).setNavDarkTransparent();

        noteEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("valerie", String.valueOf(noteEt.length()));
                charCount.setText(noteEt.length() + "/200");
                orderNote = noteEt.getText().toString();
            }
        });
        orderNoteCl.setVisibility(View.VISIBLE);
        buttonCloseAddNoteCl.setOnClickListener(v -> {
            orderNoteCl.setVisibility(View.GONE);
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        });
        buttonSaveNote.setOnClickListener(v -> {
            orderNote = noteEt.getText().toString();
            orderNoteCl.setVisibility(View.GONE);
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        });

    }

    public void sumPrice() {
    drCart.addListenerForSingleValueEvent(new ValueEventListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()) {
                int total = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String stringPrice = String.valueOf(dataSnapshot.child("price").getValue());
                    String stringQty = String.valueOf(dataSnapshot.child("qty").getValue());
                    int valuePrice = Integer.parseInt(stringPrice);
                    int valueQty = Integer.parseInt(stringQty);
                    int value = valuePrice * valueQty;
                    total = total + value;
                }
                DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                idr.setRoundingMode(RoundingMode.FLOOR);
                idr.setMinimumFractionDigits(0);
                idr.setMaximumFractionDigits(2);
                DecimalFormatSymbols dfs = new DecimalFormatSymbols();
                dfs.setGroupingSeparator('.');
                dfs.setCurrencySymbol("");
                idr.setDecimalFormatSymbols(dfs);
                idr.setParseIntegerOnly(true);
                totalPrice = idr.format(total);
                totalPriceTv.setText("Rp" + totalPrice);
            } else {
                totalPriceTv.setText("0");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    private void preCalculate() {
        drCart.orderByChild("type").equalTo("Main Course").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mcCount = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        drCart.orderByChild("type").equalTo("Beverage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bvCount = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        drCart.orderByChild("type").equalTo("Main Course & Beverage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mcBvCount = (int) snapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void calculator() {
        getCartItemCount();
//        Log.d("valerie", cartCount);
        if (cartCount == null) {
            Toast.makeText(requireContext(), "Customer cart is still empty, add something before use this function", Toast.LENGTH_LONG).show();
        } else {
            if (guestNumber.length() != 0) {
                nogAmount.setText(guestNumber.getText());
                String nogString = nogAmount.getText().toString();
                int nogInt = Integer.parseInt(nogString);

                MaterialButton buttonAutoSet = requireView().findViewById(R.id.auto_set_qty_button);

                // ideal
                TextView idealBvTv = requireView().findViewById(R.id.ideal_bv_tv);
                TextView idealMcTv = requireView().findViewById(R.id.ideal_mc_tv);
                TextView appTv = requireView().findViewById(R.id.ideal_app_tv);
                TextView desTv = requireView().findViewById(R.id.ideal_des_tv);

                // current
                currentMcTv.setText(String.valueOf(totalCurrentMcPack + totalCurrentMcStall));
                currentBvTv.setText(String.valueOf(totalCurrentBvPack + totalCurrentBvItem));

                idealBvTv.setText(String.valueOf(nogInt));
                idealMcTv.setText(String.valueOf(Integer.valueOf(nogInt * 3)));
                appTv.setText(String.valueOf(nogInt));
                desTv.setText(String.valueOf(Integer.valueOf((int) (nogInt * 1.25))));

                calculatorCl.setVisibility(View.VISIBLE);
                calculatorCl.setOnClickListener(v1 -> calculatorCl.setVisibility(View.GONE));
                buttonCloseCalculator.setOnClickListener(v1 -> {
                    calculatorCl.setVisibility(View.GONE);
                    ((MainActivity) requireContext()).setNavNotDarkTransparent();
                });

                int idealBv = Integer.parseInt(idealBvTv.getText().toString());
                int currentBv = Integer.parseInt(currentBvTv.getText().toString());

                int idealMc = Integer.parseInt(idealMcTv.getText().toString());
                int currentMc = Integer.parseInt(currentMcTv.getText().toString());

                if (currentMc < idealMc || currentBv < idealBv) {
                    currentMcTv.setTextColor(getResources().getColor(R.color.iwc_orange));
                    currentMcLabelTv.setTextColor(getResources().getColor(R.color.iwc_orange));
                    currentBvTv.setTextColor(getResources().getColor(R.color.iwc_orange));
                    currentBvLabelTv.setTextColor(getResources().getColor(R.color.iwc_orange));
                    buttonAutoSet.setText(("set beverage & main course qty").toUpperCase(Locale.ROOT));
                    buttonAutoSet.setEnabled(true);
                    buttonAutoSet.setBackgroundColor(getResources().getColor(R.color.iwc_orange));
                } else {
                    currentMcTv.setTextColor(getResources().getColor(R.color.iwc_green));
                    currentMcLabelTv.setTextColor(getResources().getColor(R.color.iwc_green));
                    buttonAutoSet.setEnabled(false);
                    buttonAutoSet.setText(("all set").toUpperCase(Locale.ROOT));
                    buttonAutoSet.setBackgroundColor(getResources().getColor(R.color.text_dark_4));

                    currentBvTv.setTextColor(getResources().getColor(R.color.iwc_green));
                    currentBvLabelTv.setTextColor(getResources().getColor(R.color.iwc_green));
                    buttonAutoSet.setEnabled(false);
                    buttonAutoSet.setText(("all set").toUpperCase(Locale.ROOT));
                    buttonAutoSet.setBackgroundColor(getResources().getColor(R.color.text_dark_3));
                }

                ((MainActivity) requireActivity()).setNavDarkTransparent();
                buttonAutoSet.setOnClickListener(v -> autoSet());

            } else {
                openForm();
                guestNumber.requestFocus();
                Toast.makeText(requireContext(), "Guest number is required, Please fulfill the order form before use this function.", Toast.LENGTH_LONG).show();
            }
        }

        sumCartMcBv();
        sumCartBv();
        sumCartMc();
        preCalculate();

    }

    private void sumCartMcBv() {
        drCart.orderByChild("type").equalTo("Main Course & Beverage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalCurrentMcPack = 0;
                totalCurrentBvPack = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String stringQty = String.valueOf(dataSnapshot.child("qty").getValue());
                    totalCurrentMcPack = totalCurrentMcPack + Integer.parseInt(stringQty);
                    totalCurrentBvPack = totalCurrentBvPack + Integer.parseInt(stringQty);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sumCartMc() {
        drCart.orderByChild("type").equalTo("Main Course").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalCurrentMcStall = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String stringQty = String.valueOf(dataSnapshot.child("qty").getValue());
                    totalCurrentMcStall = totalCurrentMcStall + Integer.parseInt(stringQty);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sumCartBv() {
        drCart.orderByChild("type").equalTo("Beverage").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalCurrentBvItem = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String stringQty = String.valueOf(dataSnapshot.child("qty").getValue());
                    totalCurrentBvItem = totalCurrentBvItem + Integer.parseInt(stringQty);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void autoSet() {
        MaterialButton buttonAutoSet = requireView().findViewById(R.id.auto_set_qty_button);

        nogAmount.setText(guestNumber.getText());
        String nogString = nogAmount.getText().toString();
        int nogInt = Integer.parseInt(nogString);
        mcTotalCount = mcBvCount + mcCount;

        if (Integer.parseInt(currentMcTv.getText().toString()) < (nogInt * 3) && Integer.parseInt(currentBvTv.getText().toString()) < (nogInt)) {
            // if leaks of main course and beverage too
            drCart.orderByChild("type").equalTo("Main Course & Beverage").addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            DatabaseReference mcBvQtyDr = dataSnapshot.child("qty").getRef();
                            int arg = nogInt * 3;
                            if (arg % (mcTotalCount) == 1) {
                                mcBvQtyDr.setValue((nogInt / mcBvCount) + 1);
                            } else {
                                mcBvQtyDr.setValue((nogInt / mcBvCount));
                            }
                        }
                        mvBvQtyDr = String.valueOf(snapshot.getChildrenCount());
                        Log.d("valerie", mvBvQtyDr);
                        drCart.orderByChild("type").equalTo("Main Course").addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        DatabaseReference mcQtyDr = dataSnapshot.child("qty").getRef();
                                        int arg = nogInt * 3;
                                        if (arg % (mcTotalCount) == 1) {
                                            mcQtyDr.setValue((arg - nogInt) + 1);
                                        } else {
                                            mcQtyDr.setValue((arg - nogInt) / mcCount);
                                        }
                                    }
                                    Log.d("valerie", "case IA");
                                } else {
                                    drCart.orderByChild("type").equalTo("Main Course & Beverage").addListenerForSingleValueEvent(new ValueEventListener() {
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                DatabaseReference mcQtyDr = dataSnapshot.child("qty").getRef();
                                                int arg = nogInt * 3;
                                                if (arg % (mcTotalCount) == 1) {
                                                    mcQtyDr.setValue((arg / mcTotalCount) + 1);
                                                } else {
                                                    mcQtyDr.setValue((arg) / mcTotalCount);
                                                }
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    } else {
                        // when cart has no buffet pack
                        drCart.orderByChild("type").equalTo("Main Course").addListenerForSingleValueEvent(new ValueEventListener() {
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    DatabaseReference mcQtyDr = dataSnapshot.child("qty").getRef();
                                    int arg = nogInt * 3;
                                    if (arg % (mcTotalCount) == 1) {
                                        mcQtyDr.setValue((arg / mcTotalCount) + 1);
                                    } else {
                                        mcQtyDr.setValue((arg) / mcTotalCount);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                        drBuffetItem.orderByChild("item_type").equalTo("Beverage").limitToFirst(1).addValueEventListener(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String keyItem = "";
                                    for (DataSnapshot itemBuffetSnapshot : snapshot.getChildren()) {
                                        keyItem = itemBuffetSnapshot.getKey();
                                    }
                                    BuffetItemAdditional buffetItemAdditional = new BuffetItemAdditional();

                                    assert keyItem != null;
                                    buffetItemAdditional.setCategory("Buffet Item");
                                    buffetItemAdditional.setName(String.valueOf(snapshot.child(keyItem).child("item_name").getValue()));
                                    if (town == 1) {
                                        buffetItemAdditional.setPrice(Integer.parseInt(String.valueOf(snapshot.child(keyItem).child("item_price_dk").getValue())));
                                    } else {
                                        buffetItemAdditional.setPrice(Integer.parseInt(String.valueOf(snapshot.child(keyItem).child("item_price_lk").getValue())));
                                    }
                                    buffetItemAdditional.setQty(nogInt - Integer.parseInt(currentBvTv.getText().toString()));
                                    buffetItemAdditional.setSequence(2);
                                    buffetItemAdditional.setType(String.valueOf(snapshot.child(keyItem).child("item_type").getValue()));

                                    drCart.orderByChild("name").equalTo("Es Puter/Buah Segar, Air Mineral, dan Soft Drink").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                for (DataSnapshot ds : snapshot.getChildren()) {
                                                    int currentQtyInCart = Integer.parseInt(String.valueOf(ds.child("qty").getValue()));
                                                    ds.getRef().child("qty").setValue((nogInt - Integer.parseInt(currentBvTv.getText().toString())) + currentQtyInCart);
                                                }
                                            } else {
                                                drCart.push().setValue(buffetItemAdditional);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) { }
                                    });
                                } else {
                                    Log.d("valerie", "error");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        } else if (Integer.parseInt(currentMcTv.getText().toString()) < (nogInt * 3)) {
            // if leaks of main course
            drCart.orderByChild("type").equalTo("Main Course").addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DatabaseReference mcQtyDr = dataSnapshot.child("qty").getRef();
                        int arg = nogInt * 3;
                        if (arg % (mcTotalCount) == 1) {
                            mcQtyDr.setValue((arg / mcTotalCount) + 1);
                        } else {
                            mcQtyDr.setValue((arg) / mcTotalCount);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
            drCart.orderByChild("type").equalTo("Main Course & Beverage").addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        DatabaseReference mcQtyDr = dataSnapshot.child("qty").getRef();
                        int arg = nogInt * 3;
                        if (arg % (mcTotalCount) == 1) {
                            mcQtyDr.setValue((arg / mcTotalCount) + 1);
                        } else {
                            mcQtyDr.setValue((arg) / mcTotalCount);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        } else {
            // if leaks of beverage
            drBuffetItem.orderByChild("item_type").equalTo("Beverage").limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String keyBuffet = "";
                        for (DataSnapshot itemBuffetSnapshot : snapshot.getChildren()) {
                            keyBuffet = itemBuffetSnapshot.getKey();
                        }
                        BuffetItemAdditional buffetItemAdditional = new BuffetItemAdditional();
                        assert keyBuffet != null;
                        buffetItemAdditional.setCategory("Buffet Item");
                        buffetItemAdditional.setName(String.valueOf(snapshot.child(keyBuffet).child("item_name").getValue()));
                        if (town == 1) {
                            buffetItemAdditional.setPrice(Integer.parseInt(String.valueOf(snapshot.child(keyBuffet).child("item_price_dk").getValue())));
                        } else {
                            buffetItemAdditional.setPrice(Integer.parseInt(String.valueOf(snapshot.child(keyBuffet).child("item_price_lk").getValue())));
                        }
                        buffetItemAdditional.setQty(nogInt - Integer.parseInt(currentBvTv.getText().toString()));
                        buffetItemAdditional.setSequence(2);
                        buffetItemAdditional.setType(String.valueOf(snapshot.child(keyBuffet).child("item_type").getValue()));

                        drCart.orderByChild("name").equalTo("Es Puter/Buah Segar, Air Mineral, dan Soft Drink").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        int currentQtyInCart = Integer.parseInt(String.valueOf(ds.child("qty").getValue()));
                                        ds.getRef().child("qty").setValue((nogInt - Integer.parseInt(currentBvTv.getText().toString())) + currentQtyInCart);
                                    }
                                } else {
                                    drCart.push().setValue(buffetItemAdditional);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    } else {
                        Log.d("valerie", "error");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        buttonAutoSet.setText((getString(R.string.all_set)).toUpperCase(Locale.ROOT));
        calculatorCl.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).setNavNotDarkTransparent();
    }

    private void eventDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> {
            month1 = month1 + 1;
            String date = new TimeUtility().makeDateString(dayOfMonth, month1, year1);
            eventDate.setText(date);

            try {
                String currentString = new TimeUtility().makeDateStringNumber(dayOfMonth, month1, year1);
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentFormat = new SimpleDateFormat("d MM yyyy");
                rawEventDate = currentFormat.parse(currentString);
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }, year, month, day);
        dialog.show();
    }

    private void dp1DatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> {

            month1 = month1 + 1;
            String date = new TimeUtility().makeDateString(dayOfMonth, month1, year1);
            dp1Date.setText(date);

        }, year, month, day);
        dialog.show();
    }
    private void dp2DatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> {

            month1 = month1 + 1;
            String date = new TimeUtility().makeDateString(dayOfMonth, month1, year1);
            dp2Date.setText(date);

        }, year, month, day);
        dialog.show();
    }
    private void dp3DatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> {

            month1 = month1 + 1;
            String date = new TimeUtility().makeDateString(dayOfMonth, month1, year1);
            dp3Date.setText(date);

        }, year, month, day);
        dialog.show();
    }
    private void dp4DatePicker() {
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> {

            month1 = month1 + 1;
            String date = new TimeUtility().makeDateString(dayOfMonth, month1, year1);
            dp4Date.setText(date);

        }, year, month, day);
        dialog.show();
    }
    private void clearAllDp() {
        dp1Amount.setText("");
        dp2Amount.setText("");
        dp3Amount.setText("");
        dp4Amount.setText("");
        dp1Date.setText("");
        dp2Date.setText("");
        dp3Date.setText("");
        dp4Date.setText("");
    }

    private void openStartTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            startTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    private void openEndTimePicker() {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = (view, selectedHour, selectedMinute) -> {
            hour = selectedHour;
            minute = selectedMinute;
            endTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
        };
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    @SuppressLint("ResourceAsColor")
    private void tabManager() {
        Animation inAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.in_stall_filter_button);
        Animation inAnimFast = AnimationUtils.loadAnimation(requireContext(), R.anim.in_stall_filter_ll_fast);

        tabBuffetPack.setTextColor(getResources().getColor(R.color.iwc_orange));
        tabBuffetItem.setTextColor(getResources().getColor(R.color.text_dark_3));
        tabStall.setTextColor(getResources().getColor(R.color.text_dark_3));
        buttonFilterStall.setVisibility(View.GONE);
        buffetItemRv.setVisibility(View.INVISIBLE);
        stallRv.setVisibility(View.INVISIBLE);
        searchStallCl.setVisibility(View.GONE);
        headerText.setVisibility(View.VISIBLE);

        tabBuffetPack.setOnClickListener(v -> {
            searchStallCl.setVisibility(View.GONE);
            packDetailCl.setVisibility(View.GONE);
            filterStallMotherLl.setVisibility(View.GONE);
            headerText.setVisibility(View.VISIBLE);
            buttonFilterStall.setVisibility(View.GONE);
            tabBuffetPack.setTextColor(getResources().getColor(R.color.iwc_orange));
            tabBuffetItem.setTextColor(getResources().getColor(R.color.text_dark_3));
            tabStall.setTextColor(getResources().getColor(R.color.text_dark_3));
            buffetPackRv.setVisibility(View.VISIBLE);
            buffetItemRv.setVisibility(View.INVISIBLE);
            stallRv.setVisibility(View.INVISIBLE);
        });

        tabBuffetItem.setOnClickListener(v -> {
            searchStallCl.setVisibility(View.GONE);
            packDetailCl.setVisibility(View.GONE);
            filterStallMotherLl.setVisibility(View.GONE);
            headerText.setVisibility(View.VISIBLE);
            buttonFilterStall.setVisibility(View.GONE);
            tabBuffetItem.setTextColor(getResources().getColor(R.color.iwc_orange));
            tabStall.setTextColor(getResources().getColor(R.color.text_dark_3));
            tabBuffetPack.setTextColor(getResources().getColor(R.color.text_dark_3));
            buffetItemRv.setVisibility(View.VISIBLE);
            buffetPackRv.setVisibility(View.INVISIBLE);
            stallRv.setVisibility(View.INVISIBLE);
        });

        tabStall.setOnClickListener(v -> {
            setAllCategoryTextDefault();
            buttonFilterStall.setEnabled(true);
            packDetailCl.setVisibility(View.GONE);
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallCl.setVisibility(View.VISIBLE);
            buttonClearSearchStall.setVisibility(View.GONE);

            searchStallInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!Objects.requireNonNull(searchStallInput.getText()).toString().equals("")) {
                        buttonClearSearchStall.setVisibility(View.VISIBLE);
                        buttonClearSearchStall.setOnClickListener(v -> searchStallInput.setText(""));
                    } else {
                        buttonClearSearchStall.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    stallFilter(editable.toString());
                    setAllCategoryTextDefault();
                }
            });

            tabStall.setTextColor(getResources().getColor(R.color.iwc_orange));
            tabBuffetPack.setTextColor(getResources().getColor(R.color.text_dark_3));
            tabBuffetItem.setTextColor(getResources().getColor(R.color.text_dark_3));
            stallRv.setVisibility(View.VISIBLE);
            buffetPackRv.setVisibility(View.INVISIBLE);
            buffetItemRv.setVisibility(View.INVISIBLE);

            buttonFilterStall.setOnClickListener(v1 -> {
                filterTabOnClick();
                filterStallMotherLl.setVisibility(View.VISIBLE);
                filterStallLl.startAnimation(inAnimFast);
                buttonFilterStall.setVisibility(View.GONE);
                filterStallMotherLl.setOnClickListener(v2 -> {
                    filterStallMotherLl.setVisibility(View.GONE);
                    buttonFilterStall.setVisibility(View.VISIBLE);
                });
            });

            stallRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    buttonFilterStall.startAnimation(inAnim);
                }
            });

            if (searchStallInput.hasFocus()) {
                buttonFilterStall.setOnClickListener(v1 -> {
                    searchStallInput.clearFocus();
                    hideKeyboard();
                });
            }

        });
    }

    @SuppressLint("SetTextI18n")
    private void openForm() {
        ScrollView orderFormSv = requireView().findViewById(R.id.order_form_sv);
        orderFormSv.fullScroll(ScrollView.FOCUS_UP);
        buttonCart.setEnabled(true);
        cartCl.setVisibility(View.GONE);
        catalogueLl.setVisibility(View.VISIBLE);
        orderFormCl.setVisibility(View.VISIBLE);
        cartCountTv.setVisibility(View.VISIBLE);
        headerText.setText("New Order");

//        headerIconHandlerOnElseCartOpened();
    }

    @SuppressLint("SetTextI18n")
    private void openCatalog() {
        showCatalogButtonConfirmationCl.setVisibility(View.GONE);
        buttonFilterStall.setEnabled(false);
        buttonCart.setEnabled(true);
        cartCl.setVisibility(View.GONE);
        orderFormCl.setVisibility(View.VISIBLE);
        buttonCart.setVisibility(View.VISIBLE);
        catalogueLl.setVisibility(View.VISIBLE);
        cartCountTv.setVisibility(View.VISIBLE);

        tabManager();
        getBuffetPackData();
        getBuffetItemData();
        getStallData();

    }

    @SuppressLint("SetTextI18n")
    private void openCart() {
        hideKeyboard();
        shimmerCart.startShimmer();
        MaterialButton closeCart = requireView().findViewById(R.id.close_cart_button);
        LinearLayout threeCartMenusLl = requireView().findViewById(R.id.three_cart_menus__cl);
        ConstraintLayout cartIconCl = requireView().findViewById(R.id.cart_icon_cl);

        TextView cartHeaderTv = requireView().findViewById(R.id.cart_header_tv);
        if (!customerName.getText().toString().equals("")) {
            String str = customerName.getText().toString().toLowerCase();
            String cap = str.substring(0, 1).toUpperCase() + str.substring(1);
            cartHeaderTv.setText(cap + "'s Cart");
        } else {
            cartHeaderTv.setText("Unknown's Cart");
        }

        cartCl.setVisibility(View.VISIBLE);
        orderFormCl.setVisibility(View.GONE);
        packDetailCl.setVisibility(View.GONE);
        threeCartMenusLl.setVisibility(View.VISIBLE);

        closeCart.setOnClickListener(v -> {
            orderFormCl.setVisibility(View.VISIBLE);
            cartCl.setVisibility(View.GONE);
            threeCartMenusLl.setVisibility(View.GONE);
            buttonCart.setVisibility(View.VISIBLE);
            headerText.setVisibility(View.VISIBLE);
            cartIconCl.setVisibility(View.VISIBLE);
        });

        onCartOpened();
    }
    private void onCartOpened() {
        cartRv.setVisibility(View.INVISIBLE);
        shimmerCart.setVisibility(View.VISIBLE);
        getCartData();
        customerName.clearFocus();
        drCart.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sumPrice();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void clearCart() {
        drCart.removeValue();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void clearAllForm() {
        customerName.setText("");
        eventDate.setText("");
        eventLocation.setText("");
        startTime.setText("");
        endTime.setText("");
        guestNumber.setText("");
        theme.setText("");
        carpetHeading.setText("");
        customerName.clearFocus();
        eventDate.clearFocus();
        eventLocation.clearFocus();
        startTime.clearFocus();
        endTime.clearFocus();
        guestNumber.clearFocus();
        theme.clearFocus();
        carpetHeading.clearFocus();
        dp1Amount.setText("");
        dp2Amount.setText("");
        dp3Amount.setText("");
        dp4Amount.setText("");
        dp1Date.setText("");
        dp2Date.setText("");
        dp3Date.setText("");
        dp4Date.setText("");
        dp1Amount.clearFocus();
        dp2Amount.clearFocus();
        dp3Amount.clearFocus();
        dp4Amount.clearFocus();
        dp1Date.clearFocus();
        dp2Date.clearFocus();
        dp3Date.clearFocus();
        dp4Date.clearFocus();
        hideKeyboard();
    }
    private void getTown() {
        drTown.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    town = 1;
                } else {
                    town = Integer.parseInt(String.valueOf(snapshot.getValue()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void stallFilter(String text) {
        ArrayList<Stalls> filteredStallList = new ArrayList<>();
        if (town == 1) {
            for (Stalls item : mStallList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredStallList.add(item);
                }
                stallInAdapter.filterList(filteredStallList);
                stallInAdapter.notifyDataSetChanged();
            }
        } else {
            for (Stalls item : mStallList) {
                if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                    filteredStallList.add(item);
                }
                stallOutAdapter.filterList(filteredStallList);
                stallOutAdapter.notifyDataSetChanged();
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private void stallCategoryFilter(String filterCategory) {
        ArrayList<Stalls> filteredStallList = new ArrayList<>();
        if (town == 1) {
            for (Stalls item : mStallList) {
                if (item.getCategory().equalsIgnoreCase(filterCategory)) {
                    filteredStallList.add(item);
                }
                stallInAdapter.filterList(filteredStallList);
                stallInAdapter.notifyDataSetChanged();
            }
        } else {
            for (Stalls item : mStallList) {
                if (item.getCategory().equalsIgnoreCase(filterCategory)) {
                    filteredStallList.add(item);
                }
                stallOutAdapter.filterList(filteredStallList);
                stallOutAdapter.notifyDataSetChanged();
            }
        }
    }
    private void filterTabOnClick() {
        TextView ragam = requireView().findViewById(R.id.filter_stall_category_1_tv);
        TextView nasi = requireView().findViewById(R.id.filter_stall_category_2_tv);
        TextView minum = requireView().findViewById(R.id.filter_stall_category_3_tv);
        TextView soto = requireView().findViewById(R.id.filter_stall_category_4_tv);
        TextView sate = requireView().findViewById(R.id.filter_stall_category_5_tv);
        TextView kambing = requireView().findViewById(R.id.filter_stall_category_6_tv);
        TextView kuah = requireView().findViewById(R.id.filter_stall_category_7_tv);
        TextView showAll = requireView().findViewById(R.id.filter_stall_category_8_tv);

        ragam.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            stallCategoryFilter(ragam.getText().toString());
            ragam.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        nasi.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            stallCategoryFilter(nasi.getText().toString());
            nasi.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        minum.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            stallCategoryFilter(minum.getText().toString());
            minum.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        soto.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            stallCategoryFilter(soto.getText().toString());
            soto.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        sate.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            stallCategoryFilter(sate.getText().toString());
            sate.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        kambing.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            stallCategoryFilter(kambing.getText().toString());
            kambing.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        kuah.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            showAll.setTextColor(getResources().getColor(R.color.text_dark_3));
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            stallCategoryFilter(kuah.getText().toString());
            kuah.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        showAll.setOnClickListener(v -> {
            searchStallInput.setText("");
            stallRv.smoothScrollToPosition(0);
            setAllCategoryTextDefault();
            filterStallMotherLl.setVisibility(View.GONE);
            buttonFilterStall.setVisibility(View.VISIBLE);
            searchStallInput.clearFocus();
            getStallData();
            showAll.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
    }
    private void setAllCategoryTextDefault() {
        TextView ragam = requireView().findViewById(R.id.filter_stall_category_1_tv);
        TextView nasi = requireView().findViewById(R.id.filter_stall_category_2_tv);
        TextView minum = requireView().findViewById(R.id.filter_stall_category_3_tv);
        TextView soto = requireView().findViewById(R.id.filter_stall_category_4_tv);
        TextView sate = requireView().findViewById(R.id.filter_stall_category_5_tv);
        TextView kambing = requireView().findViewById(R.id.filter_stall_category_6_tv);
        TextView kuah = requireView().findViewById(R.id.filter_stall_category_7_tv);
        TextView showAll = requireView().findViewById(R.id.filter_stall_category_8_tv);

        ragam.setTextColor(getResources().getColor(R.color.text_dark_3));
        nasi.setTextColor(getResources().getColor(R.color.text_dark_3));
        minum.setTextColor(getResources().getColor(R.color.text_dark_3));
        soto.setTextColor(getResources().getColor(R.color.text_dark_3));
        sate.setTextColor(getResources().getColor(R.color.text_dark_3));
        kambing.setTextColor(getResources().getColor(R.color.text_dark_3));
        kuah.setTextColor(getResources().getColor(R.color.text_dark_3));
        showAll.setTextColor(getResources().getColor(R.color.iwc_orange));
    }


}