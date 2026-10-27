package com.zoxgram.features;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Manages Profile Notes for contacts.
 * Notes are stored locally + optionally in Firestore under ZoxId.
 * Expiry: 1 day, 5 days, 7 days or never.
 */
public class ProfileNotesManager {

    public static final int EXPIRY_1_DAY = 1;
    public static final int EXPIRY_5_DAYS = 5;
    public static final int EXPIRY_7_DAYS = 7;
    public static final int EXPIRY_NEVER = 0;

    private static final String PREFS = "zoxgram_profile_notes";
    private static final String KEY_NOTE_PREFIX = "note_";
    private static final String KEY_EXPIRY_PREFIX = "expiry_";
    private static final String KEY_CREATED_PREFIX = "created_";

    private final SharedPreferences prefs;
    private final FirebaseFirestore db;

    public ProfileNotesManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        db = FirebaseFirestore.getInstance();
    }

    public void setNote(long userId, String note, int expiryDays) {
        long now = System.currentTimeMillis();
        long expiresAt = expiryDays == EXPIRY_NEVER ? 0 : now + TimeUnit.DAYS.toMillis(expiryDays);

        prefs.edit()
                .putString(KEY_NOTE_PREFIX + userId, note)
                .putLong(KEY_EXPIRY_PREFIX + userId, expiresAt)
                .putLong(KEY_CREATED_PREFIX + userId, now)
                .apply();

        // Also push to Firestore under ZoxId (document = userId as string for simplicity, or map via ZoxId)
        Map<String, Object> data = new HashMap<>();
        data.put("note", note);
        data.put("expiresAt", expiresAt);
        data.put("createdAt", now);
        data.put("userId", userId);

        db.collection("profile_notes")
                .document(String.valueOf(userId))
                .set(data, SetOptions.merge());
    }

    public String getNote(long userId) {
        long expiresAt = prefs.getLong(KEY_EXPIRY_PREFIX + userId, 0);
        if (expiresAt > 0 && System.currentTimeMillis() > expiresAt) {
            // expired
            clearNote(userId);
            return null;
        }
        return prefs.getString(KEY_NOTE_PREFIX + userId, null);
    }

    public void clearNote(long userId) {
        prefs.edit()
                .remove(KEY_NOTE_PREFIX + userId)
                .remove(KEY_EXPIRY_PREFIX + userId)
                .remove(KEY_CREATED_PREFIX + userId)
                .apply();

        db.collection("profile_notes").document(String.valueOf(userId)).delete();
    }

    public boolean hasNote(long userId) {
        String note = getNote(userId);
        return note != null && !note.trim().isEmpty();
    }
}
