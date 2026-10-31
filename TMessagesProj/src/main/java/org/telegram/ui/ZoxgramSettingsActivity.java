package org.telegram.ui;

import android.view.View;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/**
 * Part 1 — Zoxgram Settings hub.
 * KEY_* constants kept for ProfileActivity / ContactAddActivity compatibility.
 */
public class ZoxgramSettingsActivity extends UniversalFragment {

    // Used by ProfileActivity rows — do not remove
    public static final String KEY_SHOW_CHAT_ID = "zoxgram_show_chat_id";
    public static final String KEY_SHOW_REGISTRATION_DATE = "zoxgram_show_registration_date";
    public static final String KEY_SHOW_CONTACT_NOTES = "zoxgram_show_contact_notes";
    public static final String KEY_VOICE_DOWNLOAD = "zoxgram_voice_download";
    public static final String KEY_SPEECH2TEXT = "zoxgram_speech2text";
    public static final String KEY_CIRCLE_VIDEO = "zoxgram_circle_video";
    public static final String KEY_GROUP_CALL_REC = "zoxgram_group_call_rec";

    private static final int ROW_GENERAL = 1;
    private static final int ROW_APPEARANCE = 2;
    private static final int ROW_CHATS = 3;
    private static final int ROW_CUSTOMIZATION = 4;
    private static final int ROW_MEDIA = 5;
    private static final int ROW_SESSION = 6;
    private static final int ROW_CHAT_EXTRAS = 7;

    @Override
    protected CharSequence getTitle() {
        return "Zoxgram";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Categories"));

        items.add(UItem.asButton(ROW_GENERAL, R.drawable.settings_features, "General",
                "Translate, profile, speed boost"));
        items.add(UItem.asButton(ROW_APPEARANCE, R.drawable.settings_chat, "Appearance",
                "Chat list, folders, snowflakes"));
        items.add(UItem.asButton(ROW_CHATS, R.drawable.settings_chat, "Chats",
                "Stickers, messages, camera, media"));
        items.add(UItem.asButton(ROW_CUSTOMIZATION, R.drawable.settings_premium, "Customization",
                "Local premium, deleted style, ads"));

        items.add(UItem.asShadow("More"));
        items.add(UItem.asButton(ROW_CHAT_EXTRAS, R.drawable.settings_privacy, "Chat extras",
                "View deleted, to beginning, official badge"));
        items.add(UItem.asButton(ROW_MEDIA, R.drawable.settings_data, "Media features",
                "Voice download, speech2text, circle video"));
        items.add(UItem.asButton(ROW_SESSION, R.drawable.settings_devices, "Session Backup",
                "Export / import local file"));

        items.add(UItem.asShadow("Zoxgram — local only, no Google Cloud."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        switch (item.id) {
            case ROW_GENERAL:
                presentFragment(new ZoxgramGeneralActivity());
                break;
            case ROW_CUSTOMIZATION:
                presentFragment(new ZoxgramCustomizationActivity());
                break;
            case ROW_APPEARANCE:
                presentFragment(new ZoxgramAppearanceActivity());
                break;
            case ROW_CHATS:
                presentFragment(new ZoxgramChatsActivity());
                break;
            case ROW_CHAT_EXTRAS:
                presentFragment(new ZoxgramChatExtrasActivity());
                break;
            case ROW_MEDIA:
                presentFragment(new ZoxgramMediaActivity());
                break;
            case ROW_SESSION:
                presentFragment(new ZoxgramSessionActivity());
                break;
        }
    }

    @Override
    public boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
