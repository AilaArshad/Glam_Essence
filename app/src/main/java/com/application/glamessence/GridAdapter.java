package com.application.glamessence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;

    private Set<String> favoriteProductIds = new HashSet<>();
    private Set<String> cartProductIds = new HashSet<>();



    // Constructor receives the product list from activity/fragment
    public GridAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    // ViewHolder class
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage ,favHeart,cartIcon;
        TextView brandName, productName, productPrice, ratingText;
        RatingBar ratingBar;

        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            brandName = itemView.findViewById(R.id.brandName);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingText = itemView.findViewById(R.id.ratingText);
            favHeart = itemView.findViewById(R.id.fav_heart);
            cartIcon= itemView.findViewById(R.id.cart_icon);

        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_grid_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            String imageUrl = product.getProductImages().get(0);
            Glide.with(context)
                    .load(imageUrl)
                    .into(holder.productImage);
        }


        holder.brandName.setText(product.getBrandName());
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getPrice()+"$");
        holder.ratingBar.setRating(product.getRating());
        holder.ratingText.setText("(" + product.getRatingCount() + ")");

        String productId = product.getProductId();

        if (favoriteProductIds.contains(productId)) {
            holder.favHeart.setImageResource(R.drawable.red_heart);
        } else {
            holder.favHeart.setImageResource(R.drawable.heart_outlined);
        }

        holder.favHeart.setOnClickListener(v -> {
            if (favoriteProductIds.contains(productId)) {
                favoriteProductIds.remove(productId);
                holder.favHeart.setImageResource(R.drawable.heart_black);
            } else {
                favoriteProductIds.add(productId);
                holder.favHeart.setImageResource(R.drawable.red_heart);
            }
        });


        if (cartProductIds.contains(productId)) {
            holder.cartIcon.setImageResource(R.drawable.filled_cart);
        } else {
            holder.cartIcon.setImageResource(R.drawable.cart);
        }

// Toggle cart icon on click
        holder.cartIcon.setOnClickListener(v -> {
            String currentProductId = product.getProductId(); // Must re-fetch it inside listener
            if (cartProductIds.contains(currentProductId)) {
                cartProductIds.remove(currentProductId);
                holder.cartIcon.setImageResource(R.drawable.cart);
            } else {
                cartProductIds.add(currentProductId);
                holder.cartIcon.setImageResource(R.drawable.filled_cart);
            }
        });


    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }
    public void updateList(List<Product> newList) {
        productList = newList;
        notifyDataSetChanged();
    }
}
