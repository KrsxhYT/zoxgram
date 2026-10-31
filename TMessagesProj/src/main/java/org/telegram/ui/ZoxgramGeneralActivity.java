package org.telegram.ui;

import android.content.SharedPreferences;
import android.view.View;

import com.zoxgram.features.ZoxgramConfig;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/** Part 3 — General. Dual-writes profile flags to MainSettings for ProfileActivity. */
public class ZoxgramGeneralActivity extends UniversalFragment {

    private static final int ROW_SHOW_TRANSLATE = 1;
    private static final int ROW_TRANSLATE_PROVIDER = 2;
    private static final int ROW_DO_NOT_TRANSLATE = 3;
    private static final int ROW_DISABLE_ROUNDING = 10;
    private static final int ROW_TIME_SECONDS = 11;
    private static final int ROW_VIBRATION = 12;
    private static final int ROW_ZALGO = 13;
    private static final int ROW_DL_BOOST = 20;
    private static final int ROW_UL_BOOST = 21;
    private static final int ROW_SHOW_CHAT_ID = 30;
    private static final int ROW_SHOW_REG_DATE = 31;
    private static final int ROW_SHOW_NOTES = 32;
    private static final int ROW_HIDE_PHONE = 33;
    private static final int ROW_RELATIVE_LAST_SEEN = 34;

    private static final String[] PROVIDERS = {"Telegram", "Google", "Yandex"};
    private static final String[] BOOSTS = {"Normal", "Fast", "Ultra"};
    private static final String[] LANGS = {"en", "hi", "ru", "es", "ar", "zh", "fr", "de"};

    @Override
    protected CharSequence getTitle() {
        return "General";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);
        SharedPreferences main = getMessagesController().getMainSettings();

        items.add(UItem.asHeader("Translate Messages"));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_TRANSLATE, "Show Translate Button")
                .setChecked(ZoxgramConfig.isShowTranslateButton()));
        items.add(UItem.asButton(ROW_TRANSLATE_PROVIDER, "Translation Provider",
                PROVIDERS[clamp(ZoxgramConfig.getTranslateProvider(), PROVIDERS.length)]));
        items.add(UItem.asButton(ROW_DO_NOT_TRANSLATE, "Do Not Translate",
                ZoxgramConfig.getDoNotTranslateLang()));
        items.add(UItem.asShadow("The Translate button appears when you single-tap a text message."));

        items.add(UItem.asHeader("General"));
        items.add(UItem.asSwitchNoIcon(ROW_DISABLE_ROUNDING, "Disable Number Rounding")
                .setChecked(ZoxgramConfig.isDisableNumberRounding()));
        items.add(UItem.asShadow("1.23K → 1,234"));
        items.add(UItem.asSwitchNoIcon(ROW_TIME_SECONDS, "Format Time with Seconds")
                .setChecked(ZoxgramConfig.isFormatTimeSeconds()));
        items.add(UItem.asShadow("12:34 → 12:34:56"));
        items.add(UItem.asSwitchNoIcon(ROW_VIBRATION, "In-App Vibration")
                .setChecked(ZoxgramConfig.isInAppVibration()));
        items.add(UItem.asSwitchNoIcon(ROW_ZALGO, "Filter Zalgo")
                .setChecked(ZoxgramConfig.isFilterZalgo()));
        items.add(UItem.asShadow("Removes Zalgo symbols in names and messages."));

        items.add(UItem.asHeader("Download Speed Boost"));
        items.add(UItem.asButton(ROW_DL_BOOST, "Download Speed",
                BOOSTS[clamp(ZoxgramConfig.getDownloadSpeedBoost(), BOOSTS.length)]));
        items.add(UItem.asSwitchNoIcon(ROW_UL_BOOST, "Upload Speed Boost")
                .setChecked(ZoxgramConfig.isUploadSpeedBoost()));
        items.add(UItem.asShadow("Ultra Boost can cause problems on slow connections."));

        items.add(UItem.asHeader("Profile"));
        boolean showId = main.getBoolean(ZoxgramSettingsActivity.KEY_SHOW_CHAT_ID, ZoxgramConfig.isShowChatId());
        boolean showReg = main.getBoolean(ZoxgramSettingsActivity.KEY_SHOW_REGISTRATION_DATE, ZoxgramConfig.isShowRegDate());
        boolean showNotes = main.getBoolean(ZoxgramSettingsActivity.KEY_SHOW_CONTACT_NOTES, ZoxgramConfig.isShowNotes());
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CHAT_ID, "Show Chat ID").setChecked(showId));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_REG_DATE, "Show Registration Date").setChecked(showReg));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_NOTES, "Show Profile Notes").setChecked(showNotes));
        items.add(UItem.asSwitchNoIcon(ROW_HIDE_PHONE, "Hide Phone Number")
                .setChecked(ZoxgramConfig.isHidePhoneInProfile()));
        items.add(UItem.asShadow("Hides only your phone number on your profile."));
        items.add(UItem.asSwitchNoIcon(ROW_RELATIVE_LAST_SEEN, "Relative Last Seen")
                .setChecked(ZoxgramConfig.isRelativeLastSeen()));
        items.add(UItem.asShadow("Shows last seen like: last seen at 10:54 PM"));
    }

    private static int clamp(int v, int len) {
        if (v < 0) return 0;
        if (v >= len) return len - 1;
        return v;
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_TRANSLATE_PROVIDER) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Translation Provider")
                    .setItems(PROVIDERS, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_TRANSLATE_PROVIDER, which);
                        refresh();
                    }).show();
            return;
        }
        if (item.id == ROW_DO_NOT_TRANSLATE) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Do Not Translate")
                    .setItems(LANGS, (d, which) -> {
                        ZoxgramConfig.setString(ZoxgramConfig.KEY_DO_NOT_TRANSLATE_LANG, LANGS[which]);
                        refresh();
                    }).show();
            return;
        }
        if (item.id == ROW_DL_BOOST) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Download Speed Boost")
                    .setItems(BOOSTS, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_DOWNLOAD_SPEED_BOOST, which);
                        refresh();
                    }).show();
            return;
        }

        // Profile flags dual-write to MainSettings (ProfileActivity reads these)
        if (item.id == ROW_SHOW_CHAT_ID || item.id == ROW_SHOW_REG_DATE || item.id == ROW_SHOW_NOTES) {
            SharedPreferences main = getMessagesController().getMainSettings();
            String mainKey;
            String configKey;
            boolean def = true;
            if (item.id == ROW_SHOW_CHAT_ID) {
                mainKey = ZoxgramSettingsActivity.KEY_SHOW_CHAT_ID;
                configKey = ZoxgramConfig.KEY_SHOW_CHAT_ID;
            } else if (item.id == ROW_SHOW_REG_DATE) {
                mainKey = ZoxgramSettingsActivity.KEY_SHOW_REGISTRATION_DATE;
                configKey = ZoxgramConfig.KEY_SHOW_REG_DATE;
            } else {
                mainKey = ZoxgramSettingsActivity.KEY_SHOW_CONTACT_NOTES;
                configKey = ZoxgramConfig.KEY_SHOW_NOTES;
            }
            boolean next = !main.getBoolean(mainKey, def);
            main.edit().putBoolean(mainKey, next).apply();
            ZoxgramConfig.set(configKey, next);
            if (view instanceof TextCheckCell2) {
                ((TextCheckCell2) view).setChecked(next);
            }
            return;
        }

        String key;
        boolean def;
        switch (item.id) {
            case ROW_SHOW_TRANSLATE: key = ZoxgramConfig.KEY_SHOW_TRANSLATE_BUTTON; def = true; break;
            case ROW_DISABLE_ROUNDING: key = ZoxgramConfig.KEY_DISABLE_NUMBER_ROUNDING; def = false; break;
            case ROW_TIME_SECONDS: key = ZoxgramConfig.KEY_FORMAT_TIME_SECONDS; def = true; break;
            case ROW_VIBRATION: key = ZoxgramConfig.KEY_IN_APP_VIBRATION; def = true; break;
            case ROW_ZALGO: key = ZoxgramConfig.KEY_FILTER_ZALGO; def = false; break;
            case ROW_UL_BOOST: key = ZoxgramConfig.KEY_UPLOAD_SPEED_BOOST; def = false; break;
            case ROW_HIDE_PHONE: key = ZoxgramConfig.KEY_HIDE_PHONE; def = true; break;
            case ROW_RELATIVE_LAST_SEEN: key = ZoxgramConfig.KEY_RELATIVE_LAST_SEEN; def = false; break;
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
