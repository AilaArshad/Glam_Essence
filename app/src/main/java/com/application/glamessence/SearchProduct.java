package com.application.glamessence;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchProduct extends Fragment {
    private RecyclerView recyclerView;
    private EditText searchEditText;
    private SearchProductAdapter adapter;
    private List<Product> productList;
    private LinearLayout emptyLayout;
    private TextView search;
    private FirebaseFirestore db;

    public SearchProduct() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_product, container, false);

        recyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        searchEditText = view.findViewById(R.id.search_product);
        emptyLayout = view.findViewById(R.id.emptyLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        productList = new ArrayList<>();
        adapter = new SearchProductAdapter(requireContext(), productList, () -> {
            if (getActivity() instanceof MainActivity) {
                int cartCount = CartManager.getCart(requireContext()).size();
                ((MainActivity) getActivity()).updateCartBadge(cartCount);
            }
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence query, int i, int i1, int i2) {
                if (!query.toString().trim().isEmpty()) {
                    searchProductsInFirestore(query.toString().trim());
                } else {
                    productList.clear();
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText searchEditText = view.findViewById(R.id.search_product);
        searchEditText.requestFocus();

        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void searchProductsInFirestore(String query) {
        db.collection("product_list")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String productName = doc.getString("productName");
                        String category = doc.getString("category");
                        String brandName = doc.getString("brandName");

                        if (matchesSearch(query, productName) ||
                                matchesSearch(query, category) ||
                                matchesSearch(query, brandName)) {

                            List<String> images = (List<String>) doc.get("productImages");
                            if (images == null) images = new ArrayList<>();

                            float rating = doc.getDouble("rating") != null
                                    ? doc.getDouble("rating").floatValue() : 0f;

                            int ratingCount = doc.getLong("ratingCount") != null
                                    ? doc.getLong("ratingCount").intValue() : 0;

                            int productQuantity = doc.getLong("productQuantity") != null
                                    ? doc.getLong("productQuantity").intValue() : 0;

                            int stock = doc.getLong("stock") != null
                                    ? doc.getLong("stock").intValue() : 0;

                            float price = doc.getDouble("price") != null
                                    ? doc.getDouble("price").floatValue() : 0f;

                            Date createdAt = doc.getDate("createdAt");
                            Date updatedAt = doc.getDate("updatedAt");
                            Boolean isVisible = doc.getBoolean("isVisible");

                            String tagName = doc.getString("tagName");

                            Product product = new Product(
                                    doc.getString("productId"),
                                    category,
                                    productName,
                                    productQuantity,
                                    stock,
                                    price,
                                    doc.getString("description"),
                                    doc.getString("moreInfo"),
                                    doc.getString("ingredients"),
                                    doc.getString("howToUse"),
                                    doc.getString("shippingInfo"),
                                    brandName,
                                    doc.getString("brandImageUri"),
                                    doc.getString("brandDescription"),
                                    images,
                                    tagName != null ? tagName : "New",
                                    rating,
                                    ratingCount,
                                    createdAt != null ? createdAt : new Date(),
                                    updatedAt != null ? updatedAt : new Date(),
                                    isVisible != null ? isVisible : true
                            );
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (productList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyLayout.setVisibility(View.GONE);
                    }

                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Failed to search.", Toast.LENGTH_SHORT).show()
                );
    }

    private boolean matchesSearch(String query, String field) {
        return field != null && field.toLowerCase().contains(query.toLowerCase());
    }

}
