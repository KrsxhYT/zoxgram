package com.zoxgram.features;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Zoxgram Settings screen.
 * This will be opened from the main Telegram Settings menu
 * (row appears at the top as "Zoxgram Settings").
 *
 * Toggles:
 * - Show Chat ID
 * - Show Notes
 * - Show Registration Date
 * - Voice Message Download
 * - Speech2Text
 * - Circle Video
 * - Group Call Recorder
 * - Account / Session Backup options
 */
public class ZoxgramSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ZoxgramConfig.init(this);

        FrameLayout root = new FrameLayout(this);
        setContentView(root);

        // Simple list of toggles (will be replaced with proper Telegram-style cells later)
        RecyclerView list = new RecyclerView(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        root.addView(list, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        // TODO: Use Telegram Cell classes (TextCheckCell etc.) for exact look
        // For now this is a placeholder Activity that can be launched from Settings
    }

    public static void open(Context context) {
        context.startActivity(new android.content.Intent(context, ZoxgramSettingsActivity.class));
    }
}
