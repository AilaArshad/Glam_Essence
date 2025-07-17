package com.application.glamessence;

import com.bumptech.glide.Glide;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;


public class ProductEditListAdapter extends ArrayAdapter<Product> {
    public ProductEditListAdapter(@NonNull Context context, List<Product> productList) {
        super(context, 0, productList);
    }

    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.custom_list_item, parent, false);
        }

        ImageView productImg = convertView.findViewById(R.id.product_img_list);
        TextView productName = convertView.findViewById(R.id.name);
        TextView productPrice = convertView.findViewById(R.id.price);
        ImageView editBtn = convertView.findViewById(R.id.editImg);

        productName.setText(product.getProductName());
        productPrice.setText("$" + product.getPrice());

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            Glide.with(getContext())
                    .load(product.getProductImages().get(0))
                    .into(productImg);
        }

        editBtn.setOnClickListener(v -> {
            EditProduct editProduct = new EditProduct();
            Bundle bundle = new Bundle();
            bundle.putString("productId", product.getProductId());
            editProduct.setArguments(bundle);

            ((MainActivity) getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayout, editProduct)
                    .addToBackStack(null)
                    .commit();
        });

        return convertView;
    }
}
