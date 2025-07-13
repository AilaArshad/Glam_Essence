package com.application.glamessence;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Typeface;

import androidx.fragment.app.Fragment;

public class ProductDetail extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private boolean isLiked = false;

    public ProductDetail() {
    }

    public static ProductDetail newInstance(String param1, String param2) {
        ProductDetail fragment = new ProductDetail();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        ImageView fixedHeart = view.findViewById(R.id.fixed_heart);

        fixedHeart.setOnClickListener(v -> {
            isLiked = !isLiked;
            if (isLiked) {
                fixedHeart.setImageResource(R.drawable.red_heart);
            } else {
                fixedHeart.setImageResource(R.drawable.heart_outlined);
            }
        });

        TextView tabDescription = view.findViewById(R.id.tab_description);
        TextView tabMoreInfo = view.findViewById(R.id.tab_more_info);
        TextView tabIngredients = view.findViewById(R.id.tab_ingredients);
        TextView tabHowToUse = view.findViewById(R.id.tab_how_to_use);
        TextView tabShipping = view.findViewById(R.id.tab_shipping);
        TextView tabContent = view.findViewById(R.id.tab_content);

        TextView[] allTabs = {
                tabDescription, tabMoreInfo, tabIngredients, tabHowToUse, tabShipping
        };

        View.OnClickListener tabClickListener = v -> {
            for (TextView tab : allTabs) {
                tab.setTypeface(null, Typeface.NORMAL);
                tab.setSelected(false);
            }

            TextView selectedTab = (TextView) v;
            selectedTab.setTypeface(null, Typeface.BOLD);
            selectedTab.setSelected(true);

            int id = v.getId();
            if (id == R.id.tab_description) {
                tabContent.setText("This product provides daily UV protection with a lightweight, oil-free formula that enhances skin glow.");
            } else if (id == R.id.tab_more_info) {
                tabContent.setText("Dermatologically tested. Suitable for all skin types. Use under makeup or on its own.");
            } else if (id == R.id.tab_ingredients) {
                tabContent.setText("Ingredients: Aqua, Glycerin, Ethylhexyl Methoxycinnamate, Tocopherol, Fragrance, and more.");
            } else if (id == R.id.tab_how_to_use) {
                tabContent.setText("Apply generously on face and neck as the last step of your skincare routine, before sun exposure.");
            } else if (id == R.id.tab_shipping) {
                tabContent.setText("We offer fast delivery within 2–5 business days. Free shipping on orders above £25.");
            }
        };

        for (TextView tab : allTabs) {
            tab.setOnClickListener(tabClickListener);
        }

        tabDescription.performClick();

        return view;
    }
}
