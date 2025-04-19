package com.example.clothingstore.model;

import com.google.gson.annotations.SerializedName;

public class Article {
    @SerializedName("id") //Part of the Gson library,
    private int id;       //allows the specification of the name of a field in the JSON file that corresponds to the field in the Java class
    @SerializedName("title")
    private String name;
    @SerializedName("price")
    private double price;
    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private String category;
    @SerializedName("image")
    private String image;


    //Class constructors

    public Article(){}

    public Article(String name, double price, String description, String category, String image) {
        setName(name);
        setPrice(price);
        setDescription(description);
        setCategory(category);
        setImage(image);
    }

    //Class getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    //Class setters with validation

    public void setName(String name) {
        if (name == null){
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

    public void setPrice(double price) {
        if (price < 0){
            throw new IllegalArgumentException("Price must be a positive number");
        }
        this.price = price;
    }

    public void setDescription(String description) {
        if (description == null){
            throw new IllegalArgumentException("Description cannot be null");
        }
        this.description = description;
    }

    public void setCategory(String category) {
        if (category == null){
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = category;
    }

    public void setImage(String image) {
        if (image == null){
            throw new IllegalArgumentException("Image cannot be null");
        }
        this.image = image;
    }

}
