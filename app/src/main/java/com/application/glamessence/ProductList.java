package com.application.glamessence;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductList extends Fragment {

    private RecyclerView recyclerView;
    private GridAdapter adapter;
    private FirebaseFirestore db;
    private TextView heading;
    private List<Product> productList = new ArrayList<>();
    private List<String> selectedTags = new ArrayList<>();
    private ImageView backBtn;
    private TextView tvSortLatest, tvFilter;
    private String currentSortOption = "Latest";
    private String categoryFilter;
    private String tagFilter;
    private String originalHeading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        recyclerView = view.findViewById(R.id.productRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new GridAdapter(productList, getContext(), false);
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


        tvSortLatest = view.findViewById(R.id.latest);
        tvSortLatest.setOnClickListener(v -> showSortOptions());

        tvFilter = view.findViewById(R.id.filter);
        tvFilter.setOnClickListener(v -> showFilterDialog());

        backBtn = view.findViewById(R.id.back_arrow);
        heading = view.findViewById(R.id.main_title);
        db = FirebaseFirestore.getInstance();

        categoryFilter = getArguments() != null ? getArguments().getString("category") : null;
        tagFilter = getArguments() != null ? getArguments().getString("tag") : null;
        originalHeading = getArguments() != null ? getArguments().getString("heading") : null;

        if (originalHeading != null) {
            heading.setText(originalHeading);
        } else if (categoryFilter != null) {
            heading.setText(categoryFilter);
        } else if (tagFilter != null) {
            heading.setText(tagFilter);
        } else {
            heading.setText("Products");
        }

        backBtn.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager().popBackStack());

        fetchProducts(categoryFilter, tagFilter);
        return view;
    }

    private void fetchProducts(@Nullable String categoryFilter, @Nullable String tagFilter) {
        Query query = db.collection("product_list").whereEqualTo("visible", true);

        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            query = query.whereEqualTo("category", categoryFilter);
        }

        if (tagFilter != null && !tagFilter.isEmpty()) {
            query = query.whereEqualTo("tagName", tagFilter.toUpperCase());
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Product product = ProductUtils.parseProduct(doc);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.e("Firestore", "Failed to get products", task.getException());
            }
        });
    }

    private void fetchProductsFromQuery(Query query) {
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                productList.clear();
                QuerySnapshot snapshot = task.getResult();
                if (snapshot != null) {
                    for (QueryDocumentSnapshot doc : snapshot) {
                        Product product = ProductUtils.parseProduct(doc);
                        if (product != null) {
                            productList.add(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.e("Firestore", "Failed to get products", task.getException());
            }
        });
    }

    private void fetchProductsSortedByHighestPrice() {
        Query query = db.collection("product_list").whereEqualTo("visible", true);
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            query = query.whereEqualTo("category", categoryFilter);
        }
        if (tagFilter != null && !tagFilter.isEmpty()) {
            query = query.whereEqualTo("tagName", tagFilter.toUpperCase());
        }
        query = query.orderBy("price", Query.Direction.DESCENDING);
        fetchProductsFromQuery(query);
    }

    private void fetchProductsSortedByLowestPrice() {
        Query query = db.collection("product_list").whereEqualTo("visible", true);
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            query = query.whereEqualTo("category", categoryFilter);
        }
        if (tagFilter != null && !tagFilter.isEmpty()) {
            query = query.whereEqualTo("tagName", tagFilter.toUpperCase());
        }
        query = query.orderBy("price", Query.Direction.ASCENDING);
        fetchProductsFromQuery(query);
    }

    private void fetchProductsSortedByBestOffer() {
        Query query = db.collection("product_list").whereEqualTo("visible", true);
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            query = query.whereEqualTo("category", categoryFilter);
        }
        if (tagFilter != null && !tagFilter.isEmpty()) {
            query = query.whereEqualTo("tagName", tagFilter.toUpperCase());
        }
        query = query.orderBy("rating", Query.Direction.DESCENDING);
        fetchProductsFromQuery(query);
    }

    private void showSortOptions() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.latest, null);
        dialog.setContentView(view);

        TextView tvLatest = view.findViewById(R.id.tv_latest);
        TextView tvHighest = view.findViewById(R.id.tv_highest);
        TextView tvLowest = view.findViewById(R.id.tv_lowest);
        TextView tvBest = view.findViewById(R.id.tv_best);

        ImageView ivLatest = view.findViewById(R.id.check_latest);
        ImageView ivHighest = view.findViewById(R.id.check_highest);
        ImageView ivLowest = view.findViewById(R.id.check_lowest);
        ImageView ivBest = view.findViewById(R.id.check_best);

        resetSortIcons(tvLatest, tvHighest, tvLowest, tvBest, ivLatest, ivHighest, ivLowest, ivBest);

        switch (currentSortOption) {
            case "Latest":
                tvLatest.setTextColor(Color.parseColor("#239D21"));
                ivLatest.setVisibility(View.VISIBLE);
                break;
            case "Highest Price":
                tvHighest.setTextColor(Color.parseColor("#239D21"));
                ivHighest.setVisibility(View.VISIBLE);
                break;
            case "Lowest Price":
                tvLowest.setTextColor(Color.parseColor("#239D21"));
                ivLowest.setVisibility(View.VISIBLE);
                break;
            case "Best Offer":
                tvBest.setTextColor(Color.parseColor("#239D21"));
                ivBest.setVisibility(View.VISIBLE);
                break;
        }

        tvLatest.setOnClickListener(v -> {
            currentSortOption = "Latest";
            tvSortLatest.setText("LATEST");
            fetchProducts(categoryFilter, tagFilter);
            dialog.dismiss();
        });

        tvHighest.setOnClickListener(v -> {
            currentSortOption = "Highest Price";
            tvSortLatest.setText("HIGHEST");
            fetchProductsSortedByHighestPrice();
            dialog.dismiss();
        });

        tvLowest.setOnClickListener(v -> {
            currentSortOption = "Lowest Price";
            tvSortLatest.setText("LOWEST");
            fetchProductsSortedByLowestPrice();
            dialog.dismiss();
        });

        tvBest.setOnClickListener(v -> {
            currentSortOption = "Best Offer";
            tvSortLatest.setText("BEST OFFER");
            fetchProductsSortedByBestOffer();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void resetSortIcons(View... views) {
        for (View view : views) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.BLACK);
            } else if (view instanceof ImageView) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void showFilterDialog() {
        boolean allowMultiple = categoryFilter != null && !categoryFilter.isEmpty();
        FilterBottomSheet sheet = new FilterBottomSheet(requireContext(), selectedTags, allowMultiple, selected -> {
            selectedTags.clear();
            selectedTags.addAll(selected);
            applyTagFilters(selectedTags);
        });
        sheet.show();
    }

    private void applyTagFilters(List<String> tags) {
        if (tags.isEmpty()) {
            resetFilters();
            return;
        }

        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            heading.setText(categoryFilter);
        } else if (tags.size() == 1) {
            heading.setText(tags.get(0));
        }

        Query query = db.collection("product_list").whereEqualTo("visible", true);

        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            query = query.whereEqualTo("category", categoryFilter);
        }

        if (!tags.isEmpty()) {
            query = query.whereIn("tagName", tags);
        }

        fetchProductsFromQuery(query);
    }

    private void resetFilters() {
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            heading.setText(categoryFilter);
            fetchProducts(categoryFilter, null);
        } else if (tagFilter != null && !tagFilter.isEmpty()) {
            heading.setText(tagFilter);
            fetchProducts(null, tagFilter);
        } else if (originalHeading != null) {
            heading.setText(originalHeading);
            fetchProducts(null, null);
        } else {
            heading.setText("Products");
            fetchProducts(null, null);
        }
    }
}
