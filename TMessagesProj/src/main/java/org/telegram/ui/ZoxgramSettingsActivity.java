package org.telegram.ui;

import android.view.View;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/** Part 1 — Zoxgram Settings hub. */
public class ZoxgramSettingsActivity extends UniversalFragment {

    private static final int ROW_GENERAL = 1;
    private static final int ROW_APPEARANCE = 2;
    private static final int ROW_CHATS = 3;
    private static final int ROW_CUSTOMIZATION = 4;
    private static final int ROW_MEDIA = 5;
    private static final int ROW_SESSION = 6;

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
