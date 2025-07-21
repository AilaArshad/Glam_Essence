package com.application.glamessence;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Product {
    private String productId;
    private String category;
    private String productName;
    private int productQuantity;
    private int stock;
    private float price;
    private String description;
    private String moreInfo;
    private String ingredients;
    private String howToUse;
    private String shippingInfo;
    private String brandName;
    private String brandImageUrl;
    private String brandDescription;
    private List<String> productImages;
    private String tagName;
    private float rating;
    private int ratingCount;
    private Date createdAt;
    private Date updatedAt;
    private boolean isVisible;

    public Product() {
    }

    public Product(String productId, String category, String productName, int productQuantity, int stock,
                   float price, String description, String moreInfo, String ingredients, String howToUse,
                   String shippingInfo, String brandName, String brandImageUrl, String brandDescription,
                   List<String> productImages, String tagName, float rating, int ratingCount,
                   Date createdAt, Date updatedAt, boolean isVisible) {

        this.productId = productId;
        this.category = category;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.stock = stock;
        this.price = price;
        this.description = description;
        this.moreInfo = moreInfo;
        this.ingredients = ingredients;
        this.howToUse = howToUse;
        this.shippingInfo = shippingInfo;
        this.brandName = brandName;
        this.brandImageUrl = brandImageUrl;
        this.brandDescription = brandDescription;
        this.productImages = productImages;
        this.tagName = tagName;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isVisible = isVisible;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getCategory() {
        return category;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public int getStock() {
        return stock;
    }

    public float getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getHowToUse() {
        return howToUse;
    }

    public String getShippingInfo() {
        return shippingInfo;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandImageUrl() {
        return brandImageUrl;
    }

    public String getBrandDescription() {
        return brandDescription;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public String getTagName() {
        return tagName;
    }

    public float getRating() {
        return rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Product product = (Product) obj;
        return Objects.equals(productId, product.productId);
    }

    public void setProductQuantity(int quantity) {
        this.productQuantity = quantity;
    }


    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}
