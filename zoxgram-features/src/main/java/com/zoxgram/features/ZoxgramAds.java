package com.zoxgram.features;

import android.content.Context;

/** Disable Ads helper — used by MessagesController hook. */
public final class ZoxgramAds {
    private ZoxgramAds() {}

    public static boolean shouldBlockSponsored(Context context) {
        try {
            if (context != null) ZoxgramConfig.init(context);
            return ZoxgramConfig.isDisableAds();
        } catch (Throwable t) {
            return false;
        }
    }
}
