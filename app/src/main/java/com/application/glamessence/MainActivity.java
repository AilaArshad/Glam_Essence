package com.application.glamessence;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    private ImageView navHome, navSearch, navBag, navHeart, navPerson;
    private FrameLayout frameHome, frameSearch, frameBag, frameHeart, framePerson;

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
        CloudinaryManager.init(this);
        setupCustomNavbar();
    }

    private void setupCustomNavbar() {
        navHome = findViewById(R.id.nav_home);
        navSearch = findViewById(R.id.nav_search);
        navBag = findViewById(R.id.nav_cart);
        navHeart = findViewById(R.id.nav_heart);
        navPerson = findViewById(R.id.nav_person);

        frameHome = findViewById(R.id.nav_home_layout);
        frameSearch = findViewById(R.id.nav_search_layout);
        frameBag = findViewById(R.id.nav_bag_layout);
        frameHeart = findViewById(R.id.nav_heart_layout);
        framePerson = findViewById(R.id.nav_person_layout);

        frameHome.setOnClickListener(v -> setSelectedIcon("home"));
        frameSearch.setOnClickListener(v -> setSelectedIcon("search"));
        frameBag.setOnClickListener(v -> setSelectedIcon("bag"));
        frameHeart.setOnClickListener(v -> setSelectedIcon("heart"));
        framePerson.setOnClickListener(v -> setSelectedIcon("person"));

        setSelectedIcon("home");
    }

    private void setSelectedIcon(String selected) {
        navHome.setImageResource(
                selected.equals("home") ? R.drawable.home_black : R.drawable.home_white);

        navSearch.setImageResource(
                selected.equals("search") ? R.drawable.search_black : R.drawable.search_white);

        navBag.setImageResource(
                selected.equals("bag") ? R.drawable.bag_black : R.drawable.bag_white);

        navHeart.setImageResource(
                selected.equals("heart") ? R.drawable.heart_black : R.drawable.heart_white);

        navPerson.setImageResource(
                selected.equals("person") ? R.drawable.person_black : R.drawable.person_white);

        Fragment selectedFragment = null;

        switch (selected) {
            case "home":
                selectedFragment = new AddProduct(); // Replace with your actual HomeFragment
                break;
            case "search":
                selectedFragment = new ProductCatalogFragment(); // Same here
                break;
            case "bag":
                selectedFragment = new ProductDetail();
                break;
            case "heart":
                selectedFragment = new ListToChangeProductInfo(); // Or null for now
                break;
//            case "person":
//                selectedFragment = new ProfileFragment(); // Or null
//                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, selectedFragment)
                    .commit();
        }
    }
}
