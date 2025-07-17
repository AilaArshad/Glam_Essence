package com.application.glamessence;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends Fragment {

    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private FirebaseFirestore db;
    private List<Product> productList = new ArrayList<>();
    private boolean isFavorite = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new GridAdapter(productList, getContext());
        recyclerView.setAdapter(adapter);



        db = FirebaseFirestore.getInstance();
        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        db.collection("product_list")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        productList.clear();
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null) {
                            for (QueryDocumentSnapshot doc : snapshot) {
                                try {
                                    String id = doc.getString("productId");
                                    String category = doc.getString("category");
                                    String name = doc.getString("productName");
                                    String quantity = doc.getString("productQuantity");
                                    String stock = doc.getString("stock");
                                    String price = doc.getString("price");
                                    String description = doc.getString("description");
                                    String moreInfo = doc.getString("moreInfo");
                                    String ingredients = doc.getString("ingredients");
                                    String howToUse = doc.getString("howToUse");
                                    String shippingInfo = doc.getString("shippingInfo");
                                    String brandName = doc.getString("brandName");
                                    String brandImageUri = doc.getString("brandImageUri");
                                    String brandDescription = doc.getString("brandDescription");

                                    List<String> imagePaths = (List<String>) doc.get("productImages");

                                    // Handle null rating safely
                                    Double ratingValue = doc.getDouble("rating");
                                    Long ratingCountValue = doc.getLong("ratingCount");

                                    float rating = ratingValue != null ? ratingValue.floatValue() : 0f;
                                    int ratingCount = ratingCountValue != null ? ratingCountValue.intValue() : 0;

                                    Product product = new Product(id, category, name, quantity, stock, price,
                                            description, moreInfo, ingredients, howToUse, shippingInfo,
                                            brandName, brandImageUri, brandDescription,
                                            imagePaths != null ? imagePaths : new ArrayList<>(),
                                            rating, ratingCount);

                                    productList.add(product);
                                } catch (Exception e) {
                                    Log.e("ProductParse", "Error parsing product", e);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.e("Firestore", "Failed to get products", task.getException());
                    }
                });
    }
}
