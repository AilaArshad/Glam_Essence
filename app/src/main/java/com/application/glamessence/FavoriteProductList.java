package com.application.glamessence;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class FavoriteProductList extends Fragment {
    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private LinearLayout emptyView;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_product_list, container, false);
        recyclerView = view.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        emptyView = view.findViewById(R.id.emptyLayout);
        db = FirebaseFirestore.getInstance();
        fetchFavoritesFromFirestore();
        AppCompatButton goShoppingButton = view.findViewById(R.id.goShoppingButton);
        goShoppingButton.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).setSelectedIcon("home");
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchFavoritesFromFirestore();
    }

    private void fetchFavoritesFromFirestore() {
        db.collection("product_list").get().addOnCompleteListener(task -> {
            List<Product> favorites = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot doc : task.getResult()) {
                    String productId = doc.getString("productId");
                    if (productId != null && FavoritesManager.isFavorite(requireContext(), productId)) {
                        Long quantityLong = doc.getLong("productQuantity");
                        int quantity = quantityLong != null ? quantityLong.intValue() : 0;

                        Long stockLong = doc.getLong("stock");
                        int stock = stockLong != null ? stockLong.intValue() : 0;

                        Double priceDouble = doc.getDouble("price");
                        float price = priceDouble != null ? priceDouble.floatValue() : 0f;

                        Product product = new Product(
                                doc.getString("productId"),
                                doc.getString("category"),
                                doc.getString("productName"),
                                quantity,
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
                                doc.getString("tagName"),
                                doc.getDouble("rating") != null ? doc.getDouble("rating").floatValue() : 0f,
                                doc.getLong("ratingCount") != null ? doc.getLong("ratingCount").intValue() : 0,
                                doc.getDate("createdAt"),
                                doc.getDate("updatedAt"),
                                Boolean.TRUE.equals(doc.getBoolean("isVisible"))
                        );
                        favorites.add(product);
                    }
                }
            }
            adapter = new GridAdapter(favorites, requireContext(), true);
            adapter.setOnFavoriteChangeListener(favoritesCount -> {
                updateUI();
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateFavoritesBadge(favoritesCount);
                }
            });
            adapter.setOnCartChangeListener(cartCount -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge(cartCount);
                }
            });
            recyclerView.setAdapter(adapter);
            updateUI();
        });
    }

    private void updateUI() {
        int favCount = FavoritesManager.getFavorites(requireContext()).size();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).updateFavoritesBadge(favCount);
            ((MainActivity) getActivity()).updateCartBadge(CartManager.getCart(requireContext()).size());
        }
        if (adapter.getItemCount() == 0) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
