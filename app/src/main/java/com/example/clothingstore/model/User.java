package com.example.clothingstore.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("username")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("address")
    private Address address;
    private List<CartItem> cart;
    private double balance;

    //Class Constructors

    public User() {}

    public User(String email, String password, String name, String phone, Address address) {
        setEmail(email);
        setPassword(password);
        setName(name);
        setPhone(phone);
        setAddress(address);
    }

    //Class getters

    public int getId() {return id;}

    public String getEmail() {return email;}

    public String getPassword() {return password;}

    public String getName() {return name;}

    public String getPhone() {return phone;}

    public Address getAddress() {return address;}

    public List<CartItem> getCart() {
        if (cart == null) {
            cart = new ArrayList<>();
        }
        return cart;
    }

    public double getBalance() {return balance;}

    //Class setters


    // Setters with validation

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name;
    }

    public void setPhone(String phone) {
        if (phone == null || !phone.matches("\\d-\\d{3}-\\d{3}-\\d{4}") && !phone.matches("\\d{9}")) {
            throw new IllegalArgumentException("Phone must be in the format x-xxx-xxx-xxx or xxxxxxxxx");
        }
        this.phone = phone;
    }

    public void setAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("Address cannot be null");
        }
        this.address = address;
    }

    public void addCartItem(CartItem item) {
        if(item == null){
            throw new IllegalArgumentException("Item cannot be null");
        }

        if (this.cart == null) {
            this.cart = new ArrayList<>();
        }

        this.cart.add(item);
    }

    public void setCartItems(List<CartItem> cart) {
        this.cart = cart;
    }

    public void addBalance(double balance) {
        this.balance += balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public CartItem getCartItemByArticle(int articleId) {
        if (this.cart == null) {
            return null;
        }
        for (CartItem item : this.cart) {
            if (item.getProduct().getId() == articleId) {
                return item;
            }
        }
        return null;
    }
}
