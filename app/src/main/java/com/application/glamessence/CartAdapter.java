package com.application.glamessence;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Product> productList;
    private Map<String, Integer> cartMap;
    private OnQuantityChangeListener listener;

    public CartAdapter(Context context, List<Product> productList, Map<String, Integer> cartMap) {
        this.context = context;
        this.productList = productList;
        this.cartMap = cartMap;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage, productTag, deleteIcon, plusIcon, minusIcon, fav_heart;
        TextView productName, productBrand, productPrice, productCode, productQuantity, productVolumn;

        int itemQuantity = 1;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productTag = itemView.findViewById(R.id.productTag);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            plusIcon = itemView.findViewById(R.id.plusIcon);
            minusIcon = itemView.findViewById(R.id.minusIcon);
            fav_heart = itemView.findViewById(R.id.fav_heart);
            productName = itemView.findViewById(R.id.productName);
            productBrand = itemView.findViewById(R.id.productBrand);
            productPrice = itemView.findViewById(R.id.productPrice);
            productCode = itemView.findViewById(R.id.productCode);
            productQuantity = itemView.findViewById(R.id.quantityTextView);
            productVolumn = itemView.findViewById(R.id.productVolume);
        }
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_adapter, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Product product = productList.get(position);
        String productId = product.getProductId();

        holder.productName.setText(product.getProductName());
        holder.productBrand.setText(product.getBrandName());
        holder.productPrice.setText(product.getPrice() + "$");
        holder.productVolumn.setText(product.getProductQuantity() + " ml");  // Volume only
        holder.productCode.setText(productId);

        holder.itemQuantity = cartMap.getOrDefault(productId, 1);
        holder.productQuantity.setText(String.valueOf(holder.itemQuantity));

        List<String> images = product.getProductImages();
        if (images != null && !images.isEmpty()) {
            Glide.with(context)
                    .load(images.get(0))
                    .placeholder(R.drawable.bathbody15)
                    .into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.bathbody15);
        }

        String tagName = product.getTagName();
        if (tagName != null) {
            switch (tagName.toLowerCase()) {
                case "new":
                    holder.productTag.setImageResource(R.drawable.tag_new);
                    break;
                case "hot product":
                    holder.productTag.setImageResource(R.drawable.tag_hot);
                    break;
                case "toprated":
                    holder.productTag.setImageResource(R.drawable.tag_top_rated);
                    break;
                case "trending":
                    holder.productTag.setImageResource(R.drawable.tag_trending);
                    break;
                case "limited":
                    holder.productTag.setImageResource(R.drawable.tag_limited);
                    break;
                default:
                    holder.productTag.setVisibility(View.GONE);
                    return;
            }
            holder.productTag.setVisibility(View.VISIBLE);
        }

        holder.deleteIcon.setOnClickListener(v -> showRemoveConfirmation(holder.getAdapterPosition(), productId));

        holder.minusIcon.setOnClickListener(v -> {
            if (holder.itemQuantity > 1) {
                holder.itemQuantity--;
                holder.productQuantity.setText(String.valueOf(holder.itemQuantity));
                cartMap.put(productId, holder.itemQuantity);
                CartManager.addToCart(context, productId, holder.itemQuantity);
                if (listener != null) listener.onQuantityChanged();
            }
        });

        holder.plusIcon.setOnClickListener(v -> {
            holder.itemQuantity++;
            holder.productQuantity.setText(String.valueOf(holder.itemQuantity));
            cartMap.put(productId, holder.itemQuantity);
            CartManager.addToCart(context, productId, holder.itemQuantity);
            if (listener != null) listener.onQuantityChanged();
        });

        boolean isFavorite = FavoritesManager.isFavorite(context, productId);
        holder.fav_heart.setImageResource(isFavorite ? R.drawable.red_heart : R.drawable.heart_outlined);

        holder.fav_heart.setOnClickListener(v -> {
            if (FavoritesManager.isFavorite(context, productId)) {
                FavoritesManager.removeFromFavorites(context, productId);
                holder.fav_heart.setImageResource(R.drawable.heart_outlined);
            } else {
                FavoritesManager.addToFavorites(context, productId);
                holder.fav_heart.setImageResource(R.drawable.red_heart);
            }
            if (favoritesChangeListener != null) {
                favoritesChangeListener.onFavoritesUpdated(FavoritesManager.getFavorites(context).size());
            }
        });

    }

    private void showRemoveConfirmation(int position, String productId) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialogue_remove_cart, null);
        dialog.setContentView(view);

        view.findViewById(R.id.yesRemoveBtn).setOnClickListener(v -> {
            CartManager.removeFromCart(context, productId);
            cartMap.remove(productId);
            productList.remove(position);
            notifyItemRemoved(position);
            if (cartRemoveListener != null) {
                cartRemoveListener.onCartUpdated(cartMap.size());
            }
            notifyItemRangeChanged(position, productList.size());
            dialog.dismiss();
            if (listener != null) listener.onQuantityChanged();
        });

        view.findViewById(R.id.cancelRemoveBtn).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged();
    }

    public void setOnQuantityChangeListener(OnQuantityChangeListener listener) {
        this.listener = listener;
    }

    public interface OnCartRemoveListener {
        void onCartUpdated(int count);
    }

    public interface OnFavoritesChangeListener {
        void onFavoritesUpdated(int count);
    }

    private OnCartRemoveListener cartRemoveListener;
    private OnFavoritesChangeListener favoritesChangeListener;

    public void setOnCartRemoveListener(OnCartRemoveListener listener) {
        this.cartRemoveListener = listener;
    }

    public void setOnFavoritesChangeListener(OnFavoritesChangeListener listener) {
        this.favoritesChangeListener = listener;
    }

}
