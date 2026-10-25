const crypto = require("crypto");
const admin = require("firebase-admin");
const functions = require("firebase-functions/v1");

admin.initializeApp();

const db = admin.firestore();
const MIN_ZOX_ID = 100000;
const MAX_ZOX_ID = 1000000;

function isGoogleAccount(context) {
  return context.auth
    && context.auth.token
    && context.auth.token.firebase
    && context.auth.token.firebase.sign_in_provider === "google.com";
}

exports.getOrCreateZoxId = functions.https.onCall(async (_data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError(
      "unauthenticated",
      "Sign in to your Google account before creating a ZoxID."
    );
  }
  if (!isGoogleAccount(context)) {
    throw new functions.https.HttpsError(
      "failed-precondition",
      "A linked Google account is required so your ZoxID can be restored on another device."
    );
  }

  const uid = context.auth.uid;
  const ownerRef = db.collection("zoxIdOwners").doc(uid);
  let zoxId;

  // Each allocation attempt is transactional: two users cannot claim the
  // same six-digit ID, and a user keeps the mapping attached to their UID.
  for (let attempt = 0; attempt < 10; attempt++) {
    try {
      zoxId = await db.runTransaction(async (transaction) => {
        const ownerSnapshot = await transaction.get(ownerRef);
        if (ownerSnapshot.exists) {
          return ownerSnapshot.get("zoxId");
        }

        for (let candidateAttempt = 0; candidateAttempt < 32; candidateAttempt++) {
          const candidate = String(crypto.randomInt(MIN_ZOX_ID, MAX_ZOX_ID));
          const claimRef = db.collection("zoxIdClaims").doc(candidate);
          const claimSnapshot = await transaction.get(claimRef);
          if (!claimSnapshot.exists) {
            const profileRef = db.collection("profiles").doc(candidate);
            const timestamp = admin.firestore.FieldValue.serverTimestamp();
            transaction.create(claimRef, { uid, createdAt: timestamp });
            transaction.create(ownerRef, { zoxId: candidate, createdAt: timestamp });
            transaction.create(profileRef, { zoxId: candidate });
            return candidate;
          }
        }
        throw new Error("No free ID found in this allocation attempt.");
      });
      break;
    } catch (error) {
      if (attempt === 9) {
        console.error("Unable to allocate a ZoxID", error);
        throw new functions.https.HttpsError(
          "resource-exhausted",
          "A ZoxID could not be allocated. Please try again."
        );
      }
    }
  }

  if (!/^[0-9]{6}$/.test(zoxId || "")) {
    throw new functions.https.HttpsError(
      "internal",
      "The account has an invalid ZoxID mapping."
    );
  }

  const userRecord = await admin.auth().getUser(uid);
  await admin.auth().setCustomUserClaims(uid, {
    ...(userRecord.customClaims || {}),
    zox_id: zoxId
  });

  return { zoxId };
});
