package org.telegram.messenger;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.android.gms.tasks.Task;

import java.util.Map;

public final class ZoxgramCloudAccount {

    public interface Callback {
        void onReady(String zoxId);
        void onError(String message);
    }

    private ZoxgramCloudAccount() {
    }

    public static void getOrCreateZoxId(Callback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            callback.onError("Connect a Google account in Zoxgram Settings to use cloud notes.");
            return;
        }
        readClaim(user, false, callback);
    }

    private static void readClaim(FirebaseUser user, boolean forceRefresh, Callback callback) {
        user.getIdToken(forceRefresh).addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                callback.onError("Could not verify the cloud account. Please try again.");
                return;
            }
            Object claim = task.getResult().getClaims().get("zox_id");
            String zoxId = claim instanceof String ? (String) claim : null;
            if (isValidZoxId(zoxId)) {
                callback.onReady(zoxId);
                return;
            }
            allocateZoxId(user, callback);
        });
    }

    private static void allocateZoxId(FirebaseUser user, Callback callback) {
        FirebaseFunctions.getInstance()
                .getHttpsCallable("getOrCreateZoxId")
                .call()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        callback.onError(messageFor(task));
                        return;
                    }
                    // The callable writes a custom claim. Refresh the Firebase
                    // ID token before attempting any owner-only Firestore read.
                    readClaim(user, true, callback);
                });
    }

    private static String messageFor(Task<?> task) {
        Exception exception = task.getException();
        String message = exception == null ? null : exception.getLocalizedMessage();
        if (TextUtils.isEmpty(message)) {
            return "Could not create a ZoxID. Check that the Firebase function is deployed.";
        }
        return message;
    }

    private static boolean isValidZoxId(String zoxId) {
        return zoxId != null && zoxId.matches("^[0-9]{6}$");
    }
}
