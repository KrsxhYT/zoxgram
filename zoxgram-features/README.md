# Zoxgram Features Module

## ✅ Completed

- Module structure + dependency in TMessagesProj
- ZoxgramConfig (all feature toggles)
- ProfileNotesManager (local + Firestore + expiry 1d/5d/7d/never)
- SessionBackupManager (File export/import/delete only)
- VoiceFeaturesHelper (Download + Speech2Text)
- CircleVideoHelper
- RegDateHelper
- ZoxgramSettingsActivity + registered in manifest
- ZoxgramInjector (central entry points for UI)
- Branding strings override (AppName, Telegram → Zoxgram, all feature strings)
- Red theme (zoxredtheme.attheme)

## UI Injection Guide (for you / next AI session)

Because the core Telegram UI classes (SettingsActivity, ProfileActivity, ChatActivity, DialogsActivity) are 100k–700k lines, full automated rewrite is risky. Use these exact injection points:

### 1. SettingsActivity (add row at top of list)
In `fillItems` or the method that builds the UniversalAdapter items, add at the very beginning:
```java
items.add(UItem.asButton(R.string.ZoxgramSettings, () -> ZoxgramInjector.openSettings(this)));
```
or using TextCell / HeaderCell style matching the rest of the settings list.

### 2. ProfileActivity
- After bio / status section → show note if present (ProfileNotesManager.getNote(userId))
- Add “Set Note” button / long-press → ZoxgramInjector.showSetNoteDialog(...)
- Add “Get Registration Date” row → ZoxgramInjector.requestRegDate(...)

### 3. ChatActivity / ChatMessageCell
- On voice message long-press menu → add “Download” → ZoxgramInjector.downloadVoice(...)
- Add small “→A” button next to voice player → ZoxgramInjector.speech2Text(...)

### 4. Attachment menu (ChatAttachAlert)
- Add “Send as Rounded Video” option → ZoxgramInjector.sendAsCircleVideo(...)

### 5. DialogsActivity / ActionBar title
Wherever the main title is set to the app name, use:
```java
actionBar.setTitle(ZoxgramInjector.brand(getString(R.string.AppName)));
```
or simply hardcode "Zoxgram".

### 6. Launcher icons
Replace all `ic_launcher*.png` / adaptive icons in `TMessagesProj/src/main/res/mipmap-*` with the white Z logo you provided.

### 7. Init
In ApplicationLoader.onCreate or postInitApplication add:
```java
com.zoxgram.features.ZoxgramInjector.init(applicationContext);
```

## Firestore
Your existing firestore.rules already perfectly support the notes structure. No change needed.

Pull latest and continue wiring the 5–6 injection points above. The hard logic is already done.
