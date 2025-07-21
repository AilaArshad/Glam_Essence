package com.application.glamessence;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class cart_fragment extends Fragment {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<Product> cartList = new ArrayList<>();
    private FirebaseFirestore firestore;
    private TextView search;
    private View cartContentLayout;
    private View emptyLayout;
    private AppCompatButton btn, btnCheckout;
    private float totalAmount = 0f;
    private Map<String, Integer> cartMap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bag_fragment, container, false);

        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartMap = CartManager.getCart(requireContext());
        cartAdapter = new CartAdapter(requireContext(), cartList, cartMap);
        cartAdapter.setOnCartRemoveListener(count -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateCartBadge(count);
            }
            updateLayoutVisibility();
            calculateTotalAmount();
        });

        cartAdapter.setOnFavoritesChangeListener(count -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).updateFavoritesBadge(count);
            }
        });
        recyclerView.setAdapter(cartAdapter);

        cartContentLayout = view.findViewById(R.id.cartContentLayout);
        emptyLayout = view.findViewById(R.id.emptyLayout);
        btn = view.findViewById(R.id.goShoppingButton);
        search = view.findViewById(R.id.searchTextView);
        btnCheckout = view.findViewById(R.id.btn_continue);

        btnCheckout.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putFloat("totalAmount", totalAmount);
            Checkout checkoutFragment = new Checkout();
            checkoutFragment.setArguments(bundle);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, checkoutFragment)
                    .addToBackStack(null)
                    .commit();
        });

        cartAdapter.setOnQuantityChangeListener(() -> {
            calculateTotalAmount();
            updateLayoutVisibility();
        });

        btn.setOnClickListener(view1 -> {
            ((MainActivity) requireActivity()).setSelectedIcon("home");
        });

        search.setOnClickListener(v -> requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new SearchProduct())
                .addToBackStack(null)
                .commit());

        firestore = FirebaseFirestore.getInstance();
        fetchCartItems();

        return view;
    }

    private void fetchCartItems() {
        Set<String> cartIds = cartMap.keySet();

        if (cartIds.isEmpty()) {
            cartList.clear();
            cartAdapter.notifyDataSetChanged();
            updateLayoutVisibility();
            return;
        }

        firestore.collection("product_list")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    cartList.clear();
                    for (QueryDocumentSnapshot snapshot : querySnapshot) {
                        String productId = snapshot.getString("productId");
                        if (!cartIds.contains(productId)) continue;

                        int userSelectedQuantity = CartManager.getQuantity(requireContext(), productId);

                        Product product = new Product(
                                productId,
                                snapshot.getString("category"),
                                snapshot.getString("productName"),
                                userSelectedQuantity,
                                snapshot.getLong("stock") != null ? snapshot.getLong("stock").intValue() : 0,
                                snapshot.getDouble("price") != null ? snapshot.getDouble("price").floatValue() : 0f,
                                snapshot.getString("description"),
                                snapshot.getString("moreInfo"),
                                snapshot.getString("ingredients"),
                                snapshot.getString("howToUse"),
                                snapshot.getString("shippingInfo"),
                                snapshot.getString("brandName"),
                                snapshot.getString("brandImageUrl"),
                                snapshot.getString("brandDescription"),
                                (List<String>) snapshot.get("productImages"),
                                snapshot.getString("tagName"),
                                snapshot.getDouble("rating") != null ? snapshot.getDouble("rating").floatValue() : 0f,
                                snapshot.getLong("ratingCount") != null ? snapshot.getLong("ratingCount").intValue() : 0,
                                snapshot.getDate("createdAt"),
                                snapshot.getDate("updatedAt"),
                                snapshot.getBoolean("visible") != null && snapshot.getBoolean("visible")
                        );

                        cartList.add(product);
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateLayoutVisibility();
                    calculateTotalAmount();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error fetching data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void calculateTotalAmount() {
        totalAmount = 0f;
        for (Product product : cartList) {
            String productId = product.getProductId();
            int qty = cartMap.getOrDefault(productId, 1);
            float price = product.getPrice();
            totalAmount += qty * price;
        }

        TextView subtotal = requireView().findViewById(R.id.sub_total);
        TextView toPay = requireView().findViewById(R.id.toPayText);
        subtotal.setText("$" + String.format("%.2f", totalAmount));
        toPay.setText("To Pay: $" + String.format("%.2f", totalAmount));
    }

    private void updateLayoutVisibility() {
        if (cartList.isEmpty()) {
            cartContentLayout.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            cartContentLayout.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
        }
    }
}
