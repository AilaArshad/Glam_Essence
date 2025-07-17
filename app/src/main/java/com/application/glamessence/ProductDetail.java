package com.application.glamessence;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ProductDetail extends Fragment {

    private ViewPager2 viewPager;
    private boolean isLiked = false;

    public ProductDetail() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        viewPager = view.findViewById(R.id.viewPagerImages);

        fetchProductData(view);
        setupHeartToggle(view);

        return view;
    }

    private void fetchProductData(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("product_list").document("1")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Product product = new Product(
                                documentSnapshot.getString("productId"),
                                documentSnapshot.getString("category"),
                                documentSnapshot.getString("productName"),
                                documentSnapshot.getString("productQuantity"),
                                documentSnapshot.getString("stock"),
                                documentSnapshot.getString("price"),
                                documentSnapshot.getString("description"),
                                documentSnapshot.getString("moreInfo"),
                                documentSnapshot.getString("ingredients"),
                                documentSnapshot.getString("howToUse"),
                                documentSnapshot.getString("shippingInfo"),
                                documentSnapshot.getString("brandName"),
                                documentSnapshot.getString("brandImageUri"),
                                documentSnapshot.getString("brandDescription"),
                                (List<String>) documentSnapshot.get("productImages")
                        );

                        // Set ViewPager images
                        List<String> imageUrls = product.getProductImages();
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            ImageSliderAdapter adapter = new ImageSliderAdapter(requireContext(), imageUrls);
                            viewPager.setAdapter(adapter);
                        }

                        // Set product name
                        TextView productNameTV = view.findViewById(R.id.productNameTextView);
                        productNameTV.setText(product.getProductName());

                        // Set brand name under product title
                        String brandName = product.getBrandName() != null ? product.getBrandName() : "Unknown Brand";
                        TextView brandNameTV = view.findViewById(R.id.productBrandTextView);
                        brandNameTV.setText(brandName);

                        // Set brand name near brand logo
                        TextView brandTitleTV = view.findViewById(R.id.brandTitleTextView);
                        brandTitleTV.setText(brandName);

                        // Set price
                        TextView priceTV = view.findViewById(R.id.productPriceTextView);
                        priceTV.setText("£" + product.getPrice());

                        // Set brand description
                        TextView brandDescTV = view.findViewById(R.id.brandDescriptionTextView);
                        brandDescTV.setText(product.getBrandDescription());

                        // Load brand image
                        ImageView brandLogoIV = view.findViewById(R.id.brand_logo);
                        Glide.with(requireContext())
                                .load(product.getBrandImageUri())
                                .into(brandLogoIV);

                        // Rating & Count
                        Number ratingVal = documentSnapshot.get("rating", Number.class);
                        Number ratingCountVal = documentSnapshot.get("ratingCount", Number.class);

                        float rating = ratingVal != null ? ratingVal.floatValue() : 0f;
                        int ratingCount = ratingCountVal != null ? ratingCountVal.intValue() : 0;

                        RatingBar ratingBar = view.findViewById(R.id.ratingStarsTextView);
                        TextView ratingCountTV = view.findViewById(R.id.ratingCountTextView);

                        ratingBar.setRating(rating);
                        ratingCountTV.setText("(" + ratingCount + ")");

                        // Tabs
                        setupTabs(view,
                                product.getDescription(),
                                product.getMoreInfo(),
                                product.getIngredients(),
                                product.getHowToUse(),
                                product.getShippingInfo()
                        );
                    } else {
                        Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductDetail", "Error fetching product", e);
                    Toast.makeText(requireContext(), "Failed to fetch product", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupTabs(View view, String desc, String info, String ing, String use, String ship) {
        TextView tabDescription = view.findViewById(R.id.tab_description);
        TextView tabMoreInfo = view.findViewById(R.id.tab_more_info);
        TextView tabIngredients = view.findViewById(R.id.tab_ingredients);
        TextView tabHowToUse = view.findViewById(R.id.tab_how_to_use);
        TextView tabShipping = view.findViewById(R.id.tab_shipping);
        TextView tabContent = view.findViewById(R.id.tab_content);

        TextView[] allTabs = { tabDescription, tabMoreInfo, tabIngredients, tabHowToUse, tabShipping };

        View.OnClickListener listener = v -> {
            for (TextView tab : allTabs) {
                tab.setTypeface(null, Typeface.NORMAL);
                tab.setSelected(false);
            }

            TextView selected = (TextView) v;
            selected.setTypeface(null, Typeface.BOLD);
            selected.setSelected(true);

            if (v.getId() == R.id.tab_description) tabContent.setText(desc);
            else if (v.getId() == R.id.tab_more_info) tabContent.setText(info);
            else if (v.getId() == R.id.tab_ingredients) tabContent.setText(ing);
            else if (v.getId() == R.id.tab_how_to_use) tabContent.setText(use);
            else if (v.getId() == R.id.tab_shipping) tabContent.setText(ship);
        };

        for (TextView tab : allTabs) tab.setOnClickListener(listener);
        tabDescription.performClick(); // default selected
    }

    private void setupHeartToggle(View view) {
        ImageView fixedHeart = view.findViewById(R.id.fixed_heart);
        fixedHeart.setOnClickListener(v -> {
            isLiked = !isLiked;
            fixedHeart.setImageResource(isLiked ? R.drawable.red_heart : R.drawable.heart_outlined);
        });
    }
}
