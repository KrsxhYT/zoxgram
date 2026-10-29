package org.telegram.ui.Components;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

/**
 * Builds readable JSON-style metadata for any message.
 * Wire from ChatActivity.createMenu:
 *   options/popup add "Metadata" → MessageMetadataHelper.show(activity, messageObject, resourcesProvider)
 */
public final class MessageMetadataHelper {

    private MessageMetadataHelper() {}

    public static void show(Activity activity, MessageObject messageObject, Theme.ResourcesProvider resourcesProvider) {
        if (activity == null || messageObject == null || messageObject.messageOwner == null) {
            return;
        }
        try {
            String json = toPrettyJson(messageObject);
            TextView tv = new TextView(activity);
            tv.setText(json);
            tv.setTextIsSelectable(true);
            tv.setTextSize(12);
            tv.setTypeface(AndroidUtilities.getTypeface("fonts/rmono.ttf"));
            tv.setPadding(AndroidUtilities.dp(16), AndroidUtilities.dp(12), AndroidUtilities.dp(16), AndroidUtilities.dp(12));
            tv.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));

            ScrollView scroll = new ScrollView(activity);
            scroll.addView(tv);

            new AlertDialog.Builder(activity, resourcesProvider)
                    .setTitle("Message Metadata")
                    .setView(scroll)
                    .setPositiveButton("Copy", (d, w) -> {
                        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                        if (cm != null) {
                            cm.setPrimaryClip(ClipData.newPlainText("metadata", json));
                            Toast.makeText(activity, "Metadata copied", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Close", null)
                    .show();
        } catch (Exception e) {
            Toast.makeText(activity, "Metadata error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static String toPrettyJson(MessageObject mo) throws Exception {
        TLRPC.Message m = mo.messageOwner;
        JSONObject root = new JSONObject();

        root.put("id", m.id);
        root.put("date", m.date);
        root.put("edit_date", m.edit_date);
        root.put("message", m.message != null ? m.message : "");
        root.put("out", m.out);
        root.put("mentioned", m.mentioned);
        root.put("media_unread", m.media_unread);
        root.put("silent", m.silent);
        root.put("post", m.post);
        root.put("from_scheduled", m.from_scheduled);
        root.put("legacy", m.legacy);
        root.put("edit_hide", m.edit_hide);
        root.put("pinned", m.pinned);
        root.put("noforwards", m.noforwards);
        root.put("views", m.views);
        root.put("forwards", m.forwards);
        root.put("ttl_period", m.ttl_period);
        root.put("grouped_id", m.grouped_id);
        root.put("flags", m.flags);
        root.put("flags2", m.flags2);
        root.put("via_bot_id", m.via_bot_id);
        root.put("dialog_id", mo.getDialogId());
        root.put("send_state", mo.messageOwner != null ? 0 : 0);
        root.put("type", mo.type);
        root.put("isRoundVideo", mo.isRoundVideo());
        root.put("isVoice", mo.isVoice());
        root.put("isSticker", mo.isSticker());
        root.put("isAnimatedEmoji", mo.isAnimatedEmoji());

        if (m.from_id != null) {
            JSONObject from = new JSONObject();
            from.put("user_id", m.from_id.user_id);
            from.put("chat_id", m.from_id.chat_id);
            from.put("channel_id", m.from_id.channel_id);
            root.put("from_id", from);
        }
        if (m.peer_id != null) {
            JSONObject peer = new JSONObject();
            peer.put("user_id", m.peer_id.user_id);
            peer.put("chat_id", m.peer_id.chat_id);
            peer.put("channel_id", m.peer_id.channel_id);
            root.put("peer_id", peer);
        }
        if (m.fwd_from != null) {
            JSONObject fwd = new JSONObject();
            fwd.put("date", m.fwd_from.date);
            fwd.put("channel_post", m.fwd_from.channel_post);
            fwd.put("post_author", m.fwd_from.post_author != null ? m.fwd_from.post_author : "");
            fwd.put("from_name", m.fwd_from.from_name != null ? m.fwd_from.from_name : "");
            if (m.fwd_from.from_id != null) {
                JSONObject fid = new JSONObject();
                fid.put("user_id", m.fwd_from.from_id.user_id);
                fid.put("channel_id", m.fwd_from.from_id.channel_id);
                fid.put("chat_id", m.fwd_from.from_id.chat_id);
                fwd.put("from_id", fid);
            }
            root.put("fwd_from", fwd);
        }
        if (m.reply_to != null) {
            JSONObject reply = new JSONObject();
            reply.put("reply_to_msg_id", m.reply_to.reply_to_msg_id);
            reply.put("reply_to_top_id", m.reply_to.reply_to_top_id);
            reply.put("forum_topic", m.reply_to.forum_topic);
            root.put("reply_to", reply);
        }
        if (m.entities != null && !m.entities.isEmpty()) {
            JSONArray ents = new JSONArray();
            for (int i = 0; i < m.entities.size(); i++) {
                TLRPC.MessageEntity e = m.entities.get(i);
                JSONObject en = new JSONObject();
                en.put("offset", e.offset);
                en.put("length", e.length);
                en.put("_class", e.getClass().getSimpleName());
                ents.put(en);
            }
            root.put("entities", ents);
        }
        if (m.replies != null) {
            JSONObject replies = new JSONObject();
            replies.put("replies", m.replies.replies);
            replies.put("replies_pts", m.replies.replies_pts);
            replies.put("comments", m.replies.comments);
            replies.put("channel_id", m.replies.channel_id);
            root.put("replies", replies);
        }
        if (m.reactions != null && m.reactions.results != null) {
            JSONArray arr = new JSONArray();
            for (int i = 0; i < m.reactions.results.size(); i++) {
                TLRPC.ReactionCount rc = m.reactions.results.get(i);
                JSONObject r = new JSONObject();
                r.put("count", rc.count);
                r.put("chosen", rc.chosen);
                if (rc.reaction instanceof TLRPC.TL_reactionEmoji) {
                    r.put("emoticon", ((TLRPC.TL_reactionEmoji) rc.reaction).emoticon);
                }
                arr.put(r);
            }
            root.put("reactions", arr);
        }
        if (m.media != null) {
            JSONObject media = new JSONObject();
            media.put("_class", m.media.getClass().getSimpleName());
            root.put("media", media);
        }
        if (mo.messageOwner.attachPath != null) {
            root.put("attachPath", mo.messageOwner.attachPath);
        }

        return root.toString(2);
    }
}
