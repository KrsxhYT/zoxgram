# Zoxgram private notes and ZoxID setup

The app uses the Firebase Google account as the cloud-note owner. The same
Google account keeps the same ZoxID across app reinstalls and devices. Telegram
session credentials are not stored in Firestore.
The ZoxID is attached to the connected Google/Firebase account, not to one
Telegram account; Telegram accounts used in the same app share that notes owner.

## Firebase Console

1. Enable Google under **Authentication → Sign-in method**.
2. Confirm the Android OAuth client and the Web OAuth client are present in the
   Firebase project configured by the Android app's Google Services files.
3. Enable Cloud Firestore and deploy the repository's rules and function:

   ```sh
   firebase deploy --only firestore:rules,functions:getOrCreateZoxId
   ```

The callable function reserves a six-digit ZoxID transactionally and places it
in a Firebase Auth custom claim. The Android app does not choose or create its
own owner ID.

## Data shape

```text
profiles/{ownerZoxId}
  zoxId: "123456"

profiles/{ownerZoxId}/notes/{contactTelegramUserId}
  text: "Private note"
  expiryOption: "1d" | "5d" | "7d" | "never"
  expiresAt: Firestore timestamp | null
  updatedAt: server timestamp
```

Only the signed-in owner whose Firebase token carries the matching `zox_id`
claim can read, edit, or delete notes under that ZoxID. The claims and UID
mapping collections are server-only. If the function is not deployed or the
user is not signed into the same Google account, cloud notes cannot load or
save.

The app hides expired notes and deletes them when their profile is open at
expiry or the next time the note is loaded.
