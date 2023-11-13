package com.iwc.iwctablet;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iwc.iwctablet.fragment.BuffetListFragment;
import com.iwc.iwctablet.fragment.CustomerListFragment;
import com.iwc.iwctablet.fragment.OrderNewFragment;
import com.iwc.iwctablet.fragment.StallListFragment;
import com.iwc.iwctablet.utility.NetworkChangeListener;

import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    CustomerListFragment customerListFragment = new CustomerListFragment();
    BuffetListFragment buffetListFragment = new BuffetListFragment();
    StallListFragment stallListFragment = new StallListFragment();
    OrderNewFragment orderNewFragment = new OrderNewFragment();
    NavigationView navigation;
    ImageView navDot1, navDot2, navDot3, navDot4, navDot5;
    String currentVersion, latestVersion;
    ConstraintLayout touchToHide, versionCl, exitConfirmationCl;
    MaterialButton moreButton, downloadButton;
    TextView logoutTv, textMessage;
    Uri downloadUri;
    DatabaseReference drVersion = FirebaseDatabase.getInstance().getReference("settings");
    NetworkChangeListener networkChangeListener = new NetworkChangeListener();


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this, new String[] {
                READ_EXTERNAL_STORAGE,
                WRITE_EXTERNAL_STORAGE
        }, PackageManager.PERMISSION_GRANTED);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        downloadButton = findViewById(R.id.button_download_latest_version);
        currentVersion = getResources().getString(R.string.current_version);
        navigation = findViewById(R.id.navigation_view);
        navDot1 = findViewById(R.id.dot_nav_1);
        navDot2 = findViewById(R.id.dot_nav_2);
        navDot3 = findViewById(R.id.dot_nav_3);
        navDot4 = findViewById(R.id.dot_nav_4);
        navDot5 = findViewById(R.id.dot_nav_5);
        exitConfirmationCl = findViewById(R.id.exit_confirmation_cl);
        touchToHide = findViewById(R.id.menu_list_top_right);
        textMessage = findViewById(R.id.version_message_tv);
        moreButton = findViewById(R.id.more_button);
        versionCl = findViewById(R.id.version_cl);
        logoutTv = findViewById(R.id.logout_tv);
        touchToHide.setVisibility(View.GONE);
        navigation.setVisibility(View.GONE);
        exitConfirmationCl.setVisibility(View.GONE);
        versionCl.setVisibility(View.GONE);
        AtomicInteger navStatus = new AtomicInteger();

        navDot1.setVisibility(View.GONE); navDot2.setVisibility(View.GONE); navDot3.setVisibility(View.GONE); navDot4.setVisibility(View.GONE); navDot5.setVisibility(View.GONE);

        Animation moveDotAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.in_stall_filter_button);

        drVersion.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    latestVersion = String.valueOf(snapshot.child("tablet_latest_version").getValue());

                    if (currentVersion.equals(latestVersion)) {
                        navigation.setVisibility(View.VISIBLE);
                        versionCl.setVisibility(View.GONE);
                        moreButton.setOnClickListener(v -> touchToHide.setVisibility(View.VISIBLE));
                        logoutTv.setOnClickListener(v -> {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        });
                        touchToHide.setOnClickListener(v -> touchToHide.setVisibility(View.GONE));
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, customerListFragment).commit();
                        navigation.setCheckedItem(R.id.customer);
                        Handler showDot = new Handler();
                        showDot.postDelayed(() -> navDot1.setVisibility(View.VISIBLE), 800);
                        navDot1.startAnimation(moveDotAnim);
                        navigation.setNavigationItemSelectedListener(item -> {
                            switch (item.getItemId()) {
                                case R.id.customer:
                                    dotManagement(1);
                                    navStatus.set(1);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.container, customerListFragment).addToBackStack(null).commit();
                                    return true;
                                case R.id.buffet:
                                    dotManagement(2);
                                    navStatus.set(2);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.container, buffetListFragment).addToBackStack(null).commit();
                                    return true;
                                case R.id.order:
                                    dotManagement(3);
                                    navStatus.set(3);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.container, orderNewFragment).addToBackStack(null).commit();
                                    return true;
                                case R.id.stall:
                                    dotManagement(4);
                                    navStatus.set(4);
                                    getSupportFragmentManager().beginTransaction().replace(R.id.container, stallListFragment).addToBackStack(null).commit();
                                    return true;
                            }
                            return false;
                        });

                        allInOneMethod();

                    } else {
                        navigation.setVisibility(View.GONE);
                        versionCl.setVisibility(View.VISIBLE);
                        textMessage.setText("Current version of your IWC App    (" + currentVersion + ") is not up to date. Simply tap button bellow to get the latest version.");
                        downloadButton.setText("DOWNLOAD VERSION " + latestVersion);
                        downloadButton.setOnClickListener(v -> {
                            downloadUri = Uri.parse("https://drive.google.com/file/d/1LZ71wWtgGegi1CRWL4ZWZNGEsjSXaTKW/view"); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, downloadUri);
                            startActivity(intent);
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    protected void dotManagement(int dotStatus) {
        Animation shakeDot = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake_dot);
        switch (dotStatus) {
            case 1:
                navDot1.startAnimation(shakeDot);
                navDot1.setVisibility(View.VISIBLE); navDot2.setVisibility(View.GONE); navDot3.setVisibility(View.GONE); navDot4.setVisibility(View.GONE); navDot5.setVisibility(View.GONE);
                break;
            case 2:
                navDot2.startAnimation(shakeDot);
                navDot2.setVisibility(View.VISIBLE); navDot3.setVisibility(View.GONE); navDot4.setVisibility(View.GONE); navDot5.setVisibility(View.GONE); navDot1.setVisibility(View.GONE);
                break;
            case 3:
                navDot3.startAnimation(shakeDot);
                navDot3.setVisibility(View.VISIBLE); navDot4.setVisibility(View.GONE); navDot5.setVisibility(View.GONE); navDot1.setVisibility(View.GONE); navDot2.setVisibility(View.GONE);
                break;
            case 4:
                navDot4.startAnimation(shakeDot);
                navDot4.setVisibility(View.VISIBLE); navDot5.setVisibility(View.GONE); navDot1.setVisibility(View.GONE); navDot2.setVisibility(View.GONE); navDot3.setVisibility(View.GONE);
                break;
//            case 5:
//                navDot5.startAnimation(shakeDot);
//                navDot5.setVisibility(View.VISIBLE); navDot1.setVisibility(View.GONE); navDot2.setVisibility(View.GONE); navDot3.setVisibility(View.GONE); navDot4.setVisibility(View.GONE);
//                break;
        }
    }

    @Override
    public void onBackPressed() {
        exitConfirmationCl.setVisibility(View.VISIBLE);
        MaterialButton cancelExit = findViewById(R.id.exit_cancel_button);
        MaterialButton buttonExit = findViewById(R.id.exit_confirmation_button);
        buttonExit.setOnClickListener(v -> {
            exitConfirmationCl.setVisibility(View.GONE);
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        });
        cancelExit.setOnClickListener(v -> exitConfirmationCl.setVisibility(View.GONE));

        exitConfirmationCl.setOnClickListener(v -> exitConfirmationCl.setVisibility(View.GONE));
    }

    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }

    public void setNavDarkTransparent() {
        View darkTransparentView = findViewById(R.id.dark_transparent_view);
        darkTransparentView.setVisibility(View.VISIBLE);
    }
    public void setNavNotDarkTransparent() {
        View darkTransparentView = findViewById(R.id.dark_transparent_view);
        darkTransparentView.setVisibility(View.GONE);
    }

    public void allInOneMethod() {
        Animation inAnimNav = AnimationUtils.loadAnimation(this, R.anim.in_navbar);
        navigation.startAnimation(inAnimNav);
    }


}