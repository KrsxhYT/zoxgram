package com.zoxgram.features;

import android.content.Context;
import android.content.SharedPreferences;

/** Central prefs for Zoxgram (exteraGram/AyuGram-style options). */
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

    public static final String KEY_LOCAL_PREMIUM = "local_telegram_premium";
    public static final String KEY_TRANSLUCENT_DELETED = "translucent_deleted_messages";
    public static final String KEY_DELETED_ICON = "deleted_icon_style";
    public static final String KEY_DELETED_ICON_COLOR = "deleted_icon_color";
    public static final String KEY_DISABLE_ADS = "disable_ads";

    public static final String KEY_SHOW_TRANSLATE_BUTTON = "show_translate_button";
    public static final String KEY_TRANSLATE_PROVIDER = "translate_provider";
    public static final String KEY_DO_NOT_TRANSLATE_LANG = "do_not_translate_lang";
    public static final String KEY_DISABLE_NUMBER_ROUNDING = "disable_number_rounding";
    public static final String KEY_FORMAT_TIME_SECONDS = "format_time_seconds";
    public static final String KEY_IN_APP_VIBRATION = "in_app_vibration";
    public static final String KEY_FILTER_ZALGO = "filter_zalgo";
    public static final String KEY_DOWNLOAD_SPEED_BOOST = "download_speed_boost";
    public static final String KEY_UPLOAD_SPEED_BOOST = "upload_speed_boost";
    public static final String KEY_RELATIVE_LAST_SEEN = "relative_last_seen";

    public static final String KEY_FORCE_SNOWFLAKES = "force_snowflakes";
    public static final String KEY_CENTER_TITLE = "center_title";
    public static final String KEY_HIDE_STORIES = "hide_stories";
    public static final String KEY_FOLDER_TITLE_MODE = "folder_title_mode";
    public static final String KEY_FOLDER_NOTIFICATION_COUNTER = "folder_notification_counter";
    public static final String KEY_HIDE_ALL_CHATS_TAB = "hide_all_chats_tab";

    public static final String KEY_STICKER_SIZE = "sticker_size";
    public static final String KEY_HIDE_TIME_ON_STICKERS = "hide_time_on_stickers";
    public static final String KEY_STICKER_SHAPE = "sticker_shape";
    public static final String KEY_UNLIMITED_RECENT_STICKERS = "unlimited_recent_stickers";
    public static final String KEY_REMOVE_MESSAGE_TAIL = "remove_message_tail";
    public static final String KEY_EDITED_AS_ICON = "edited_as_icon";
    public static final String KEY_SHOW_ONLINE_INDICATOR = "show_online_indicator";
    public static final String KEY_SHOW_RESULTS_BEFORE_VOTING = "show_results_before_voting";

    public static final String KEY_CAMERA_TYPE = "camera_type";
    public static final String KEY_CAM_SEAMLESS = "cam_seamless";
    public static final String KEY_CAM_EXTENDED_FPS = "cam_extended_fps";
    public static final String KEY_CAM_STABILIZATION = "cam_stabilization";
    public static final String KEY_CAM_MIRROR = "cam_mirror";
    public static final String KEY_CAM_VIDEO_MSG = "cam_video_msg";
    public static final String KEY_CAM_REMEMBER_LAST = "cam_remember_last";
    public static final String KEY_CAM_STATIC_ZOOM = "cam_static_zoom";
    public static final String KEY_ALWAYS_SEND_HD = "always_send_hd";
    public static final String KEY_HIDE_PHOTO_COUNTER = "hide_photo_counter";
    public static final String KEY_HIDE_INSTANT_CAMERA_TILE = "hide_instant_camera_tile";
    public static final String KEY_DOUBLE_TAP_SKIP = "double_tap_skip_sec";
    public static final String KEY_PREFER_ORIGINAL_QUALITY = "prefer_original_quality";
    public static final String KEY_SWIPE_TO_PIP = "swipe_to_pip";
    public static final String KEY_UNMUTE_WITH_VOLUME = "unmute_with_volume";
    public static final String KEY_AUTO_PAUSE_VIDEO = "auto_pause_video";
    public static final String KEY_AUTO_PAUSE_VOICE = "auto_pause_voice";
    public static final String KEY_AUTO_PAUSE_ROUND = "auto_pause_round";

    public static final String[] OFFICIAL_USERNAMES = new String[]{"Krsxh", "OfficialZoxgram", "ZoxgramChat"};

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

    public static int getInt(String key, int def) {
        return prefs != null ? prefs.getInt(key, def) : def;
    }

    public static void setInt(String key, int value) {
        if (prefs != null) prefs.edit().putInt(key, value).apply();
    }

    public static String getString(String key, String def) {
        return prefs != null ? prefs.getString(key, def) : def;
    }

    public static void setString(String key, String value) {
        if (prefs != null) prefs.edit().putString(key, value).apply();
    }

    public static boolean isShowChatId() { return get(KEY_SHOW_CHAT_ID, true); }
    public static boolean isShowNotes() { return get(KEY_SHOW_NOTES, true); }
    public static boolean isShowRegDate() { return get(KEY_SHOW_REG_DATE, true); }
    public static boolean isVoiceDownloadEnabled() { return get(KEY_VOICE_DOWNLOAD, true); }
    public static boolean isSpeech2TextEnabled() { return get(KEY_SPEECH2TEXT, true); }
    public static boolean isCircleVideoEnabled() { return get(KEY_CIRCLE_VIDEO, true); }
    public static boolean isGroupCallRecorderEnabled() { return get(KEY_GROUP_CALL_RECORDER, true); }
    public static boolean isQuickCopyUsername() { return get(KEY_COPY_USERNAME, true); }
    public static boolean isHidePhoneInProfile() { return get(KEY_HIDE_PHONE, true); }

    public static boolean isLocalPremium() { return get(KEY_LOCAL_PREMIUM, false); }
    public static boolean isTranslucentDeleted() { return get(KEY_TRANSLUCENT_DELETED, true); }
    public static int getDeletedIconStyle() { return getInt(KEY_DELETED_ICON, 0); }
    public static int getDeletedIconColor() { return getInt(KEY_DELETED_ICON_COLOR, 0xFFFF5252); }
    public static boolean isDisableAds() { return get(KEY_DISABLE_ADS, true); }

    public static boolean isShowTranslateButton() { return get(KEY_SHOW_TRANSLATE_BUTTON, true); }
    public static int getTranslateProvider() { return getInt(KEY_TRANSLATE_PROVIDER, 0); }
    public static String getDoNotTranslateLang() { return getString(KEY_DO_NOT_TRANSLATE_LANG, "en"); }
    public static boolean isDisableNumberRounding() { return get(KEY_DISABLE_NUMBER_ROUNDING, false); }
    public static boolean isFormatTimeSeconds() { return get(KEY_FORMAT_TIME_SECONDS, true); }
    public static boolean isInAppVibration() { return get(KEY_IN_APP_VIBRATION, true); }
    public static boolean isFilterZalgo() { return get(KEY_FILTER_ZALGO, false); }
    public static int getDownloadSpeedBoost() { return getInt(KEY_DOWNLOAD_SPEED_BOOST, 0); }
    public static boolean isUploadSpeedBoost() { return get(KEY_UPLOAD_SPEED_BOOST, false); }
    public static boolean isRelativeLastSeen() { return get(KEY_RELATIVE_LAST_SEEN, false); }

    public static boolean isForceSnowflakes() { return get(KEY_FORCE_SNOWFLAKES, true); }
    public static boolean isCenterTitle() { return get(KEY_CENTER_TITLE, false); }
    public static boolean isHideStories() { return get(KEY_HIDE_STORIES, false); }
    public static int getFolderTitleMode() { return getInt(KEY_FOLDER_TITLE_MODE, 0); }
    public static boolean isFolderNotificationCounter() { return get(KEY_FOLDER_NOTIFICATION_COUNTER, true); }
    public static boolean isHideAllChatsTab() { return get(KEY_HIDE_ALL_CHATS_TAB, false); }

    public static int getStickerSize() { return getInt(KEY_STICKER_SIZE, 12); }
    public static boolean isHideTimeOnStickers() { return get(KEY_HIDE_TIME_ON_STICKERS, true); }
    public static int getStickerShape() { return getInt(KEY_STICKER_SHAPE, 1); }
    public static boolean isUnlimitedRecentStickers() { return get(KEY_UNLIMITED_RECENT_STICKERS, false); }
    public static boolean isRemoveMessageTail() { return get(KEY_REMOVE_MESSAGE_TAIL, true); }
    public static boolean isEditedAsIcon() { return get(KEY_EDITED_AS_ICON, true); }
    public static boolean isShowOnlineIndicator() { return get(KEY_SHOW_ONLINE_INDICATOR, false); }
    public static boolean isShowResultsBeforeVoting() { return get(KEY_SHOW_RESULTS_BEFORE_VOTING, false); }

    public static int getCameraType() { return getInt(KEY_CAMERA_TYPE, 2); }
    public static boolean isCamSeamless() { return get(KEY_CAM_SEAMLESS, true); }
    public static boolean isCamExtendedFps() { return get(KEY_CAM_EXTENDED_FPS, false); }
    public static boolean isCamStabilization() { return get(KEY_CAM_STABILIZATION, false); }
    public static boolean isCamMirror() { return get(KEY_CAM_MIRROR, true); }
    public static int getCamVideoMsg() { return getInt(KEY_CAM_VIDEO_MSG, 0); }
    public static boolean isCamRememberLast() { return get(KEY_CAM_REMEMBER_LAST, false); }
    public static boolean isCamStaticZoom() { return get(KEY_CAM_STATIC_ZOOM, false); }
    public static boolean isAlwaysSendHd() { return get(KEY_ALWAYS_SEND_HD, true); }
    public static boolean isHidePhotoCounter() { return get(KEY_HIDE_PHOTO_COUNTER, true); }
    public static boolean isHideInstantCameraTile() { return get(KEY_HIDE_INSTANT_CAMERA_TILE, true); }
    public static int getDoubleTapSkip() { return getInt(KEY_DOUBLE_TAP_SKIP, 10); }
    public static boolean isPreferOriginalQuality() { return get(KEY_PREFER_ORIGINAL_QUALITY, false); }
    public static boolean isSwipeToPip() { return get(KEY_SWIPE_TO_PIP, false); }
    public static boolean isUnmuteWithVolume() { return get(KEY_UNMUTE_WITH_VOLUME, true); }
    public static boolean isAutoPauseVideo() { return get(KEY_AUTO_PAUSE_VIDEO, true); }
    public static boolean isAutoPauseVoice() { return get(KEY_AUTO_PAUSE_VOICE, false); }
    public static boolean isAutoPauseRound() { return get(KEY_AUTO_PAUSE_ROUND, false); }

    public static boolean isOfficialUsername(String username) {
        if (username == null || username.length() == 0) return false;
        String u = username.startsWith("@") ? username.substring(1) : username;
        for (String o : OFFICIAL_USERNAMES) {
            if (o.equalsIgnoreCase(u)) return true;
        }
        return false;
    }

    public static String officialBadgeMessage(String username) {
        if (username == null) return "Official Zoxgram";
        String u = username.startsWith("@") ? username.substring(1) : username;
        if ("Krsxh".equalsIgnoreCase(u)) return "This is the Owner of Zoxgram";
        if ("OfficialZoxgram".equalsIgnoreCase(u) || "ZoxgramChat".equalsIgnoreCase(u)) {
            return "This is an official Zoxgram channel";
        }
        return "Official Zoxgram";
    }
}
