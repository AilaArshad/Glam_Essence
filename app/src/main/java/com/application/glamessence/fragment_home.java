package com.application.glamessence;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class fragment_home extends Fragment {

    private ViewPager2 bannerViewPager;
    private int currentBannerIndex = 0;
    private final Handler bannerHandler = new Handler();
    private FirebaseFirestore db;
    private View rootView;

    public fragment_home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.rootView = view;
        db = FirebaseFirestore.getInstance();

        TextView searchTextView = view.findViewById(R.id.searchTextView);
        searchTextView.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, new SearchProduct())
                    .addToBackStack(null)
                    .commit();
        });

        bannerViewPager = view.findViewById(R.id.bannerViewPager);
        loadDashboardProducts();

        RecyclerView newArrivalsRecycler = view.findViewById(R.id.newArrivalsRecycler);
        RecyclerView hotDealsRecycler = view.findViewById(R.id.hotDealsRecycler);
        RecyclerView topRatedRecycler = view.findViewById(R.id.topRatedRecycler);
        RecyclerView trendingRecycler = view.findViewById(R.id.trendingRecycler);
        RecyclerView limitedRecycler = view.findViewById(R.id.limitedTimeRecycler);

        newArrivalsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        hotDealsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        topRatedRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        trendingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        limitedRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        loadProductsByTag("NEW", newArrivalsRecycler);
        loadProductsByTag("HOT PRODUCT", hotDealsRecycler);
        loadProductsByTag("TOPRATED", topRatedRecycler);
        loadProductsByTag("TRENDING", trendingRecycler);
        loadProductsByTag("LIMITED", limitedRecycler);

        view.findViewById(R.id.btn_view_all_new)
                .setOnClickListener(v -> openProductList(null, "NEW", "New Arrivals"));

        view.findViewById(R.id.btn_view_all_hot)
                .setOnClickListener(v -> openProductList(null, "HOT DEAL", "Hot Deals"));

        view.findViewById(R.id.btn_view_all_top)
                .setOnClickListener(v -> openProductList(null, "TOP RATED", "Top Rated"));

        view.findViewById(R.id.btn_view_all_limited)
                .setOnClickListener(v -> openProductList(null, "LIMITED", "Limited Time Picks"));

        view.findViewById(R.id.btn_view_all_trending)
                .setOnClickListener(v -> openProductList(null, "TRENDING", "Trending Now"));
    }

    private void openProductList(@Nullable String category, @Nullable String tag, String headingText) {
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        bundle.putString("tag", tag);
        bundle.putString("heading", headingText);

        ProductList productListFragment = new ProductList();
        productListFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, productListFragment)
                .addToBackStack(null)
                .commit();
    }

    private void startBannerTimer(int bannerSize) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                bannerHandler.post(() -> {
                    currentBannerIndex = (currentBannerIndex + 1) % bannerSize;
                    bannerViewPager.setCurrentItem(currentBannerIndex, true);
                });
            }
        }, 7000, 7000);
    }

    private void loadProductsByTag(String tagFilter, RecyclerView recyclerView) {
        db.collection("product_list")
                .whereEqualTo("visible", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> productList = new ArrayList<>();
                    Log.d("FirestoreFetch", "Fetched " + queryDocumentSnapshots.size() + " products for tag: " + tagFilter);

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String tagName = doc.getString("tagName");
                        if (tagName == null || !tagName.equals(tagFilter)) {
                            continue;
                        }

                        float rating = doc.getDouble("rating") != null ? doc.getDouble("rating").floatValue() : 0f;
                        int ratingCount = doc.getLong("ratingCount") != null ? doc.getLong("ratingCount").intValue() : 0;
                        int productQuantity = doc.getLong("productQuantity") != null ? doc.getLong("productQuantity").intValue() : 0;
                        int stock = doc.getLong("stock") != null ? doc.getLong("stock").intValue() : 0;
                        float price = doc.getDouble("price") != null ? doc.getDouble("price").floatValue() : 0f;

                        Product product = new Product(
                                doc.getString("productId"),
                                doc.getString("category"),
                                doc.getString("productName"),
                                productQuantity,
                                stock,
                                price,
                                doc.getString("description"),
                                doc.getString("moreInfo"),
                                doc.getString("ingredients"),
                                doc.getString("howToUse"),
                                doc.getString("shippingInfo"),
                                doc.getString("brandName"),
                                doc.getString("brandImageUri"),
                                doc.getString("brandDescription"),
                                (List<String>) doc.get("productImages"),
                                tagName,
                                rating,
                                ratingCount,
                                doc.getDate("createdAt"),
                                doc.getDate("updatedAt"),
                                Boolean.TRUE.equals(doc.getBoolean("visible"))
                        );
                        productList.add(product);
                    }

                    ProductAdapter adapter = new ProductAdapter(productList, getContext(), false);

                    adapter.setOnCartChangeListener(count -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateCartBadge(count);
                        }
                    });

                    adapter.setOnFavoriteChangeListener(count -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).updateFavoritesBadge(count);
                        }
                    });

                    recyclerView.setAdapter(adapter);

                })
                .addOnFailureListener(e ->
                        Log.e("FirestoreFetch", "Error loading " + tagFilter + " products", e)
                );
    }

    private void loadDashboardProducts() {
        db.collection("dashboard_prducts")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!isAdded() || rootView == null) return;

                    List<DashboardProduct> products = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long productIdLong = doc.getLong("productId");
                        String productId = (productIdLong != null) ? String.valueOf(productIdLong) : "";

                        products.add(new DashboardProduct(
                                productId,
                                doc.getString("category"),
                                doc.getString("productName"),
                                doc.getString("subHeading"),
                                doc.getString("url")
                        ));
                    }

                    if (!products.isEmpty()) {
                        BannerAdapter adapter = new BannerAdapter(products, getContext());
                        bannerViewPager.setAdapter(adapter);
                        startBannerTimer(products.size());

                        int size = products.size();

                        DashboardProduct bannerProduct = products.get(size - 1); // Last
                        ImageView bannerImage = rootView.findViewById(R.id.bannerImage);
                        TextView bannerTextTop = rootView.findViewById(R.id.bannerTextTop);
                        TextView bannerTextBottom = rootView.findViewById(R.id.bannerTextBottom);
                        Glide.with(requireContext())
                                .load(bannerProduct.getUrl())
                                .into(bannerImage);
                        bannerTextTop.setText(bannerProduct.getSubHeading());
                        bannerTextBottom.setText(bannerProduct.getProductName());

                        DashboardProduct hotDealsProduct = (size >= 2) ? products.get(size - 2) : bannerProduct;
                        ImageView belowHotDealsImage = rootView.findViewById(R.id.belowHotDealsImage);
                        Glide.with(requireContext())
                                .load(hotDealsProduct.getUrl())
                                .into(belowHotDealsImage);

                        DashboardProduct glowProduct = (size >= 3) ? products.get(size - 3) : bannerProduct;
                        ImageView glowBannerImage = rootView.findViewById(R.id.glowBannerImage);
                        Glide.with(requireContext())
                                .load(glowProduct.getUrl())
                                .into(glowBannerImage);

                        TextView sectionTitleGlow = rootView.findViewById(R.id.sectionTitleGlow);
                        TextView sectionSubTextGlow = rootView.findViewById(R.id.sectionSubTextGlow);
                        sectionTitleGlow.setText(glowProduct.getProductName());
                        sectionSubTextGlow.setText(glowProduct.getSubHeading());

                        AppCompatButton btnGlowBanner = rootView.findViewById(R.id.btnGlowBanner);
                        AppCompatButton btnHotDealsBanner = rootView.findViewById(R.id.btnHotDealsBanner);
                        AppCompatButton btnTrendingBanner = rootView.findViewById(R.id.btnTrendingBanner);

                        btnGlowBanner.setOnClickListener(v -> openProductListFragment("NEW", "New Arrivals"));
                        btnHotDealsBanner.setOnClickListener(v -> openProductListFragment("NEW", "New Arrivals"));
                        btnTrendingBanner.setOnClickListener(v -> openProductListFragment("NEW", "New Arrivals"));
                    }
                });
    }

    private void openProductListFragment(String tag, String heading) {
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putString("heading", heading);

        ProductList productListFragment = new ProductList();
        productListFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, productListFragment)
                .addToBackStack(null)
                .commit();
    }

}