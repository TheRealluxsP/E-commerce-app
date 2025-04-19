package com.example.clothingstore.retrofit;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    //Singleton instance of retrofit (Only one instance of the class)
    private static Retrofit retrofit;
    //Base URL for the API
    private static final String BASE_URL = "https://fakestoreapi.com";

    //Method to get the Retrofit instance
    private static Retrofit getRetrofitInstance() {
        //Check if the Retrofit instance has not been created yet
        if (retrofit == null) {
            //Build the Retrofit instance
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  //Set the base URL for the API
                    .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter for JSON serialization/deserialization
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create()) // Add RxJava adapter for asynchronous calls
                    .build(); //Build the Retrofit instance                    //(calls that allow tasks to run without blocking the main thread)
        }
        //Return the created Retrofit instance
        return retrofit;
    }

    //Method to get the API interface
    public static RetrofitApiInterface getRetrofitApiInterface() {
        //Ensure the Retrofit instance is initialized
        retrofit = getRetrofitInstance();
        //Create and return the API interface for making network calls
        return retrofit.create(RetrofitApiInterface.class);
    }
}
