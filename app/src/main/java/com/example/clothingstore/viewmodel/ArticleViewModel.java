package com.example.clothingstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.repository.ArticleRepo;

import java.util.List;

public class ArticleViewModel extends AndroidViewModel {
    //The extension of the AndroidViewModel class allows the ViewModel to hold and manage UI-related data in a lifecycle-conscious way

    //Instance of the repository that will provide article data
    private ArticleRepo articleRepo;

    //MutableLiveData to hold the list of articles that can be observed by the UI
    private MutableLiveData<List<Article>> articleList;

    //Constructor for the ArticleViewModel
    public ArticleViewModel(@NonNull Application application) {
        super(application); // Call the superclass constructor with the application context

        // Get the singleton instance of the ArticleRepo
        articleRepo = ArticleRepo.getInstance();

        // Get the MutableLiveData containing the list of articles from the repository
        articleList = articleRepo.getArticles();
    }

    public void addNewArticle(Article article) {
        articleRepo.addNewArticle(article);
    }

    public void updateArticle(int id, Article article) {
        articleRepo.updateArticle(id, article);
    }

    public void deleteArticle(int id) {
        articleRepo.deleteArticle(id);
    }

    // Public method to expose the article list to the UI
    public MutableLiveData<List<Article>> getArticles() {
        return articleList; // Return the MutableLiveData containing the list of articles
    }
}
