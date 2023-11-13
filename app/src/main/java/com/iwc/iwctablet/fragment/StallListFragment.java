package com.iwc.iwctablet.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.iwc.iwctablet.MainActivity;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.adapter.StallAdapter;
import com.iwc.iwctablet.model.Stalls;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class StallListFragment extends Fragment {

    FirebaseDatabase fd;
    DatabaseReference dr;
    StorageReference storageReference;
    StallAdapter stallAdapter;
    ArrayList<Stalls> list;
    RecyclerView recyclerView;
    MaterialButton clearSearch;
    TextInputEditText searchStall;
    LinearLayout allLl, ragamLl, nasiLl, minumLl, sotoLl, sateLl, kambingLl, kuahLl;
    TextView all, ragam, nasi, minum, soto, sate, kambing, kuah, stallListHeaderText;
    ShimmerFrameLayout shimmerFrameLayout;
    LinearLayout stallMainContentLl, newStallLl;

    // new stall
    MaterialButton buttonNewStall, buttonCloseNewStall;
    EditText inputNewStallName, inputNewStallPriceDk, inputNewStallPriceLk;
    AutoCompleteTextView inputNewStallCategory, inputNewStallType;
    MaterialButton buttonResetNewStall, buttonSaveNewStall;
    ImageView stallImage;

    // edit stall
    TextView stallName;
    ProgressBar progressBar;
    EditText editStallPriceDk, editStallPriceLk;
    MaterialButton buttonDeleteStall, buttonUpdateStall;
    AutoCompleteTextView stallCategoryAcTv, stallTypeAcTv;
    ImageView popUpStallImage, buttonEditImage;
    ConstraintLayout popUpMotherCl, popUpContentCl, searchStallCl;
    String selectedCategory, selectedType, inputNewStallNameString, uploadedImgUrl, uploadedEditImgUrl, stringGetImgUrl;

    boolean isRightSheetShowed;
    boolean isStallNameRegistered;
    Uri stallImageUri, stallEditImageUri;

    public StallListFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stall_list, container, false);

        buttonNewStall = view.findViewById(R.id.button_new_stall);
        searchStall = view.findViewById(R.id.search_stall);
        recyclerView = view.findViewById(R.id.stall_rv);
        clearSearch = view.findViewById(R.id.clear_search);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_stall_list);
        stallMainContentLl = view.findViewById(R.id.stall_main_content_ll);
        searchStallCl = view.findViewById(R.id.search_stall_cl);
        stallListHeaderText = view.findViewById(R.id.title_stall_list);

        allLl = view.findViewById(R.id.all_ll);
        ragamLl = view.findViewById(R.id.ragam_ll);
        nasiLl = view.findViewById(R.id.nasi_ll);
        minumLl = view.findViewById(R.id.minum_ll);
        sotoLl = view.findViewById(R.id.soto_ll);
        sateLl = view.findViewById(R.id.sate_ll);
        kambingLl = view.findViewById(R.id.kambing_ll);
        kuahLl = view.findViewById(R.id.kuah_ll);

        all = view.findViewById(R.id.stall_category_filter_all);
        ragam = view.findViewById(R.id.stall_category_filter_ragam);
        nasi = view.findViewById(R.id.stall_category_filter_nasi);
        minum = view.findViewById(R.id.stall_category_filter_minum);
        soto = view.findViewById(R.id.stall_category_filter_soto);
        sate = view.findViewById(R.id.stall_category_filter_sate);
        kambing = view.findViewById(R.id.stall_category_filter_kambing);
        kuah = view.findViewById(R.id.stall_category_filter_kuah);

        // new stall
        newStallLl = view.findViewById(R.id.right_sheet_ll);
        stallImage = view.findViewById(R.id.stall_image);
        buttonCloseNewStall = view.findViewById(R.id.back_button);
        inputNewStallName = view.findViewById(R.id.input_stall_name);
        inputNewStallPriceDk = view.findViewById(R.id.input_stall_price_dk);
        inputNewStallPriceLk = view.findViewById(R.id.input_stall_price_lk);
        inputNewStallCategory = view.findViewById(R.id.new_stall_category_actv);
        inputNewStallType = view.findViewById(R.id.new_stall_type_actv);
        buttonResetNewStall = view.findViewById(R.id.clear_fields_button);
        buttonSaveNewStall = view.findViewById(R.id.save_stall_button);
        progressBar = view.findViewById(R.id.progress_new_stall);

        // edit stall
        popUpMotherCl = view.findViewById(R.id.pop_up_stall_mother_cl);
        popUpContentCl = view.findViewById(R.id.pop_up_content_cl);
        stallName = view.findViewById(R.id.stall_name_edit_pop_up);
        buttonDeleteStall = view.findViewById(R.id.button_delete_stall);
        editStallPriceDk = view.findViewById(R.id.pop_up_stall_price_dk_et);
        popUpStallImage = view.findViewById(R.id.pop_up_stall_image_edit);
        buttonEditImage = view.findViewById(R.id.button_update_upload_stall_image);
        editStallPriceLk = view.findViewById(R.id.pop_up_stall_price_lk_et);
        stallCategoryAcTv = view.findViewById(R.id.stall_category_actv);
        stallTypeAcTv = view.findViewById(R.id.stall_type_actv);
        buttonUpdateStall = view.findViewById(R.id.button_save_update_stall);

        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference("stalls");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        list = new ArrayList<>();
        isRightSheetShowed = false;

        progressBar.setVisibility(View.INVISIBLE);

        LinearLayout.LayoutParams mainContentLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mainContentLp.weight = 24;
        stallMainContentLl.setLayoutParams(mainContentLp);

        popUpMotherCl.setVisibility(View.GONE);

        clearSearch.setVisibility(View.GONE);
        all.setTextColor(getResources().getColor(R.color.brand_color));

        tabClickListener();
        getData();

        buttonNewStall.setOnClickListener(v -> {
            isRightSheetShowed = true;
            showNewStallLl();
            stallListHeaderText.setVisibility(View.GONE);
            searchStallCl.setVisibility(View.GONE);
        });
        buttonCloseNewStall.setOnClickListener(v -> {
            stallListHeaderText.setVisibility(View.VISIBLE);
            hideRightSheet();
        });
        searchStall.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(searchStall.getText()).toString().equals("")) {
                    clearSearch.setVisibility(View.VISIBLE);
                    stallCategoryNoFilter();
                    setAllCategoryTextDefault();
                    all.setTextColor(getResources().getColor(R.color.brand_color));
                } else {
                    clearSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                stallFilter(s.toString());
            }
        });
        clearSearch.setOnClickListener(v -> {
            searchStall.setText("");
            clearSearch.setVisibility(View.GONE);
        });

        ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        return view;
    }

    private void hideRightSheet() {
        newStallLl.setVisibility(View.GONE);
        searchStallCl.setVisibility(View.VISIBLE);
        buttonNewStall.setVisibility(View.VISIBLE);
        stallMainContentLl.setVisibility(View.VISIBLE);
        hideKeyboard();
    }

    private void showNewStallLl() {
        stallListHeaderText.setVisibility(View.GONE);
        buttonNewStall.setVisibility(View.GONE);
        stallMainContentLl.setVisibility(View.GONE);
        Glide.with(requireActivity()).load(R.drawable.png_npt).transform(new CenterCrop(), new RoundedCorners(36)).into(stallImage);
        newStallLl.setVisibility(View.VISIBLE);

        inputNewStallCategory.setOnClickListener(v -> hideKeyboard());
        inputNewStallType.setOnClickListener(v -> hideKeyboard());
        ArrayAdapter<CharSequence> stallCategoryAdapter = ArrayAdapter.createFromResource(requireActivity().getApplicationContext(), R.array.category_stall, R.layout.item_stall_category);
        inputNewStallCategory.setAdapter(stallCategoryAdapter);
        ArrayAdapter<CharSequence> stallTypeAdapter = ArrayAdapter.createFromResource(requireActivity().getApplicationContext(), R.array.type_stall, R.layout.item_stall_category);
        inputNewStallType.setAdapter(stallTypeAdapter);

        inputNewStallName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                dr.orderByChild("name").equalTo(inputNewStallName.getText().toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isStallNameRegistered = snapshot.exists();
                        inputNewStallNameString = inputNewStallName.getText().toString();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        stallImage.setOnClickListener(v -> selectImage());

        buttonResetNewStall.setOnClickListener(v -> clearForm());
        buttonSaveNewStall.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            validation();
        });
    }

    private void getData() {
        dr.orderByChild("name").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Stalls stalls = dataSnapshot.getValue(Stalls.class);
                        list.add(stalls);
                        stallAdapter = new StallAdapter(requireActivity(), list, stall1 -> editStall(stall1.getName()));
                        recyclerView.setAdapter(stallAdapter);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(gridLayoutManager);
                    }
                    stallAdapter.notifyDataSetChanged();
                    shimmerFrameLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(getActivity(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void editStall(String stallNameParams) {
        stallEditImageUri = null;
        uploadedImgUrl = "";
        uploadedEditImgUrl = "";
        ((MainActivity) requireActivity()).setNavDarkTransparent();
        popUpMotherCl.setVisibility(View.VISIBLE);
        popUpMotherCl.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
            popUpMotherCl.setVisibility(View.GONE);
        });

        buttonEditImage.setOnClickListener(view -> selectImageEdit());

        stallName.setText(stallNameParams);
        dr.orderByChild("name").equalTo(stallNameParams).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String stallPriceStringDkEdit = "";
                    String stallPriceStringLkEdit = "";
                    String stallCategoryStringEdit = "";
                    String stallTypeStringEdit = "";
                    stringGetImgUrl = "";
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        stallPriceStringDkEdit = Objects.requireNonNull(dataSnapshot.child("price_dk").getValue()).toString();
                        stallPriceStringLkEdit = Objects.requireNonNull(dataSnapshot.child("price_lk").getValue()).toString();
                        stringGetImgUrl = Objects.requireNonNull(dataSnapshot.child("img_url").getValue()).toString();
                        stallCategoryStringEdit = Objects.requireNonNull(dataSnapshot.child("category").getValue()).toString();
                        stallTypeStringEdit = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();
                        stringGetImgUrl = Objects.requireNonNull(dataSnapshot.child("img_url").getValue()).toString();
                    }
                    editStallPriceDk.setText(stallPriceStringDkEdit);
                    editStallPriceLk.setText(stallPriceStringLkEdit);
                    stallCategoryAcTv.setText(stallCategoryStringEdit);
                    stallTypeAcTv.setText(stallTypeStringEdit);

                    if (!stringGetImgUrl.equals("")) {
                        Glide.with(requireContext())
                                .load(stringGetImgUrl)
                                .placeholder(R.drawable.png_npt)
                                .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                                .into(popUpStallImage);
                    } else {
                        Glide.with(requireContext())
                                .load(R.drawable.png_npt)
                                .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                                .into(popUpStallImage);
                    }

                    ArrayAdapter<CharSequence> stallCategoryAdapter = ArrayAdapter.createFromResource(requireActivity().getApplicationContext(), R.array.category_stall, R.layout.item_stall_category);
                    stallCategoryAcTv.setAdapter(stallCategoryAdapter);

                    ArrayAdapter<CharSequence> stallTypeAdapter = ArrayAdapter.createFromResource(requireActivity().getApplicationContext(), R.array.type_stall, R.layout.item_stall_category);
                    stallTypeAcTv.setAdapter(stallTypeAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ViewGroup.LayoutParams popUpContentClLayoutParams = popUpContentCl.getLayoutParams();
        popUpContentClLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        popUpContentCl.setLayoutParams(popUpContentClLayoutParams);

        buttonDeleteStall.setOnClickListener(v -> {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("iinwidodo/stall/");
            storageReference.child(stallNameParams + ".jpg").delete().addOnSuccessListener(unused -> Toast.makeText(requireContext(), stallNameParams + " is successfully removed", Toast.LENGTH_SHORT).show());

            dr.orderByChild("name").equalTo(stallNameParams).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            popUpMotherCl.setVisibility(View.GONE);
            getData();
        });

        stallCategoryAcTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                selectedCategory = String.valueOf(stallCategoryAcTv.getText());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedCategory = String.valueOf(stallCategoryAcTv.getText());
            }
        });

        stallTypeAcTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                selectedType = String.valueOf(stallTypeAcTv.getText());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                selectedType = String.valueOf(stallTypeAcTv.getText());
            }
        });

        buttonUpdateStall.setOnClickListener(v -> storeUpdatedStall(stallNameParams));
    }

    private void validation() {
        if (!checkAllFieldsBuffetPack()) {
            Toast.makeText(requireActivity().getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
        } else {
            if (!isStallNameRegistered) {
                storeToDatabase();
            } else {
                Toast.makeText(requireActivity().getApplicationContext(), "Stall is already exist, please use another name.", Toast.LENGTH_SHORT).show();
                inputNewStallName.setText("");
                inputNewStallName.requestFocus();
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void stallFilter(String text) {
        ArrayList<Stalls> filteredStallList = new ArrayList<>();
        for (Stalls item : list) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredStallList.add(item);
            }
            stallAdapter.filterList(filteredStallList);
            stallAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void stallCategoryFilter(String filterCategory) {
        ArrayList<Stalls> filteredStallList = new ArrayList<>();
        for (Stalls item : list) {
            if (item.getCategory().equalsIgnoreCase(filterCategory)) {
                filteredStallList.add(item);
            }
            stallAdapter.filterList(filteredStallList);
            stallAdapter.notifyDataSetChanged();
        }
    }

    private void stallCategoryNoFilter() {
        ArrayList<Stalls> filteredStallList = new ArrayList<>();
        for (Stalls item : list) {
            if (!item.getCategory().isEmpty()) {
                filteredStallList.add(item);
            }
            stallAdapter.filterList(filteredStallList);
        }
    }

    private void setAllCategoryTextDefault() {
        all.setTextColor(getResources().getColor(R.color.text_dark_3));
        ragam.setTextColor(getResources().getColor(R.color.text_dark_3));
        nasi.setTextColor(getResources().getColor(R.color.text_dark_3));
        minum.setTextColor(getResources().getColor(R.color.text_dark_3));
        soto.setTextColor(getResources().getColor(R.color.text_dark_3));
        sate.setTextColor(getResources().getColor(R.color.text_dark_3));
        kambing.setTextColor(getResources().getColor(R.color.text_dark_3));
        kuah.setTextColor(getResources().getColor(R.color.text_dark_3));
    }

    private void tabClickListener() {
        allLl.setOnClickListener(v -> {
            stallCategoryNoFilter();
            setAllCategoryTextDefault();
            all.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        ragamLl.setOnClickListener(v -> {
            stallCategoryFilter(ragam.getText().toString());
            setAllCategoryTextDefault();
            ragam.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        nasiLl.setOnClickListener(v -> {
            stallCategoryFilter(nasi.getText().toString());
            setAllCategoryTextDefault();
            nasi.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        minumLl.setOnClickListener(v -> {
            stallCategoryFilter(minum.getText().toString());
            setAllCategoryTextDefault();
            minum.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        sotoLl.setOnClickListener(v -> {
            stallCategoryFilter(soto.getText().toString());
            setAllCategoryTextDefault();
            soto.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        sateLl.setOnClickListener(v -> {
            stallCategoryFilter(sate.getText().toString());
            setAllCategoryTextDefault();
            sate.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        kambingLl.setOnClickListener(v -> {
            stallCategoryFilter(kambing.getText().toString());
            setAllCategoryTextDefault();
            kambing.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
        kuahLl.setOnClickListener(v -> {
            stallCategoryFilter(kuah.getText().toString());
            setAllCategoryTextDefault();
            kuah.setTextColor(getResources().getColor(R.color.iwc_orange));
        });
    }

    private boolean checkAllFieldsBuffetPack() {
        if (inputNewStallName.length() == 0) {
            return false;
        }
        if (inputNewStallPriceDk.length() == 0) {
            return false;
        }
        return inputNewStallCategory.length() != 0;
    }

    private void clearForm() {
        inputNewStallName.setText("");
        inputNewStallPriceDk.setText("");
        inputNewStallPriceLk.setText("");
        inputNewStallCategory.setText("");
        inputNewStallType.setText("");
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void storeToDatabase() {
        uploadedImgUrl = "";
        if (stallImageUri != null) {
            storageReference = FirebaseStorage.getInstance().getReference().child("iwc_app/stalls/" + inputNewStallNameString + "." + getFileExtension(stallImageUri));
            storageReference.putFile(stallImageUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                uploadedImgUrl = uri.toString();

                Stalls stalls = new Stalls();
                stalls.setImg_url(uploadedImgUrl);
                stalls.setName(Objects.requireNonNull(inputNewStallName.getText()).toString());
                stalls.setPrice_dk(Objects.requireNonNull(inputNewStallPriceDk.getText()).toString());
                stalls.setPrice_lk(Objects.requireNonNull(inputNewStallPriceLk.getText()).toString());
                stalls.setCategory(Objects.requireNonNull(inputNewStallCategory.getText()).toString());
                stalls.setType(Objects.requireNonNull(inputNewStallType.getText()).toString());

                dr.push().setValue(stalls).addOnCompleteListener(task -> {
                    clearForm();
                    Toast.makeText(requireContext().getApplicationContext(), "Stall was successfully saved to database.", Toast.LENGTH_SHORT).show();

                    InputMethodManager inputManager = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(requireActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    hideRightSheet();
                    stallCategoryNoFilter();
                    setAllCategoryTextDefault();
                    all.setTextColor(getResources().getColor(R.color.brand_color));
                });
                progressBar.setProgress(100);
            })).addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar.setProgress((int) progress);

            });
        }
    }

    private void storeUpdatedStall(String stallNameParams) {
        if (!(editStallPriceDk.length() == 0)) {
            if (!(editStallPriceLk.length() == 0)) {
                if (stallEditImageUri != null) { // when user change the stall image
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference("iwc_app/stalls/");
                    if (!stringGetImgUrl.equals("")) { // if stall image is already in the storage database
                        Log.d("valerie", "sudah ada gambar di storage");
                        storageReference.child(stallNameParams + ".jpg").delete().addOnSuccessListener(unused -> storageReference.child(stallNameParams + ".jpg").putFile(stallEditImageUri).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(requireContext(), "Stall image updated successfully", Toast.LENGTH_SHORT).show();
                            storageReference.child(stallNameParams + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                                uploadedEditImgUrl = uri.toString();
                                Log.d("valerie", uploadedEditImgUrl);
                                new Handler().postDelayed(() -> dr.orderByChild("name").equalTo(stallNameParams).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().child("price_dk").setValue(editStallPriceDk.getText().toString());
                                            dataSnapshot.getRef().child("price_lk").setValue(editStallPriceLk.getText().toString());
                                            dataSnapshot.getRef().child("img_url").setValue(uploadedEditImgUrl);
                                            dataSnapshot.getRef().child("category").setValue(selectedCategory);
                                            dataSnapshot.getRef().child("type").setValue(selectedType);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                }), 1000);
                            });
                        }));
                    } else { // if no stall image found in the storage database
                        Log.d("valerie", "belum ada gambar di storage");
                        storageReference.child(stallNameParams + "." + getFileExtensionEdit(stallEditImageUri)).putFile(stallEditImageUri).addOnSuccessListener(taskSnapshot -> {
                            Toast.makeText(requireContext(), "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            storageReference.child(stallNameParams + "." + getFileExtensionEdit(stallEditImageUri)).getDownloadUrl().addOnSuccessListener(uri -> {
                                uploadedEditImgUrl = uri.toString();
                                Log.d("valerie", uploadedEditImgUrl);
                                dr.orderByChild("name").equalTo(stallNameParams).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            dataSnapshot.getRef().child("price_dk").setValue(editStallPriceDk.getText().toString());
                                            dataSnapshot.getRef().child("price_lk").setValue(editStallPriceLk.getText().toString());
                                            dataSnapshot.getRef().child("img_url").setValue(uploadedEditImgUrl);
                                            dataSnapshot.getRef().child("category").setValue(selectedCategory);
                                            dataSnapshot.getRef().child("type").setValue(selectedType);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            });
                        });
                    }
                } else { // ketika tidak ganti gambar
                    Log.d("valerie", "ketika tidak ganti gambar");
                    dr.orderByChild("name").equalTo(stallNameParams).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("price_dk").setValue(editStallPriceDk.getText().toString());
                                dataSnapshot.getRef().child("price_lk").setValue(editStallPriceLk.getText().toString());
                                dataSnapshot.getRef().child("category").setValue(selectedCategory);
                                dataSnapshot.getRef().child("type").setValue(selectedType);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

//                LinearLayout.LayoutParams mainContentLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
//                mainContentLp.weight = 24;
//                stallMainContentLl.setLayoutParams(mainContentLp);
            }
        }
        Toast.makeText(requireContext(), "Stall was updated successfully", Toast.LENGTH_SHORT).show();
        ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        popUpMotherCl.setVisibility(View.GONE);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch("image/*");
    }

    private void selectImageEdit() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContentEdit.launch("image/*");
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private String getFileExtensionEdit(Uri uri) {
        ContentResolver cR = requireContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        stallImageUri = uri;
        stallImage.setImageURI(stallImageUri);
    });

    ActivityResultLauncher<String> mGetContentEdit = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        stallEditImageUri = uri;
        popUpStallImage.setImageURI(stallEditImageUri);
        if(stallEditImageUri != null) {
            Glide.with(requireContext())
                    .load(stallEditImageUri)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(popUpStallImage);
        } else {
            Glide.with(requireContext())
                    .load(stringGetImgUrl)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(popUpStallImage);
        }
    });

}