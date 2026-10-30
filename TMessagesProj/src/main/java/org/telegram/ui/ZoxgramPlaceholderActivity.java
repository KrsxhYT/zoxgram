package org.telegram.ui;

import android.view.View;

import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalFragment;

import java.util.ArrayList;

public class ZoxgramPlaceholderActivity extends UniversalFragment {

    private final String title;
    private final String message;

    public ZoxgramPlaceholderActivity(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    protected CharSequence getTitle() {
        return title;
    }

    @Override
    protected void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asHeader(title));
        items.add(UItem.asShadow(message));
    }

    @Override
    protected void onClick(UItem item, View view, int position, float x, float y) {}

    @Override
    public boolean onLongClick(UItem item, View view, int position, float x, float y) {
        return false;
    }
}
