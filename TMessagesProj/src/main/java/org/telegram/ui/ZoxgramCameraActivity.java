package org.telegram.ui;

import android.view.View;

import com.zoxgram.features.ZoxgramConfig;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

/** Part 6 — Camera / Photos / Videos. */
public class ZoxgramCameraActivity extends UniversalFragment {

    private static final int ROW_CAM_TYPE = 1;
    private static final int ROW_SEAMLESS = 2;
    private static final int ROW_EXT_FPS = 3;
    private static final int ROW_STAB = 4;
    private static final int ROW_MIRROR = 5;
    private static final int ROW_CAM_VIDEO_MSG = 6;
    private static final int ROW_REMEMBER_LAST = 7;
    private static final int ROW_STATIC_ZOOM = 8;

    private static final int ROW_ALWAYS_HD = 20;
    private static final int ROW_HIDE_COUNTER = 21;
    private static final int ROW_HIDE_INSTANT = 22;

    private static final int ROW_SKIP = 30;
    private static final int ROW_ORIGINAL = 31;
    private static final int ROW_PIP = 32;
    private static final int ROW_UNMUTE_VOL = 33;
    private static final int ROW_PAUSE_VIDEO = 34;
    private static final int ROW_PAUSE_VOICE = 35;
    private static final int ROW_PAUSE_ROUND = 36;

    private static final String[] CAM_TYPES = {"Camera 1", "Camera 2", "Camera X"};
    private static final String[] CAM_VM = {"Front", "Rear", "Ask"};
    private static final String[] SKIPS = {"5 seconds", "10 seconds", "15 seconds", "30 seconds"};
    private static final int[] SKIP_VALS = {5, 10, 15, 30};

    @Override
    protected CharSequence getTitle() {
        return "Camera & Media";
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        items.add(UItem.asHeader("Camera"));
        int ct = ZoxgramConfig.getCameraType();
        if (ct < 0 || ct >= CAM_TYPES.length) ct = 2;
        items.add(UItem.asButton(ROW_CAM_TYPE, "Camera Type", CAM_TYPES[ct]));
        items.add(UItem.asSwitchNoIcon(ROW_SEAMLESS, "Seamless Switching")
                .setChecked(ZoxgramConfig.isCamSeamless()));
        items.add(UItem.asSwitchNoIcon(ROW_EXT_FPS, "Extended FPS Range")
                .setChecked(ZoxgramConfig.isCamExtendedFps()));
        items.add(UItem.asSwitchNoIcon(ROW_STAB, "Stabilization")
                .setChecked(ZoxgramConfig.isCamStabilization()));
        items.add(UItem.asSwitchNoIcon(ROW_MIRROR, "Mirror Mode")
                .setChecked(ZoxgramConfig.isCamMirror()));

        int cvm = ZoxgramConfig.getCamVideoMsg();
        if (cvm < 0 || cvm >= CAM_VM.length) cvm = 0;
        items.add(UItem.asButton(ROW_CAM_VIDEO_MSG, "Camera in Video Messages", CAM_VM[cvm]));
        if (cvm != 2) {
            items.add(UItem.asSwitchNoIcon(ROW_REMEMBER_LAST, "Remember Last Used Camera")
                    .setChecked(ZoxgramConfig.isCamRememberLast()));
        }
        items.add(UItem.asSwitchNoIcon(ROW_STATIC_ZOOM, "Static Zoom")
                .setChecked(ZoxgramConfig.isCamStaticZoom()));
        items.add(UItem.asShadow("When recording video messages, zoom does not reset on finger release."));

        items.add(UItem.asHeader("Photos"));
        items.add(UItem.asSwitchNoIcon(ROW_ALWAYS_HD, "Always Send in HD")
                .setChecked(ZoxgramConfig.isAlwaysSendHd()));
        items.add(UItem.asSwitchNoIcon(ROW_HIDE_COUNTER, "Hide Photo Counter")
                .setChecked(ZoxgramConfig.isHidePhotoCounter()));
        items.add(UItem.asSwitchNoIcon(ROW_HIDE_INSTANT, "Hide Instant Camera Tile")
                .setChecked(ZoxgramConfig.isHideInstantCameraTile()));
        items.add(UItem.asShadow("Camera tile becomes a floating button."));

        items.add(UItem.asHeader("Videos"));
        int skip = ZoxgramConfig.getDoubleTapSkip();
        String skipLabel = skip + " seconds";
        items.add(UItem.asButton(ROW_SKIP, "Double Tap Skip Time", skipLabel));
        items.add(UItem.asSwitchNoIcon(ROW_ORIGINAL, "Prefer Original Quality")
                .setChecked(ZoxgramConfig.isPreferOriginalQuality()));
        items.add(UItem.asSwitchNoIcon(ROW_PIP, "Swipe to PiP")
                .setChecked(ZoxgramConfig.isSwipeToPip()));
        items.add(UItem.asSwitchNoIcon(ROW_UNMUTE_VOL, "Unmute Videos with Volume Button")
                .setChecked(ZoxgramConfig.isUnmuteWithVolume()));
        items.add(UItem.asShadow("Volume button unmutes instead of changing system volume."));

        items.add(UItem.asHeader("Auto-Pause"));
        items.add(UItem.asSwitchNoIcon(ROW_PAUSE_VIDEO, "Video")
                .setChecked(ZoxgramConfig.isAutoPauseVideo()));
        items.add(UItem.asSwitchNoIcon(ROW_PAUSE_VOICE, "Voice")
                .setChecked(ZoxgramConfig.isAutoPauseVoice()));
        items.add(UItem.asSwitchNoIcon(ROW_PAUSE_ROUND, "Round")
                .setChecked(ZoxgramConfig.isAutoPauseRound()));
        items.add(UItem.asShadow("Pauses media when the device is locked or Zoxgram is minimized."));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {
        ZoxgramConfig.init(ApplicationLoader.applicationContext);

        if (item.id == ROW_CAM_TYPE) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Camera Type")
                    .setItems(CAM_TYPES, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_CAMERA_TYPE, which);
                        refresh();
                    }).show();
            return;
        }
        if (item.id == ROW_CAM_VIDEO_MSG) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Camera in Video Messages")
                    .setItems(CAM_VM, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_CAM_VIDEO_MSG, which);
                        refresh();
                    }).show();
            return;
        }
        if (item.id == ROW_SKIP) {
            new AlertDialog.Builder(getParentActivity(), getResourceProvider())
                    .setTitle("Double Tap Skip Time")
                    .setItems(SKIPS, (d, which) -> {
                        ZoxgramConfig.setInt(ZoxgramConfig.KEY_DOUBLE_TAP_SKIP, SKIP_VALS[which]);
                        refresh();
                    }).show();
            return;
        }

        String key;
        boolean def;
        switch (item.id) {
            case ROW_SEAMLESS: key = ZoxgramConfig.KEY_CAM_SEAMLESS; def = true; break;
            case ROW_EXT_FPS: key = ZoxgramConfig.KEY_CAM_EXTENDED_FPS; def = false; break;
            case ROW_STAB: key = ZoxgramConfig.KEY_CAM_STABILIZATION; def = false; break;
            case ROW_MIRROR: key = ZoxgramConfig.KEY_CAM_MIRROR; def = true; break;
            case ROW_REMEMBER_LAST: key = ZoxgramConfig.KEY_CAM_REMEMBER_LAST; def = false; break;
            case ROW_STATIC_ZOOM: key = ZoxgramConfig.KEY_CAM_STATIC_ZOOM; def = false; break;
            case ROW_ALWAYS_HD: key = ZoxgramConfig.KEY_ALWAYS_SEND_HD; def = true; break;
            case ROW_HIDE_COUNTER: key = ZoxgramConfig.KEY_HIDE_PHOTO_COUNTER; def = true; break;
            case ROW_HIDE_INSTANT: key = ZoxgramConfig.KEY_HIDE_INSTANT_CAMERA_TILE; def = true; break;
            case ROW_ORIGINAL: key = ZoxgramConfig.KEY_PREFER_ORIGINAL_QUALITY; def = false; break;
            case ROW_PIP: key = ZoxgramConfig.KEY_SWIPE_TO_PIP; def = false; break;
            case ROW_UNMUTE_VOL: key = ZoxgramConfig.KEY_UNMUTE_WITH_VOLUME; def = true; break;
            case ROW_PAUSE_VIDEO: key = ZoxgramConfig.KEY_AUTO_PAUSE_VIDEO; def = true; break;
            case ROW_PAUSE_VOICE: key = ZoxgramConfig.KEY_AUTO_PAUSE_VOICE; def = false; break;
            case ROW_PAUSE_ROUND: key = ZoxgramConfig.KEY_AUTO_PAUSE_ROUND; def = false; break;
            default: return;
        }
        boolean next = !ZoxgramConfig.get(key, def);
        ZoxgramConfig.set(key, next);
        if (view instanceof TextCheckCell2) {
            ((TextCheckCell2) view).setChecked(next);
        } else {
            refresh();
        }
    }

    private void refresh() {
        if (listView != null && listView.adapter != null) {
            listView.adapter.update(true);
        }
    }

    @Override
    public boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
