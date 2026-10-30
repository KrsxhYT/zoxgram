package org.telegram.ui;

import android.view.View;

import static org.telegram.messenger.LocaleController.getString;

import org.telegram.messenger.R;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/**
 * Part 1 — Zoxgram Settings hub (exteraGram-style categories).
 */
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

        items.add(UItem.asButton(ROW_GENERAL, R.drawable.msg_settings, "General",
                "Translate, profile, speed boost"));
        items.add(UItem.asButton(ROW_APPEARANCE, R.drawable.msg_theme, "Appearance",
                "Chat list, folders, snowflakes"));
        items.add(UItem.asButton(ROW_CHATS, R.drawable.msg_discuss, "Chats",
                "Stickers, messages, camera, media"));
        items.add(UItem.asButton(ROW_CUSTOMIZATION, R.drawable.msg_edit, "Customization",
                "Local premium, deleted style, ads"));

        items.add(UItem.asShadow("More"));
        items.add(UItem.asButton(ROW_MEDIA, R.drawable.msg_media, "Media features",
                "Voice download, speech2text, circle video"));
        items.add(UItem.asButton(ROW_SESSION, R.drawable.msg_download, "Session Backup",
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
                // Part 4
                presentFragment(new ZoxgramPlaceholderActivity("Appearance", "Part 4 — coming next"));
                break;
            case ROW_CHATS:
                // Part 5–6
                presentFragment(new ZoxgramPlaceholderActivity("Chats", "Part 5–6 — coming next"));
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
    public void onLongClick(UItem item, View view, int position, float x, float y) {}
}
