# Zoxgram Features Module

All Zoxgram-specific enhancements live here.

## ✅ Done (committed)

| Feature | Class / File |
|---------|--------------|
| Feature toggles | `ZoxgramConfig.java` |
| Profile Notes (local + Firestore + expiry) | `ProfileNotesManager.java` |
| Session Backup (File only) | `SessionBackupManager.java` |
| Voice Download + Speech2Text | `VoiceFeaturesHelper.java` |
| Circle / Rounded Video | `CircleVideoHelper.java` |
| Registration Date | `RegDateHelper.java` |
| Settings Activity skeleton | `ZoxgramSettingsActivity.java` |
| Red Theme (gold → red) | `assets/zoxredtheme.attheme` |
| Module linked to TMessagesProj | `settings.gradle` + dependency |

## 🔧 Next (UI wiring into Telegram screens)

These require editing core Telegram UI classes:

1. **Settings row** – add "Zoxgram Settings" at the top of SettingsActivity / BasePreferencesActivity
2. **Profile Notes UI** – "Set Note" popup + note display under bio (ProfileActivity)
3. **Get Registration Date** button on profiles
4. **Voice message** long-press Download + →A Speech2Text button (ChatActivity / ChatMessageCell)
5. **Attachment menu** – "Send as Rounded Video" option
6. **Group Call Recorder** → save to Saved Messages
7. **Main header** after login – change "Telegram" text to "Zoxgram"
8. **App logo / launcher icons** – replace all mipmap ic_launcher* with the white Z logo
9. **Full string replace** – every visible "Telegram" → "Zoxgram" (strings.xml + code)
10. **Unlimited accounts** support (UserConfig.MAX_ACCOUNT_COUNT already high in modern Telegram)

## Logo

Your logo is ready. Place it in:
```
TMessagesProj/src/main/res/mipmap-mdpi/ic_launcher.png
TMessagesProj/src/main/res/mipmap-hdpi/ic_launcher.png
...
(and adaptive icons)
```
Also copy to `drawable-xxhdpi/ic_zoxgram_logo.png` for in-app use.

## How to test so far

1. Sync / rebuild the project (new module will appear).
2. ZoxgramConfig, ProfileNotesManager, etc. can already be called from any Activity.
3. Theme file can be loaded via Theme.applyTheme or by placing it in themes folder.

Continue wiring the UI entry points next.
