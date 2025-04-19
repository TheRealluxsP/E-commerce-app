package com.example.clothingstore.model;

import com.google.gson.annotations.SerializedName;

public class CartItem {

    private Article article;
    private int quantity;

    public CartItem() {}

    public CartItem(Article article, int quantity) {

        setProduct(article);
        setQuantity(quantity);
    }


    public Article getProduct() {return article;}

    public int getQuantity() {return quantity;}

    

    public void setProduct(Article article) {
        this.article = article;
    }

    public void setQuantity(int quantity) {

        this.quantity = quantity;
    }
}


