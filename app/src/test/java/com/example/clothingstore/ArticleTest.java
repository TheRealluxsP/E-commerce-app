package com.example.clothingstore;

import com.example.clothingstore.model.Article;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArticleTest {
    @Test
    public void testValidArticleCreation() {

        Article article = new Article("T-Shirt", 25.0f, "A comfortable t-shirt", "Clothing", "image.jpg");

        assertEquals("T-Shirt", article.getName());
        assertEquals(25.0f, article.getPrice(), 0.001);
        assertEquals("A comfortable t-shirt", article.getDescription());
        assertEquals("Clothing", article.getCategory());
        assertEquals("image.jpg", article.getImage());
    }


    @Test
    public void testInvalidNameThrowsException() {
        Article article = new Article();
        assertThrows(IllegalArgumentException.class, () -> article.setName(null));
    }

    @Test
    public void testInvalidPriceThrowsException() {
        Article article = new Article();
        assertThrows(IllegalArgumentException.class, () -> article.setPrice(-10.0f));
    }

    @Test
    public void testInvalidDescriptionThrowsException() {
        Article article = new Article();
        assertThrows(IllegalArgumentException.class, () -> article.setDescription(null));
    }

    @Test
    public void testInvalidCategoryThrowsException() {
        Article article = new Article();
        assertThrows(IllegalArgumentException.class, () -> article.setCategory(null));
    }

    @Test
    public void testInvalidImageThrowsException() {
        Article article = new Article();
        assertThrows(IllegalArgumentException.class, () -> article.setImage(null));
    }


    @Test
    public void testConstructorWithInvalidNameThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Article(null, 25.0, "A comfortable t-shirt", "Clothing", "image.jpg")
        );
    }

    @Test
    public void testConstructorWithInvalidPriceThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Article("T-Shirt", -5.0f, "A comfortable t-shirt", "Clothing", "image.jpg")
        );
    }

    @Test
    public void testConstructorWithInvalidDescriptionThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Article("T-Shirt", 25.0f, null, "Clothing", "image.jpg")
        );
    }

    @Test
    public void testConstructorWithInvalidCategoryThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Article("T-Shirt", 25.0f, "A comfortable t-shirt", null, "image.jpg")
        );
    }

    @Test
    public void testConstructorWithInvalidImageThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                new Article("T-Shirt", 25.0f, "A comfortable t-shirt", "Clothing", null)
        );
    }




}
