package org.telegram.messenger;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

        private Note(String text, String expiryOption, long expiresAtMillis) {
            this.text = text;
            this.expiryOption = expiryOption;
            this.expiresAtMillis = expiresAtMillis;
        }

        public boolean isExpired() {
            return expiresAtMillis > 0 && expiresAtMillis <= System.currentTimeMillis();
        }
    }

    private static final ConcurrentHashMap<String, Note> cachedNotes = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> loadedNotes = new ConcurrentHashMap<>();

    private ZoxgramNoteRepository() {
    }

    public static Note getCachedNote(long contactUserId) {
        String key = cacheKey(contactUserId);
        Note note = cachedNotes.get(key);
        if (note != null && note.isExpired()) {
            cachedNotes.remove(key);
            loadedNotes.remove(key);
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
            callback.onError("A contact user ID is required.");
            return;
        }
        ZoxgramCloudAccount.getOrCreateZoxId(new ZoxgramCloudAccount.Callback() {
            @Override
            public void onReady(String zoxId) {
                FirebaseFirestore.getInstance()
                        .collection("profiles")
                        .document(zoxId)
                        .collection("notes")
                        .document(Long.toString(contactUserId))
                        .get()
                        .addOnSuccessListener(snapshot -> handleLoadedNote(
                                zoxId,
                                contactUserId,
                                snapshot,
                                callback
                        ))
                        .addOnFailureListener(error -> callback.onError(
                                "Could not load this private note: " + safeMessage(error)
                        ));
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    private static void handleLoadedNote(
            String zoxId,
            long contactUserId,
            DocumentSnapshot snapshot,
            NoteCallback callback
    ) {
        String key = cacheKey(contactUserId);
        if (!snapshot.exists()) {
            cachedNotes.remove(key);
            loadedNotes.put(key, true);
            callback.onSuccess(null);
            return;
        }

        String text = snapshot.getString("text");
        String expiryOption = snapshot.getString("expiryOption");
        Timestamp expiresAt = snapshot.getTimestamp("expiresAt");
        if (text == null || !isValidExpiry(expiryOption)) {
            callback.onError("This note has an invalid format and could not be opened.");
            return;
        }
        long expiresAtMillis = expiresAt == null ? 0 : expiresAt.toDate().getTime();
        Note note = new Note(text, expiryOption, expiresAtMillis);
        if (note.isExpired()) {
            FirebaseFirestore.getInstance()
                    .collection("profiles")
                    .document(zoxId)
                    .collection("notes")
                    .document(Long.toString(contactUserId))
                    .delete();
            cachedNotes.remove(key);
            loadedNotes.put(key, true);
            callback.onSuccess(null);
            return;
        }
        cachedNotes.put(key, note);
        loadedNotes.put(key, true);
        callback.onSuccess(note);
    }

    public static void save(
            long contactUserId,
            String text,
            String expiryOption,
            SaveCallback callback
    ) {
        String trimmedText = text == null ? "" : text.trim();
        if (contactUserId <= 0) {
            callback.onError("A contact user ID is required.");
            return;
        }
        if (trimmedText.isEmpty() || trimmedText.length() > 1200) {
            callback.onError("Enter a note of up to 1200 characters.");
            return;
        }
        if (!isValidExpiry(expiryOption)) {
            callback.onError("Choose a valid note expiration.");
            return;
        }

        ZoxgramCloudAccount.getOrCreateZoxId(new ZoxgramCloudAccount.Callback() {
            @Override
            public void onReady(String zoxId) {
                long expiresAtMillis = expiryMillis(expiryOption);
                Map<String, Object> data = new HashMap<>();
                data.put("text", trimmedText);
                data.put("expiryOption", expiryOption);
                data.put("expiresAt", expiresAtMillis == 0 ? null : new Timestamp(new Date(expiresAtMillis)));
                data.put("updatedAt", FieldValue.serverTimestamp());

                FirebaseFirestore.getInstance()
                        .collection("profiles")
                        .document(zoxId)
                        .collection("notes")
                        .document(Long.toString(contactUserId))
                        .set(data)
                        .addOnSuccessListener(unused -> {
                            String key = cacheKey(contactUserId);
                            cachedNotes.put(key, new Note(trimmedText, expiryOption, expiresAtMillis));
                            loadedNotes.put(key, true);
                            callback.onSuccess();
                        })
                        .addOnFailureListener(error -> callback.onError(
                                "Could not save this private note: " + safeMessage(error)
                        ));
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public static void delete(long contactUserId, SaveCallback callback) {
        if (contactUserId <= 0) {
            callback.onError("A contact user ID is required.");
            return;
        }
        ZoxgramCloudAccount.getOrCreateZoxId(new ZoxgramCloudAccount.Callback() {
            @Override
            public void onReady(String zoxId) {
                FirebaseFirestore.getInstance()
                        .collection("profiles")
                        .document(zoxId)
                        .collection("notes")
                        .document(Long.toString(contactUserId))
                        .delete()
                        .addOnSuccessListener(unused -> {
                            String key = cacheKey(contactUserId);
                            cachedNotes.remove(key);
                            loadedNotes.put(key, true);
                            callback.onSuccess();
                        })
                        .addOnFailureListener(error -> callback.onError(
                                "Could not delete this private note: " + safeMessage(error)
                        ));
            }

            @Override
            public void onError(String message) {
                callback.onError(message);
            }
        });
    }

    public static boolean isValidExpiry(String value) {
        return EXPIRY_1_DAY.equals(value)
                || EXPIRY_5_DAYS.equals(value)
                || EXPIRY_7_DAYS.equals(value)
                || EXPIRY_NEVER.equals(value);
    }

    private static long expiryMillis(String expiryOption) {
        long days;
        if (EXPIRY_1_DAY.equals(expiryOption)) {
            days = 1;
        } else if (EXPIRY_5_DAYS.equals(expiryOption)) {
            days = 5;
        } else if (EXPIRY_7_DAYS.equals(expiryOption)) {
            days = 7;
        } else {
            return 0;
        }
        return System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L;
    }

    private static String cacheKey(long contactUserId) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser() == null ? "signed-out" : auth.getCurrentUser().getUid();
        return uid + ":" + contactUserId;
    }

    private static String safeMessage(Exception error) {
        String message = error == null ? null : error.getLocalizedMessage();
        return message == null || message.trim().isEmpty() ? "Please try again." : message;
    }
}
