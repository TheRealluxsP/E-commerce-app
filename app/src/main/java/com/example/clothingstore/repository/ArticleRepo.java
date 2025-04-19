package com.example.clothingstore.repository;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.retrofit.RetrofitApiInterface;
import com.example.clothingstore.retrofit.RetrofitClientInstance;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class ArticleRepo {

    private MutableLiveData<List<Article>> articleList; //Data holder class (MutableLiveData) that can be observed
                                                        //It is designed to hold data that can be changed over time
    //Singleton instance of ArticleRepo
    private static ArticleRepo instance;

    //Get an instance of the Retrofit API interface
    private RetrofitApiInterface service = RetrofitClientInstance.getRetrofitApiInterface();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // Get the singleton instance of ArticleRepo
    public static ArticleRepo getInstance() {
        if (instance == null) {
            instance = new ArticleRepo();
        }
        return instance;
    }

    //Private constructor for ArticleRepo
    private ArticleRepo() {
        //Initialize the MutableLiveData object
        articleList = new MutableLiveData<List<Article>>();

        fetchArticles();

    }

    public void fetchArticles() {

        //Create an observable to fetch a list of articles from the API
        Observable<List<Article>> observable = service.getArticles();

        //Create an Observer to handle the Observable's events
        Observer<List<Article>> observer = new Observer<List<Article>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                //This method is called when the observer subscribes to the Observable
                //Can add logic, in this case it isn't necessary
            }
            @Override
            public void onNext(@NonNull List<Article> articles) {
                //Gets called when new data is emitted by the Observable
                //Set the received articles into the MutableLiveData
                articleList.setValue(articles);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                //Gets called when an error occurs during the data fetch
                //Logs the error when it happens, usually visible with logcat
                Log.e("ArticleRepo", e.getMessage());
            }
            @Override
            public void onComplete() {
                //Gets called when the Observable has completed emitting items
                //Can add logic, but typically not required for data fetching
            }
        };

        //Here, we subscribe to the Observable to start receiving data
        observable
                .subscribeOn(Schedulers.io()) //Perform the network operation on the IO thread (New thread in order to not block the UI thread)
                .observeOn(AndroidSchedulers.mainThread()) //Observe the results on the main (UI) thread
                .subscribeWith(observer); //Subscribe the observer to the Observable
    }

    public void addNewArticle(Article article) {
        Observable<Article> addArticleObservable = service.addArticle(article);
        DisposableObserver<Article> addArticleObserver = new DisposableObserver<Article>() {
            @Override
            public void onNext(@NonNull Article addedArticle) {
                List<Article> currentArticleList = articleList.getValue();
                List<Article> updatedArticleList = new ArrayList<>();
                if (currentArticleList != null) {
                    updatedArticleList.addAll(currentArticleList);
                }
                updatedArticleList.add(addedArticle);
                articleList.setValue(updatedArticleList);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ArticleRepo", e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onComplete() {

            }
        };

        compositeDisposable.add(addArticleObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(addArticleObserver));
    }

    public void updateArticle(int id, Article article) {
        Observable<Article> updateArticleObservable = service.updateArticle(id, article);
        DisposableObserver<Article> updateArticleObserver = new DisposableObserver<Article>() {
            @Override
            public void onNext(@NonNull Article updatedArticle) {
                List<Article> currentArticleList = articleList.getValue();

                if (currentArticleList == null) {
                    return;
                }

                //Create a copy of the current list to modify
                List<Article> updatedArticleList = new ArrayList<>(currentArticleList);

                //Find and replace the article with the matching ID
                for (int i = 0; i < updatedArticleList.size(); i++) {
                    Article articleForLoop = updatedArticleList.get(i);
                    if(articleForLoop.getId()==updatedArticle.getId()){
                        updatedArticleList.set(i, updatedArticle);
                        break;
                    }
                }
                //Post the updated list
                articleList.setValue(updatedArticleList);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ArticleRepo", "Error updating article: " + e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onComplete() {}
        };

        compositeDisposable.add(updateArticleObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(updateArticleObserver));
    }

    public void deleteArticle(int articleId){
        Observable<Article> deleteArticleObservable = service.deleteArticle(articleId);
        DisposableObserver<Article> deleteArticleObserver = new DisposableObserver<Article>() {
            @Override
            public void onNext(@NonNull Article deletedArticle) {
                List<Article> currentArticleList = articleList.getValue();

                if (currentArticleList == null) {
                    return;
                }

                List<Article> updatedArticleList = new ArrayList<>();
                for (Article article : currentArticleList) {
                    if (article.getId() != articleId) {
                        updatedArticleList.add(article);
                    }
                }
                articleList.setValue(updatedArticleList);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ArticleRepo", "Error deleting article: " + e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onComplete() {
            }
        };

        compositeDisposable.add(deleteArticleObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(deleteArticleObserver));
    }

    //Public method to get the MutableLiveData containing the list of articles
    public MutableLiveData<List<Article>> getArticles(){
        return articleList;
    }
    
    public void onCleared(){
        compositeDisposable.clear();
    }
}


