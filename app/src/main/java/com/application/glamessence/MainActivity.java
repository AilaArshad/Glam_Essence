package com.application.glamessence;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private ImageView navHome, navSearch, navBag, navHeart;
    private FrameLayout frameHome, frameSearch, frameBag, frameHeart;
    private TextView heartBadge, cartBadge; // Declare these as fields
    private int favoriteCount = 0;
    private int cartCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        heartBadge = findViewById(R.id.heart_badge);
        cartBadge = findViewById(R.id.cart_badge);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        CloudinaryManager.init(this);
        setupCustomNavbar();

        // ✅ Initialize badges on startup from preferences
        updateBadge(heartBadge, FavoritesManager.getFavorites(this).size());
        updateBadge(cartBadge, CartManager.getCart(this).size());
    }

    private void setupCustomNavbar() {
        navHome = findViewById(R.id.nav_home);
        navSearch = findViewById(R.id.nav_search);
        navBag = findViewById(R.id.nav_cart);
        navHeart = findViewById(R.id.nav_heart);

        frameHome = findViewById(R.id.nav_home_layout);
        frameSearch = findViewById(R.id.nav_search_layout);
        frameBag = findViewById(R.id.nav_bag_layout);
        frameHeart = findViewById(R.id.nav_heart_layout);

        frameHome.setOnClickListener(v -> setSelectedIcon("home"));
        frameSearch.setOnClickListener(v -> setSelectedIcon("search"));
        frameBag.setOnClickListener(v -> setSelectedIcon("bag"));
        frameHeart.setOnClickListener(v -> setSelectedIcon("heart"));

        setSelectedIcon("home");
    }

    public void setSelectedIcon(String selected) {
        navHome.setImageResource(
                selected.equals("home") ? R.drawable.home_black : R.drawable.home_white);

        navSearch.setImageResource(
                selected.equals("search") ? R.drawable.search_black : R.drawable.search_white);

        navBag.setImageResource(
                selected.equals("bag") ? R.drawable.bag_black : R.drawable.bag_white);

        navHeart.setImageResource(
                selected.equals("heart") ? R.drawable.heart_black : R.drawable.heart_white);

        Fragment selectedFragment = null;
        switch (selected) {
            case "home":
                selectedFragment = new fragment_home();
                break;
            case "search":
                selectedFragment = new ProductCatalogFragment();
                break;
            case "bag":
                selectedFragment = new cart_fragment();
                break;
            case "heart":
                selectedFragment = new FavoriteProductList();
                break;
        }
        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, selectedFragment)
                    .commit();
        }
    }

    public void updateFavoritesBadge(int count) {
        updateBadge(heartBadge, count);
    }

    public void updateCartBadge(int count) {
        updateBadge(cartBadge, count);
    }

    private void updateBadge(TextView badgeView, int count) {
        if (count > 0) {
            badgeView.setText(String.valueOf(count));
            badgeView.setVisibility(View.VISIBLE);
        } else {
            badgeView.setVisibility(View.GONE);
        }
    }
}