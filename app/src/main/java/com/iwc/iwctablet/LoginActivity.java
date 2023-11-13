package com.iwc.iwctablet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextInputEditText login_email, login_password;
    private ImageView eye_on, eye_off;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MaterialButton login_button = findViewById(R.id.login_button);
        MaterialCheckBox remember_me = findViewById(R.id.remember_me_cb);
        login_button.setOnClickListener(this);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);

        eye_on = findViewById(R.id.eye_on);
        eye_off = findViewById(R.id.eye_off);

        eye_on.setOnClickListener(v -> {
            if(eye_off.getVisibility() == View.INVISIBLE) {
                login_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eye_off.setVisibility(View.VISIBLE);
                eye_on.setVisibility(View.INVISIBLE);
            } else {
                Log.d("angie", "else of eye conditions");
            }
        });
        eye_off.setOnClickListener(v -> {
            if(eye_off.getVisibility() == View.VISIBLE) {
                login_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eye_on.setVisibility(View.VISIBLE);
                eye_off.setVisibility(View.INVISIBLE);
            } else {
                Log.d("angie", "else of eye conditions");
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = sharedPreferences.getString("remember", "");
        if(checkbox.equals("true")) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (checkbox.equals("false")) {
            Toast.makeText(this, "Please log in first.", Toast.LENGTH_SHORT).show();
        }

        remember_me.setOnCheckedChangeListener((button, isChecked) -> {
            if(button.isChecked()) {
                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "true");
                editor.apply();
            } else if (!button.isChecked()) {
                SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        mAuth = FirebaseAuth.getInstance();

//        MaterialTextView forgot_password  = findViewById(R.id.forgot_password);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.login_button) {
            userLogin();
        } else if (v.getId() == R.id.forgot_password) {
            Toast.makeText(this, "test forgot password toast", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void userLogin() {
        String email = Objects.requireNonNull(login_email.getText()).toString().trim();
        String password = Objects.requireNonNull(login_password.getText()).toString().trim();

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(LoginActivity.this, "Please check your email address", Toast.LENGTH_LONG).show();
//            login_email.setError("Please enter a valid email!", getDrawable(R.drawable.input_bg));
//            login_email.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please check your password", Toast.LENGTH_LONG).show();
//            login_password.setError("Password enter a valid password!", getDrawable(R.drawable.input_bg));
//            login_password.requestFocus();
            return;
        }

        MaterialButton login_button = findViewById(R.id.login_button);
//        login_button.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                if(user.isEmailVerified()) {
                    //direct to homepage
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    login_button.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    user.sendEmailVerification();
                    Toast.makeText(LoginActivity.this, "Check your email to verify your account before you log in to IWC App", Toast.LENGTH_LONG).show();
                }

            } else {
                //login failed
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, "Failed to login, please check your credentials.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}