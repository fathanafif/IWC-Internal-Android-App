package com.iwc.iwctablet.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iwc.iwctablet.MainActivity;
import com.iwc.iwctablet.R;
import com.iwc.iwctablet.adapter.BuffetAdapter;
import com.iwc.iwctablet.adapter.BuffetItemAdapter;
import com.iwc.iwctablet.model.BuffetItems;
import com.iwc.iwctablet.model.Buffets;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Objects;

public class BuffetListFragment extends Fragment {

    FirebaseDatabase fd;
    DatabaseReference dr;
    StorageReference storageReference, removeReference;
    ShapeableImageView gradientStart, gradientEnd;
    BuffetAdapter buffetPackAdapter;
    BuffetItemAdapter buffetItemAdapter;
    MaterialButton buttonNewBuffet, buttonEditPack;
    ImageView buffetImage, newPackImg;
    LinearLayout mainContentLl, newBuffetLl;
    ScrollView newPackSv, newItemSv;
    EditText nameBuffetPackInput, priceBuffetPackDkInput, priceBuffetPackLkInput, requiredBuffetPackInput, choiceBuffetPackInput, nameBuffetItemInput, priceBuffetItemDkInput, priceBuffetItemLkInput;
    TextView viewBuffetPack, viewBuffetItem, packBreadCrumbNewBuffet, itemBreadCrumbNewBuffet, headerText;
    ShimmerFrameLayout shimmerFrameLayout;

    //detail pack
    ConstraintLayout popUpMotherCl;
    TextView packName, packPriceDk, packPriceLk, packRequired, packChoice, editPackTitle, editItemTitle;
    MaterialButton buttonResetBuffetPack, buttonSave, buttonDeletePack, buttonBack, buttonBackPopup, buttonSaveUpdatePack, buttonSaveUpdateItem;
    ShapeableImageView packEditImageSiv;
    ImageView buttonUploadUpdatePackImage;
    LinearLayout dataPackLl, editPackLl, editItemLl;
    EditText editPackPriceDk, editPackPriceLk, editPackRequired, editPackChoice, editItemPriceDk, editItemPriceLk;
    ScrollView packEditSv;

    RecyclerView buffetPackRv, buffetItemRv;
    ArrayList<Buffets> packArrayList;
    ArrayList<BuffetItems> itemArrayList;

    Uri newBuffetImageUri, packEditImageUri;
    int packPosition;
    String uploadedImgUrl, uploadedEditImgUrl;
    boolean isPackNameRegistered, isItemNameRegistered, isNewPackBreadCrumbActive;
    boolean isAfterEditNamePackExist = false;

    public BuffetListFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buffet_list, container, false);

        mainContentLl = view.findViewById(R.id.main_content_ll);
        newBuffetLl = view.findViewById(R.id.right_sheet_ll);
        buttonNewBuffet = view.findViewById(R.id.new_buffet_button);
        buttonBack = view.findViewById(R.id.back_button);
        viewBuffetPack = view.findViewById(R.id.buffet_subject_1);
        viewBuffetItem = view.findViewById(R.id.buffet_subject_2);
        buffetPackRv = view.findViewById(R.id.buffet_pack_rv);
        buffetItemRv = view.findViewById(R.id.buffet_item_rv);
        shimmerFrameLayout = view.findViewById(R.id.shimmer_buffet_list);
        headerText = view.findViewById(R.id.header_text);
        gradientStart = view.findViewById(R.id.gradient_start);
        gradientEnd = view.findViewById(R.id.gradient_end);

        // new buffet packs
        packBreadCrumbNewBuffet = view.findViewById(R.id.title_buffet_pack);
        itemBreadCrumbNewBuffet = view.findViewById(R.id.title_buffet_item);
        newPackImg = view.findViewById(R.id.new_buffet_pack_image);
        newPackSv = view.findViewById(R.id.new_buffet_pack_sv);
        newItemSv = view.findViewById(R.id.new_buffet_item_sv);

        // new buffet pack
        nameBuffetPackInput = view.findViewById(R.id.input_buffet_pack_name);
        priceBuffetPackDkInput = view.findViewById(R.id.input_buffet_pack_price_dk);
        priceBuffetPackLkInput = view.findViewById(R.id.input_buffet_pack_price_lk);
        requiredBuffetPackInput = view.findViewById(R.id.input_buffet_pack_required);
        choiceBuffetPackInput = view.findViewById(R.id.input_buffet_pack_choice);
        // new buffet pack
        nameBuffetItemInput = view.findViewById(R.id.input_buffet_item_name);
        priceBuffetItemDkInput = view.findViewById(R.id.input_buffet_item_price_dk);
        priceBuffetItemLkInput = view.findViewById(R.id.input_buffet_item_price_lk);

        buffetImage = view.findViewById(R.id.buffet_pack_img);

        buttonResetBuffetPack = view.findViewById(R.id.clear_fields_button);
        buttonSave = view.findViewById(R.id.save_buffet_pack_button);

        //detail pack / item
        popUpMotherCl = view.findViewById(R.id.pop_up_mother_cl);
        packEditSv = view.findViewById(R.id.pacK_edit_sv);
        packName = view.findViewById(R.id.pop_up_name_tv);
        packPriceDk = view.findViewById(R.id.pop_up_price_dk_tv);
        packPriceLk = view.findViewById(R.id.pop_up_price_lk_tv);
        packRequired = view.findViewById(R.id.pop_up_required_menu_tv);
        packChoice = view.findViewById(R.id.pop_up_choice_menu_tv);

        buttonEditPack = view.findViewById(R.id.button_update_pack);
        buttonDeletePack = view.findViewById(R.id.button_delete_pack);
        packEditImageSiv = view.findViewById(R.id.pop_up_pack_image_edit);
        buttonUploadUpdatePackImage = view.findViewById(R.id.button_update_upload_pack_image);
        buttonSaveUpdatePack = view.findViewById(R.id.button_save_update_pack);
        buttonBackPopup = view.findViewById(R.id.button_back_to_data);
        dataPackLl = view.findViewById(R.id.data_pack_ll);
        editPackLl = view.findViewById(R.id.edit_pack_ll);
        editItemLl = view.findViewById(R.id.edit_item_ll);

        editPackTitle = view.findViewById(R.id.pack_name_edit_pop_up);
        editPackPriceDk = view.findViewById(R.id.pop_up_pack_price_dk_et);
        editPackPriceLk = view.findViewById(R.id.pop_up_pack_price_lk_et);
        editPackRequired = view.findViewById(R.id.pop_up_pack_required_et);
        editPackChoice = view.findViewById(R.id.pop_up_pac_choice_et);

        editItemTitle = view.findViewById(R.id.item_name_edit_pop_up);
        editItemPriceDk = view.findViewById(R.id.pop_up_item_price_dk_et);
        editItemPriceLk = view.findViewById(R.id.pop_up_item_price_lk_et);
        buttonSaveUpdateItem = view.findViewById(R.id.button_save_update_item);

        viewBuffetPack.setTextColor(getResources().getColor(R.color.iwc_orange, null));
        newBuffetLl.setVisibility(View.GONE);
//        buttonCheck.setVisibility(View.GONE);

        fd = FirebaseDatabase.getInstance();
        dr = fd.getReference("buffets");
        packArrayList = new ArrayList<>();
        itemArrayList = new ArrayList<>();
        isNewPackBreadCrumbActive = true;
        newBuffetImageUri = null;
        packEditImageUri = null;

        popUpMotherCl.setVisibility(View.GONE);
        newItemSv.setVisibility(View.GONE);

        LinearLayout.LayoutParams mainContentLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mainContentLp.weight = 24;
        mainContentLl.setLayoutParams(mainContentLp);

        loadPackData();
        loadItemData();
        checkPackAvailability();
        checkItemAvailability();
        allOnClickListener();

        buffetItemRv.setVisibility(View.GONE);
        buttonBack.setVisibility(View.GONE);

        ((MainActivity) requireActivity()).setNavNotDarkTransparent();

        return view;
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void allOnClickListener() {
        buttonSave.setOnClickListener(v -> storeToDatabase());

        viewBuffetPack.setOnClickListener(v -> {
            viewBuffetItem.setTextColor(getResources().getColor(R.color.text_dark_3, null));
            viewBuffetPack.setTextColor(getResources().getColor(R.color.iwc_orange, null));
            buffetPackRv.setVisibility(View.VISIBLE);
            buffetItemRv.setVisibility(View.GONE);
        });
        viewBuffetItem.setOnClickListener(v -> {
            viewBuffetPack.setTextColor(getResources().getColor(R.color.text_dark_3, null));
            viewBuffetItem.setTextColor(getResources().getColor(R.color.iwc_orange, null));
            buffetPackRv.setVisibility(View.GONE);
            gradientStart.setVisibility(View.GONE);
            gradientEnd.setVisibility(View.GONE);
            buffetItemRv.setVisibility(View.VISIBLE);
        });

        buttonNewBuffet.setOnClickListener(v -> showNewBuffetPage());
        itemBreadCrumbNewBuffet.setOnClickListener(v -> {
            isNewPackBreadCrumbActive = false;
            newPackSv.setVisibility(View.GONE);
            itemBreadCrumbNewBuffet.setTextColor(getResources().getColor(R.color.iwc_orange));
            packBreadCrumbNewBuffet.setTextColor(getResources().getColor(R.color.text_dark_3));
            newItemSv.setVisibility(View.VISIBLE);
            buttonSave.setText("Save Buffet Item");
        });
        packBreadCrumbNewBuffet.setOnClickListener(v -> {
            isNewPackBreadCrumbActive = true;
            newPackSv.setVisibility(View.VISIBLE);
            newItemSv.setVisibility(View.GONE);
            packBreadCrumbNewBuffet.setTextColor(getResources().getColor(R.color.iwc_orange));
            itemBreadCrumbNewBuffet.setTextColor(getResources().getColor(R.color.text_dark_3));
            buttonSave.setText("Save Buffet Pack");
        });
        buttonResetBuffetPack.setOnClickListener(v -> resetPackAndItemForm());

    }

    private void loadPackData() {
        RecyclerView.LayoutManager packRvLayoutManager;
        packRvLayoutManager = new LinearLayoutManager(requireContext());

        shimmerFrameLayout.setVisibility(View.VISIBLE);
        dr.child("packs").orderByChild("buffet_name").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    packArrayList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Buffets buffets = dataSnapshot.getValue(Buffets.class);
                        packArrayList.add(buffets);
                        buffetPackAdapter = new BuffetAdapter(requireActivity(), packArrayList, buffetPosition -> getBuffetPackDetail(buffetPosition));
                        buffetPackRv.setLayoutManager(packRvLayoutManager);
                        LinearLayoutManager horizontalLayout = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
                        buffetPackRv.setLayoutManager(horizontalLayout);
                        buffetPackRv.setAdapter(buffetPackAdapter);
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                    buffetPackRv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (scrollX > oldScrollX) {
                            gradientStart.setVisibility(View.VISIBLE);
                            gradientEnd.setVisibility(View.GONE);
                        } else if (scrollX < oldScrollX) {
                            gradientStart.setVisibility(View.GONE);
                            gradientEnd.setVisibility(View.VISIBLE);
                        } else if (scrollX == 0) {
                            gradientStart.setVisibility(View.GONE);
                            gradientEnd.setVisibility(View.VISIBLE);
                        } else {
                            gradientStart.setVisibility(View.GONE);
                            gradientEnd.setVisibility(View.GONE);
                        }
                    });
                } else {
                    Toast.makeText(requireActivity().getApplicationContext(), "No pack data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity().getApplicationContext(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadItemData() {
        shimmerFrameLayout.setVisibility(View.VISIBLE);
        dr.child("items").orderByChild("item_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    itemArrayList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BuffetItems buffetItems = dataSnapshot.getValue(BuffetItems.class);
                        itemArrayList.add(buffetItems);
                        buffetItemAdapter = new BuffetItemAdapter(requireActivity(), itemArrayList, buffetItemPosition -> editBuffetItem(buffetItemPosition));
                        buffetItemRv.setAdapter(buffetItemAdapter);
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
                        buffetItemRv.setLayoutManager(gridLayoutManager);
                    }
                    shimmerFrameLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(requireActivity().getApplicationContext(), "No item data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity().getApplicationContext(), "Fail to load data..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBuffetPackDetail(int buffetPosition) {
        ((MainActivity) requireActivity()).setNavDarkTransparent();
        popUpPack(buffetPosition);
    }

    private void editBuffetItem(int buffetItemPosition) {
        popUpItem(buffetItemPosition);
    }

//    ==============================================================================================

    private void showNewBuffetPage() {
        Log.d("valerie", String.valueOf(isNewPackBreadCrumbActive));
        newPackImg.setOnClickListener(v -> selectImage());
        gradientEnd.setVisibility(View.VISIBLE);
        buttonBack.setVisibility(View.VISIBLE);
        buttonNewBuffet.setVisibility(View.GONE);
        newBuffetLl.setVisibility(View.VISIBLE);

        Glide.with(requireContext())
                .load(R.drawable.png_npt)
                .placeholder(R.drawable.png_npt)
                .transform(new CenterCrop(), new GranularRoundedCorners(28, 0, 0, 0))
                .into(newPackImg);

        LinearLayout.LayoutParams mainContentLp16 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        mainContentLp16.weight = 16;
        mainContentLl.setLayoutParams(mainContentLp16);

        buttonBack.setOnClickListener(v1 -> {
            buttonBack.setVisibility(View.GONE);
            newBuffetLl.setVisibility(View.GONE);
            mainContentLl.setVisibility(View.VISIBLE);
            buttonNewBuffet.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams mainContentLp24 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            mainContentLp24.weight = 24;
            mainContentLl.setLayoutParams(mainContentLp24);

            hideKeyboard();
        });

//        formHandler();
        buttonSave.setOnClickListener(v -> storeToDatabase());
    }

    private void storeToDatabase() {
        if (isNewPackBreadCrumbActive) {
            if (!checkAllFieldsBuffetPack()) {
                Toast.makeText(requireActivity().getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
            } else {
                if (!isPackNameRegistered) {
                    // if pack name is available (not registered)
                    uploadedImgUrl = "";
                    String newPackName = nameBuffetPackInput.getText().toString();
                    Buffets buffets = new Buffets();
                    if (newBuffetImageUri != null) {
                        // store new pack with new image
                        storageReference = FirebaseStorage.getInstance().getReference().child("iwc_app/buffet_packs/" + newPackName + "." + getFileExtension(newBuffetImageUri));
                        storageReference.putFile(newBuffetImageUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            uploadedImgUrl = uri.toString();
                            buffets.setImg_url(uploadedImgUrl);
                            buffets.setBuffet_name(newPackName);
                            buffets.setBuffet_price_dk(Objects.requireNonNull(priceBuffetPackDkInput.getText()).toString());
                            buffets.setBuffet_price_lk(Objects.requireNonNull(priceBuffetPackLkInput.getText()).toString());
                            buffets.setBuffet_required_menu(Objects.requireNonNull(requiredBuffetPackInput.getText()).toString());
                            buffets.setBuffet_choice_menu(Objects.requireNonNull(choiceBuffetPackInput.getText()).toString());
                            buffets.setBuffet_category("main");

                            dr.child("packs").push().setValue(buffets).addOnCompleteListener(task -> {
                                newPackSv.scrollTo(0,0);
                                newBuffetLl.setVisibility(View.GONE);
                                buttonNewBuffet.setVisibility(View.VISIBLE);
                                Toast.makeText(requireContext().getApplicationContext(), "Buffet Pack was successfully saved to database.", Toast.LENGTH_SHORT).show();
                                LinearLayout.LayoutParams mainContentLp24 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                                mainContentLp24.weight = 24;
                                mainContentLl.setLayoutParams(mainContentLp24);
                                resetPackAndItemForm();
                            });

                        })).addOnProgressListener(snapshot -> {
//                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//                            progressBar.setProgress((int) progress);
                        });
                    } else {
                        // store new pack without image
                        buffets.setBuffet_name(Objects.requireNonNull(nameBuffetPackInput.getText()).toString());
                        buffets.setBuffet_price_dk(Objects.requireNonNull(priceBuffetPackDkInput.getText()).toString());
                        buffets.setBuffet_price_lk(Objects.requireNonNull(priceBuffetPackLkInput.getText()).toString());
                        buffets.setBuffet_required_menu(Objects.requireNonNull(requiredBuffetPackInput.getText()).toString());
                        buffets.setBuffet_choice_menu(Objects.requireNonNull(choiceBuffetPackInput.getText()).toString());
                        buffets.setImg_url("");
                        buffets.setBuffet_category("main");

                        dr.child("packs").push().setValue(buffets).addOnCompleteListener(task -> {
                            newPackSv.scrollTo(0,0);
                            newBuffetLl.setVisibility(View.GONE);
                            buttonNewBuffet.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext().getApplicationContext(), "Buffet Pack was successfully saved to database.", Toast.LENGTH_SHORT).show();
                            LinearLayout.LayoutParams mainContentLp24 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                            mainContentLp24.weight = 24;
                            mainContentLl.setLayoutParams(mainContentLp24);
                            resetPackAndItemForm();
                        });
                    }
                } else {
                    // if pack name is registered
                    Toast.makeText(requireActivity().getApplicationContext(), "Buffet pack name is already exist, please use another name.", Toast.LENGTH_SHORT).show();
                    newPackSv.scrollTo(0,0);
                    nameBuffetPackInput.setText("");
                    nameBuffetPackInput.requestFocus();
                }
            }
        } else {
            // if user wants to store new item
            if (!checkAllFieldsBuffetItem()) {
                Toast.makeText(requireActivity().getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
            } else {
                // if item name is available (not registered)
                String newItemName = nameBuffetItemInput.getText().toString();
                BuffetItems buffetItems = new BuffetItems();
                if (newBuffetImageUri != null) {
                    // store new item with new image
                    storageReference = FirebaseStorage.getInstance().getReference().child("iwc_app/buffet_items/" + newItemName + "." + getFileExtension(newBuffetImageUri));
                    storageReference.putFile(newBuffetImageUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedImgUrl = uri.toString();
                        buffetItems.setImg_url(uploadedImgUrl);
                        buffetItems.setItem_name(newItemName);
                        buffetItems.setItem_price_dk(Objects.requireNonNull(priceBuffetItemDkInput.getText()).toString());
                        buffetItems.setItem_price_lk(Objects.requireNonNull(priceBuffetItemLkInput.getText()).toString());
                        buffetItems.setItem_type("Beverage");

                        dr.child("items").push().setValue(buffetItems).addOnCompleteListener(task -> {
                            newBuffetLl.setVisibility(View.GONE);
                            buttonNewBuffet.setVisibility(View.VISIBLE);
                            Toast.makeText(requireContext().getApplicationContext(), "Buffet Item was successfully saved to database.", Toast.LENGTH_SHORT).show();
                            LinearLayout.LayoutParams mainContentLp24 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                            mainContentLp24.weight = 24;
                            mainContentLl.setLayoutParams(mainContentLp24);
                            resetPackAndItemForm();
                        });

                    })).addOnProgressListener(snapshot -> {
//                            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
//                            progressBar.setProgress((int) progress);
                    });
                } else {
                    // store new item without image
                    buffetItems.setImg_url("");
                    buffetItems.setItem_name(Objects.requireNonNull(nameBuffetItemInput.getText()).toString());
                    buffetItems.setItem_price_dk(Objects.requireNonNull(priceBuffetItemDkInput.getText()).toString());
                    buffetItems.setItem_price_lk(Objects.requireNonNull(priceBuffetItemLkInput.getText()).toString());
                    buffetItems.setItem_type("Beverage");

                    dr.child("items").push().setValue(buffetItems).addOnCompleteListener(task -> {
                        newBuffetLl.setVisibility(View.GONE);
                        buttonNewBuffet.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext().getApplicationContext(), "Buffet Item was successfully saved to database.", Toast.LENGTH_SHORT).show();
                        LinearLayout.LayoutParams mainContentLp24 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
                        mainContentLp24.weight = 24;
                        mainContentLl.setLayoutParams(mainContentLp24);
                        resetPackAndItemForm();
                    });
                }


//                if (!isItemNameRegistered) {
//                    // if buffet item name is available (not existed)
//                    BuffetItems buffetItems = new BuffetItems();
//
//
//                    buffetItems.setItem_name(Objects.requireNonNull(nameBuffetItemInput.getText()).toString());
//                    buffetItems.setItem_price_dk(Objects.requireNonNull(priceBuffetItemDkInput.getText()).toString());
//                    buffetItems.setItem_price_lk(Objects.requireNonNull(priceBuffetItemLkInput.getText()).toString());
//
//                    fd = FirebaseDatabase.getInstance();
//                    dr = fd.getReference("buffets/items").push();
//                    dr.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            dr.setValue(buffetItems);
//                            newBuffetLl.setVisibility(View.GONE);
//                            mainContentLl.setVisibility(View.VISIBLE);
//                            Toast.makeText(requireActivity().getApplicationContext(), "Data was successfully saved to database.", Toast.LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Toast.makeText(requireActivity().getApplicationContext(), "Fail to save data " + error, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    buttonNewBuffet.setVisibility(View.VISIBLE);
//                    resetPackAndItemForm();
//                } else {
//                    // if buffet item name is registered
//                    Toast.makeText(requireActivity().getApplicationContext(), "Buffet item name is already exist. Please use another name.", Toast.LENGTH_SHORT).show();
//                    nameBuffetItemInput.setText("");
//                    nameBuffetItemInput.requestFocus();
//                }
            }
        }
    }

    private boolean checkAllFieldsBuffetPack() {
        if (nameBuffetPackInput.length() == 0) {
            return false;
        }
        if (priceBuffetPackDkInput.length() == 0) {
            return false;
        }
        if (priceBuffetPackLkInput.length() == 0) {
            return false;
        }
        if (requiredBuffetPackInput.length() == 0) {
            return false;
        }
        return choiceBuffetPackInput.length() != 0;
    }

    private boolean checkAllFieldsBuffetItem() {
        if (nameBuffetItemInput.length() == 0) {
            return false;
        }
        if (priceBuffetItemDkInput.length() == 0) {
            return false;
        }
        return priceBuffetItemLkInput.length() != 0;
    }

    private boolean checkAllFieldsEditPack() {
        if (editPackPriceDk.length() == 0) {
            return false;
        }
        if (editPackRequired.length() == 0) {
            return false;
        }
        return editPackChoice.length() != 0;
    }

    private boolean checkAllFieldsEditItem() {
        return editItemPriceDk.length() != 0;
    }

    private void checkPackAvailability() {
        nameBuffetPackInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPackNameAvailability();
            }
        });
    }

    private void checkPackNameAvailability() {
        String name = String.valueOf(nameBuffetPackInput.getText());

        dr.child("packs").orderByChild("buffet_name").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isPackNameRegistered = snapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkItemAvailability() {
        nameBuffetItemInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkItemNameAvailability();
            }
        });
    }

    private void checkItemNameAvailability() {
        String name = String.valueOf(nameBuffetItemInput.getText());

        dr.child("items").orderByChild("item_name").equalTo(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                isItemNameRegistered = snapshot.exists();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void resetPackAndItemForm() {
        nameBuffetPackInput.setText("");
        priceBuffetPackDkInput.setText("");
        priceBuffetPackLkInput.setText("");
        requiredBuffetPackInput.setText("");
        choiceBuffetPackInput.setText("");
        nameBuffetItemInput.setText("");
        priceBuffetItemDkInput.setText("");
        priceBuffetItemLkInput.setText("");
    }

    private void updatePack(int buffetPosition) {
        MaterialButton buttonBackFromEdit = requireView().findViewById(R.id.button_back_to_data);

        editPackTitle.setText(packArrayList.get(buffetPosition).getBuffet_name());
        dataPackLl.setVisibility(View.GONE);

        buttonBackFromEdit.setOnClickListener(v1 -> dataPackLl.setVisibility(View.VISIBLE));

        String packEditImageUrl = packArrayList.get(buffetPosition).getImg_url();

        if (!packEditImageUrl.equals("")) {
            Glide.with(requireContext())
                    .load(packEditImageUrl)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(packEditImageSiv);
        } else {
            Glide.with(requireContext())
                    .load(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(packEditImageSiv);
        }

        buttonUploadUpdatePackImage.setOnClickListener(v -> {
            uploadedEditImgUrl = "";
            selectImageUpdate();
        });

        editPackPriceDk.setText(packArrayList.get(buffetPosition).getBuffet_price_dk());
        editPackPriceLk.setText(packArrayList.get(buffetPosition).getBuffet_price_lk());
        editPackRequired.setText(packArrayList.get(buffetPosition).getBuffet_required_menu());
        editPackChoice.setText(packArrayList.get(buffetPosition).getBuffet_choice_menu());

        buttonSaveUpdatePack.setOnClickListener(v -> storeUpdatePack(buffetPosition));
    }

    private void storeUpdatePack(int buffetPosition) {
        if (!checkAllFieldsEditPack()) {
            Toast.makeText(requireActivity().getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
        } else {
            if (!isAfterEditNamePackExist) {
                uploadedEditImgUrl = "";
                if (packEditImageUri != null) {
                    // if user update pack with changing pack image
                    // delete existing image first in firebase storage database
                    removeReference = FirebaseStorage.getInstance().getReference().child("iwc_app/buffet_packs/" + editPackTitle.getText().toString() + ".jpg");
                    removeReference.delete().addOnSuccessListener(unused -> Toast.makeText(requireContext(), "Buffet pack was successfully updated.", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                        Toast.makeText(requireContext().getApplicationContext(), "Adding new buffet pack image, please wait a moment.", Toast.LENGTH_SHORT).show();
                    });
                    // if user uploads image that is not exists yet in storage
                    storageReference = FirebaseStorage.getInstance().getReference().child("iwc_app/buffet_packs/" + editPackTitle.getText().toString() + "." + getFileExtension(packEditImageUri));
                    storageReference.putFile(packEditImageUri).addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        uploadedEditImgUrl = uri.toString();
                        dr.child("packs").orderByChild("buffet_name").equalTo(packArrayList.get(buffetPosition).getBuffet_name()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().child("img_url").setValue(uploadedEditImgUrl);
                                    dataSnapshot.getRef().child("buffet_price_dk").setValue(editPackPriceDk.getText().toString());
                                    dataSnapshot.getRef().child("buffet_price_lk").setValue(editPackPriceLk.getText().toString());
                                    dataSnapshot.getRef().child("buffet_required_menu").setValue(editPackRequired.getText().toString());
                                    dataSnapshot.getRef().child("buffet_choice_menu").setValue(editPackChoice.getText().toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        Toast.makeText(requireContext(), "Buffet pack was successfully updated.", Toast.LENGTH_SHORT).show();
                    })).addOnCompleteListener(task -> {
                        packEditImageUri = null;
                    });
                } else {
                    // if user update pack without changing pack image
                    dr.child("packs").orderByChild("buffet_name").equalTo(packArrayList.get(buffetPosition).getBuffet_name()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                dataSnapshot.getRef().child("buffet_price_dk").setValue(editPackPriceDk.getText().toString());
                                dataSnapshot.getRef().child("buffet_price_lk").setValue(editPackPriceLk.getText().toString());
                                dataSnapshot.getRef().child("buffet_required_menu").setValue(editPackRequired.getText().toString());
                                dataSnapshot.getRef().child("buffet_choice_menu").setValue(editPackChoice.getText().toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    Toast.makeText(requireContext(), "Buffet pack was successfully updated.", Toast.LENGTH_SHORT).show();
                }
                closePopUp();
                packEditImageUri = null;
            } else {
                Toast.makeText(requireContext(), "Buffet pack " + packArrayList.get(buffetPosition).getBuffet_name() + " is already exist, please use another name.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void popUpPack(int buffetPosition) {
        ShapeableImageView packImage = requireView().findViewById(R.id.pop_up_pack_image);
        packPosition = buffetPosition;
        popUpMotherCl.setOnClickListener(v -> {
            hideKeyboard();
            ((MainActivity) requireActivity()).setNavNotDarkTransparent();
            popUpMotherCl.setVisibility(View.GONE);
        });

        String packImageUrl = packArrayList.get(buffetPosition).getImg_url();
        if (!packImageUrl.equals("")) {
            Glide.with(requireContext())
                    .load(packImageUrl)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(packImage);
        } else {
            Glide.with(requireContext())
                    .load(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(packImage);
        }

        dataPackLl.setVisibility(View.VISIBLE);
        popUpMotherCl.setVisibility(View.VISIBLE);

        buttonEditPack.setOnClickListener(v -> updatePack(buffetPosition));

        buttonDeletePack.setOnClickListener(v -> {
            deletePack(buffetPosition);
        });

        int convertedDk = Integer.parseInt((packArrayList.get(buffetPosition).getBuffet_price_dk()));
        int convertedLk = Integer.parseInt((packArrayList.get(buffetPosition).getBuffet_price_lk()));
        DecimalFormat idr = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        idr.setRoundingMode(RoundingMode.FLOOR);
        idr.setMinimumFractionDigits(0);
        idr.setMaximumFractionDigits(2);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setGroupingSeparator('.');
        dfs.setCurrencySymbol("");
        idr.setDecimalFormatSymbols(dfs);
        idr.setParseIntegerOnly(true);
        String packPriceDkString = idr.format(convertedDk);
        String packPriceLkString = idr.format(convertedLk);

        packName.setText(packArrayList.get(buffetPosition).getBuffet_name());
        packPriceDk.setText(packPriceDkString);
        packPriceLk.setText(packPriceLkString);
        packRequired.setText(packArrayList.get(buffetPosition).getBuffet_required_menu());
        packChoice.setText(packArrayList.get(buffetPosition).getBuffet_choice_menu());
    }

    private void popUpItem(int buffetItemPosition) {
        editPackLl.setVisibility(View.GONE);
        dataPackLl.setVisibility(View.GONE);

        popUpMotherCl.setOnClickListener(v -> {
            hideKeyboard();
            popUpMotherCl.setVisibility(View.GONE);
        });

        editItemLl.setVisibility(View.VISIBLE);
        buttonEditPack.setVisibility(View.VISIBLE);
        popUpMotherCl.setVisibility(View.VISIBLE);

        editItemTitle.setText(itemArrayList.get(buffetItemPosition).getItem_name());
        editItemPriceDk.setText(itemArrayList.get(buffetItemPosition).getItem_price_dk());
        editItemPriceLk.setText(itemArrayList.get(buffetItemPosition).getItem_price_lk());

        buttonSaveUpdateItem.setOnClickListener(v -> {
            if (!checkAllFieldsEditItem()) {
                Toast.makeText(requireActivity().getApplicationContext(), "Please complete all fields.", Toast.LENGTH_SHORT).show();
            } else {
                dr.child("items").orderByChild("item_name").equalTo(itemArrayList.get(buffetItemPosition).getItem_name()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            dataSnapshot.getRef().child("item_price_dk").setValue(editItemPriceDk.getText().toString());
                            dataSnapshot.getRef().child("item_price_lk").setValue(editItemPriceLk.getText().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                Toast.makeText(requireContext(), "Buffet item was successfully updated.", Toast.LENGTH_SHORT).show();
                popUpMotherCl.setVisibility(View.GONE);
            }
        });
    }

    private void deletePack(int buffetPosition) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("iwc_app/buffet_packs");
        storageReference.child(packArrayList.get(buffetPosition).getBuffet_name() + ".jpg").delete().addOnSuccessListener(unused -> {
            Toast.makeText(requireContext(), packArrayList.get(buffetPosition).getBuffet_name() + " was successfully removed", Toast.LENGTH_SHORT).show();
        });

        dr.child("packs").orderByChild("buffet_name").equalTo(packArrayList.get(buffetPosition).getBuffet_name()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        dataSnapshot.getRef().removeValue();
                        Toast.makeText(requireContext(), packArrayList.get(buffetPosition).getBuffet_name() + " was successfully removed", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "Failed to remove data, pack does not exist.", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        closePopUp();
    }

    private void closePopUp() {
        popUpMotherCl.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).setNavNotDarkTransparent();
        loadPackData();
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = requireActivity().getCurrentFocus();
        if (view == null) {
            view = new View(requireContext());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContent.launch("image/*");
    }

    private void selectImageUpdate() {
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

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        newBuffetImageUri = uri;
        newPackImg.setImageURI(newBuffetImageUri);
        Glide.with(requireContext())
                .load(newBuffetImageUri)
                .placeholder(R.drawable.png_npt)
                .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                .into(newPackImg);
    });

    ActivityResultLauncher<String> mGetContentEdit = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        packEditImageUri = uri;
        packEditImageSiv.setImageURI(packEditImageUri);
        if (packEditImageUri != null) {
            Glide.with(requireContext())
                    .load(packEditImageUri)
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(packEditImageSiv);
        } else {
            Glide.with(requireContext())
                    .load(packArrayList.get(packPosition).getImg_url())
                    .placeholder(R.drawable.png_npt)
                    .transform(new CenterCrop(), new GranularRoundedCorners(28, 28, 0, 0))
                    .into(packEditImageSiv);
        }
    });


}