package com.example.clothingstore.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.clothingstore.R;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.model.User;
import com.example.clothingstore.viewmodel.UserViewModel;
import com.google.gson.Gson;

public class UpdateAccountActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText etUpdateEmail, etUpdatePassword, etConfirmUpdatePassword, etUpdateUsername, etUpdatePhone, etUpdateCity, etUpdateStreetName, etUpdateStreetNumber, etUpdateZipcode;
    private Button btnUpdateAccount;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);

        etUpdateEmail = findViewById(R.id.editTextUpdateEmail);
        etUpdatePassword = findViewById(R.id.editTextUpdatePassword);
        etConfirmUpdatePassword = findViewById(R.id.editTextConfirmUpdatePassword);
        etUpdateUsername = findViewById(R.id.editTextUpdateUsername);
        etUpdatePhone = findViewById(R.id.editTextUpdatePhone);
        etUpdateCity = findViewById(R.id.editTextUpdateCity);
        etUpdateStreetName = findViewById(R.id.editTextUpdateStreetName);
        etUpdateStreetNumber = findViewById(R.id.editTextUpdateStreetNumber);
        etUpdateZipcode = findViewById(R.id.editTextUpdateZipcode);

        btnUpdateAccount = findViewById(R.id.btnUpdateAccount);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        user = getUserInfo();

        etUpdateEmail.setText(user.getEmail());
        etUpdatePassword.setText(user.getPassword());
        etConfirmUpdatePassword.setText(user.getPassword());
        etUpdateUsername.setText(user.getName());
        etUpdatePhone.setText(user.getPhone());
        etUpdateCity.setText(user.getAddress().getCity());
        etUpdateStreetName.setText(user.getAddress().getStreet());
        etUpdateStreetNumber.setText(String.valueOf(user.getAddress().getNumber()));
        etUpdateZipcode.setText(user.getAddress().getZipcode());

        btnUpdateAccount.setOnClickListener(view -> {
            String email = etUpdateEmail.getText().toString().trim();
            String password = etUpdatePassword.getText().toString().trim();
            String confirmPassword = etConfirmUpdatePassword.getText().toString().trim();
            String username = etUpdateUsername.getText().toString().trim();
            String phone = etUpdatePhone.getText().toString().trim();
            String city = etUpdateCity.getText().toString().trim();
            String streetName = etUpdateStreetName.getText().toString().trim();
            String streetNumber = etUpdateStreetNumber.getText().toString().trim();
            String zipcode = etUpdateZipcode.getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = null;
            Address address = null;

            try {
                address = new Address(city, zipcode, Integer.parseInt(streetNumber), streetName);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                newUser = new User(email, password, username, phone, address);
                newUser.setBalance(user.getBalance());
            }catch (Exception e){
                Toast.makeText(this, "Invalid user Data", Toast.LENGTH_SHORT).show();
                return;
            }

            userViewModel.updateUser(user.getId(), newUser);
            Toast.makeText(UpdateAccountActivity.this, "User Data Updated", Toast.LENGTH_SHORT).show();
            saveUserInfo(newUser);
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