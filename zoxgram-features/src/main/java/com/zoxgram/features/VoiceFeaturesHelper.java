package com.zoxgram.features;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public final class VoiceFeaturesHelper {

    public interface Speech2TextCallback {
        void onResult(String text);
        void onError(String message);
    }

    private VoiceFeaturesHelper() {}

    public static boolean downloadVoiceMessage(Context context, File voiceFile, String fileName) {
        ZoxgramConfig.init(context);
        if (!ZoxgramConfig.isVoiceDownloadEnabled()) {
            Toast.makeText(context, "Voice download disabled in Zoxgram Settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (voiceFile == null || !voiceFile.exists()) {
            Toast.makeText(context, "Voice file not found", Toast.LENGTH_SHORT).show();
            return false;
        }
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Zoxgram");
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(dir, fileName != null ? fileName : ("voice_" + System.currentTimeMillis() + ".ogg"));
            try (FileInputStream in = new FileInputStream(voiceFile); FileOutputStream out = new FileOutputStream(dest)) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
            }
            MediaScannerConnection.scanFile(context, new String[]{dest.getAbsolutePath()}, null, null);
            Toast.makeText(context, "Saved: " + dest.getName(), Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Save failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void requestSpeechToText(Context context, File voiceFile, Speech2TextCallback callback) {
        ZoxgramConfig.init(context);
        if (!ZoxgramConfig.isSpeech2TextEnabled()) {
            if (callback != null) callback.onError("Speech2Text disabled");
            return;
        }
        if (voiceFile == null || !voiceFile.exists()) {
            if (callback != null) callback.onError("Voice file not found");
            return;
        }
        Handler main = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                Thread.sleep(600);
                // Placeholder until on-device STT is wired; keeps UI flow working
                String demo = "[Zoxgram Speech2Text] Transcription ready. Connect STT engine for live text.";
                main.post(() -> {
                    if (callback != null) callback.onResult(demo);
                });
            } catch (Exception e) {
                main.post(() -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
            }
        }).start();
    }
}
