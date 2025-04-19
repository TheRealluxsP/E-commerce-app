package com.example.clothingstore;

import com.example.clothingstore.model.Address;
import org.junit.Test;

import static org.junit.Assert.*;

public class AddressTest {

    // Test for valid address creation
    @Test
    public void testValidAddressCreation() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        assertEquals("Springfield", address.getCity());
        assertEquals("Main Street", address.getStreet());
        assertEquals(101, address.getNumber());
        assertEquals("12345-5678", address.getZipcode());
    }

    // Test for invalid zipcode format in constructor
    @Test
    public void testInvalidZipcodeFormatInConstructor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("Springfield", "1234567", 101, "Main Street"));
        assertThrows(IllegalArgumentException.class,
                () -> new Address("Springfield", "123-567", 101, "Main Street"));
    }

    // Test for invalid street number in constructor
    @Test
    public void testInvalidNumberInConstructor() {
        assertThrows(IllegalArgumentException.class,
                () -> new Address("Springfield", "12345-5678", -1, "Main Street"));
        assertThrows(IllegalArgumentException.class,
                () -> new Address("Springfield", "12345-5678", 0, "Main Street"));
    }


    // Test setter for valid zipcode
    @Test
    public void testValidZipcodeSetter() {
        Address address = new Address();
        address.setZipcode("98765-5435");
        assertEquals("98765-5435", address.getZipcode());
    }

    // Test setter for invalid zipcode
    @Test
    public void testInvalidZipcodeSetter() {
        Address address = new Address();
        assertThrows(IllegalArgumentException.class, () -> address.setZipcode("9876543"));
        assertThrows(IllegalArgumentException.class, () -> address.setZipcode("98-543"));
    }

    //Test setter for valid city
    @Test
     public void testValidCitySetter() {
        Address address = new Address();
        address.setCity("Springfield");
        assertEquals("Springfield", address.getCity());
    }

    // Test setter for invalid city
    @Test
    public void testInvalidCitySetter() {
        Address address = new Address();
        assertThrows(IllegalArgumentException.class, () -> address.setCity(""));
        assertThrows(IllegalArgumentException.class, () -> address.setCity(null));
    }

    // Test setter for valid street
    @Test
    public void testValidStreetSetter() {
        Address address = new Address();
        address.setStreet("Elm Street");
        assertEquals("Elm Street", address.getStreet());
    }

    // Test setter for invalid street
    @Test
    public void testInvalidStreetSetter() {
        Address address = new Address();
        assertThrows(IllegalArgumentException.class, () -> address.setStreet(""));
        assertThrows(IllegalArgumentException.class, () -> address.setStreet(null));
    }

    // Test setter for valid street number
    @Test
    public void testValidNumberSetter() {
        Address address = new Address();
        address.setNumber(45);
        assertEquals(45, address.getNumber());
    }

    // Test setter for invalid street number
    @Test
    public void testInvalidNumberSetter() {
        Address address = new Address();
        assertThrows(IllegalArgumentException.class, () -> address.setNumber(-1));
        assertThrows(IllegalArgumentException.class, () -> address.setNumber(0));
    }

    //Test new toString method
    @Test
    public void testToString() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        assertEquals("Main Street 101, Springfield, 12345-5678", address.toString());
    }



}
