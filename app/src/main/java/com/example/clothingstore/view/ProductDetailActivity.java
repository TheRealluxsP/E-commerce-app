package com.example.clothingstore.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.clothingstore.R;
import com.example.clothingstore.adapters.ArticleAdapter;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.model.CartItem;
import com.example.clothingstore.model.User;
import com.example.clothingstore.viewmodel.ArticleViewModel;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewName;
    private TextView textViewPrice;
    private TextView textViewDescription;
    private TextView tvQuantity;
    private Button btnUpdate, btnDelete, btnAddToCart;
    private boolean isAdmin, isUser;
    private ArticleViewModel articleViewModel;
    private ArticleAdapter articleAdapter;
    private final int UPDATE_ARTICLE_REQUEST = 1;
    private int id;
    private String name, description, category, imageUrl;
    private double price;
    private Spinner itemQuantitySpinner;
    private User user;
    private Article article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        user = getUserInfo();
        article = getArticleInfo();

        //Initialize views
        imageView = findViewById(R.id.imageViewProductDetail);
        textViewName = findViewById(R.id.textViewProductName);
        textViewPrice = findViewById(R.id.textViewProductPrice);
        textViewDescription = findViewById(R.id.textViewProductDescription);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        itemQuantitySpinner = findViewById(R.id.itemQuantitySpinner);
        tvQuantity = findViewById(R.id.tvQuantity);
        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        // Get data passed from the MainActivity
        id = article.getId();
        name = article.getName();
        imageUrl = article.getImage();
        price = article.getPrice();
        description = article.getDescription();
        category = article.getCategory();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
        isUser = userPreferences.getBoolean("isUser", false);

        if (isAdmin) {
            btnUpdate.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            Log.d("ProductDetailActivity", "Admin buttons should be visible");

            btnUpdate.setOnClickListener(v -> {
                Intent intent = new Intent(ProductDetailActivity.this, UpdateArticleActivity.class);
                intent.putExtra("productName", name);
                intent.putExtra("productImage", imageUrl);
                intent.putExtra("productPrice", price);
                intent.putExtra("productDescription", description);
                intent.putExtra("productCategory", category);
                startActivityForResult(intent, UPDATE_ARTICLE_REQUEST);
            });

            btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(ProductDetailActivity.this)
                        .setTitle("Delete Product")
                        .setMessage("Are you sure you want to delete this product?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                articleViewModel.deleteArticle(id);

                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

            });
        } else if (isUser) {
            btnAddToCart.setVisibility(View.VISIBLE);
            itemQuantitySpinner.setVisibility(View.VISIBLE);
            tvQuantity.setVisibility(View.VISIBLE);

            ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Arrays.asList(1, 2, 3, 4, 5));
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemQuantitySpinner.setAdapter(adapter);

            btnAddToCart.setOnClickListener(v -> {
                CartItem cartItem = null;
                int selectedQuantity = (Integer) itemQuantitySpinner.getSelectedItem();
                try {
                    cartItem = new CartItem(article, selectedQuantity);
                }catch (Exception e){
                    Toast.makeText(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                try {
                    user.addCartItem(cartItem);
                    saveUserInfo(user);
                }catch (Exception e){
                    Toast.makeText(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                Toast.makeText(ProductDetailActivity.this, "Added" + " " + String.valueOf(user.getCartItemByArticle(article.getId()).getQuantity()) + " "  + "product(s) to cart.", Toast.LENGTH_SHORT).show();
                finish();
            });

        }


        // Populate the views with product details
        textViewName.setText(name);
        textViewPrice.setText(String.format("$%.2f", price));
        textViewDescription.setText(description);
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(imageView);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.loginButton){
            //Handling onClickEvents in the loginButton
            Toast.makeText(ProductDetailActivity.this, "Login", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_ARTICLE_REQUEST && resultCode == RESULT_OK && data != null) {
            name = data.getStringExtra("articleName");
            price = data.getDoubleExtra("articlePrice", 0.0);
            description = data.getStringExtra("articleDescription");
            imageUrl = data.getStringExtra("articleImageUrl");
            category = data.getStringExtra("articleCategory");

            Article article = new Article(name, price, description, category, imageUrl);
            articleViewModel.updateArticle(id, article);
            finish();
        }
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

    private Article getArticleInfo(){
        SharedPreferences articlePreferences = getSharedPreferences("ArticleInfo", MODE_PRIVATE);
        Gson gson = new Gson();
        String articleJson = articlePreferences.getString("article", "");

        if(!articleJson.isEmpty()){
            return gson.fromJson(articleJson, Article.class);
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