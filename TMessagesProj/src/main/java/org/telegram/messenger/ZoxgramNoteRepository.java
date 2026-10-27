package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Fully local private notes. No Google sign-in required.
 * Notes are stored on-device only (SharedPreferences).
 */
public final class ZoxgramNoteRepository {

    public static final String EXPIRY_1_DAY = "1d";
    public static final String EXPIRY_5_DAYS = "5d";
    public static final String EXPIRY_7_DAYS = "7d";
    public static final String EXPIRY_NEVER = "never";

    public interface NoteCallback {
        void onSuccess(Note note);
        void onError(String message);
    }

    public interface SaveCallback {
        void onSuccess();
        void onError(String message);
    }

    public static final class Note {
        public final String text;
        public final String expiryOption;
        public final long expiresAtMillis;

        public Note(String text, String expiryOption, long expiresAtMillis) {
            this.text = text == null ? "" : text;
            this.expiryOption = expiryOption == null ? EXPIRY_NEVER : expiryOption;
            this.expiresAtMillis = expiresAtMillis;
        }

        public boolean isExpired() {
            return expiresAtMillis > 0 && expiresAtMillis <= System.currentTimeMillis();
        }
    }

    private static final ConcurrentHashMap<String, Note> cachedNotes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> loadedNotes = new ConcurrentHashMap<>();
    private static final String PREFS = "zoxgram_local_notes";

    private ZoxgramNoteRepository() {}

    private static SharedPreferences prefs() {
        Context ctx = ApplicationLoader.applicationContext;
        if (ctx == null) return null;
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    private static String cacheKey(long contactUserId) {
        return "u_" + contactUserId;
    }

    private static long expiryMillis(String option) {
        long now = System.currentTimeMillis();
        if (EXPIRY_1_DAY.equals(option)) return now + 1L * 24 * 60 * 60 * 1000;
        if (EXPIRY_5_DAYS.equals(option)) return now + 5L * 24 * 60 * 60 * 1000;
        if (EXPIRY_7_DAYS.equals(option)) return now + 7L * 24 * 60 * 60 * 1000;
        return 0;
    }

    public static Note getCachedNote(long contactUserId) {
        String key = cacheKey(contactUserId);
        Note note = cachedNotes.get(key);
        if (note != null && note.isExpired()) {
            cachedNotes.remove(key);
            loadedNotes.remove(key);
            SharedPreferences p = prefs();
            if (p != null) p.edit().remove(key).apply();
            return null;
        }
        return note;
    }

    public static boolean isLoaded(long contactUserId) {
        return loadedNotes.containsKey(cacheKey(contactUserId));
    }

    public static void clearCache() {
        cachedNotes.clear();
        loadedNotes.clear();
    }

    public static void load(long contactUserId, NoteCallback callback) {
        if (contactUserId <= 0) {
            if (callback != null) callback.onError("Invalid user");
            return;
        }
        String key = cacheKey(contactUserId);
        try {
            SharedPreferences p = prefs();
            if (p == null) {
                loadedNotes.put(key, true);
                if (callback != null) callback.onSuccess(null);
                return;
            }
            String raw = p.getString(key, null);
            if (raw == null || raw.isEmpty()) {
                loadedNotes.put(key, true);
                cachedNotes.remove(key);
                if (callback != null) callback.onSuccess(null);
                return;
            }
            JSONObject o = new JSONObject(raw);
            Note note = new Note(
                    o.optString("text", ""),
                    o.optString("expiry", EXPIRY_NEVER),
                    o.optLong("expiresAt", 0)
            );
            if (note.isExpired()) {
                p.edit().remove(key).apply();
                cachedNotes.remove(key);
                loadedNotes.put(key, true);
                if (callback != null) callback.onSuccess(null);
                return;
            }
            cachedNotes.put(key, note);
            loadedNotes.put(key, true);
            if (callback != null) callback.onSuccess(note);
        } catch (Exception e) {
            loadedNotes.put(key, true);
            if (callback != null) callback.onError(e.getMessage());
        }
    }

    public static void save(long contactUserId, String text, String expiryOption, SaveCallback callback) {
        if (contactUserId <= 0) {
            if (callback != null) callback.onError("Invalid user");
            return;
        }
        String key = cacheKey(contactUserId);
        try {
            if (text == null) text = "";
            text = text.trim();
            if (text.isEmpty()) {
                delete(contactUserId, callback);
                return;
            }
            if (expiryOption == null) expiryOption = EXPIRY_NEVER;
            long expiresAt = expiryMillis(expiryOption);
            Note note = new Note(text, expiryOption, expiresAt);

            SharedPreferences p = prefs();
            if (p != null) {
                JSONObject o = new JSONObject();
                o.put("text", text);
                o.put("expiry", expiryOption);
                o.put("expiresAt", expiresAt);
                p.edit().putString(key, o.toString()).apply();
            }
            cachedNotes.put(key, note);
            loadedNotes.put(key, true);
            if (callback != null) callback.onSuccess();
        } catch (Exception e) {
            if (callback != null) callback.onError(e.getMessage());
        }
    }

    public static void delete(long contactUserId, SaveCallback callback) {
        String key = cacheKey(contactUserId);
        try {
            SharedPreferences p = prefs();
            if (p != null) p.edit().remove(key).apply();
            cachedNotes.remove(key);
            loadedNotes.put(key, true);
            if (callback != null) callback.onSuccess();
        } catch (Exception e) {
            if (callback != null) callback.onError(e.getMessage());
        }
    }
}
