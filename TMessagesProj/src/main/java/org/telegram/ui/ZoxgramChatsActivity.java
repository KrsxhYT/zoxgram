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

/** Part 5 — Chats (stickers + messages). Part 6 camera linked from here. */
public class ZoxgramChatsActivity extends UniversalFragment {

    private static final int ROW_STICKER_SIZE = 1;
    private static final int ROW_HIDE_TIME_STICKER = 2;
    private static final int ROW_STICKER_SHAPE = 3;
    private static final int ROW_UNLIMITED_RECENT = 4;
    private static final int ROW_CHAT_SETTINGS = 5;

    private static final int ROW_REMOVE_TAIL = 10;
    private static final int ROW_EDITED_ICON = 11;
    private static final int ROW_ONLINE_INDICATOR = 12;
    private static final int ROW_RESULTS_BEFORE_VOTE = 13;

    private static final int ROW_CAMERA = 20;

    private static final String[] SHAPES = {"Default", "Rounded", "Message"};
    private static final String[] SIZES = {"8", "10", "12", "14", "16", "18", "20"};

    @Override
    protected CharSequence getTitle() {
        return "Chats";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        items.add(UItem.asHeader("Stickers"));
        items.add(UItem.asButton(ROW_STICKER_SIZE, "Sticker Size", String.valueOf(ZoxgramConfig.getStickerSize())));
        items.add(UItem.asSwitchNoIcon(ROW_HIDE_TIME_STICKER, "Hide Time on Stickers")
                .setChecked(ZoxgramConfig.isHideTimeOnStickers()));
        int shape = ZoxgramConfig.getStickerShape();
        if (shape < 0 || shape >= SHAPES.length) shape = 1;
        items.add(UItem.asButton(ROW_STICKER_SHAPE, "Sticker Shape", SHAPES[shape]));
        items.add(UItem.asSwitchNoIcon(ROW_UNLIMITED_RECENT, "Unlimited Recent Stickers")
                .setChecked(ZoxgramConfig.isUnlimitedRecentStickers()));
        items.add(UItem.asButton(ROW_CHAT_SETTINGS, "Chat Settings", "Open Telegram chat settings"));

        items.add(UItem.asHeader("Messages"));
        items.add(UItem.asSwitchNoIcon(ROW_REMOVE_TAIL, "Remove Message Tail")
                .setChecked(ZoxgramConfig.isRemoveMessageTail()));
        items.add(UItem.asSwitchNoIcon(ROW_EDITED_ICON, "Replace \"edited\" with an Icon")
                .setChecked(ZoxgramConfig.isEditedAsIcon()));
        items.add(UItem.asSwitchNoIcon(ROW_ONLINE_INDICATOR, "Show Online Indicator")
                .setChecked(ZoxgramConfig.isShowOnlineIndicator()));
        items.add(UItem.asSwitchNoIcon(ROW_RESULTS_BEFORE_VOTE, "Show Results Before Voting")
                .setChecked(ZoxgramConfig.isShowResultsBeforeVoting()));
        items.add(UItem.asShadow("Visual message tweaks. Full UI wire in a later part."));

        items.add(UItem.asHeader("Camera & Media"));
        items.add(UItem.asButton(ROW_CAMERA, "Camera, Photos, Videos", "Part 6 settings"));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_CAMERA) {
            presentFragment(new ZoxgramCameraActivity());
            return;
        }
        if (item.id == ROW_CHAT_SETTINGS) {
            presentFragment(new ChatActivityEnterView.ChatActivityEnterViewDelegatePlaceholder() == null
                    ? new NotificationsSettingsActivity()
                    : new NotificationsSettingsActivity());
            // Open standard chat settings if available
            try {
                presentFragment(new ThemeActivity(ThemeActivity.THEME_TYPE_BASIC));
            } catch (Throwable ignore) {
                presentFragment(new NotificationsSettingsActivity());
            }
            return;
        }
        if (item.id == ROW_STICKER_SIZE) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Sticker Size")
                    .setItems(SIZES, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_STICKER_SIZE, Integer.parseInt(SIZES[which]));
                        refresh();
                    }).show();
            return;
        }
        if (item.id == ROW_STICKER_SHAPE) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Sticker Shape")
                    .setItems(SHAPES, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_STICKER_SHAPE, which);
                        refresh();
                    }).show();
            return;
        }

        String key;
        boolean def;
        switch (item.id) {
            case ROW_HIDE_TIME_STICKER: key = ZoxgramConfig.KEY_HIDE_TIME_ON_STICKERS; def = true; break;
            case ROW_UNLIMITED_RECENT: key = ZoxgramConfig.KEY_UNLIMITED_RECENT_STICKERS; def = false; break;
            case ROW_REMOVE_TAIL: key = ZoxgramConfig.KEY_REMOVE_MESSAGE_TAIL; def = true; break;
            case ROW_EDITED_ICON: key = ZoxgramConfig.KEY_EDITED_AS_ICON; def = true; break;
            case ROW_ONLINE_INDICATOR: key = ZoxgramConfig.KEY_SHOW_ONLINE_INDICATOR; def = false; break;
            case ROW_RESULTS_BEFORE_VOTE: key = ZoxgramConfig.KEY_SHOW_RESULTS_BEFORE_VOTING; def = false; break;
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
