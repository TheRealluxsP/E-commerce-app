package com.example.clothingstore;

import com.example.clothingstore.model.Address;
import com.example.clothingstore.model.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    // Test for valid user creation
    @Test
    public void testValidUserCreation() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        User user = new User("test@example.com", "password123", "John Doe", "1-234-567-8910", address);

        assertEquals("test@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("John Doe", user.getName());
        assertEquals("1-234-567-8910", user.getPhone());
        assertEquals(address, user.getAddress());
    }

    // Test for invalid email in constructor
    @Test
    public void testInvalidEmailFormat() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        assertThrows(IllegalArgumentException.class, () -> new User("invalid-email", "password123", "John Doe", "1-234-567-8910", address));
        assertThrows(IllegalArgumentException.class, () -> new User("test@.com", "password123", "John Doe", "1-234-567-8910", address));
        assertThrows(IllegalArgumentException.class, () -> new User("test@domain", "password123", "John Doe", "1-234-567-8910", address));
    }

    // Test for invalid password in constructor
    @Test
    public void testInvalidPassword() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", null, "John Doe", "1-234-567-8910", address));
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "", "John Doe", "1-234-567-8910", address));
    }

    // Test for invalid name in constructor
    @Test
    public void testInvalidName() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", "", "1-234-567-8910", address));
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", "   ", "1-234-567-8910", address));
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", null, "1-234-567-8910", address));
    }

    // Test for invalid phone in constructor
    @Test
    public void testInvalidPhoneFormat() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", "John Doe", "12345678", address));  // 8 digits
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", "John Doe", "1234567890", address)); // 10 digits
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", "John Doe", "123-456-789", address)); // Non-digit characters
    }

    // Test for invalid address in constructor
    @Test
    public void testInvalidAddress() {
        assertThrows(IllegalArgumentException.class, () -> new User("test@example.com", "password123", "John Doe", "1-234-567-8910", null));
    }

    // Test setter with valid values
    @Test
    public void testSettersWithValidValues() {
        Address address = new Address("Springfield", "12345-5678", 101, "Main Street");
        User user = new User();

        user.setEmail("valid@example.com");
        user.setPassword("newpassword");
        user.setName("Jane Smith");
        user.setPhone("9-876-543-2100");
        user.setAddress(address);

        assertEquals("valid@example.com", user.getEmail());
        assertEquals("newpassword", user.getPassword());
        assertEquals("Jane Smith", user.getName());
        assertEquals("9-876-543-2100", user.getPhone());
        assertEquals(address, user.getAddress());
    }


    // Test setter with invalid values
    @Test
    public void testSettersWithInvalidValues() {
        User user = new User();

        assertThrows(IllegalArgumentException.class, () -> user.setEmail("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> user.setName(null));
        assertThrows(IllegalArgumentException.class, () -> user.setPhone("12345678"));
        assertThrows(IllegalArgumentException.class, () -> user.setAddress(null));
    }



}
