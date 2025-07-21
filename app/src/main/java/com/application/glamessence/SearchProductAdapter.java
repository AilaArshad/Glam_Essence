package com.application.glamessence;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> productList;
    private final OnCartChangeListener cartChangeListener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public SearchProductAdapter(Context context, List<Product> productList, OnCartChangeListener listener) {
        this.context = context;
        this.productList = productList;
        this.cartChangeListener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(String.format("$%.2f", product.getPrice()));
        holder.productId.setText(product.getProductId());

        List<String> images = product.getProductImages();
        if (images != null && !images.isEmpty()) {
            Glide.with(context)
                    .load(images.get(0))
                    .into(holder.productImage);
        }

        String productId = product.getProductId();
        boolean isInCart = CartManager.isInCart(context, productId);
        holder.bagIcon.setImageResource(isInCart ? R.drawable.filled_cart : R.drawable.cart);

        holder.bagIcon.setOnClickListener(v -> {
            if (CartManager.isInCart(context, productId)) {
                CartManager.removeFromCart(context, productId);
                holder.bagIcon.setImageResource(R.drawable.cart);
                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
            } else {
                CartManager.addToCart(context, productId, 1);
                holder.bagIcon.setImageResource(R.drawable.filled_cart);
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            }

            if (cartChangeListener != null) {
                cartChangeListener.onCartChanged();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("productId", product.getProductId());
            bundle.putString("productName", product.getProductName());
            bundle.putString("productPrice", String.valueOf(product.getPrice()));
            bundle.putString("brandName", product.getBrandName());

            if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                bundle.putString("imageUrl", product.getProductImages().get(0));
            }

            ProductDetail productDetailFragment = new ProductDetail();
            productDetailFragment.setArguments(bundle);

            if (context instanceof MainActivity) {
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, productDetailFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, bagIcon;
        TextView productName, productPrice, productId;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            bagIcon = itemView.findViewById(R.id.cart_icon);
            productId = itemView.findViewById(R.id.productId);
        }
    }
}