package com.zoxgram.features;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Voice message helpers:
 * 1. Download / Save voice message to device storage
 * 2. Speech2Text (transcription) – placeholder for real STT engine
 *
 * UI integration points (to be wired in ChatActivity):
 * - Long press on voice message → show "Download" button
 * - Tap "→A" button on voice message → show transcription
 */
public class VoiceFeaturesHelper {

    public static boolean downloadVoiceMessage(Context context, File voiceFile, String fileName) {
        if (!ZoxgramConfig.isVoiceDownloadEnabled()) {
            Toast.makeText(context, "Voice download is disabled in Zoxgram Settings", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (voiceFile == null || !voiceFile.exists()) {
            Toast.makeText(context, "Voice file not found", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "Zoxgram");
            if (!dir.exists()) dir.mkdirs();

            File dest = new File(dir, fileName != null ? fileName : "voice_" + System.currentTimeMillis() + ".ogg");
            copyFile(voiceFile, dest);

            MediaScannerConnection.scanFile(context, new String[]{dest.getAbsolutePath()}, null, null);
            Toast.makeText(context, "Saved to " + dest.getAbsolutePath(), Toast.LENGTH_LONG).show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to save voice message", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public static void requestSpeechToText(Context context, File voiceFile, Speech2TextCallback callback) {
        if (!ZoxgramConfig.isSpeech2TextEnabled()) {
            if (callback != null) callback.onError("Speech2Text is disabled in Zoxgram Settings");
            return;
        }
        // Placeholder: real implementation will use on-device STT or a privacy-friendly service
        // For now return a demo transcription so UI flow can be tested
        new Thread(() -> {
            try {
                Thread.sleep(800); // simulate processing
                String demo = "[Zoxgram Speech2Text]\nThis is a demo transcription. Replace with real STT engine.";
                if (callback != null) {
                    android.os.Handler h = new android.os.Handler(context.getMainLooper());
                    h.post(() -> callback.onResult(demo));
                }
            } catch (InterruptedException e) {
                if (callback != null) callback.onError("Cancelled");
            }
        }).start();
    }

    private static void copyFile(File src, File dst) throws IOException {
        try (FileInputStream in = new FileInputStream(src);
             FileOutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        }
    }

    public interface Speech2TextCallback {
        void onResult(String text);
        void onError(String message);
    }
}
