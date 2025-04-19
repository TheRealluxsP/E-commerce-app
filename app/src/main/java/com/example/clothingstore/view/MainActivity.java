package com.example.clothingstore.view;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.clothingstore.R;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.viewmodel.ArticleViewModel;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //Initialization
    private ArticleViewModel articleViewModel;
    private ImageView imageView;
    private List<Article> filteredArticleList;
    private List<Article> articleList;
    private int currentIndex = 0;
    private Handler handler = new Handler();
    private Runnable imageSwitcher;
    private boolean isAdmin, isUser;
    private final int LOGIN_REQUEST = 1;
    private final int ADD_ARTICLE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ImageView
        imageView = findViewById(R.id.mainDisplayImage);

        // Initialize ViewModel
        articleViewModel = new ViewModelProvider(this).get(ArticleViewModel.class);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
        isUser = userPreferences.getBoolean("isUser", false);


        // Observe the articles from ViewModel
        articleViewModel.getArticles().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null && !articles.isEmpty()) {
                    // Filter articles to include only clothing and jewelry items
                    filteredArticleList = new ArrayList<>();
                    for (Article article : articles) {
                        String category = article.getCategory().toLowerCase();
                        if (category.contains("clothing") || category.contains("jewelery")) {
                            filteredArticleList.add(article);
                        }
                    }

                    if (!filteredArticleList.isEmpty()) {
                        startImageCycling(); // Start cycling images when filtered data is available
                    }
                }
            }
        });
        // Button setup for navigation
        Button btnWomen = findViewById(R.id.btnWomen);
        Button btnMen = findViewById(R.id.btnMen);
        Button btnAccessories = findViewById(R.id.btnAccessories);
        Button btnAll = findViewById(R.id.btnAll);

        btnWomen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WomenActivity.class);
                startActivity(intent);
            }
        });

        btnMen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MenActivity.class);
                startActivity(intent);
            }
        });

        btnAccessories.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AccessoriesActivity.class);
                startActivity(intent);
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AllProductActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        isAdmin = sharedPreferences.getBoolean("isAdmin", false);

        SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
        isUser = userPreferences.getBoolean("isUser", false);

        invalidateOptionsMenu();
    }

    // Method to start cycling through filtered images
    private void startImageCycling() {
        if (filteredArticleList == null || filteredArticleList.isEmpty()) return;

        // Stop previous cycling if any
        handler.removeCallbacks(imageSwitcher);

        // Define the image switching behavior
        imageSwitcher = new Runnable() {
            @Override
            public void run() {
                if (filteredArticleList != null && !filteredArticleList.isEmpty()) {
                    // Load the current image into the ImageView
                    Glide.with(MainActivity.this)
                            .load(filteredArticleList.get(currentIndex).getImage())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error_image)
                            .into(imageView);

                    // Move to the next image, looping back to the start if necessary
                    currentIndex = (currentIndex + 1) % filteredArticleList.size();

                    // Schedule the next image switch after a delay (e.g., 3 seconds)
                    handler.postDelayed(this, 3000);
                }
            }
        };

        // Start the initial image switch
        handler.post(imageSwitcher);
    }

    private void updateFilteredArticleListAndCycle() {
        // Re-filter the article list (including the newly added one)
        articleViewModel.getArticles().observe(this, new Observer<List<Article>>() {
            @Override
            public void onChanged(List<Article> articles) {
                if (articles != null && !articles.isEmpty()) {
                    filteredArticleList = new ArrayList<>();
                    for (Article article : articles) {
                        String category = article.getCategory().toLowerCase();
                        if (category.contains("clothing") || category.contains("jewelery")) {
                            filteredArticleList.add(article);
                        }
                    }

                    if (!filteredArticleList.isEmpty()) {
                        // Reset current index and start the cycling from the first image
                        currentIndex = 0; // Or set to filteredArticleList.size() - 1 if you want to start from the new article

                        // Adding a small delay to ensure list is fully updated before starting the cycle
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startImageCycling(); // Start cycling images again after the delay
                            }
                        }, 100); // 100ms delay
                    }
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove callbacks to avoid memory leaks
        handler.removeCallbacks(imageSwitcher);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAdmin) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);

        }else if (isUser) {
            getMenuInflater().inflate(R.menu.user_menu, menu);
        }else {
            getMenuInflater().inflate(R.menu.login_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.loginButton) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST);
        }else if (item.getItemId() == R.id.logoutButton){
            new AlertDialog.Builder(MainActivity.this)
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
            Intent intent = new Intent(MainActivity.this, AddArticleActivity.class);
            startActivityForResult(intent, ADD_ARTICLE_REQUEST);
        } else if (item.getItemId() == R.id.profileButton) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (item.getItemId() == R.id.userCartButton) {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
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
            invalidateOptionsMenu();

            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isAdmin", isAdmin);
            editor.apply();

            SharedPreferences userPreferences = getSharedPreferences("isUser", MODE_PRIVATE);
            SharedPreferences.Editor userEditor = userPreferences.edit();
            userEditor.putBoolean("isUser", isUser);
            userEditor.apply();
        } else if (requestCode == ADD_ARTICLE_REQUEST && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("articleName");
            double price = data.getDoubleExtra("articlePrice", 0);
            String description = data.getStringExtra("articleDescription");
            String category = data.getStringExtra("articleCategory");
            String image = data.getStringExtra("articleImageUrl");

            Article article = new Article(name, price, description, category, image);

            articleViewModel.addNewArticle(article);

            updateFilteredArticleListAndCycle();
        }
    }


}

