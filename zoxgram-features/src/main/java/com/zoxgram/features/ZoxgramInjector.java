package com.zoxgram.features;

import android.content.Context;
import android.widget.Toast;

/**
 * Central entry points for injecting Zoxgram features into Telegram UI.
 * Call these methods from the corresponding Telegram classes.
 * This class is intentionally free of Telegram-specific imports so the module compiles standalone.
 */
public class ZoxgramInjector {

    private static boolean initialized = false;

    public static void init(Context context) {
        if (initialized) return;
        if (context != null) {
            ZoxgramConfig.init(context);
        }
        initialized = true;
    }

    /**
     * Call from SettingsActivity to open Zoxgram Settings.
     */
    public static void openSettings(Context context) {
        init(context);
        ZoxgramSettingsActivity.open(context);
    }

    /**
     * Call from ProfileActivity to show "Set Note" dialog.
     */
    public static void showSetNoteDialog(Context context, long userId) {
        init(context);
        Toast.makeText(context, "Set Note for user " + userId + " (UI coming)", Toast.LENGTH_SHORT).show();
    }

    /**
     * Call from ProfileActivity to show "Get Registration Date".
     */
    public static void requestRegDate(Context context, long userId, RegDateHelper.RegDateCallback callback) {
        init(context);
        RegDateHelper.requestRegDate(context, userId, callback);
    }

    /**
     * Call from ChatMessageCell / ChatActivity on long-press of voice message.
     */
    public static void downloadVoice(Context context, java.io.File file, String name) {
        init(context);
        VoiceFeaturesHelper.downloadVoiceMessage(context, file, name);
    }

    /**
     * Call when user taps the →A button on a voice message.
     */
    public static void speech2Text(Context context, java.io.File file, VoiceFeaturesHelper.Speech2TextCallback callback) {
        init(context);
        VoiceFeaturesHelper.requestSpeechToText(context, file, callback);
    }

    /**
     * Call from attachment menu for "Send as Rounded Video".
     */
    public static boolean sendAsCircleVideo(Context context, java.io.File video) {
        init(context);
        return CircleVideoHelper.startCircleConversion(context, video);
    }

    /**
     * Apply Zoxgram branding title where "Telegram" appears in UI.
     */
    public static String brand(String original) {
        if (original == null) return null;
        return original.replace("Telegram", "Zoxgram").replace("telegram", "zoxgram");
    }
}
