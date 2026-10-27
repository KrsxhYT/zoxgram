# Zoxgram Features Module

This module holds all Zoxgram-specific enhancements on top of the Telegram Android base.

## Implemented / Scaffolded

- **ZoxgramConfig** – On/Off toggles for:
  - Show Chat ID
  - Show Notes
  - Show Registration Date
  - Voice Message Download
  - Speech2Text
  - Circle Video
  - Group Call Recorder

- **ProfileNotesManager** – Local + Firestore notes with expiry (1d / 5d / 7d / never).
  Uses the existing `firestore.rules` (ZoxId based).

- **SessionBackupManager** – File-only export / import / delete (no Google Cloud).

## Still to wire into UI (next commits)

1. Zoxgram Settings screen (appears at top of Telegram Settings)
2. Profile note UI ("Set Note" popup exactly like the video)
3. "Get Registration Date" button on profiles
4. Long-press Download on voice messages
5. ->A Speech2Text button on voice messages
6. "Send as Rounded Video" option
7. Group Call Recorder → Saved Messages
8. Unlimited accounts support
9. Full "Telegram" → "Zoxgram" string & logo replacement
10. Red theme (goldietheme.attheme with gold/yellow → red)

## Logo

Place the provided logo.png into:
`TMessagesProj/src/main/res/mipmap-*/ic_launcher*.png` (all densities)
and also as drawable for in-app use.

## How to continue

The core managers are ready. Next step is injecting the UI entry points into:
- `LaunchActivity` / `DialogsActivity` (header title → Zoxgram)
- `ProfileActivity` (notes + reg date)
- `ChatActivity` (voice download + speech2text)
- `SettingsActivity` / `BasePreferencesActivity` (Zoxgram Settings row)
- Attachment menu (circle video)
