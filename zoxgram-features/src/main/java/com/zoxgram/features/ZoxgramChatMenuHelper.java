package com.zoxgram.features;

/**
 * Part 7 — Chat menu option IDs / labels for ChatActivity injection.
 * Keep pure (no Telegram UI imports) so features module compiles.
 */
public final class ZoxgramChatMenuHelper {

    public static final int OPTION_ZOX_SUBMENU = 9100;
    public static final int OPTION_VIEW_DELETED = 9101;
    public static final int OPTION_TO_BEGINNING = 9102;

    public static final String LABEL_SUBMENU = "Zoxgram";
    public static final String LABEL_VIEW_DELETED = "View Deleted";
    public static final String LABEL_TO_BEGINNING = "To Beginning";

    private ZoxgramChatMenuHelper() {}
}
