package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.selection.Selection;
import androidx.recyclerview.selection.SelectionTracker;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.DeleteDialogBuilder;

import static android.content.Context.CLIPBOARD_SERVICE;

public class ActionModeController implements ActionMode.Callback {

    @NonNull
    private final Context context;
    @NonNull
    private final String accountUrl;
    private final long cardRemoteId;
    @NonNull
    private final List<Attachment> attachments;
    @NonNull
    private final SelectionTracker<Long> selectionTracker;
    @NonNull
    private final AttachmentDeletedListener attachmentDeletedListener;

    ActionModeController(
            @NonNull Context context, @NonNull String accountUrl,
            long cardRemoteId, @NonNull List<Attachment> attachments,
            @NonNull SelectionTracker<Long> selectionTracker,
            @NonNull AttachmentDeletedListener attachmentDeletedListener) {
        this.context = context;
        this.accountUrl = accountUrl;
        this.cardRemoteId = cardRemoteId;
        this.attachments = attachments;
        this.selectionTracker = selectionTracker;
        this.attachmentDeletedListener = attachmentDeletedListener;
    }

    @Override
    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
        actionMode.getMenuInflater().inflate(R.menu.attachment_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
        menu.findItem(android.R.id.copyUrl).setVisible(selectionTracker.getSelection().size() == 1);
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
        Selection<Long> s = selectionTracker.getSelection();
        switch (menuItem.getItemId()) {
            case R.id.delete:
                new DeleteDialogBuilder(context)
                        .setTitle(context.getString(R.string.delete_something, String.valueOf(s.size())))
                        .setMessage(R.string.attachment_delete_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.simple_delete, (dialog, which) -> {
                            for (Long attachmentLocalId : s) {
                                attachmentDeletedListener.onAttachmentDeleted(attachmentLocalId);
                            }
                            actionMode.finish();
                        })
                        .show();
                return true;
            case android.R.id.copyUrl:
                for (Long id : s) {
                    for (Attachment attachment : attachments) {
                        if (id.equals(attachment.getLocalId())) {
                            @Nullable final String uri = AttachmentUtil.getUrl(accountUrl, cardRemoteId, attachment.getId());
                            if (uri == null) {
                                Toast.makeText(context, "Not yet synced", Toast.LENGTH_SHORT).show();
                                return false;
                            }
                            final ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText(attachment.getFilename(), uri);
                            if (clipboardManager == null) {
                                DeckLog.error("clipboardManager is null");
                                return false;
                            } else {
                                clipboardManager.setPrimaryClip(clipData);
                                Toast.makeText(context, R.string.simple_copied, Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    }
                }
                return false;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode actionMode) {
        selectionTracker.clearSelection();
    }
}