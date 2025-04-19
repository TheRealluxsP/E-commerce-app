package com.example.clothingstore.model;

import android.annotation.SuppressLint;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public class Address {

    @SerializedName("city")
    private String city;
    @SerializedName("street")
    private String street;
    @SerializedName("number")
    private int number;
    @SerializedName("zipcode")
    private String zipcode;

    //Class constructors

    public Address() {}

    public Address(String city, String zipcode, int number, String street) {
        setCity(city);
        setZipcode(zipcode);
        setNumber(number);
        setStreet(street);
    }

    //Class getters

    public String getCity() {return city;}

    public String getStreet() {return street;}

    public int getNumber() {return number;}

    public String getZipcode() {return zipcode;}

    // Setters with validation
    public void setCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        this.city = city;
    }

    public void setStreet(String street) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        this.street = street;
    }

    public void setNumber(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Street number must be positive");
        }
        this.number = number;
    }

    public void setZipcode(String zipcode) {
        if (zipcode == null || !zipcode.matches("\\d{5}-\\d{4}") && !zipcode.matches("\\d{4}-\\d{3}")) {
            throw new IllegalArgumentException("Zipcode must be in the format 'xxxxx-xxxx'");
        }
        this.zipcode = zipcode;
    }

    // Override toString for readable format
    @NotNull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%s %d, %s, %s", street, number, city, zipcode);
    }
}
