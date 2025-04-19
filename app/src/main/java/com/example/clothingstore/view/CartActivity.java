package com.example.clothingstore.view;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.clothingstore.R;
import com.example.clothingstore.model.CartItem;
import com.example.clothingstore.model.User;
import com.google.gson.Gson;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView totalPriceTextView, tvYourBalance;
    private Button checkoutButton;
    private User user;
    private List<CartItem> cartItems;
    private double totalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsContainer = findViewById(R.id.cartItemsContainer);
        totalPriceTextView = findViewById(R.id.tvTotalPrice);
        checkoutButton = findViewById(R.id.btnCheckout);
        tvYourBalance = findViewById(R.id.tvYourBalance);

        try {
            user = getUserInfo();
            cartItems = user.getCart();
        }catch (Exception e){
            Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        tvYourBalance.setText(String.format("Your Balance: $%.2f", user.getBalance()));


        // Populate cart items dynamically
        for (CartItem cartItem : cartItems) {
            addCartItemToLayout(cartItem);
        }

        // Update the total price
        updateTotalPrice(cartItems);

        // Handle checkout button click
        checkoutButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(CartActivity.this, "No items in your cart.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (user.getBalance() < totalPrice) {
                Toast.makeText(CartActivity.this, "You do not have enough balance.", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(CartActivity.this, "Transaction completed", Toast.LENGTH_SHORT).show();
            user.setBalance(user.getBalance() - totalPrice);
            user.getCart().clear();
            saveUserInfo(user);
            finish();
        });
    }

    private void addCartItemToLayout(CartItem cartItem) {
        // Inflate the cart item view
        View cartItemView = getLayoutInflater().inflate(R.layout.cart_item, null);

        TextView name = cartItemView.findViewById(R.id.cartItemName);
        TextView price = cartItemView.findViewById(R.id.cartItemPrice);
        TextView quantity = cartItemView.findViewById(R.id.cartItemQuantity);
        ImageView image = cartItemView.findViewById(R.id.cartItemImage);
        Button removeButton = cartItemView.findViewById(R.id.removeCartItem);

        name.setText(cartItem.getProduct().getName());
        price.setText(String.format("$%.2f", cartItem.getProduct().getPrice()));
        quantity.setText("Quantity: " + cartItem.getQuantity());

        // Load image using Glide
        Glide.with(this)
                .load(cartItem.getProduct().getImage())
                .placeholder(R.drawable.placeholder)
                .into(image);

        // Set remove button functionality
        removeButton.setOnClickListener(v -> {
            int newQuantity = cartItem.getQuantity() - 1;
            if (newQuantity > 0) {
                int productId = cartItem.getProduct().getId();
                user.getCartItemByArticle(productId).setQuantity(newQuantity);
                saveUserInfo(user);
                quantity.setText("Quantity: " + newQuantity);
            } else {
                // Remove item from the list if quantity is 0
                user.getCart().remove(cartItem);
                // Save current user data in sharedPreferences
                saveUserInfo(user);
                // Remove the cart item view because it has reached 0
                cartItemsContainer.removeView(cartItemView);
            }
            // Update total price
            updateTotalPrice(cartItems);
        });

        // Add the cart item view to the container
        cartItemsContainer.addView(cartItemView);
    }

    public void updateTotalPrice(List<CartItem> cartItems) {
        totalPrice = 0.0;
        for (CartItem cartItem : cartItems) {
            totalPrice += cartItem.getProduct().getPrice() * cartItem.getQuantity();
        }

        totalPriceTextView.setText(String.format("Total: $%.2f", totalPrice));
    }

    private User getUserInfo() {
        SharedPreferences userPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        Gson gson = new Gson();
        String userJson = userPreferences.getString("user", "");

        if (!userJson.isEmpty()) {
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
