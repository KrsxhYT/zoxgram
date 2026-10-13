package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;

import java.util.ArrayList;

public class DeletedMessagesActivity extends BaseFragment {
    private final ArrayList<MessagesStorage.DeletedMessageRecord> messages = new ArrayList<>();
    private RecyclerListView listView;
    private ListAdapter listAdapter;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getMessagesStorage().loadDeletedMessages(loadedMessages -> {
            messages.clear();
            messages.addAll(loadedMessages);
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        });
        return true;
    }

    @Override
    public View createView(Context context) {
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setTitle(getString(R.string.DeletedMessagesHistory));
        actionBar.setAllowOverlayTitle(true);

        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context));
        listAdapter = new ListAdapter(context);
        listView.setAdapter(listAdapter);
        frameLayout.addView(listView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        fragmentView = frameLayout;
        return fragmentView;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        private final Context context;

        ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return false;
        }

        @Override
        public int getItemCount() {
            return messages.isEmpty() ? 1 : messages.size();
        }

        @Override
        public int getItemViewType(int position) {
            return messages.isEmpty() ? 1 : 0;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 1) {
                view = new TextInfoPrivacyCell(context);
            } else {
                view = new TextCell(context);
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (messages.isEmpty()) {
                ((TextInfoPrivacyCell) holder.itemView).setText(getString(R.string.DeletedMessagesEmpty));
                return;
            }

            MessagesStorage.DeletedMessageRecord record = messages.get(position);
            String peerName = DialogObject.getShortName(record.dialogId);
            if (peerName == null || peerName.isEmpty()) {
                peerName = Long.toString(record.dialogId);
            }
            String text = record.text == null || record.text.isEmpty() ? getString(R.string.DeletedMessageMedia) : record.text;
            if (text.length() > 180) {
                text = text.substring(0, 177) + "...";
            }
            ((TextCell) holder.itemView).setTextAndValue(peerName, text, position != messages.size() - 1);
        }
    }
}