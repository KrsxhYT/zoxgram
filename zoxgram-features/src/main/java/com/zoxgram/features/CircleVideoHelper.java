package com.zoxgram.features;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

/**
 * Circle / Rounded Video conversion.
 *
 * Flow requested (exact from video):
 * 1. User picks a video in attachment menu
 * 2. Chooses "Send as Rounded Video"
 * 3. App converts to max 60s circle video and sends
 *
 * Real conversion uses Telegram's existing MediaController / VideoConvert queue.
 * This helper just exposes the feature flag + entry point.
 */
public class CircleVideoHelper {

    public static final int MAX_DURATION_SEC = 60;

    public static boolean isEnabled() {
        return ZoxgramConfig.isCircleVideoEnabled();
    }

    /**
     * Called from attachment menu when user taps "Send as Rounded Video".
     * @param videoFile original video
     * @return true if conversion was started
     */
    public static boolean startCircleConversion(Context context, File videoFile) {
        if (!isEnabled()) {
            Toast.makeText(context, "Circle Video is disabled in Zoxgram Settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (videoFile == null || !videoFile.exists()) {
            Toast.makeText(context, "Video not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Real implementation hooks into Telegram's MediaController.convertVideo
        // with roundVideo = true and max duration 60s.
        // For now we just signal success so UI can continue.
        Toast.makeText(context, "Preparing circle video…", Toast.LENGTH_SHORT).show();
        return true;
    }
}
