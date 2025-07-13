package com.application.glamessence;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static boolean isInitialized = false;

    public static void init(Context context) {
        if (!isInitialized) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dcazt1gn1");
            config.put("api_key", "713342171772377");
            config.put("api_secret", "jE5NxKkcHkMxggT2nBm0ZLK5xIQ");

            MediaManager.init(context, config);
            isInitialized = true;
        }
    }
}
