package com.example.clothingstore.view;

import android.os.Bundle;

import android.text.InputType;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.clothingstore.R;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.model.User;
import com.example.clothingstore.viewmodel.ArticleViewModel;
import com.example.clothingstore.viewmodel.UserViewModel;

public class RegisterActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private EditText etEmail, etPassword, etConfirmPassword, etUsername, etPhone, etCity, etStreetName, etStreetNumber, etZipcode;
    private Button btnRegister;
    private CheckBox checkboxShowPasswordRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
        etConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        etUsername = findViewById(R.id.editTextUsername);
        etPhone = findViewById(R.id.editTextPhone);
        etCity = findViewById(R.id.editTextCity);
        etStreetName = findViewById(R.id.editTextStreetName);
        etStreetNumber = findViewById(R.id.editTextStreetNumber);
        etZipcode = findViewById(R.id.editTextZipcode);
        checkboxShowPasswordRegister = findViewById(R.id.checkboxShowPasswordRegister);

        btnRegister = findViewById(R.id.btnRegister);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        checkboxShowPasswordRegister.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else{
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }

                etPassword.setSelection(etPassword.length());
                etConfirmPassword.setSelection(etConfirmPassword.length());
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

    }

    public void register(){

        //get inserted data
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String streetName = etStreetName.getText().toString().trim();
        String streetNumber = etStreetNumber.getText().toString().trim();
        String zipcode = etZipcode.getText().toString().trim();


        // Validate fields and confirm password match
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }


        Address address = null;
        User user = null;
        try {
            address = new Address(city, zipcode, Integer.parseInt(streetNumber), streetName);
        }catch (Exception e){
            Toast.makeText(this, "Invalid address", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            user = new User(email, password, username, phone, address);
        }catch (Exception e){
            Toast.makeText(this, "Invalid user Data", Toast.LENGTH_SHORT).show();
            return;
        }

        userViewModel.addNewUser(user);
        Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }
}