package com.zoxgram.features;

import android.content.Context;

/** Entry helper. Real UI is org.telegram.ui.ZoxgramSettingsActivity. */
public final class ZoxgramSettingsActivity {
    private ZoxgramSettingsActivity() {}
    public static void open(Context context) {
        if (context != null) ZoxgramConfig.init(context);
    }
}
