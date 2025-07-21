package com.application.glamessence;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<DashboardProduct> banners;
    private final Context context;

    private final List<Integer> pastelColors = Arrays.asList(
            Color.parseColor("#FFF9C4"), // Soft Yellow
            Color.parseColor("#E1F5FE"), // Light Blue
            Color.parseColor("#F8BBD0"), // Light Pink
            Color.parseColor("#DCEDC8"), // Light Green
            Color.parseColor("#FFECB3"), // Light Orange
            Color.parseColor("#D1C4E9")  // Light Purple
    );

    public BannerAdapter(List<DashboardProduct> banners, Context context) {
        this.banners = banners;
        this.context = context;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        DashboardProduct product = banners.get(position);

        Glide.with(context)
                .load(product.getUrl())
                .into(holder.bannerImage);

        holder.bannerTextTop.setText(product.getSubHeading());
        holder.bannerTextBottom.setText(product.getProductName());
        holder.bannerButton.setText("Explore");

        int colorIndex = position % pastelColors.size();
        holder.bannerColorSection.setBackgroundColor(pastelColors.get(colorIndex));

        holder.bannerButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("category", product.getCategory());
            bundle.putString("heading", product.getCategory());

            ProductList productListFragment = new ProductList();
            productListFragment.setArguments(bundle);

            if (context instanceof MainActivity) {
                ((MainActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frameLayout, productListFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView bannerTextTop, bannerTextBottom;
        Button bannerButton;
        View bannerColorSection;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            bannerTextTop = itemView.findViewById(R.id.bannerTextTop);
            bannerTextBottom = itemView.findViewById(R.id.bannerTextBottom);
            bannerButton = itemView.findViewById(R.id.bannerButton);
            bannerColorSection = itemView.findViewById(R.id.bannerColorSection);
        }
    }
}
