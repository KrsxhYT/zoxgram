package org.telegram.ui;

import android.app.Activity;

import com.zoxgram.features.ZoxgramOfficialBadge;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

/** Part 8 — UI for official badge popup (TMessagesProj, has ActionBar deps). */
public final class ZoxgramOfficialUi {

    private ZoxgramOfficialUi() {}

    public static boolean isOfficial(String username) {
        return ZoxgramOfficialBadge.isOfficial(username);
    }

    public static void showPopup(Activity activity, String username, Theme.ResourcesProvider rp) {
        if (activity == null || !isOfficial(username)) return;
        new AlertDialog.Builder(activity, rp)
                .setTitle("Zoxgram Official")
                .setMessage(ZoxgramOfficialBadge.messageFor(username))
                .setPositiveButton("OK", null)
                .show();
    }
}
