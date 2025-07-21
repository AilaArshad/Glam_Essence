package com.application.glamessence;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductUtils {

    public static Product parseProduct(DocumentSnapshot doc) {
        String id = doc.getString("productId");
        String category = doc.getString("category");
        String name = doc.getString("productName");

        Long quantityLong = doc.getLong("productQuantity");
        int quantity = quantityLong != null ? quantityLong.intValue() : 0;

        Long stockLong = doc.getLong("stock");
        int stock = stockLong != null ? stockLong.intValue() : 0;

        Double priceDouble = doc.getDouble("price");
        float price = priceDouble != null ? priceDouble.floatValue() : 0f;

        String description = doc.getString("description");
        String moreInfo = doc.getString("moreInfo");
        String ingredients = doc.getString("ingredients");
        String howToUse = doc.getString("howToUse");
        String shippingInfo = doc.getString("shippingInfo");
        String brandName = doc.getString("brandName");
        String brandImageUri = doc.getString("brandImageUri");
        String brandDescription = doc.getString("brandDescription");
        List<String> imagePaths = (List<String>) doc.get("productImages");
        String tagName = doc.getString("tagName");

        Double ratingValue = doc.getDouble("rating");
        Long ratingCountValue = doc.getLong("ratingCount");
        float rating = ratingValue != null ? ratingValue.floatValue() : 0f;
        int ratingCount = ratingCountValue != null ? ratingCountValue.intValue() : 0;

        Date createdAt = doc.getDate("createdAt");
        Date updatedAt = doc.getDate("updatedAt");
        Boolean isVisible = doc.getBoolean("visible");
        if (isVisible == null) isVisible = true;

        return new Product(
                id, category, name, quantity, stock, price,
                description, moreInfo, ingredients, howToUse,
                shippingInfo, brandName, brandImageUri, brandDescription,
                imagePaths != null ? imagePaths : new ArrayList<>(),
                tagName != null ? tagName : "NEW",
                rating, ratingCount, createdAt, updatedAt, isVisible
        );
    }
}
