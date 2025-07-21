package com.application.glamessence;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListToChangeProductInfo extends Fragment {

    private ListView listView;
    private ProductEditListAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private EditText searchEditText;
    private String categoryFilter;
    private List<Product> filteredList = new ArrayList<>();
    private TextView heading, noProductsTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryFilter = getArguments().getString("category");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_product_edit, container, false);
        listView = view.findViewById(R.id.listView);
        ImageView backBtn = view.findViewById(R.id.imageView_back_list);
        backBtn.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager().popBackStack());
        searchEditText = view.findViewById(R.id.searchEditText);
        heading = view.findViewById(R.id.main_heading_list);
        noProductsTextView = view.findViewById(R.id.textView_no_products);

        adapter = new ProductEditListAdapter(requireContext(), getParentFragmentManager(), new ArrayList<>());
        listView.setAdapter(adapter);
        heading.setText(categoryFilter);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProductsByName(s.toString());
            }
        });

        fetchProducts();
        Log.d("CategoryDebug", "Category Filter: " + categoryFilter);

        return view;
    }

    private void filterProductsByName(String query) {
        filteredList.clear();
        for (Product product : productList) {
            if (product.getProductName() != null &&
                    product.getProductName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        adapter.updateList(query.isEmpty() ? productList : filteredList);
    }

    private void fetchProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            db.collection("product_list")
                    .whereEqualTo("category", categoryFilter)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        productList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Product product = createProductFromDoc(doc);
                            productList.add(product);
                        }
                        adapter.updateList(new ArrayList<>(productList));

                        if (productList.isEmpty()) {
                            noProductsTextView.setVisibility(View.VISIBLE);
                        } else {
                            noProductsTextView.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to fetch products.", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private Product createProductFromDoc(QueryDocumentSnapshot doc) {
        return new Product(
                doc.getString("productId"),
                doc.getString("category"),
                doc.getString("productName"),
                parseIntSafely(doc.get("productQuantity")),
                parseIntSafely(doc.get("stock")),
                parseFloatSafely(doc.get("price")),
                doc.getString("description"),
                doc.getString("moreInfo"),
                doc.getString("ingredients"),
                doc.getString("howToUse"),
                doc.getString("shippingInfo"),
                doc.getString("brandName"),
                doc.getString("brandImageUrl"),
                doc.getString("brandDescription"),
                (List<String>) doc.get("productImages"),
                doc.getString("tagName"),
                parseFloatSafely(doc.get("rating")),
                parseIntSafely(doc.get("ratingCount")),
                doc.getDate("createdAt"),
                doc.getDate("updatedAt"),
                Boolean.TRUE.equals(doc.getBoolean("isVisible"))
        );

    }

    private int parseIntSafely(Object value) {
        try {
            if (value instanceof Number) return ((Number) value).intValue();
            if (value instanceof String && !((String) value).isEmpty()) {
                return Integer.parseInt((String) value);
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    private float parseFloatSafely(Object value) {
        try {
            if (value instanceof Number) return ((Number) value).floatValue();
            if (value instanceof String && !((String) value).isEmpty()) {
                return Float.parseFloat((String) value);
            }
        } catch (Exception ignored) {
        }
        return 0f;
    }
}
