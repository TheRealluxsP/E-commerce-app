package com.example.clothingstore.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.clothingstore.R;
import com.example.clothingstore.model.User;
import com.google.gson.Gson;

public class AddBalanceActivity extends AppCompatActivity {

    private User user;
    private Button addBalance;
    private TextView tvAddBalance;
    private EditText etAddBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

        user = getUserInfo();
        addBalance = findViewById(R.id.addBalanceConfirmationButton);
        tvAddBalance = findViewById(R.id.addBalanceTextView);
        etAddBalance = findViewById(R.id.addBalanceEdiText);

        addBalance.setOnClickListener(view ->{
            if (etAddBalance.getText().toString().equals("")) {
                Toast.makeText(AddBalanceActivity.this, "Please enter your amount", Toast.LENGTH_SHORT).show();
                return;
            }

            if (Double.parseDouble(etAddBalance.getText().toString()) > 1000){
                Toast.makeText(AddBalanceActivity.this, "Sorry, we dont support transactions of more than 1000 at a time.", Toast.LENGTH_SHORT).show();
                return;
            }

            String balanceText = etAddBalance.getText().toString();
            double balance = Double.parseDouble(balanceText);

            user.addBalance(balance);

            saveUserInfo(user);
            finish();

        });

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

    private void saveUserInfo(User user) {
        SharedPreferences profilePreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        SharedPreferences.Editor userProfileEditor = profilePreferences.edit();


        Gson gson = new Gson();
        String userJson = gson.toJson(user);

        userProfileEditor.putString("user", userJson);
        userProfileEditor.apply();
    }
}