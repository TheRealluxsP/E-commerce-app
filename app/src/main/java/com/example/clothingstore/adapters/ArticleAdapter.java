package com.example.clothingstore.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.clothingstore.R;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.view.ProductDetailActivity;
import com.google.gson.Gson;

import java.util.List;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> articleList;
    private Context context;
    private boolean isAdmin = false; // Admin status flag

    public ArticleAdapter(Context context, List<Article> articleList) {
        this.context = context;
        this.articleList = articleList;
    }

    // Setter to define if the current user is an admin
    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Setter for articles
    public void setArticles(List<Article> articles) {
        this.articleList = articles;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article article = articleList.get(position);

        // Load article image with Glide
        Glide.with(context)
                .load(article.getImage())
                .placeholder(R.drawable.placeholder) // Optional placeholder
                .error(R.drawable.error_image) // Optional error image
                .into(holder.imageView);

        // Set article name and price
        holder.textViewArticleName.setText(article.getName());
        holder.textViewArticlePrice.setText(String.format("$%.2f", article.getPrice()));

        // Set click listener for each item
        holder.itemView.setOnClickListener(v -> {
            saveArticleInfo(article);
            Intent intent = new Intent(context, ProductDetailActivity.class);
            /*intent.putExtra("productId", article.getId());
            intent.putExtra("productName", article.getName());
            intent.putExtra("productImage", article.getImage());
            intent.putExtra("productPrice", article.getPrice());
            intent.putExtra("productDescription", article.getDescription());
            intent.putExtra("productCategory", article.getCategory());*/
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewArticleName, textViewArticlePrice;

        public ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewArticle);
            textViewArticleName = itemView.findViewById(R.id.textViewArticleName);
            textViewArticlePrice = itemView.findViewById(R.id.textViewArticlePrice);
        }
    }

    private void saveArticleInfo(Article article) {
        SharedPreferences articlePreferences = context.getSharedPreferences("ArticleInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor articleEditor = articlePreferences.edit();

        Gson gson = new Gson();
        String articleJson = gson.toJson(article);

        articleEditor.putString("article", articleJson);
        articleEditor.apply();
    }
}
