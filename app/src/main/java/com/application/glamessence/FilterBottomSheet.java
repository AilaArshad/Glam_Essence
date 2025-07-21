package com.application.glamessence;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheet {

    public interface OnFilterApplyListener {
        void onApplyFilters(List<String> selectedTags);
    }

    private final BottomSheetDialog dialog;
    private final List<String> selectedTags = new ArrayList<>();
    private final Context context;
    private final OnFilterApplyListener listener;

    private final int selectedColor = Color.WHITE;
    private final int selectedBg = R.drawable.selected_tag_bg;
    private final int unselectedColor = Color.BLACK;
    private final int unselectedBg = R.drawable.rounded_edittext;

    private View[] tagViews;
    private String[] tags;

    private final boolean allowMultipleSelection;

    public FilterBottomSheet(Context context, List<String> alreadySelectedTags, boolean allowMultipleSelection, OnFilterApplyListener listener) {
        this.context = context;
        this.listener = listener;
        this.allowMultipleSelection = allowMultipleSelection;
        this.selectedTags.addAll(alreadySelectedTags);
        dialog = new BottomSheetDialog(context);
        initDialog();
    }

    private void initDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.filter, null);
        dialog.setContentView(view);

        ImageView ivClose = view.findViewById(R.id.iv_close);
        ImageView ivDropdown = view.findViewById(R.id.iv_dropdown);
        ConstraintLayout tagContainer = view.findViewById(R.id.constraint);
        AppCompatButton applyFilterBtn = view.findViewById(R.id.apply_filter);
        TextView clearAll = view.findViewById(R.id.tv_clear_all);

        tagViews = new View[]{
                view.findViewById(R.id.tag_new),
                view.findViewById(R.id.tag_limited),
                view.findViewById(R.id.tag_hot),
                view.findViewById(R.id.tag_top),
                view.findViewById(R.id.tag_trending)
        };
        tags = new String[]{"NEW", "LIMITED", "HOT PRODUCT", "TOP RATED", "TRENDING"};

        for (int i = 0; i < tagViews.length; i++) {
            TextView tagView = (TextView) tagViews[i];
            String tagName = tags[i];
            tagView.setOnClickListener(v -> toggleTag(tagView, tagName));
        }

        updateTagSelectionUI();

        ivDropdown.setOnClickListener(v -> {
            tagContainer.setVisibility(tagContainer.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        });

        ivClose.setOnClickListener(v -> dialog.dismiss());

        applyFilterBtn.setOnClickListener(v -> {
            listener.onApplyFilters(new ArrayList<>(selectedTags));
            dialog.dismiss();
        });

        clearAll.setOnClickListener(v -> {
            selectedTags.clear();
            updateTagSelectionUI();
            listener.onApplyFilters(new ArrayList<>());
            dialog.dismiss();
        });
    }

    private void toggleTag(TextView textView, String tagName) {
        if (!allowMultipleSelection && !selectedTags.isEmpty() && !selectedTags.contains(tagName)) {
            Toast.makeText(context, "Only 1 tag can be applied here.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedTags.contains(tagName)) {
            selectedTags.remove(tagName);
        } else {
            if (!allowMultipleSelection) {
                selectedTags.clear();
            }
            selectedTags.add(tagName);
        }
        updateTagSelectionUI();
    }

    private void updateTagSelectionUI() {
        for (int i = 0; i < tagViews.length; i++) {
            TextView tagView = (TextView) tagViews[i];
            String tagText = tags[i];
            if (selectedTags.contains(tagText)) {
                tagView.setBackgroundResource(selectedBg);
                tagView.setTextColor(selectedColor);
            } else {
                tagView.setBackgroundResource(unselectedBg);
                tagView.setTextColor(unselectedColor);
            }
        }
    }

    public void show() {
        dialog.show();
    }
}
