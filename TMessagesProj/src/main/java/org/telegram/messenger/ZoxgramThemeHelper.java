package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.telegram.ui.ActionBar.Theme;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Nicegram-style theme force apply.
 * Copies zoxgram_red.attheme from assets and applies via Theme.applyThemeFile,
 * then writes theme + nighttheme prefs so it sticks across restarts.
 */
public final class ZoxgramThemeHelper {

    public static final String THEME_ASSET = "zoxgram_red.attheme";
    public static final String THEME_NAME = "Zoxgram Red";
    private static final String PREF_FORCED = "zoxgram_theme_forced_v2";

    private ZoxgramThemeHelper() {}

    /** Call once after app init (UI thread preferred). */
    public static void forceApplyRedThemeIfNeeded() {
        try {
            Context ctx = ApplicationLoader.applicationContext;
            if (ctx == null) return;

            SharedPreferences global = MessagesController.getGlobalMainSettings();
            // Always ensure prefs point at Zoxgram Red on first installs / after update
            boolean alreadyForced = global.getBoolean(PREF_FORCED, false);

            File themeFile = copyThemeFromAssets(ctx);
            if (themeFile == null || !themeFile.exists()) {
                // Fallback: built-in asset theme registered in Theme.java
                Theme.ThemeInfo info = Theme.getTheme("Zoxgram Red");
                if (info == null) {
                    // try by iterating is not public; use apply from themesDict via name
                    applyPrefsOnly("Zoxgram Red");
                } else {
                    Theme.applyTheme(info, false);
                    Theme.applyTheme(info, true);
                    applyPrefsOnly(info.getKey());
                }
                global.edit().putBoolean(PREF_FORCED, true).apply();
                return;
            }

            Theme.ThemeInfo theme = Theme.applyThemeFile(themeFile, THEME_NAME, null, false);
            if (theme != null) {
                try {
                    Theme.setCurrentNightTheme(theme);
                } catch (Throwable ignore) {}
                applyPrefsOnly(theme.getKey());
                global.edit().putBoolean(PREF_FORCED, true).apply();
            } else if (!alreadyForced) {
                applyPrefsOnly(THEME_NAME);
                global.edit().putBoolean(PREF_FORCED, true).apply();
            }
        } catch (Throwable t) {
            try {
                FileLog.e(t);
            } catch (Throwable ignore) {}
        }
    }

    /** Force every cold start until user explicitly changes theme (optional hard force). */
    public static void forceApplyRedThemeAlways() {
        try {
            MessagesController.getGlobalMainSettings().edit().putBoolean(PREF_FORCED, false).apply();
        } catch (Throwable ignore) {}
        forceApplyRedThemeIfNeeded();
    }

    private static void applyPrefsOnly(String key) {
        if (key == null) key = THEME_NAME;
        try {
            SharedPreferences preferences = MessagesController.getGlobalMainSettings();
            preferences.edit()
                    .putString("theme", key)
                    .putString("nighttheme", key)
                    .apply();

            SharedPreferences themeConfig = ApplicationLoader.applicationContext
                    .getSharedPreferences("themeconfig", Activity.MODE_PRIVATE);
            themeConfig.edit()
                    .putString("lastDarkTheme", key)
                    .putString("lastDayTheme", key)
                    .apply();
        } catch (Throwable t) {
            try { FileLog.e(t); } catch (Throwable ignore) {}
        }
    }

    private static File copyThemeFromAssets(Context context) {
        try {
            File outFile = new File(ApplicationLoader.getFilesDirFixed(), THEME_ASSET);
            // Refresh copy so updates to asset are picked up
            try (InputStream in = context.getAssets().open(THEME_ASSET);
                 FileOutputStream out = new FileOutputStream(outFile)) {
                byte[] buf = new byte[4096];
                int n;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                }
            }
            return outFile;
        } catch (Throwable t) {
            try { FileLog.e(t); } catch (Throwable ignore) {}
            return null;
        }
    }
}
