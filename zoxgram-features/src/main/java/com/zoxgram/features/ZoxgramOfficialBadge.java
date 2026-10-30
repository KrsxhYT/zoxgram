package com.zoxgram.features;

import android.app.Activity;
import android.content.Context;

import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

/** Client-side verified badge for official Zoxgram accounts. */
public final class ZoxgramOfficialBadge {

    private ZoxgramOfficialBadge() {}

    public static boolean isOfficial(String username) {
        return ZoxgramConfig.isOfficialUsername(username);
    }

    public static void showOfficialPopup(Activity activity, String username, Theme.ResourcesProvider rp) {
        if (activity == null) return;
        String msg = ZoxgramConfig.officialBadgeMessage(username);
        new AlertDialog.Builder(activity, rp)
                .setTitle("Zoxgram Official")
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    public static void showOfficialPopup(Context context, String username) {
        if (!(context instanceof Activity)) return;
        showOfficialPopup((Activity) context, username, null);
    }
}
