package com.application.glamessence;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProductCatalogFragment extends Fragment {

    public ProductCatalogFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_catalog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Expandable menus
        setupExpandableMenu(view, R.id.nutritionBlock, R.id.nutritionMenu);
        setupExpandableMenu(view, R.id.skincareBlock, R.id.skincareMenu);
        setupExpandableMenu(view, R.id.makeupBlock, R.id.makeupMenu);
        setupExpandableMenu(view, R.id.fragranceBlock, R.id.fragranceMenu);
        setupExpandableMenu(view, R.id.bathBlock, R.id.bathMenu);
        setupExpandableMenu(view, R.id.hairBlock, R.id.hairMenu);

        // Add Item clicks
        setupAddItemClick(view, R.id.nutritionAddItem, "Nutrition");
        setupAddItemClick(view, R.id.skincareAddItem, "Skincare");
        setupAddItemClick(view, R.id.makeupAddItem, "Makeup");
        setupAddItemClick(view, R.id.fragranceAddItem, "Fragrance");
        setupAddItemClick(view, R.id.bathAddItem, "Bath and Body");
        setupAddItemClick(view, R.id.hairAddItem, "Hair");

        // Edit Item clicks
        setupEditItemClick(view, R.id.nutritionEditItem, "Nutrition");
        setupEditItemClick(view, R.id.skincareEditItem, "Skincare");
        setupEditItemClick(view, R.id.makeupEditItem, "Makeup");
        setupEditItemClick(view, R.id.fragranceEditItem, "Fragrance");
        setupEditItemClick(view, R.id.bathEditItem, "Bath and Body");
        setupEditItemClick(view, R.id.hairEditItem, "Hair");

        // View All clicks — DO NOTHING for now (disabled)
        setupViewAllClick(view, R.id.nutritionViewAll, "Nutrition");
        setupViewAllClick(view, R.id.skincareViewAll, "Skincare");
        setupViewAllClick(view, R.id.makeupViewAll, "Makeup");
        setupViewAllClick(view, R.id.fragranceViewAll, "Fragrance");
        setupViewAllClick(view, R.id.bathViewAll, "Bath and Body");
        setupViewAllClick(view, R.id.hairViewAll, "Hair");
    }

    private void setupExpandableMenu(View view, int blockId, int menuId) {
        View block = view.findViewById(blockId);
        View menu = view.findViewById(menuId);

        block.setOnClickListener(v -> {
            if (menu.getVisibility() == View.GONE) {
                menu.setVisibility(View.VISIBLE);
            } else {
                menu.setVisibility(View.GONE);
            }
        });
    }

    private void setupAddItemClick(View view, int addItemId, String categoryName) {
        TextView addItem = view.findViewById(addItemId);
        addItem.setOnClickListener(v -> openAddProductFragment(categoryName));
    }

    private void setupEditItemClick(View view, int editItemId, String categoryName) {
        TextView editItem = view.findViewById(editItemId);
        editItem.setOnClickListener(v -> openEditProductFragment(categoryName));
    }

    // View All handler — currently not used
    private void setupViewAllClick(View view, int viewAllId, String categoryName) {
        TextView viewAll = view.findViewById(viewAllId);
        viewAll.setOnClickListener(v -> openEditProductFragment(categoryName));
    }


    private void openAddProductFragment(String category) {
        AddProduct addProductFragment = new AddProduct();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        addProductFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, addProductFragment)
                .addToBackStack(null)
                .commit();
    }

    private void openEditProductFragment(String category) {
        ListToChangeProductInfo listFragment = new ListToChangeProductInfo();
        Bundle bundle = new Bundle();
        bundle.putString("category", category);
        listFragment.setArguments(bundle);

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, listFragment)
                .addToBackStack(null)
                .commit();
    }
}
