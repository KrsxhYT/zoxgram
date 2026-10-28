package com.zoxgram.features;

import android.content.Context;
import android.content.SharedPreferences;

public final class ZoxgramConfig {

    private static final String PREFS = "zoxgram_config";

    public static final String KEY_SHOW_CHAT_ID = "show_chat_id";
    public static final String KEY_SHOW_NOTES = "show_notes";
    public static final String KEY_SHOW_REG_DATE = "show_reg_date";
    public static final String KEY_VOICE_DOWNLOAD = "voice_download";
    public static final String KEY_SPEECH2TEXT = "speech2text";
    public static final String KEY_CIRCLE_VIDEO = "circle_video";
    public static final String KEY_GROUP_CALL_RECORDER = "group_call_recorder";
    public static final String KEY_COPY_USERNAME = "copy_username_quick";
    public static final String KEY_HIDE_PHONE = "hide_phone_in_profile";

    private static SharedPreferences prefs;

    private ZoxgramConfig() {}

    public static void init(Context context) {
        if (prefs == null && context != null) {
            prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        }
    }

    public static boolean get(String key, boolean def) {
        return prefs != null ? prefs.getBoolean(key, def) : def;
    }

    public static void set(String key, boolean value) {
        if (prefs != null) prefs.edit().putBoolean(key, value).apply();
    }

    public static boolean isShowChatId() { return get(KEY_SHOW_CHAT_ID, true); }
    public static boolean isShowNotes() { return get(KEY_SHOW_NOTES, true); }
    public static boolean isShowRegDate() { return get(KEY_SHOW_REG_DATE, true); }
    public static boolean isVoiceDownloadEnabled() { return get(KEY_VOICE_DOWNLOAD, true); }
    public static boolean isSpeech2TextEnabled() { return get(KEY_SPEECH2TEXT, true); }
    public static boolean isCircleVideoEnabled() { return get(KEY_CIRCLE_VIDEO, true); }
    public static boolean isGroupCallRecorderEnabled() { return get(KEY_GROUP_CALL_RECORDER, true); }
    public static boolean isQuickCopyUsername() { return get(KEY_COPY_USERNAME, true); }
    public static boolean isHidePhoneInProfile() { return get(KEY_HIDE_PHONE, false); }
}
