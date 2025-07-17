package com.application.glamessence;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class fragment_home extends Fragment {

    private ViewPager2 bannerViewPager;
    private int currentBannerIndex = 0;
    private final Handler bannerHandler = new Handler();
    private FirebaseFirestore db;

    private final List<Integer> bannerImages = Arrays.asList(
            R.drawable.lipstick08,
            R.drawable.skincare15,
            R.drawable.fragrance15
    );

    private final List<String> bannerTopTexts = Arrays.asList(
            "NOVAGE+ SERUMS UP TO 40% OFF",
            "NATURAL SKIN CARE SOLUTIONS",
            "PREMIUM FRAGRANCES"
    );

    private final List<String> bannerBottomTexts = Arrays.asList(
            "Find your hero serum",
            "Glow naturally every day",
            "Confidence in a bottle"
    );

    private final List<String> bannerButtons = Arrays.asList(
            "Shop Now",
            "Explore",
            "Get Started"
    );

    private final List<Integer> bannerBgColors = Arrays.asList(
            0xFFA0D6B4,
            0xFFFFCDD2,
            0xFFFFF9C4
    );

    public fragment_home() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        // Search bar
        EditText searchBar = view.findViewById(R.id.search_product);

        // ViewPager banner
        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        BannerAdapter bannerAdapter = new BannerAdapter(
                bannerImages, bannerTopTexts, bannerBottomTexts, bannerButtons, bannerBgColors
        );
        bannerViewPager.setAdapter(bannerAdapter);

        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentBannerIndex = position;
            }
        });

        startBannerTimer();

        // Product lists
        RecyclerView whatsNewRecycler = view.findViewById(R.id.whatsNewRecycler);
        RecyclerView bestOffersRecycler = view.findViewById(R.id.bestOffersRecycler);
        RecyclerView recommendedRecycler = view.findViewById(R.id.recommendedProductsRecycler);

        whatsNewRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestOffersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recommendedRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Load real data from Firestore
        loadProductsByTag("whats_new", whatsNewRecycler);
        loadProductsByTag("best_offer", bestOffersRecycler);
        loadProductsByTag("recommended", recommendedRecycler);
    }

    private void startBannerTimer() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bannerHandler.post(() -> {
                    int nextIndex = (currentBannerIndex + 1) % bannerImages.size();
                    bannerViewPager.setCurrentItem(nextIndex, true);
                });
            }
        }, 3000, 3000);
    }

    private void loadProductsByTag(String tag, RecyclerView recyclerView) {
        db.collection("products")
                .whereArrayContains("tags", tag)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Product product = doc.toObject(Product.class);
                      //  product.setId(doc.getId());
                        productList.add(product);
                    }

                   // ProductAdapter adapter = new ProductAdapter(getContext(), productList); // ✅ FIXED
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                    //recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading products", Toast.LENGTH_SHORT).show();
                });
    }

}
