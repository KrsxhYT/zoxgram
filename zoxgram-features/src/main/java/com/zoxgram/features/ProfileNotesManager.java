package com.zoxgram.features;

import android.content.Context;
import android.content.SharedPreferences;

/** Local-only profile notes (no cloud). */
public final class ProfileNotesManager {

    public static final int EXPIRY_1_DAY = 1;
    public static final int EXPIRY_5_DAYS = 5;
    public static final int EXPIRY_7_DAYS = 7;
    public static final int EXPIRY_NEVER = 0;

    private static final String PREFS = "zoxgram_profile_notes";

    public interface NoteCallback {
        void onSuccess(String text, int expiryDays, long createdAt);
        void onError(String message);
    }

    private final SharedPreferences prefs;

    public ProfileNotesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public void getNote(long userId, NoteCallback cb) {
        try {
            String text = prefs.getString("note_" + userId, null);
            int expiry = prefs.getInt("expiry_" + userId, EXPIRY_NEVER);
            long created = prefs.getLong("created_" + userId, 0);
            if (text != null && expiry > 0 && created > 0) {
                long ms = expiry * 24L * 60 * 60 * 1000;
                if (System.currentTimeMillis() - created > ms) {
                    deleteNote(userId);
                    if (cb != null) cb.onSuccess(null, EXPIRY_NEVER, 0);
                    return;
                }
            }
            if (cb != null) cb.onSuccess(text, expiry, created);
        } catch (Exception e) {
            if (cb != null) cb.onError(e.getMessage());
        }
    }

    public void saveNote(long userId, String text, int expiryDays, NoteCallback cb) {
        try {
            if (text == null || text.trim().isEmpty()) {
                deleteNote(userId);
                if (cb != null) cb.onSuccess(null, EXPIRY_NEVER, 0);
                return;
            }
            long now = System.currentTimeMillis();
            prefs.edit()
                    .putString("note_" + userId, text.trim())
                    .putInt("expiry_" + userId, expiryDays)
                    .putLong("created_" + userId, now)
                    .apply();
            if (cb != null) cb.onSuccess(text.trim(), expiryDays, now);
        } catch (Exception e) {
            if (cb != null) cb.onError(e.getMessage());
        }
    }

    public void deleteNote(long userId) {
        prefs.edit()
                .remove("note_" + userId)
                .remove("expiry_" + userId)
                .remove("created_" + userId)
                .apply();
    }
}
