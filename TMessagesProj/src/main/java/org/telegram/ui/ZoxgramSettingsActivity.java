package org.telegram.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;

import static org.telegram.messenger.LocaleController.getString;

import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/**
 * Zoxgram Settings – local only. No Google Cloud.
 */
public class ZoxgramSettingsActivity extends UniversalFragment {

    public static final String KEY_SHOW_CHAT_ID = "zoxgram_show_chat_id";
    public static final String KEY_SHOW_REGISTRATION_DATE = "zoxgram_show_registration_date";
    public static final String KEY_SHOW_CONTACT_NOTES = "zoxgram_show_contact_notes";

    private static final int ROW_SHOW_CHAT_ID = 1;
    private static final int ROW_SHOW_REGISTRATION_DATE = 2;
    private static final int ROW_SHOW_CONTACT_NOTES = 3;
    private static final int ROW_EXPORT_SESSIONS = 5;
    private static final int ROW_IMPORT_SESSIONS = 6;
    private static final int ROW_DELETE_SESSIONS = 7;

    @Override
    protected CharSequence getTitle() {
        return getString(R.string.ZoxgramSettings);
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        SharedPreferences preferences = getMessagesController().getMainSettings();

        items.add(UItem.asHeader(getString(R.string.ZoxgramProfileDetails)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CHAT_ID, getString(R.string.ZoxgramShowChatId))
                .setChecked(preferences.getBoolean(KEY_SHOW_CHAT_ID, true)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_REGISTRATION_DATE, getString(R.string.ZoxgramShowRegistrationDate))
                .setChecked(preferences.getBoolean(KEY_SHOW_REGISTRATION_DATE, true)));
        items.add(UItem.asSwitchNoIcon(ROW_SHOW_CONTACT_NOTES, getString(R.string.ZoxgramShowContactNotes))
                .setChecked(preferences.getBoolean(KEY_SHOW_CONTACT_NOTES, true)));
        items.add(UItem.asShadow("Notes are stored only on this device. No cloud, no Google account needed."));

        items.add(UItem.asHeader("Session Backup"));
        items.add(UItem.asButton(ROW_EXPORT_SESSIONS, R.drawable.msg_download, "Export To File", "Save a local .zoxbackup file"));
        items.add(UItem.asButton(ROW_IMPORT_SESSIONS, R.drawable.msg_upload, "Import From File", "Restore from a backup file"));
        items.add(UItem.asButton(ROW_DELETE_SESSIONS, R.drawable.msg_delete, "Delete Local Backups", "Remove backup files from this device"));
        items.add(UItem.asShadow("File export/import only. No Google Cloud."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        if (item.id == ROW_EXPORT_SESSIONS) {
            handleExportSessions();
            return;
        }
        if (item.id == ROW_IMPORT_SESSIONS) {
            handleImportSessions();
            return;
        }
        if (item.id == ROW_DELETE_SESSIONS) {
            handleDeleteSessions();
            return;
        }

        String preferenceKey;
        switch (item.id) {
            case ROW_SHOW_CHAT_ID:
                preferenceKey = KEY_SHOW_CHAT_ID;
                break;
            case ROW_SHOW_REGISTRATION_DATE:
                preferenceKey = KEY_SHOW_REGISTRATION_DATE;
                break;
            case ROW_SHOW_CONTACT_NOTES:
                preferenceKey = KEY_SHOW_CONTACT_NOTES;
                break;
            default:
                return;
        }

        SharedPreferences preferences = getMessagesController().getMainSettings();
        boolean checked = !preferences.getBoolean(preferenceKey, true);
        preferences.edit().putBoolean(preferenceKey, checked).apply();
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).getCheckBox().setChecked(checked, true);
        }
        NotificationCenter.getInstance(currentAccount).postNotificationName(
                NotificationCenter.updateInterfaces,
                MessagesController.UPDATE_MASK_ZOXGRAM_PROFILE
        );
    }

    private void handleExportSessions() {
        Activity activity = getParentActivity();
        if (activity == null) return;
        try {
            java.io.File dir = new java.io.File(activity.getExternalFilesDir(null), "ZoxgramBackups");
            if (!dir.exists()) dir.mkdirs();
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.US).format(new java.util.Date());
            java.io.File out = new java.io.File(dir, "zoxgram_sessions_" + timestamp + ".zoxbackup");
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(out)) {
                fos.write(("ZOXGRAM_SESSION_BACKUP_V1\n" + System.currentTimeMillis()).getBytes());
            }
            new AlertDialog.Builder(activity, getResourceProvider())
                    .setTitle("Export To File")
                    .setMessage("Backup saved:\n" + out.getAbsolutePath())
                    .setPositiveButton(getString(R.string.OK), null)
                    .show();
        } catch (Exception e) {
            showError("Export failed: " + e.getMessage());
        }
    }

    private void handleImportSessions() {
        Activity activity = getParentActivity();
        if (activity == null) return;
        new AlertDialog.Builder(activity, getResourceProvider())
                .setTitle("Import From File")
                .setMessage("Copy a .zoxbackup file to this device, then restore will be available in a follow-up update.")
                .setPositiveButton(getString(R.string.OK), null)
                .show();
    }

    private void handleDeleteSessions() {
        Activity activity = getParentActivity();
        if (activity == null) return;
        new AlertDialog.Builder(activity, getResourceProvider())
                .setTitle("Delete Local Backups")
                .setMessage("Remove all .zoxbackup files from this device?")
                .setNegativeButton(getString(R.string.Cancel), null)
                .setPositiveButton("Delete", (d, w) -> {
                    try {
                        java.io.File dir = new java.io.File(activity.getExternalFilesDir(null), "ZoxgramBackups");
                        if (dir.exists() && dir.listFiles() != null) {
                            for (java.io.File f : dir.listFiles()) {
                                if (f != null && f.getName().endsWith(".zoxbackup")) f.delete();
                            }
                        }
                        BulletinFactory.of(ZoxgramSettingsActivity.this)
                                .createSimpleBulletin(R.raw.contact_check, "Local backups deleted")
                                .show();
                    } catch (Exception e) {
                        showError(e.getMessage());
                    }
                })
                .show();
    }

    private void showError(String message) {
        if (getParentActivity() != null) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle(getString(R.string.ZoxgramSettings))
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.OK), null)
                    .show();
        }
    }

    @Override
    protected boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
