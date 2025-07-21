package com.application.glamessence;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class CartManager {
    private static final String PREFS_NAME = "cart_prefs";
    private static final String CART_KEY = "cart_items";

    public static void addToCart(Context context, String productId, int quantity) {
        Map<String, Integer> cartMap = getCart(context);
        cartMap.put(productId, quantity);
        saveCart(context, cartMap);
    }

    public static void removeFromCart(Context context, String productId) {
        Map<String, Integer> cartMap = getCart(context);
        cartMap.remove(productId);
        saveCart(context, cartMap);
    }

    public static Map<String, Integer> getCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(CART_KEY, null);

        if (json == null) return new HashMap<>();

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static boolean isInCart(Context context, String productId) {
        Map<String, Integer> cartMap = getCart(context);
        return cartMap.containsKey(productId);
    }

    public static int getQuantity(Context context, String productId) {
        Map<String, Integer> cartMap = getCart(context);
        return cartMap.getOrDefault(productId, 1);
    }

    private static void saveCart(Context context, Map<String, Integer> cartMap) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = new Gson().toJson(cartMap);
        prefs.edit().putString(CART_KEY, json).apply();
    }

    public static void clearCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

}
