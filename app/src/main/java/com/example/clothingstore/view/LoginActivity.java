package com.example.clothingstore.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.lifecycle.Observer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.clothingstore.R;
import com.example.clothingstore.model.User;
import com.example.clothingstore.viewmodel.UserViewModel;
import com.google.gson.Gson;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button btnLogin, btnRegister;
    private UserViewModel userViewModel;
    private CheckBox checkboxShowPassword;

    // Define fixed admin credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        checkboxShowPassword = findViewById(R.id.checkboxShowPassword);

        // Initialize ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);


        // Set login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                    // First, check if credentials match the fixed admin credentials
                if (email.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                    // Admin login successful
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("isAdmin", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                    return;
                }

                // If not admin, call loginUser from ViewModel for regular user check
                userViewModel.loginUser(email, password).observe(LoginActivity.this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            // Regular user login successful
                            saveUserInfo(user);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("isUser", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            // Login failed
                            Toast.makeText(LoginActivity.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        checkboxShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    editTextPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                editTextPassword.setSelection(editTextPassword.length());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void saveUserInfo(User user) {
        SharedPreferences profilePreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor userProfileEditor = profilePreferences.edit();


        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        userProfileEditor.putString("user", userJson);
        userProfileEditor.apply();
    }

}