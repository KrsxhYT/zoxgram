package org.telegram.ui;

import android.view.View;

import com.zoxgram.features.ZoxgramConfig;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/** Part 4 — Appearance. */
public class ZoxgramAppearanceActivity extends UniversalFragment {

    private static final int ROW_SNOWFLAKES = 1;
    private static final int ROW_CENTER_TITLE = 2;
    private static final int ROW_HIDE_STORIES = 3;
    private static final int ROW_FOLDER_TITLE = 10;
    private static final int ROW_FOLDER_COUNTER = 11;
    private static final int ROW_HIDE_ALL_CHATS = 12;

    private static final String[] FOLDER_MODES = {"Names only", "Icons only", "Names and icons"};

    @Override
    protected CharSequence getTitle() {
        return "Appearance";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        items.add(UItem.asHeader("Chat List"));
        items.add(UItem.asSwitchNoIcon(ROW_SNOWFLAKES, "Force Snowflakes")
                .setChecked(ZoxgramConfig.isForceSnowflakes()));
        items.add(UItem.asShadow("Falling snowflakes in action bar, side menu, and chat backgrounds."));
        items.add(UItem.asSwitchNoIcon(ROW_CENTER_TITLE, "Center Title")
                .setChecked(ZoxgramConfig.isCenterTitle()));
        items.add(UItem.asShadow("Centers titles in the action bar."));
        items.add(UItem.asSwitchNoIcon(ROW_HIDE_STORIES, "Hide Stories")
                .setChecked(ZoxgramConfig.isHideStories()));
        items.add(UItem.asShadow("Hides stories from the home screen."));

        items.add(UItem.asHeader("Chat Folders"));
        int mode = ZoxgramConfig.getFolderTitleMode();
        if (mode < 0 || mode >= FOLDER_MODES.length) mode = 0;
        items.add(UItem.asButton(ROW_FOLDER_TITLE, "Folder Title", FOLDER_MODES[mode]));
        items.add(UItem.asSwitchNoIcon(ROW_FOLDER_COUNTER, "Notification Counter")
                .setChecked(ZoxgramConfig.isFolderNotificationCounter()));
        items.add(UItem.asSwitchNoIcon(ROW_HIDE_ALL_CHATS, "Hide \"All Chats\" Tab")
                .setChecked(ZoxgramConfig.isHideAllChatsTab()));
        items.add(UItem.asShadow("Folder tabs on the home screen. Wiring in a later part."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_FOLDER_TITLE) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Folder Title")
                    .setItems(FOLDER_MODES, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_FOLDER_TITLE_MODE, which);
                        refresh();
                    }).show();
            return;
        }

        String key;
        boolean def;
        switch (item.id) {
            case ROW_SNOWFLAKES: key = ZoxgramConfig.KEY_FORCE_SNOWFLAKES; def = true; break;
            case ROW_CENTER_TITLE: key = ZoxgramConfig.KEY_CENTER_TITLE; def = false; break;
            case ROW_HIDE_STORIES: key = ZoxgramConfig.KEY_HIDE_STORIES; def = false; break;
            case ROW_FOLDER_COUNTER: key = ZoxgramConfig.KEY_FOLDER_NOTIFICATION_COUNTER; def = true; break;
            case ROW_HIDE_ALL_CHATS: key = ZoxgramConfig.KEY_HIDE_ALL_CHATS_TAB; def = false; break;
            default: return;
        }
        boolean next = !ZoxgramConfig.get(key, def);
        ZoxgramConfig.set(key, next);
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).setChecked(next);
        } else {
            refresh();
        }
    }

    private void refresh() {
        if (listView != null && listView.adapter != null) {
            listView.adapter.update(true);
        }
    }

    @Override
    public boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
