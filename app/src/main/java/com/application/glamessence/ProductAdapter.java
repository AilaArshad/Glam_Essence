package com.application.glamessence;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private Context context;
    private boolean isFavoritesScreen;

    private OnCartChangeListener cartChangeListener;

    public ProductAdapter(List<Product> productList, Context context, boolean isFavoritesScreen) {
        this.productList = productList;
        this.context = context;
        this.isFavoritesScreen = isFavoritesScreen;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, favHeart, cartIcon, tagImage;
        TextView productName, brandName, productPrice, ratingText;
        RatingBar ratingBar;
        public ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            brandName = itemView.findViewById(R.id.brandName);
            productPrice = itemView.findViewById(R.id.productPrice);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            ratingText = itemView.findViewById(R.id.ratingText);
            favHeart = itemView.findViewById(R.id.fav_heart);
            cartIcon = itemView.findViewById(R.id.cart_icon);
            tagImage = itemView.findViewById(R.id.tagImage);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            String imageUrl = product.getProductImages().get(0);
            Glide.with(context).load(imageUrl).into(holder.productImage);
        }

        holder.brandName.setText(product.getBrandName());
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getPrice() + "$");
        holder.ratingBar.setRating(product.getRating());
        holder.ratingText.setText("(" + product.getRatingCount() + ")");

        boolean isFavorite = FavoritesManager.isFavorite(context, product.getProductId());
        holder.favHeart.setImageResource(isFavorite ? R.drawable.red_heart : R.drawable.heart_outlined);

        holder.favHeart.setOnClickListener(v -> {
            if (FavoritesManager.isFavorite(context, product.getProductId())) {
                if (isFavoritesScreen) {
                    showRemoveConfirmation(holder.getAdapterPosition(), product);
                } else {
                    FavoritesManager.removeFromFavorites(context, product.getProductId());
                    holder.favHeart.setImageResource(R.drawable.heart_outlined);
                    notifyItemChanged(holder.getAdapterPosition());
                    if (favoriteChangeListener != null) {
                        favoriteChangeListener.onFavoritesCountChanged(FavoritesManager.getFavorites(context).size());
                    }
                }
            } else {
                FavoritesManager.addToFavorites(context, product.getProductId());
                holder.favHeart.setImageResource(R.drawable.red_heart);
                notifyItemChanged(holder.getAdapterPosition());
                if (favoriteChangeListener != null) {
                    favoriteChangeListener.onFavoritesCountChanged(FavoritesManager.getFavorites(context).size());
                }
            }
        });

        holder.cartIcon.setOnClickListener(v -> {
            String productId = product.getProductId();
            if (CartManager.isInCart(context, productId)) {
                CartManager.removeFromCart(context, productId);
                holder.cartIcon.setImageResource(R.drawable.cart);
                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show();
            } else {
                CartManager.addToCart(context, productId, 1);
                holder.cartIcon.setImageResource(R.drawable.filled_cart);
                Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
            }
            if (cartChangeListener != null) {
                cartChangeListener.onCartCountChanged(CartManager.getCart(context).size());
            }
        });
        holder.cartIcon.setImageResource(CartManager.isInCart(context, product.getProductId())
                ? R.drawable.filled_cart
                : R.drawable.cart);

        holder.tagImage.setVisibility(View.GONE);
        String tagName = product.getTagName();
        if (tagName != null) {
            switch (tagName.toLowerCase()) {
                case "new":
                    holder.tagImage.setImageResource(R.drawable.tag_new);
                    break;
                case "hot product":
                    holder.tagImage.setImageResource(R.drawable.tag_hot);
                    break;
                case "toprated":
                    holder.tagImage.setImageResource(R.drawable.tag_top_rated);
                    break;
                case "trending":
                    holder.tagImage.setImageResource(R.drawable.tag_trending);
                    break;
                case "limited":
                    holder.tagImage.setImageResource(R.drawable.tag_limited);
                    break;
                default:
                    holder.tagImage.setVisibility(View.GONE);
                    return;
            }
            holder.tagImage.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            ProductDetail productDetailFragment = new ProductDetail();
            Bundle bundle = new Bundle();
            bundle.putString("productId", product.getProductId());
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

    public interface OnFavoriteChangeListener {
        void onFavoritesCountChanged(int count);
    }

    private OnFavoriteChangeListener favoriteChangeListener;

    public void setOnFavoriteChangeListener(OnFavoriteChangeListener listener) {
        this.favoriteChangeListener = listener;
    }

    private void showRemoveConfirmation(int position, Product product) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogue_remove_favorite, null);
        dialog.setContentView(view);

        view.findViewById(R.id.yesRemoveBtn).setOnClickListener(v -> {
            FavoritesManager.removeFromFavorites(context, product.getProductId());
            dialog.dismiss();
            if (isFavoritesScreen && position != RecyclerView.NO_POSITION) {
                productList.remove(position);
                notifyItemRemoved(position);
                if (favoriteChangeListener != null) {
                    favoriteChangeListener.onFavoritesCountChanged(productList.size());
                }
            } else {
                notifyItemChanged(position);
            }
        });

        view.findViewById(R.id.cancelRemoveBtn).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    public interface OnCartChangeListener {
        void onCartCountChanged(int count);
    }

    public void setOnCartChangeListener(OnCartChangeListener listener) {
        this.cartChangeListener = listener;
    }


}
