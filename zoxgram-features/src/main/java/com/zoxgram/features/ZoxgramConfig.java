package com.zoxgram.features;

import android.content.Context;
import android.content.SharedPreferences;

public class ZoxgramConfig {

    private static final String PREFS_NAME = "zoxgram_config";

    public static final String KEY_SHOW_CHAT_ID = "show_chat_id";
    public static final String KEY_SHOW_NOTES = "show_notes";
    public static final String KEY_SHOW_REG_DATE = "show_reg_date";
    public static final String KEY_VOICE_DOWNLOAD = "voice_download";
    public static final String KEY_SPEECH2TEXT = "speech2text";
    public static final String KEY_CIRCLE_VIDEO = "circle_video";
    public static final String KEY_GROUP_CALL_RECORDER = "group_call_recorder";

    private static SharedPreferences prefs;

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        return prefs != null ? prefs.getBoolean(key, def) : def;
    }

    public static void setBoolean(String key, boolean value) {
        if (prefs != null) {
            prefs.edit().putBoolean(key, value).apply();
        }
    }

    public static boolean isShowChatId() {
        return getBoolean(KEY_SHOW_CHAT_ID, true);
    }

    public static boolean isShowNotes() {
        return getBoolean(KEY_SHOW_NOTES, true);
    }

    public static boolean isShowRegDate() {
        return getBoolean(KEY_SHOW_REG_DATE, true);
    }

    public static boolean isVoiceDownloadEnabled() {
        return getBoolean(KEY_VOICE_DOWNLOAD, true);
    }

    public static boolean isSpeech2TextEnabled() {
        return getBoolean(KEY_SPEECH2TEXT, true);
    }

    public static boolean isCircleVideoEnabled() {
        return getBoolean(KEY_CIRCLE_VIDEO, true);
    }

    public static boolean isGroupCallRecorderEnabled() {
        return getBoolean(KEY_GROUP_CALL_RECORDER, true);
    }
}
