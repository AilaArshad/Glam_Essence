package com.application.glamessence;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Checkout extends Fragment {
    private EditText etName, etPhone, etAddress;
    private TextView tvTotalAmount;
    private Button btnPlaceOrder;

    private ImageView img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etName = view.findViewById(R.id.et_name);
        etPhone = view.findViewById(R.id.et_phone);
        etAddress = view.findViewById(R.id.et_address);
        tvTotalAmount = view.findViewById(R.id.tv_total_amount);
        btnPlaceOrder = view.findViewById(R.id.btn_place_order);
        img = view.findViewById(R.id.btn_back);

        float totalAmount = getArguments() != null ? getArguments().getFloat("totalAmount", 0f) : 0f;
        tvTotalAmount.setText("Total: $" + String.format("%.2f", totalAmount));

        btnPlaceOrder.setOnClickListener(v -> {
            if (validateInputs()) {
                CartManager.clearCart(requireContext());

                // Update cart badge to 0 after clearing
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge(0);
                }

                Toast.makeText(requireContext(), "Your order is placed!\nYou will receive details via email.", Toast.LENGTH_LONG).show();
                ((MainActivity) requireActivity()).setSelectedIcon("home");
            }
        });

        img.setOnClickListener(view1 -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    private boolean validateInputs() {
        if (etName.getText().toString().trim().isEmpty() ||
                etPhone.getText().toString().trim().isEmpty() ||
                etAddress.getText().toString().trim().isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}