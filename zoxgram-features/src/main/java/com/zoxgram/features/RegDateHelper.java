package com.zoxgram.features;

import android.content.Context;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registration Date feature.
 * Shows "Get Registration Date" on profiles.
 * On tap reveals approximate registration month/year (like Nicegram).
 *
 * Real data comes from Telegram's user full info or a side channel.
 * This helper caches results and formats them.
 */
public class RegDateHelper {

    private static final ConcurrentHashMap<Long, String> cache = new ConcurrentHashMap<>();

    public static boolean isEnabled() {
        return ZoxgramConfig.isShowRegDate();
    }

    public static String getCachedRegDate(long userId) {
        return cache.get(userId);
    }

    public static void requestRegDate(Context context, long userId, RegDateCallback callback) {
        if (!isEnabled()) {
            if (callback != null) callback.onError("Registration date is disabled in Zoxgram Settings");
            return;
        }

        String cached = cache.get(userId);
        if (cached != null) {
            if (callback != null) callback.onResult(cached);
            return;
        }

        // Placeholder – real implementation queries Telegram API / user full
        // or uses known estimation methods.
        new Thread(() -> {
            try {
                Thread.sleep(600);
                // Demo value – replace with real lookup
                String demo = "~ October 2018";
                cache.put(userId, demo);
                if (callback != null) {
                    android.os.Handler h = new android.os.Handler(context.getMainLooper());
                    h.post(() -> callback.onResult(demo));
                }
            } catch (InterruptedException e) {
                if (callback != null) callback.onError("Cancelled");
            }
        }).start();
    }

    public interface RegDateCallback {
        void onResult(String dateText);
        void onError(String message);
    }
}
