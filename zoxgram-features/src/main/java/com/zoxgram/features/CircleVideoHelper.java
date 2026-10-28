package com.zoxgram.features;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

public final class CircleVideoHelper {

    public static final int MAX_DURATION_SEC = 60;

    private CircleVideoHelper() {}

    public static boolean isEnabled() {
        return ZoxgramConfig.isCircleVideoEnabled();
    }

    public static boolean startCircleConversion(Context context, File videoFile) {
        ZoxgramConfig.init(context);
        if (!isEnabled()) {
            Toast.makeText(context, "Circle Video disabled in Zoxgram Settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (videoFile == null || !videoFile.exists()) {
            Toast.makeText(context, "Video not found", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Real conversion hooks into Telegram MediaController from ChatActivity injection
        Toast.makeText(context, "Circle Video ready (wire MediaController.convert for final output)", Toast.LENGTH_SHORT).show();
        return true;
    }
}
