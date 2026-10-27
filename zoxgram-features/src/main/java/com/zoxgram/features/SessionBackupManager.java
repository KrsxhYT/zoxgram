package com.zoxgram.features;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Session Export / Import to local file only (no Google Cloud).
 * Matches the Nicegram-style flow requested by user:
 * - Export To File
 * - Import From File
 * - Delete Sessions
 */
public class SessionBackupManager {

    private final Context context;

    public SessionBackupManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Export current account sessions to a local .zoxbackup file.
     * Returns the File created, or null on failure.
     */
    public File exportToFile() {
        try {
            File dir = new File(context.getExternalFilesDir(null), "ZoxgramBackups");
            if (!dir.exists()) dir.mkdirs();

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            File out = new File(dir, "zoxgram_sessions_" + timestamp + ".zoxbackup");

            // Placeholder: in real integration we serialize UserConfig / AccountInstance data
            // For now create a marker file; real serialization will be hooked into Telegram's AccountInstance
            try (FileOutputStream fos = new FileOutputStream(out)) {
                fos.write(("ZOXGRAM_SESSION_BACKUP_V1\n" + System.currentTimeMillis()).getBytes());
            }
            return out;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Import sessions from a user-selected file (Uri from file picker).
     */
    public boolean importFromFile(Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            if (is == null) return false;

            File temp = new File(context.getCacheDir(), "import_temp.zoxbackup");
            try (OutputStream os = new FileOutputStream(temp)) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = is.read(buf)) > 0) {
                    os.write(buf, 0, len);
                }
            }
            is.close();

            // Placeholder: parse and restore sessions into UserConfig
            // Real implementation will call AccountInstance restore logic
            return temp.length() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAllSessions() {
        // Placeholder: clear local session files / call Telegram logout for all accounts
        File dir = new File(context.getExternalFilesDir(null), "ZoxgramBackups");
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                if (f != null && f.getName().endsWith(".zoxbackup")) {
                    f.delete();
                }
            }
        }
    }
}
