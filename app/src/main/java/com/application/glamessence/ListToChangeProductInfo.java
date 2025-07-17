package com.application.glamessence;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListToChangeProductInfo extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ListView listView;
    private ProductEditListAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    // New key for category filter (unchange mParam1/mParam2 logic)
    private String categoryFilter;

    public ListToChangeProductInfo() {
    }

    public static ListToChangeProductInfo newInstance(String param1, String param2) {
        ListToChangeProductInfo fragment = new ListToChangeProductInfo();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // Optional newInstance for passing category filter
    public static ListToChangeProductInfo newInstanceWithCategory(String category) {
        ListToChangeProductInfo fragment = new ListToChangeProductInfo();
        Bundle args = new Bundle();
        args.putString("category", category); // <- new key
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

            // Get category filter (if passed)
            categoryFilter = getArguments().getString("category");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_product_edit, container, false);
        listView = view.findViewById(R.id.listView);
        adapter = new ProductEditListAdapter(requireContext(), productList);
        listView.setAdapter(adapter);
        fetchProducts();
        return view;
    }

    private void fetchProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Firestore query based on category if available
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            db.collection("product_list")
                    .whereEqualTo("category", categoryFilter)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        productList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Product product = new Product(
                                    doc.getString("productId"),
                                    doc.getString("category"),
                                    doc.getString("productName"),
                                    doc.getString("productQuantity"),
                                    doc.getString("stock"),
                                    doc.getString("price"),
                                    doc.getString("description"),
                                    doc.getString("moreInfo"),
                                    doc.getString("ingredients"),
                                    doc.getString("howToUse"),
                                    doc.getString("shippingInfo"),
                                    doc.getString("brandName"),
                                    doc.getString("brandImageUri"),
                                    doc.getString("brandDescription"),
                                    (List<String>) doc.get("productImages")
                            );
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to fetch filtered products.", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // fallback: show all products (default logic)
            db.collection("product_list")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        productList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            Product product = new Product(
                                    doc.getString("productId"),
                                    doc.getString("category"),
                                    doc.getString("productName"),
                                    doc.getString("productQuantity"),
                                    doc.getString("stock"),
                                    doc.getString("price"),
                                    doc.getString("description"),
                                    doc.getString("moreInfo"),
                                    doc.getString("ingredients"),
                                    doc.getString("howToUse"),
                                    doc.getString("shippingInfo"),
                                    doc.getString("brandName"),
                                    doc.getString("brandImageUri"),
                                    doc.getString("brandDescription"),
                                    (List<String>) doc.get("productImages")
                            );
                            productList.add(product);
                        }
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Failed to fetch all products.", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}
