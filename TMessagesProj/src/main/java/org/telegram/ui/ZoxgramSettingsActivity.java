package org.telegram.ui;

import android.content.SharedPreferences;
import android.view.View;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class ZoxgramSettingsActivity extends UniversalFragment {

    public static final String KEY_SHOW_CHAT_ID = "zoxgram_show_chat_id";
    public static final String KEY_SHOW_REGISTRATION_DATE = "zoxgram_show_registration_date";
    public static final String KEY_SHOW_CONTACT_NOTES = "zoxgram_show_contact_notes";

    private static final int ROW_SHOW_CHAT_ID = 1;
    private static final int ROW_SHOW_REGISTRATION_DATE = 2;
    private static final int ROW_SHOW_CONTACT_NOTES = 3;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.ZoxgramSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        SharedPreferences preferences = getMessagesController().getMainSettings();
        items.add(UItem.asHeader(getString(R.string.ZoxgramProfileDetails)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CHAT_ID, getString(R.string.ZoxgramShowChatId))
                .setChecked(preferences.getBoolean(KEY_SHOW_CHAT_ID, true)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_REGISTRATION_DATE, getString(R.string.ZoxgramShowRegistrationDate))
                .setChecked(preferences.getBoolean(KEY_SHOW_REGISTRATION_DATE, true)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CONTACT_NOTES, getString(R.string.ZoxgramShowContactNotes))
                .setChecked(preferences.getBoolean(KEY_SHOW_CONTACT_NOTES, true)));
        items.add(UItem.asShadow(getString(R.string.ZoxgramSettingsPrivacyInfo)));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        String preferenceKey;
        switch (item.id) {
            case ROW_SHOW_CHAT_ID:
                preferenceKey = KEY_SHOW_CHAT_ID;
                break;
            case ROW_SHOW_REGISTRATION_DATE:
                preferenceKey = KEY_SHOW_REGISTRATION_DATE;
                break;
            case ROW_SHOW_CONTACT_NOTES:
                preferenceKey = KEY_SHOW_CONTACT_NOTES;
                break;
            default:
                return;
        }

        SharedPreferences preferences = getMessagesController().getMainSettings();
        boolean checked = !preferences.getBoolean(preferenceKey, true);
        preferences.edit().putBoolean(preferenceKey, checked).apply();
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).getCheckBox().setChecked(checked, true);
        }
        NotificationCenter.getInstance(currentAccount).postNotificationName(
                NotificationCenter.updateInterfaces,
                MessagesController.UPDATE_MASK_ZOXGRAM_PROFILE
        );
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
