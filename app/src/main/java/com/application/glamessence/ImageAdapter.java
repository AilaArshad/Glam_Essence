package com.application.glamessence;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_ADD = 2;

    private final List<Object> images;
    private final OnImageActionListener listener;

    public interface OnImageActionListener {
        void onAddImageClicked();
        void onDeleteImageClicked(int position);
    }

    public ImageAdapter(List<Object> images, OnImageActionListener listener) {
        this.images = images;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == images.size() ? TYPE_ADD : TYPE_IMAGE;
    }

    @Override
    public int getItemCount() {
        return images.size() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adding_img, parent, false);
            return new ImageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adding_img_sign, parent, false);
            return new AddViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_IMAGE) {
            ImageViewHolder imageHolder = (ImageViewHolder) holder;
            imageHolder.bind(images.get(position));
        } else {
            AddViewHolder addHolder = (AddViewHolder) holder;
            addHolder.bind();
        }
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, deleteButton;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(Object imageObj) {
            if (imageObj instanceof Uri) {
                imageView.setImageURI((Uri) imageObj);
            } else if (imageObj instanceof String) {
                Glide.with(imageView.getContext())
                        .load((String) imageObj)
                        .into(imageView);
            }

            deleteButton.setOnClickListener(v -> listener.onDeleteImageClicked(getAdapterPosition()));
        }
    }

    class AddViewHolder extends RecyclerView.ViewHolder {
        ImageView addImageBtn;

        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
            addImageBtn = itemView.findViewById(R.id.addImageBtn);
        }

        public void bind() {
            addImageBtn.setOnClickListener(v -> listener.onAddImageClicked());
        }
    }
}
