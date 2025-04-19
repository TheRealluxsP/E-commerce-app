package com.example.clothingstore.retrofit;

import com.example.clothingstore.model.Article; // Imports the Article model class that represents the data structure for articles/products.
import com.example.clothingstore.model.User;
import io.reactivex.rxjava3.core.Observable; // This is an RxJava3 class used to represent and handle asynchronous streams of data.
import retrofit2.http.*;

import java.util.List;

public interface RetrofitApiInterface {

    //Annotation to define a GET request to the specified endpoint
    @GET("/products")
    // Method to retrieve a list of articles from the API
    Observable<List<Article>> getArticles();

    @GET("/users")
    Observable<List<User>> getUsers();

    @POST("/products")
    Observable<Article> addArticle(@Body Article article);

    @POST("/users")
    Observable<User> addUser(@Body User user);

    @PATCH ("/products/{id}")
    Observable<Article> updateArticle(@Path("id") int id, @Body Article article);

    @PATCH("/users/{id}")
    Observable<User> updateUser(@Path("id") int id, @Body User user);

    @DELETE("/products/{id}")
    Observable<Article> deleteArticle(@Path("id") int id);

    @DELETE("/users/{id}")
    Observable<User> deleteUser(@Path("id") int id);

}
