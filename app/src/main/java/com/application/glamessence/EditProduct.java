package com.application.glamessence;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditProduct extends Fragment {

    private EditText productNameEditText, categoryEditText, productQuantityEditText, productStockEditText, productPriceEditText, productIdEditText;
    private EditText productDescriptionEditText, productMoreInfoEditText, productIngredientsEditText, productHowToUseEditText, productShippingInfoEditText;
    private EditText brandNameEditText, brandDescriptionEditText;
    private CardView cardView;
    private CircularProgressIndicator circularProgressIndicator;
    private ImageView brandImageView, addImageView, deleteImageView;
    private RecyclerView recyclerView;
    private AppCompatButton updateBtn, deleteBtn;

    private FirebaseFirestore db;

    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> pickBrandImageLauncher;

    private Uri brandImageUri = null;
    private String brandImageUrl;
    private String documentId;

    private ImageAdapter adapter;
    private final List<Object> productImageUris = new ArrayList<>();
    private final List<String> existingImageUrls = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI(view);
        setupActions();
        fetchProductData();
    }

    private void initUI(View view) {
        db = FirebaseFirestore.getInstance();

        productIdEditText = view.findViewById(R.id.productIdEditText);
        categoryEditText = view.findViewById(R.id.categoryEditText);
        productNameEditText = view.findViewById(R.id.productNameEditText);
        productQuantityEditText = view.findViewById(R.id.productQuantityEditText);
        productStockEditText = view.findViewById(R.id.productStockEditText);
        productPriceEditText = view.findViewById(R.id.productPriceEditText);
        productDescriptionEditText = view.findViewById(R.id.productDescriptionEditText);
        productMoreInfoEditText = view.findViewById(R.id.productMoreInfoEditText);
        productIngredientsEditText = view.findViewById(R.id.productIngredientsEditText);
        productHowToUseEditText = view.findViewById(R.id.productHowToUseEditText);
        productShippingInfoEditText = view.findViewById(R.id.productShippingEditText);
        brandNameEditText = view.findViewById(R.id.brandNameEditText);
        brandDescriptionEditText = view.findViewById(R.id.brandDescriptionEditText);
        brandImageView = view.findViewById(R.id.brandImage);
        recyclerView = view.findViewById(R.id.recyclerViewImages);
        addImageView = view.findViewById(R.id.singleAddImageIcon);
        cardView = view.findViewById(R.id.singleImageCard);
        deleteImageView = view.findViewById(R.id.deleteSingleImageBtn);
        updateBtn = view.findViewById(R.id.updateButton);
        deleteBtn = view.findViewById(R.id.deleteButton);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImageAdapter(productImageUris, null);
        recyclerView.setAdapter(adapter);

        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        circularProgressIndicator.setMax(100);
        circularProgressIndicator.setProgress(0);
    }

    private void setupActions() {
        setupMultipleImagePicker();
        setupBrandImagePicker();
        setupEditTextListeners();
        updateBtn.setOnClickListener(v -> onUpdateButtonClicked());
        deleteBtn.setOnClickListener(v -> onDeleteButtonClicked());
    }

    private void setupMultipleImagePicker() {
        adapter = new ImageAdapter(productImageUris, new ImageAdapter.OnImageActionListener() {
            @Override
            public void onAddImageClicked() {
                if (productImageUris.size() >= 5) {
                    Toast.makeText(requireContext(), "Maximum 5 images allowed", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickImageLauncher.launch("image/*");
            }

            @Override
            public void onDeleteImageClicked(int position) {
                productImageUris.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, adapter.getItemCount());
                updateProgressBar();
            }
        });
        recyclerView.setAdapter(adapter);

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        productImageUris.add(uri);
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
                        updateProgressBar();
                    }
                });
    }

    private void setupBrandImagePicker() {
        pickBrandImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        brandImageUri = uri;
                        cardView.setVisibility(View.VISIBLE);
                        brandImageView.setVisibility(View.VISIBLE);
                        deleteImageView.setVisibility(View.VISIBLE);
                        addImageView.setVisibility(View.GONE);
                        brandImageView.setImageURI(uri);
                        updateProgressBar();
                    }
                });
        addImageView.setOnClickListener(v -> pickBrandImageLauncher.launch("image/*"));
        deleteImageView.setOnClickListener(v -> {
            brandImageUri = null;
            brandImageUrl = null;
            cardView.setVisibility(View.GONE);
            deleteImageView.setVisibility(View.GONE);
            addImageView.setVisibility(View.VISIBLE);
            updateProgressBar();
        });
    }

    private void fetchProductData() {
        if (getArguments() != null) {
            documentId = getArguments().getString("productId");
            if (documentId == null || documentId.isEmpty()) {
                Toast.makeText(requireContext(), "Product ID is missing!", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(requireContext(), "No product ID provided.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("product_list").document(documentId)
                .get()
                .addOnSuccessListener(this::populateFields)
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to load product.", Toast.LENGTH_SHORT).show());
    }

    private void populateFields(DocumentSnapshot snapshot) {
        if (!snapshot.exists()) {
            Toast.makeText(requireContext(), "Product not found!", Toast.LENGTH_SHORT).show();
            return;
        }
        productIdEditText.setText(documentId);
        categoryEditText.setText(snapshot.getString("category"));
        productNameEditText.setText(snapshot.getString("productName"));
        productQuantityEditText.setText(snapshot.getString("productQuantity"));
        productStockEditText.setText(snapshot.getString("stock"));
        productPriceEditText.setText(snapshot.getString("price"));
        productDescriptionEditText.setText(snapshot.getString("description"));
        productMoreInfoEditText.setText(snapshot.getString("moreInfo"));
        productIngredientsEditText.setText(snapshot.getString("ingredients"));
        productHowToUseEditText.setText(snapshot.getString("howToUse"));
        productShippingInfoEditText.setText(snapshot.getString("shippingInfo"));
        brandNameEditText.setText(snapshot.getString("brandName"));
        brandDescriptionEditText.setText(snapshot.getString("brandDescription"));

        brandImageUrl = snapshot.getString("brandImageUri");
        if (brandImageUrl != null && !brandImageUrl.isEmpty()) {
            cardView.setVisibility(View.VISIBLE);
            addImageView.setVisibility(View.GONE);
            deleteImageView.setVisibility(View.VISIBLE);
            Glide.with(this).load(brandImageUrl).into(brandImageView);
        }
        List<String> imageUrls = (List<String>) snapshot.get("productImages");
        if (imageUrls != null) {
            existingImageUrls.clear();
            existingImageUrls.addAll(imageUrls);
            productImageUris.clear();
            productImageUris.addAll(imageUrls);
            adapter.notifyDataSetChanged();
        }
        updateProgressBar();
    }

    private void setupEditTextListeners() {
        SimpleTextWatcher watcher = new SimpleTextWatcher() {
            @Override
            public void onChanged() {
                updateProgressBar();
            }
        };
        productNameEditText.addTextChangedListener(watcher);
        productQuantityEditText.addTextChangedListener(watcher);
        productStockEditText.addTextChangedListener(watcher);
        productPriceEditText.addTextChangedListener(watcher);
        productDescriptionEditText.addTextChangedListener(watcher);
        productMoreInfoEditText.addTextChangedListener(watcher);
        productIngredientsEditText.addTextChangedListener(watcher);
        productHowToUseEditText.addTextChangedListener(watcher);
        productShippingInfoEditText.addTextChangedListener(watcher);
        brandNameEditText.addTextChangedListener(watcher);
        brandDescriptionEditText.addTextChangedListener(watcher);
    }

    private void updateProgressBar() {
        int totalFields = 13;
        int filledCount = 0;
        if (!isEmpty(productNameEditText)) filledCount++;
        if (!isEmpty(productQuantityEditText)) filledCount++;
        if (!isEmpty(productStockEditText)) filledCount++;
        if (!isEmpty(productPriceEditText)) filledCount++;
        if (!isEmpty(productDescriptionEditText)) filledCount++;
        if (!isEmpty(productMoreInfoEditText)) filledCount++;
        if (!isEmpty(productIngredientsEditText)) filledCount++;
        if (!isEmpty(productHowToUseEditText)) filledCount++;
        if (!isEmpty(productShippingInfoEditText)) filledCount++;
        if (!isEmpty(brandNameEditText)) filledCount++;
        if (!isEmpty(brandDescriptionEditText)) filledCount++;
        if (!productImageUris.isEmpty()) filledCount++;
        if (brandImageUri != null || (brandImageUrl != null && !brandImageUrl.isEmpty()))
            filledCount++;

        int progress = (filledCount * 100) / totalFields;
        circularProgressIndicator.setProgress(progress);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().isEmpty();
    }

    private void onUpdateButtonClicked() {
        uploadNewImagesToCloudinary();
    }

    private void uploadNewImagesToCloudinary() {
        List<Uri> newUris = new ArrayList<>();
        List<String> oldUrls = new ArrayList<>();
        for (Object obj : productImageUris) {
            if (obj instanceof Uri) {
                newUris.add((Uri) obj);
            } else if (obj instanceof String) {
                oldUrls.add((String) obj);
            }
        }

        if (newUris.isEmpty()) {
            uploadBrandImageIfNeeded(oldUrls);
        } else {
            List<String> uploadedUrls = new ArrayList<>();
            uploadImagesRecursively(newUris, oldUrls, uploadedUrls, 0);
        }
    }

    private void uploadImagesRecursively(List<Uri> newUris, List<String> oldUrls, List<String> uploadedUrls, int index) {
        if (index >= newUris.size()) {
            List<String> allUrls = new ArrayList<>(oldUrls);
            allUrls.addAll(uploadedUrls);
            uploadBrandImageIfNeeded(allUrls);
            return;
        }

        Uri currentUri = newUris.get(index);
        MediaManager.get().upload(currentUri)
                .option("folder", "glamessence/glamessence")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        uploadedUrls.add((String) resultData.get("secure_url"));
                        uploadImagesRecursively(newUris, oldUrls, uploadedUrls, index + 1);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                }).dispatch();
    }

    private void uploadBrandImageIfNeeded(List<String> finalProductImages) {
        if (brandImageUri != null) {
            MediaManager.get().upload(brandImageUri)
                    .option("folder", "glamessence/glamessence")
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String uploadedBrandUrl = (String) resultData.get("secure_url");
                            updateFirestore(finalProductImages, uploadedBrandUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            Toast.makeText(requireContext(), "Brand image upload failed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {
                        }
                    }).dispatch();
        } else {
            updateFirestore(finalProductImages, brandImageUrl);
        }
    }

    private void updateFirestore(List<String> productImageUrls, String brandImageFinalUrl) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("category", categoryEditText.getText().toString().trim());
        updatedData.put("productName", productNameEditText.getText().toString().trim());
        updatedData.put("productQuantity", productQuantityEditText.getText().toString().trim());
        updatedData.put("stock", productStockEditText.getText().toString().trim());
        updatedData.put("price", productPriceEditText.getText().toString().trim());
        updatedData.put("description", productDescriptionEditText.getText().toString().trim());
        updatedData.put("moreInfo", productMoreInfoEditText.getText().toString().trim());
        updatedData.put("ingredients", productIngredientsEditText.getText().toString().trim());
        updatedData.put("howToUse", productHowToUseEditText.getText().toString().trim());
        updatedData.put("shippingInfo", productShippingInfoEditText.getText().toString().trim());
        updatedData.put("brandName", brandNameEditText.getText().toString().trim());
        updatedData.put("brandDescription", brandDescriptionEditText.getText().toString().trim());
        updatedData.put("productImages", productImageUrls);
        updatedData.put("brandImageUri", brandImageFinalUrl);

        db.collection("product_list").document(documentId)
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Product updated successfully.", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, new ListToChangeProductInfo())
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e -> Toast.makeText(requireContext(), "Failed to update product.", Toast.LENGTH_SHORT).show());
    }

    private void onDeleteButtonClicked() {
        if (documentId == null || documentId.isEmpty()) {
            Toast.makeText(requireContext(), "No Product ID found.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("product_list").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Product deleted successfully.", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.frameLayout, new ListToChangeProductInfo())
                            .addToBackStack(null)
                            .commit();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to delete product.", Toast.LENGTH_SHORT).show();
                });
    }
}
