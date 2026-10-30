package org.telegram.ui;

import android.view.View;

import com.zoxgram.features.ZoxgramConfig;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/** Existing media feature toggles (from earlier Nicegram-style work). */
public class ZoxgramMediaActivity extends UniversalFragment {

    private static final int ROW_VOICE = 1;
    private static final int ROW_SPEECH = 2;
    private static final int ROW_CIRCLE = 3;
    private static final int ROW_CALL_REC = 4;

    @Override
    protected CharSequence getTitle() {
        return "Media features";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);
        items.add(UItem.asHeader("Media"));
        items.add(UItem.asSwitchNoIcon(ROW_VOICE, "Download voice messages")
                .setChecked(ZoxgramConfig.isVoiceDownloadEnabled()));
        items.add(UItem.asSwitchNoIcon(ROW_SPEECH, "Speech to text")
                .setChecked(ZoxgramConfig.isSpeech2TextEnabled()));
        items.add(UItem.asSwitchNoIcon(ROW_CIRCLE, "Circle / rounded video")
                .setChecked(ZoxgramConfig.isCircleVideoEnabled()));
        items.add(UItem.asSwitchNoIcon(ROW_CALL_REC, "Group call recorder")
                .setChecked(ZoxgramConfig.isGroupCallRecorderEnabled()));
        items.add(UItem.asShadow("Extra media tools. Full chat injection in later parts."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);
        String key;
        boolean def = true;
        switch (item.id) {
            case ROW_VOICE: key = ZoxgramConfig.KEY_VOICE_DOWNLOAD; break;
            case ROW_SPEECH: key = ZoxgramConfig.KEY_SPEECH2TEXT; break;
            case ROW_CIRCLE: key = ZoxgramConfig.KEY_CIRCLE_VIDEO; break;
            case ROW_CALL_REC: key = ZoxgramConfig.KEY_GROUP_CALL_RECORDER; break;
            default: return;
        }
        boolean next = !ZoxgramConfig.get(key, def);
        ZoxgramConfig.set(key, next);
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).setChecked(next);
        }
    }

    @Override
    public void onLongClick(UItem item, View view, int position, float x, float y) {}
}
