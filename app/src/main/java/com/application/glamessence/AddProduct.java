package com.application.glamessence;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AddProduct extends Fragment {
    private RecyclerView recyclerView;
    private FrameLayout singleImageFrame;
    private ImageView singleSelectedImageView, singleAddImageIcon, deleteSingleImageBtn;
    private CardView cardView;
    private ImageAdapter adapter;
    private final List<Uri> imageUris = new ArrayList<>();
    private Uri selectedSingleImageUri;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String> singleImagePicker;
    private CircularProgressIndicator circularProgressIndicator;
    private EditText productNameEditText, productQuantityEditText, productStockEditText,
            productPriceEditText, productDescriptionEditText, productMoreInfoEditText,
            productIngredientsEditText, productHowToUseEditText, productShippingInfoEditText,
            brandNameEditText, brandDescriptionEditText, productIdEditText, categoryEditText;
    private String categoryFromIntent;
    private FirebaseFirestore db;
    private AppCompatButton btn;
    private String productId, category, productName, productQuantity, stock, price, description,
            moreInfo, ingredients, howToUse, shippingInfo, brandName, brandDescription;
    private List<Object> productImageUris = new ArrayList<>();
    private String brandImageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categoryFromIntent = getArguments().getString("category");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        generateAndSetProductIdAndCategory();
        setupMultipleImagePicker();
        setupSingleImagePicker();
        addClickListener();
        setupEditTextListeners();
    }
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewImages);
        singleImageFrame = view.findViewById(R.id.singleImageFrame);
        singleSelectedImageView = view.findViewById(R.id.singleSelectedImageView);
        singleAddImageIcon = view.findViewById(R.id.singleAddImageIcon);
        deleteSingleImageBtn = view.findViewById(R.id.deleteSingleImageBtn);
        cardView = view.findViewById(R.id.singleImageCard);

        cardView.setVisibility(View.GONE);
        singleSelectedImageView.setVisibility(View.GONE);
        deleteSingleImageBtn.setVisibility(View.GONE);
        singleAddImageIcon.setVisibility(View.VISIBLE);

        circularProgressIndicator = view.findViewById(R.id.circularProgressIndicator);
        circularProgressIndicator.setMax(100);
        circularProgressIndicator.setProgress(0);

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
        productShippingInfoEditText = view.findViewById(R.id.productShippingInfoEditText);
        brandNameEditText = view.findViewById(R.id.brandNameEditText);
        brandDescriptionEditText = view.findViewById(R.id.brandDescriptionEditText);

        db = FirebaseFirestore.getInstance();
        btn = view.findViewById(R.id.addButton);
    }
    private void uploadImageToCloudinary(Uri imageUri, OnImageUploadedCallback callback) {
        MediaManager.get().upload(imageUri)
                .option("folder", "glamessence")
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) { }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) { }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String url = resultData.get("secure_url").toString();
                        callback.onSuccess(url);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onFailure(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) { }
                })
                .dispatch();
    }
    interface OnImageUploadedCallback {
        void onSuccess(String url);
        void onFailure(String error);
    }
    private void generateAndSetProductIdAndCategory() {
        db.collection("product_list")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int nextId = querySnapshot.size() + 1;
                    productIdEditText.setText(String.valueOf(nextId));
                    categoryEditText.setText("Makeup");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed to generate ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void setupMultipleImagePicker() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        adapter = new ImageAdapter(productImageUris, new ImageAdapter.OnImageActionListener() {
            @Override
            public void onAddImageClicked() {
                if (imageUris.size() >= 5) {
                    Toast.makeText(requireContext(), "Maximum 5 images allowed", Toast.LENGTH_SHORT).show();
                    return;
                }
                pickImageLauncher.launch("image/*");
            }

            @Override
            public void onDeleteImageClicked(int position) {
                imageUris.remove(position);
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
                        imageUris.add(uri);
                        adapter.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(imageUris.size() - 1);
                        updateProgressBar();
                    }
                });
    }
    private void setupSingleImagePicker() {
        singleImagePicker = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        selectedSingleImageUri = uri;
                        brandImageUri = uri.toString();
                        singleSelectedImageView.setImageURI(uri);
                        cardView.setVisibility(View.VISIBLE);
                        singleSelectedImageView.setVisibility(View.VISIBLE);
                        singleAddImageIcon.setVisibility(View.GONE);
                        deleteSingleImageBtn.setVisibility(View.VISIBLE);
                        updateProgressBar();
                    }
                });

        singleImageFrame.setOnClickListener(v -> {
            if (selectedSingleImageUri == null) {
                singleImagePicker.launch("image/*");
            }
        });

        deleteSingleImageBtn.setOnClickListener(v -> {
            selectedSingleImageUri = null;
            brandImageUri = null;
            cardView.setVisibility(View.GONE);
            singleSelectedImageView.setVisibility(View.GONE);
            singleAddImageIcon.setVisibility(View.VISIBLE);
            deleteSingleImageBtn.setVisibility(View.GONE);
            updateProgressBar();
        });
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

        productId = productIdEditText.getText().toString().trim();
        category = categoryEditText.getText().toString().trim();
        productName = productNameEditText.getText().toString().trim();
        productQuantity = productQuantityEditText.getText().toString().trim();
        stock = productStockEditText.getText().toString().trim();
        price = productPriceEditText.getText().toString().trim();
        description = productDescriptionEditText.getText().toString().trim();
        moreInfo = productMoreInfoEditText.getText().toString().trim();
        ingredients = productIngredientsEditText.getText().toString().trim();
        howToUse = productHowToUseEditText.getText().toString().trim();
        shippingInfo = productShippingInfoEditText.getText().toString().trim();
        brandName = brandNameEditText.getText().toString().trim();
        brandDescription = brandDescriptionEditText.getText().toString().trim();

        productImageUris.clear();
        for (Uri uri : imageUris) {
            productImageUris.add(uri.toString());
        }

        if (!imageUris.isEmpty()) filledCount++;
        if (!productName.isEmpty()) filledCount++;
        if (!productQuantity.isEmpty()) filledCount++;
        if (!stock.isEmpty()) filledCount++;
        if (!price.isEmpty()) filledCount++;
        if (!description.isEmpty()) filledCount++;
        if (!moreInfo.isEmpty()) filledCount++;
        if (!ingredients.isEmpty()) filledCount++;
        if (!howToUse.isEmpty()) filledCount++;
        if (!shippingInfo.isEmpty()) filledCount++;
        if (!brandName.isEmpty()) filledCount++;
        if (selectedSingleImageUri != null) filledCount++;
        if (!brandDescription.isEmpty()) filledCount++;

        int progress = (filledCount * 100) / totalFields;
        circularProgressIndicator.setProgress(progress);
    }
    private boolean validateFields() {
        updateProgressBar();
        if (circularProgressIndicator.getProgress() < 100) {
            Toast.makeText(requireContext(), "Please fill all fields and select images.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void uploadProductImagesSequentially(List<Uri> imageUris, List<String> uploadedUrls, Runnable onComplete) {
        if (imageUris.isEmpty()) {
            onComplete.run();
            return;
        }
        Uri currentUri = imageUris.remove(0);
        uploadImageToCloudinary(currentUri, new OnImageUploadedCallback() {
            @Override
            public void onSuccess(String url) {
                uploadedUrls.add(url);
                uploadProductImagesSequentially(imageUris, uploadedUrls, onComplete);
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(requireContext(), "Product Image Upload Failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void uploadAllImagesAndSaveToFirestore() {
        List<String> uploadedProductImageUrls = new ArrayList<>();
        List<Uri> productImageUris = new ArrayList<>(imageUris);
        Uri brandImageUri = selectedSingleImageUri;

        uploadProductImagesSequentially(productImageUris, uploadedProductImageUrls, () -> {
            uploadImageToCloudinary(brandImageUri, new OnImageUploadedCallback() {
                @Override
                public void onSuccess(String brandImageUrl) {
                    saveToFirestore(uploadedProductImageUrls, brandImageUrl);
                }
                @Override
                public void onFailure(String error) {
                    Toast.makeText(requireContext(), "Brand Image Upload Failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void saveToFirestore(List<String> productImageUrls, String brandImageUrl) {
        int productId = Integer.parseInt(productIdEditText.getText().toString().trim());
        Map<String, Object> productData = new HashMap<>();
        productData.put("productId", String.valueOf(productId));
        productData.put("category", categoryEditText.getText().toString().trim());
        productData.put("productName", productNameEditText.getText().toString().trim());
        productData.put("productQuantity", productQuantityEditText.getText().toString().trim());
        productData.put("stock", productStockEditText.getText().toString().trim());
        productData.put("price", productPriceEditText.getText().toString().trim());
        productData.put("description", productDescriptionEditText.getText().toString().trim());
        productData.put("moreInfo", productMoreInfoEditText.getText().toString().trim());
        productData.put("ingredients", productIngredientsEditText.getText().toString().trim());
        productData.put("howToUse", productHowToUseEditText.getText().toString().trim());
        productData.put("shippingInfo", productShippingInfoEditText.getText().toString().trim());
        productData.put("brandName", brandNameEditText.getText().toString().trim());
        productData.put("brandImageUri", brandImageUrl);
        productData.put("brandDescription", brandDescriptionEditText.getText().toString().trim());
        productData.put("productImages", productImageUrls);

        db.collection("product_list")
                .document(String.valueOf(productId))
                .set(productData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(requireContext(), "Product Saved Successfully!", Toast.LENGTH_SHORT).show();
                    navigateToProductList();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void navigateToProductList() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, new ListToChangeProductInfo())
                .addToBackStack(null)
                .commit();
    }
    private void addClickListener() {
        btn.setOnClickListener(view -> {
            if (validateFields()) {
                uploadAllImagesAndSaveToFirestore();
            }
        });
    }
}