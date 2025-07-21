package com.application.glamessence;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

public class ProductDetail extends Fragment {

    private ViewPager2 viewPager;
    private boolean isLiked = false;

    public ProductDetail() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);
        viewPager = view.findViewById(R.id.viewPagerImages);
        ImageView backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());
        Button addToCartButton = view.findViewById(R.id.add_to_cart_button);

        Bundle args = getArguments();
        if (args != null && args.getString("productId") != null) {
            String productId = args.getString("productId");

            if (CartManager.isInCart(requireContext(), productId)) {
                addToCartButton.setText("Added to Cart");
                addToCartButton.setEnabled(false);
                if (getActivity() instanceof MainActivity) {
                    int cartCount = CartManager.getCart(requireContext()).size();
                    ((MainActivity) getActivity()).updateCartBadge(cartCount);
                }
            }

            addToCartButton.setOnClickListener(v -> {
                CartManager.addToCart(requireContext(), productId, 1);
                addToCartButton.setText("Added to Cart");
                addToCartButton.setEnabled(false);
                Toast.makeText(requireContext(), "Added to Cart", Toast.LENGTH_SHORT).show();

                if (getActivity() instanceof MainActivity) {
                    int cartCount = CartManager.getCart(requireContext()).size();
                    ((MainActivity) getActivity()).updateCartBadge(cartCount);
                }
            });
        }

        fetchProductData(view);
        setupHeartToggle(view);

        return view;
    }

    private void fetchProductData(View view) {
        Bundle args = getArguments();
        if (args == null || args.getString("productId") == null) {
            Toast.makeText(requireContext(), "No product ID provided", Toast.LENGTH_SHORT).show();
            return;
        }
        String productId = args.getString("productId");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product_list").document(productId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        float rating = documentSnapshot.getDouble("rating") != null
                                ? documentSnapshot.getDouble("rating").floatValue() : 0f;
                        int ratingCount = documentSnapshot.getLong("ratingCount") != null
                                ? documentSnapshot.getLong("ratingCount").intValue() : 0;

                        int productQuantity = documentSnapshot.getLong("productQuantity") != null
                                ? documentSnapshot.getLong("productQuantity").intValue() : 0;
                        int stock = documentSnapshot.getLong("stock") != null
                                ? documentSnapshot.getLong("stock").intValue() : 0;
                        float price = documentSnapshot.getDouble("price") != null
                                ? documentSnapshot.getDouble("price").floatValue() : 0f;

                        Date createdAt = documentSnapshot.getDate("createdAt");
                        Date updatedAt = documentSnapshot.getDate("updatedAt");
                        boolean isVisible = Boolean.TRUE.equals(documentSnapshot.getBoolean("isVisible"));

                        Product product = new Product(
                                documentSnapshot.getString("productId"),
                                documentSnapshot.getString("category"),
                                documentSnapshot.getString("productName"),
                                productQuantity,
                                stock,
                                price,
                                documentSnapshot.getString("description"),
                                documentSnapshot.getString("moreInfo"),
                                documentSnapshot.getString("ingredients"),
                                documentSnapshot.getString("howToUse"),
                                documentSnapshot.getString("shippingInfo"),
                                documentSnapshot.getString("brandName"),
                                documentSnapshot.getString("brandImageUrl"),
                                documentSnapshot.getString("brandDescription"),
                                (List<String>) documentSnapshot.get("productImages"),
                                documentSnapshot.getString("tagName"),
                                rating,
                                ratingCount,
                                createdAt,
                                updatedAt,
                                isVisible
                        );

                        List<String> imageUrls = product.getProductImages();
                        if (imageUrls != null && !imageUrls.isEmpty()) {
                            ImageSliderAdapter adapter = new ImageSliderAdapter(requireContext(), imageUrls);
                            viewPager.setAdapter(adapter);
                        }

                        TextView productNameTV = view.findViewById(R.id.productNameTextView);
                        productNameTV.setText(product.getProductName());

                        String brandName = product.getBrandName() != null ? product.getBrandName() : "Unknown Brand";
                        TextView brandNameTV = view.findViewById(R.id.productBrandTextView);
                        brandNameTV.setText(brandName);

                        TextView brandTitleTV = view.findViewById(R.id.brandTitleTextView);
                        brandTitleTV.setText(brandName);

                        TextView priceTV = view.findViewById(R.id.productPriceTextView);
                        priceTV.setText("$" + product.getPrice());

                        TextView brandDescTV = view.findViewById(R.id.brandDescriptionTextView);
                        brandDescTV.setText(product.getBrandDescription());

                        ImageView brandLogoIV = view.findViewById(R.id.brand_logo);
                        Glide.with(requireContext())
                                .load(product.getBrandImageUrl())
                                .into(brandLogoIV);

                        RatingBar ratingBar = view.findViewById(R.id.ratingStarsTextView);
                        TextView ratingCountTV = view.findViewById(R.id.ratingCountTextView);

                        ratingBar.setRating(product.getRating());
                        ratingCountTV.setText("(" + product.getRatingCount() + ")");

                        setupTabs(view,
                                product.getDescription(),
                                product.getMoreInfo(),
                                product.getIngredients(),
                                product.getHowToUse(),
                                product.getShippingInfo()
                        );

                        // ⭐ Setup Tag Image View
                        ImageView tagImageView = view.findViewById(R.id.tagImageView);
                        tagImageView.setVisibility(View.GONE); // Hide by default
                        String tagName = product.getTagName();
                        if (tagName != null) {
                            switch (tagName.toLowerCase()) {
                                case "new":
                                    tagImageView.setImageResource(R.drawable.tag_new);
                                    break;
                                case "hot product":
                                    tagImageView.setImageResource(R.drawable.tag_hot);
                                    break;
                                case "toprated":
                                    tagImageView.setImageResource(R.drawable.tag_top_rated);
                                    break;
                                case "trending":
                                    tagImageView.setImageResource(R.drawable.tag_trending);
                                    break;
                                case "limited":
                                    tagImageView.setImageResource(R.drawable.tag_limited);
                                    break;
                                default:
                                    tagImageView.setVisibility(View.GONE);
                                    return;
                            }
                            tagImageView.setVisibility(View.VISIBLE);
                        }

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

        TextView[] allTabs = {tabDescription, tabMoreInfo, tabIngredients, tabHowToUse, tabShipping};

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
        tabDescription.performClick();
    }

    private void setupHeartToggle(View view) {
        ImageView fixedHeart = view.findViewById(R.id.fixed_heart);

        Bundle args = getArguments();
        if (args == null || args.getString("productId") == null) return;
        String productId = args.getString("productId");

        isLiked = FavoritesManager.isFavorite(requireContext(), productId);
        fixedHeart.setImageResource(isLiked ? R.drawable.red_heart : R.drawable.heart_outlined);

        fixedHeart.setOnClickListener(v -> {
            if (isLiked) {
                FavoritesManager.removeFromFavorites(requireContext(), productId);
                fixedHeart.setImageResource(R.drawable.heart_outlined);
            } else {
                FavoritesManager.addToFavorites(requireContext(), productId);
                fixedHeart.setImageResource(R.drawable.red_heart);
            }
            isLiked = !isLiked;

            if (getActivity() instanceof MainActivity) {
                int favoritesCount = FavoritesManager.getFavorites(requireContext()).size();
                ((MainActivity) getActivity()).updateFavoritesBadge(favoritesCount);
            }
        });
    }

}
