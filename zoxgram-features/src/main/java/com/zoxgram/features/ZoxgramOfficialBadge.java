package com.zoxgram.features;

/** Client-side official badge helpers (no UI deps — safe for features module). */
public final class ZoxgramOfficialBadge {

    private ZoxgramOfficialBadge() {}

    public static boolean isOfficial(String username) {
        return ZoxgramConfig.isOfficialUsername(username);
    }

    public static String messageFor(String username) {
        return ZoxgramConfig.officialBadgeMessage(username);
    }
}
