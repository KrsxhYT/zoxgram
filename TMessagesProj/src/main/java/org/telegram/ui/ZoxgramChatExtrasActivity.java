package org.telegram.ui;

import android.view.View;

import com.zoxgram.features.ZoxgramConfig;
import com.zoxgram.features.ZoxgramOfficialBadge;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/**
 * Part 7–8 — Chat extras settings + official badge info.
 * Full ChatActivity ⋮ menu injection is wired where possible via flags;
 * View Deleted archive needs deeper hooks in a follow-up.
 */
public class ZoxgramChatExtrasActivity extends UniversalFragment {

    private static final int ROW_VIEW_DELETED_INFO = 1;
    private static final int ROW_TO_BEGINNING_INFO = 2;
    private static final int ROW_OFFICIAL_INFO = 3;
    private static final int ROW_TEST_BADGE_OWNER = 4;
    private static final int ROW_TEST_BADGE_CHANNEL = 5;
    private static final int ROW_TRANSLUCENT = 10;

    @Override
    protected CharSequence getTitle() {
        return "Chat extras";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        items.add(UItem.asHeader("Per-chat menu (Zoxgram)"));
        items.add(UItem.asButton(ROW_VIEW_DELETED_INFO, "View Deleted",
                "Shows deleted messages for this chat only"));
        items.add(UItem.asButton(ROW_TO_BEGINNING_INFO, "To Beginning",
                "Jump to the first message in the chat"));
        items.add(UItem.asShadow("These appear under Chat ⋮ → Zoxgram when the menu injection is active."));

        items.add(UItem.asHeader("Deleted style"));
        items.add(UItem.asSwitchNoIcon(ROW_TRANSLUCENT, "Translucent Deleted Messages")
                .setChecked(ZoxgramConfig.isTranslucentDeleted()));

        items.add(UItem.asHeader("Official accounts"));
        items.add(UItem.asButton(ROW_OFFICIAL_INFO, "Official usernames",
                "@Krsxh, @OfficialZoxgram, @ZoxgramChat"));
        items.add(UItem.asButton(ROW_TEST_BADGE_OWNER, "Preview Owner badge", "@Krsxh"));
        items.add(UItem.asButton(ROW_TEST_BADGE_CHANNEL, "Preview Channel badge", "@OfficialZoxgram"));
        items.add(UItem.asShadow("Client-side verified badge + popup. Visible only in Zoxgram."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_VIEW_DELETED_INFO) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("View Deleted")
                    .setMessage("Opens deleted messages saved for the current chat/group/channel. Full archive UI hooks into ChatActivity menu as Part 7 injection.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        if (item.id == ROW_TO_BEGINNING_INFO) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("To Beginning")
                    .setMessage("Scrolls to the oldest message in the current chat.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        if (item.id == ROW_OFFICIAL_INFO) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Official Zoxgram")
                    .setMessage("These usernames show a client-side verified badge:\n\n• @Krsxh — Owner of Zoxgram\n• @OfficialZoxgram — Official channel\n• @ZoxgramChat — Official chat")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }
        if (item.id == ROW_TEST_BADGE_OWNER) {
            showOfficial("Krsxh");
            return;
        }
        if (item.id == ROW_TEST_BADGE_CHANNEL) {
            showOfficial("OfficialZoxgram");
            return;
        }
        if (item.id == ROW_TRANSLUCENT) {
            boolean next = !ZoxgramConfig.isTranslucentDeleted();
            ZoxgramConfig.set(ZoxgramConfig.KEY_TRANSLUCENT_DELETED, next);
            if (view instanceof TextCheckCell2) {
                ((TextCheckCell2) view).setChecked(next);
            }
        }
    }

    private void showOfficial(String username) {
        if (getParentActivity() == null) return;
        new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                .setTitle("Zoxgram Official")
                .setMessage(ZoxgramOfficialBadge.messageFor(username))
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
