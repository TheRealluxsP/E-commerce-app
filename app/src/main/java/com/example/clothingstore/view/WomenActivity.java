package com.example.clothingstore.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clothingstore.Utils.SimpleTextWatcher;
import com.example.clothingstore.adapters.ArticleAdapter;
import com.example.clothingstore.R;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.viewmodel.ArticleViewModel;

import java.util.ArrayList;
import java.util.List;

public class WomenActivity extends AppCompatActivity {

    private ArticleViewModel articleViewModel;
    private RecyclerView recyclerView;
    private ArticleAdapter articleAdapter;
    private List<Article> fullArticleList = new ArrayList<>();
    private List<Article> filteredArticleList = new ArrayList<>();
    private EditText searchFilter, minPriceFilter, maxPriceFilter;
    private Spinner typeFilter;
    boolean isAdmin, isUser;
    private final int LOGIN_REQUEST = 1;
    private final int ADD_ARTICLE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_women);


        searchFilter = findViewById(R.id.searchFilter);

        minPriceFilter = findViewById(R.id.minPriceFilter);
        maxPriceFilter = findViewById(R.id.maxPriceFilter);
        typeFilter = findViewById(R.id.typeFilter);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Display items in 2 columns

        articleAdapter = new ArticleAdapter(this, filteredArticleList); // Initialize adapter with filtered list
        recyclerView.setAdapter(articleAdapter);

        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
        isUser = userPreferences.getBoolean("isUser", false);

        // Observe the articles from ViewModel
        articleViewModel.getArticles().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null) {
                    fullArticleList.clear();
                    for (Article article : articles) {
                        if ("women's clothing".equalsIgnoreCase(article.getCategory())) {
                            fullArticleList.add(article);
                        }
                    }
                    filterArticles(searchFilter.getText().toString()); // Apply initial filter based on any text in search bar
                }
            }
        });

        // Filter whenever the search text changes
        searchFilter.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { applyFilters(); }
        });

        // Filter whenever the price range or type changes
        minPriceFilter.addTextChangedListener(new SimpleTextWatcher() { @Override public void afterTextChanged(Editable s) { applyFilters(); } });
        maxPriceFilter.addTextChangedListener(new SimpleTextWatcher() { @Override public void afterTextChanged(Editable s) { applyFilters(); } });
        typeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { applyFilters(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Set up navigation buttons
        setupNavigationButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
        isUser = userPreferences.getBoolean("isUser", false);

        invalidateOptionsMenu();
    }


    private void applyFilters() {
        String query = searchFilter.getText().toString().toLowerCase();
        String selectedType = typeFilter.getSelectedItem().toString().toLowerCase();
        double minPrice = parseDouble(minPriceFilter.getText().toString(), 0);
        double maxPrice = parseDouble(maxPriceFilter.getText().toString(), Double.MAX_VALUE);

        filteredArticleList.clear();

        for (Article article : fullArticleList) {
            // Check if the name contains the search query
            boolean matchesQuery = article.getName().toLowerCase().contains(query);

            // Check if the name contains the selected type keyword
            boolean matchesType = selectedType.equals("all") || article.getName().toLowerCase().contains(selectedType) || article.getDescription().toLowerCase().contains(selectedType);

            // Check if the price is within the specified range
            boolean matchesPrice = article.getPrice() >= minPrice && article.getPrice() <= maxPrice;

            // Add to filtered list if all conditions match
            if (matchesQuery && matchesType && matchesPrice) {
                filteredArticleList.add(article);
            }
        }

        articleAdapter.notifyDataSetChanged();
    }

    private double parseDouble(String value, double defaultValue) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    // Method to filter articles based on the search query
    private void filterArticles(String query) {
        filteredArticleList.clear();

        if (query.isEmpty()) {
            // If the query is empty, show all women's clothing articles
            filteredArticleList.addAll(fullArticleList);
        } else {
            // Filter based on the query
            for (Article article : fullArticleList) {
                if (article.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredArticleList.add(article);
                }
            }
        }
        articleAdapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    // Setup navigation buttons to other activities
    private void setupNavigationButtons() {
        Button btnMen = findViewById(R.id.btnMen);
        Button btnAccessories = findViewById(R.id.btnAccessories);

        btnMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WomenActivity.this, MenActivity.class);

                startActivity(intent);
                finish();
            }
        });

        btnAccessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WomenActivity.this, AccessoriesActivity.class);

                startActivity(intent);

                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating the menu
        if (isAdmin) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        }else if (isUser) {
            getMenuInflater().inflate(R.menu.user_menu, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.login_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.loginButton) {
            Intent intent = new Intent(WomenActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        } else if (item.getItemId() == R.id.logoutButton) {
            new AlertDialog.Builder(WomenActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            isAdmin = false;
                            isUser = false;

                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isAdmin", false);
                            editor.apply();

                            SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
                            SharedPreferences.Editor userEditor = userPreferences.edit();
                            userEditor.putBoolean("isUser", false);
                            userEditor.apply();

                            invalidateOptionsMenu();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else if (item.getItemId() == R.id.addArticleButton) {
            Intent intent = new Intent(WomenActivity.this, AddArticleActivity.class);
            startActivityForResult(intent, ADD_ARTICLE_REQUEST);
        }else if (item.getItemId() == R.id.profileButton) {
            Intent intent = new Intent(WomenActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.userCartButton) {
            Intent intent = new Intent(WomenActivity.this, CartActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOGIN_REQUEST && resultCode == RESULT_OK && data != null) {
            isAdmin = data.getBooleanExtra("isAdmin", false);
            isUser = data.getBooleanExtra("isUser", false);

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAdmin", isAdmin);
            editor.apply();

            SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean("isUser", isUser);
            userEditor.apply();

            invalidateOptionsMenu();
        } else if (requestCode == ADD_ARTICLE_REQUEST && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("articleName");
            double price = data.getDoubleExtra("articlePrice", 0);
            String description = data.getStringExtra("articleDescription");
            String category = data.getStringExtra("articleCategory");
            String image = data.getStringExtra("articleImageUrl");

            Article article = new Article(name, price, description, category, image);
            articleViewModel.addNewArticle(article);
        }
    }
}
