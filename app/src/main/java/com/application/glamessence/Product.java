package com.application.glamessence;
import java.util.List;
public class Product {
    private String productId;
    private String category;
    private String productName;
    private String productQuantity;
    private String stock;
    private String price;
    private String description;
    private String moreInfo;
    private String ingredients;
    private String howToUse;
    private String shippingInfo;
    private String brandName;
    private String brandImageUri;
    private String brandDescription;
    private List<String> productImages;

    public Product(String productId, String category, String productName, String productQuantity, String stock,
                        String price, String description, String moreInfo, String ingredients, String howToUse,
                        String shippingInfo, String brandName, String brandImageUri, String brandDescription,
                        List<String> productImages) {

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
        this.brandImageUri = brandImageUri;
        this.brandDescription = brandDescription;
        this.productImages = productImages;
    }

    public String getProductId() {
        return productId;
    }

    public String getCategory() {
        return category;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductQuantity() {
        return productQuantity;
    }

    public String getStock() {
        return stock;
    }

    public String getPrice() {
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

    public String getBrandImageUri() {
        return brandImageUri;
    }

    public String getBrandDescription() {
        return brandDescription;
    }

    public List<String> getProductImages() {
        return productImages;
    }
}
