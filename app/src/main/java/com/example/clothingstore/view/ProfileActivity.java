package com.example.clothingstore.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.clothingstore.R;
import com.example.clothingstore.model.User;
import com.example.clothingstore.viewmodel.UserViewModel;
import com.google.gson.Gson;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvAddress, tvBalance;
    private User user;
    private Button deleteAccButton, updateAccButton, addBalanceButton;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAddress = findViewById(R.id.tvAddress);
        tvBalance = findViewById(R.id.tvBalance);
        deleteAccButton = findViewById(R.id.deleteAccBtn);
        updateAccButton = findViewById(R.id.updateAccBtn);
        addBalanceButton = findViewById(R.id.addBalanceButton);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        user = getUserInfo();

        setTextViews(user);

        deleteAccButton.setOnClickListener(view -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete this account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            boolean isUser = false;
                            userViewModel.deleteUser(user.getId());

                            SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
                            SharedPreferences.Editor userEditor = userPreferences.edit();
                            userEditor.putBoolean("isUser", isUser);
                            userEditor.apply();

                            finish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        });

        updateAccButton.setOnClickListener(view -> {
            Intent i = new Intent(ProfileActivity.this, UpdateAccountActivity.class);
            startActivity(i);
        });

        addBalanceButton.setOnClickListener(view -> {
            Intent i = new Intent(ProfileActivity.this, AddBalanceActivity.class);
            startActivity(i);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        user = getUserInfo();

        setTextViews(user);
    }

    private User getUserInfo(){
        SharedPreferences profilePreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = profilePreferences.getString("user", "");

        if(!userJson.isEmpty()){
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    private void setTextViews(User user){

        tvName.setText("Name: " + user.getName());
        tvEmail.setText("Email: " + user.getEmail());
        tvBalance.setText("Balance: " + String.format("%.2f", user.getBalance()) + "$");
        tvAddress.setText("Address: " + user.getAddress().getStreet() + " " + user.getAddress().getNumber() + ", " + user.getAddress().getCity() + " " + user.getAddress().getZipcode());
    }

}

