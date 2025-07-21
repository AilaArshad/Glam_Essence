package com.application.glamessence;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesManager {
    private static final String PREFS_NAME = "favorites_prefs";
    private static final String FAVORITES_KEY = "favorites_key";

    public static boolean isFavorite(Context context, String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = prefs.getStringSet(FAVORITES_KEY, new HashSet<>());
        return favorites.contains(productId);
    }

    public static void addToFavorites(Context context, String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITES_KEY, new HashSet<>()));
        favorites.add(productId);
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply();
    }

    public static void removeFromFavorites(Context context, String productId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Set<String> favorites = new HashSet<>(prefs.getStringSet(FAVORITES_KEY, new HashSet<>()));
        favorites.remove(productId);
        prefs.edit().putStringSet(FAVORITES_KEY, favorites).apply();
    }

    public static List<String> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return new ArrayList<>(prefs.getStringSet(FAVORITES_KEY, new HashSet<>()));
    }
}
