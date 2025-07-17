package com.application.glamessence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final List<Integer> banners;
    private final List<String> topTexts;
    private final List<String> bottomTexts;
    private final List<String> buttons;
    private final List<Integer> bgColors; // ✅ New

    public BannerAdapter(List<Integer> banners, List<String> topTexts, List<String> bottomTexts, List<String> buttons, List<Integer> bgColors) {
        this.banners = banners;
        this.topTexts = topTexts;
        this.bottomTexts = bottomTexts;
        this.buttons = buttons;
        this.bgColors = bgColors;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bannerImage.setImageResource(banners.get(position));
        holder.bannerTextTop.setText(topTexts.get(position));
        holder.bannerTextBottom.setText(bottomTexts.get(position));
        holder.bannerButton.setText(buttons.get(position));
        holder.bannerColorSection.setBackgroundColor(bgColors.get(position)); // ✅ Set background color
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerImage;
        TextView bannerTextTop, bannerTextBottom;
        Button bannerButton;
        View bannerColorSection; // ✅ Section reference

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerImage = itemView.findViewById(R.id.bannerImage);
            bannerTextTop = itemView.findViewById(R.id.bannerTextTop);
            bannerTextBottom = itemView.findViewById(R.id.bannerTextBottom);
            bannerButton = itemView.findViewById(R.id.bannerButton);
            bannerColorSection = itemView.findViewById(R.id.bannerColorSection); // ✅
        }
    }
}
