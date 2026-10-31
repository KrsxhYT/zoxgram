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

/**
 * Customization — Local Premium, deleted mark, disable ads (exteraGram/AyuGram-style).
 */
public class ZoxgramCustomizationActivity extends UniversalFragment {

    private static final int ROW_LOCAL_PREMIUM = 1;
    private static final int ROW_TRANSLUCENT_DELETED = 2;
    private static final int ROW_DELETED_ICON = 3;
    private static final int ROW_DELETED_COLOR = 4;
    private static final int ROW_DISABLE_ADS = 5;

    private static final String[] ICON_LABELS = {"Dustbin", "None", "Cross", "Eye crossed"};

    @Override
    protected CharSequence getTitle() {
        return "Customization";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        items.add(UItem.asHeader("Customization"));
        items.add(UItem.asShadow("Deleted messages preview uses the mark style below in supported cells."));

        items.add(UItem.asHeader("Deleted messages"));
        items.add(UItem.asSwitchNoIcon(ROW_TRANSLUCENT_DELETED, "Translucent Deleted Messages")
                .setChecked(ZoxgramConfig.isTranslucentDeleted()));
        int iconIdx = ZoxgramConfig.getDeletedIconStyle();
        if (iconIdx < 0 || iconIdx >= ICON_LABELS.length) iconIdx = 0;
        items.add(UItem.asButton(ROW_DELETED_ICON, "Deleted Mark", ICON_LABELS[iconIdx]));
        items.add(UItem.asButton(ROW_DELETED_COLOR, "Deleted Mark colour",
                String.format("#%06X", (0xFFFFFF & ZoxgramConfig.getDeletedIconColor()))));
        items.add(UItem.asShadow("Choose dustbin / cross / eye / none. Colour applies to the mark icon."));

        items.add(UItem.asHeader("Useful features"));
        items.add(UItem.asSwitchNoIcon(ROW_LOCAL_PREMIUM, "Local Telegram Premium")
                .setChecked(ZoxgramConfig.isLocalPremium()));
        items.add(UItem.asShadow("Activates client-side premium features (e.g. premium emoji UI). It won't increase your limits on pin amount, caption length, etc."));

        items.add(UItem.asSwitchNoIcon(ROW_DISABLE_ADS, "Disable Ads")
                .setChecked(ZoxgramConfig.isDisableAds()));
        items.add(UItem.asShadow("Hides sponsored / promotional posts inside Zoxgram."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_DELETED_ICON) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Deleted Mark")
                    .setItems(ICON_LABELS, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_DELETED_ICON, which);
                        refresh();
                    })
                    .show();
            return;
        }
        if (item.id == ROW_DELETED_COLOR) {
            final int[] colors = {
                    0xFFFF5252, 0xFFFF1744, 0xFFE91E63, 0xFF9C27B0,
                    0xFF673AB7, 0xFF3F51B5, 0xFF2196F3, 0xFF9E9E9E
            };
            String[] labels = {"Red", "Bright red", "Pink", "Purple", "Deep purple", "Indigo", "Blue", "Grey"};
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Deleted Mark colour")
                    .setItems(labels, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_DELETED_ICON_COLOR, colors[which]);
                        refresh();
                    })
                    .show();
            return;
        }

        String key;
        boolean def;
        switch (item.id) {
            case ROW_LOCAL_PREMIUM:
                key = ZoxgramConfig.KEY_LOCAL_PREMIUM; def = false; break;
            case ROW_TRANSLUCENT_DELETED:
                key = ZoxgramConfig.KEY_TRANSLUCENT_DELETED; def = true; break;
            case ROW_DISABLE_ADS:
                key = ZoxgramConfig.KEY_DISABLE_ADS; def = true; break;
            default:
                return;
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
