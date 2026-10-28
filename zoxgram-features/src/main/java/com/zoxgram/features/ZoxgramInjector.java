package com.zoxgram.features;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

/** Central entry points for Zoxgram features. Safe to call from Telegram UI. */
public final class ZoxgramInjector {

    private static boolean initialized;

    private ZoxgramInjector() {}

    public static void init(Context context) {
        if (initialized) return;
        if (context != null) ZoxgramConfig.init(context);
        initialized = true;
    }

    public static void openSettings(Context context) {
        init(context);
        ZoxgramSettingsActivity.open(context);
    }

    public static void showSetNoteDialog(Context context, long userId) {
        init(context);
        Toast.makeText(context, "Open contact profile → Notes to edit", Toast.LENGTH_SHORT).show();
    }

    public static void requestRegDate(Context context, long userId, RegDateHelper.RegDateCallback callback) {
        init(context);
        RegDateHelper.requestRegDate(context, userId, callback);
    }

    public static void downloadVoice(Context context, File file, String name) {
        init(context);
        VoiceFeaturesHelper.downloadVoiceMessage(context, file, name);
    }

    public static void speech2Text(Context context, File file, VoiceFeaturesHelper.Speech2TextCallback callback) {
        init(context);
        VoiceFeaturesHelper.requestSpeechToText(context, file, callback);
    }

    public static boolean sendAsCircleVideo(Context context, File video) {
        init(context);
        return CircleVideoHelper.startCircleConversion(context, video);
    }

    public static String brand(String original) {
        if (original == null) return null;
        return original.replace("Telegram", "Zoxgram").replace("telegram", "zoxgram");
    }
}
