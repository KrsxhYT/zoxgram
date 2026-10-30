package org.telegram.ui;

import android.app.Activity;
import android.view.View;
import android.widget.Toast;

import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/** Session backup — file only. */
public class ZoxgramSessionActivity extends UniversalFragment {

    private static final int ROW_EXPORT = 1;
    private static final int ROW_IMPORT = 2;
    private static final int ROW_DELETE = 3;

    @Override
    protected CharSequence getTitle() {
        return "Session Backup";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader("Local file backup"));
        items.add(UItem.asButton(ROW_EXPORT, R.drawable.msg_download, "Export To File", "Creates .zoxbackup"));
        items.add(UItem.asButton(ROW_IMPORT, R.drawable.msg_add, "Import From File", "Restore from file"));
        items.add(UItem.asButton(ROW_DELETE, R.drawable.msg_delete, "Delete Local Backups", "Clear backup files"));
        items.add(UItem.asShadow("File only. No cloud."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        Activity a = getParentActivity();
        if (a == null) return;
        if (item.id == ROW_EXPORT) {
            try {
                File dir = a.getExternalFilesDir(null);
                if (dir == null) dir = a.getFilesDir();
                File out = new File(dir, "zoxgram_sessions.zoxbackup");
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    fos.write(("Zoxgram session marker\n" + System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8));
                }
                BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, "Exported: " + out.getAbsolutePath()).show();
            } catch (Exception e) {
                Toast.makeText(a, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else if (item.id == ROW_IMPORT) {
            new AlertDialog.Builder(a, getResourceProvider())
                    .setTitle("Import")
                    .setMessage("Place zoxgram_sessions.zoxbackup in app files and re-open. Full session restore comes in a later part.")
                    .setPositiveButton("OK", null)
                    .show();
        } else if (item.id == ROW_DELETE) {
            try {
                File dir = a.getExternalFilesDir(null);
                if (dir == null) dir = a.getFilesDir();
                File f = new File(dir, "zoxgram_sessions.zoxbackup");
                boolean ok = !f.exists() || f.delete();
                BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, ok ? "Backups cleared" : "Nothing to delete").show();
            } catch (Exception e) {
                Toast.makeText(a, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLongClick(UItem item, View view, int position, float x, float y) {}
}
