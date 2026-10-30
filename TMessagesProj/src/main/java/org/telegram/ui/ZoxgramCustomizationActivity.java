package org.telegram.ui;

import android.view.View;

import com.zoxgram.features.ZoxgramConfig;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/** Part 2 — Customization (local premium, deleted msgs, ads). */
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

        items.add(UItem.asHeader("Useful features"));
        items.add(UItem.asSwitchNoIcon(ROW_LOCAL_PREMIUM, "Local Telegram Premium")
                .setChecked(ZoxgramConfig.isLocalPremium()));
        items.add(UItem.asShadow("Activates client-side premium features. It won't increase your limits on pin amount, caption length, etc."));

        items.add(UItem.asHeader("Deleted messages"));
        items.add(UItem.asSwitchNoIcon(ROW_TRANSLUCENT_DELETED, "Translucent Deleted Messages")
                .setChecked(ZoxgramConfig.isTranslucentDeleted()));
        items.add(UItem.asButton(ROW_DELETED_ICON, "Deleted Mark", ICON_LABELS[Math.min(3, Math.max(0, ZoxgramConfig.getDeletedIconStyle()))]));
        items.add(UItem.asButton(ROW_DELETED_COLOR, "Deleted icon colour", String.format("#%06X", (0xFFFFFF & ZoxgramConfig.getDeletedIconColor()))));
        items.add(UItem.asShadow("Icon under deleted messages. Customise style and colour."));

        items.add(UItem.asHeader("Ads"));
        items.add(UItem.asSwitchNoIcon(ROW_DISABLE_ADS, "Disable Ads")
                .setChecked(ZoxgramConfig.isDisableAds()));
        items.add(UItem.asShadow("Hides Telegram promotional posts in this client."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_DELETED_ICON) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Deleted Mark")
                    .setItems(ICON_LABELS, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_DELETED_ICON, which);
                        if (listView != null && listView.getAdapter() != null) {
                            listView.getAdapter().notifyDataSetChanged();
                        }
                        updateAdapter();
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
                    .setTitle("Deleted icon colour")
                    .setItems(labels, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_DELETED_ICON_COLOR, colors[which]);
                        updateAdapter();
                    })
                    .show();
            return;
        }

        String key;
        boolean def;
        switch (item.id) {
            case ROW_LOCAL_PREMIUM:
                key = ZoxgramConfig.KEY_LOCAL_PREMIUM;
                def = false;
                break;
            case ROW_TRANSLUCENT_DELETED:
                key = ZoxgramConfig.KEY_TRANSLUCENT_DELETED;
                def = true;
                break;
            case ROW_DISABLE_ADS:
                key = ZoxgramConfig.KEY_DISABLE_ADS;
                def = true;
                break;
            default:
                return;
        }
        boolean next = !ZoxgramConfig.get(key, def);
        ZoxgramConfig.set(key, next);
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).setChecked(next);
        } else {
            updateAdapter();
        }
    }

    private void updateAdapter() {
        if (listView != null && listView.getAdapter() instanceof UniversalAdapter) {
            ((UniversalAdapter) listView.getAdapter()).update(true);
        }
    }

    @Override
    public void onLongClick(UItem item, View view, int position, float x, float y) {}
}
