package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.ui.AttachmentsActivity;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.DateUtil;

import static it.niedermann.nextcloud.deck.ui.AttachmentsActivity.BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_ACCOUNT_ID;
import static it.niedermann.nextcloud.deck.ui.card.CardAdapter.BUNDLE_KEY_LOCAL_ID;

public class CardAttachmentAdapter extends RecyclerView.Adapter<AttachmentViewHolder> {

    public static final int VIEW_TYPE_DEFAULT = 2;
    public static final int VIEW_TYPE_IMAGE = 1;

    private final Account account;
    private final long cardRemoteId;
    private final long cardLocalId;
    @NonNull
    private List<Attachment> attachments;
    @Nullable
    private final AttachmentClickedListener attachmentClickedListener;
    private Context context;
    private SelectionTracker<Long> selectionTracker;

    CardAttachmentAdapter(
            @Nullable AttachmentClickedListener attachmentClickedListener,
            @NonNull Account account,
            long cardLocalId,
            long cardRemoteId,
            @NonNull List<Attachment> attachments
    ) {
        super();
        this.attachmentClickedListener = attachmentClickedListener;
        this.attachments = attachments;
        this.account = account;
        this.cardRemoteId = cardRemoteId;
        this.cardLocalId = cardLocalId;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        //noinspection SwitchStatementWithTooFewBranches
        switch (viewType) {
            case VIEW_TYPE_IMAGE: {
                return new ImageAttachmentViewHolder(
                        ItemAttachmentImageBinding.inflate(LayoutInflater.from(context), parent, false),
                        attachments,
                        context,
                        account.getUrl(),
                        cardRemoteId
                );
            }
            default: {
                return new DefaultAttachmentViewHolder(
                        ItemAttachmentDefaultBinding.inflate(LayoutInflater.from(context), parent, false),
                        attachments
                );
            }
        }
    }

    @Override
    public long getItemId(int position) {
        return attachments.get(position).getLocalId();
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        final Attachment attachment = attachments.get(position);
        final int viewType = getItemViewType(position);
        @Nullable final String uri = attachment.getId() == null ? null : AttachmentUtil.getUrl(account.getUrl(), cardRemoteId, attachment.getId());
        holder.setNotSyncedYetStatus(attachment.getStatusEnum() == DBStatus.UP_TO_DATE);

        if (attachment.getMimetype() != null) {
            if (attachment.getMimetype().startsWith("image")) {
                Glide.with(context)
                        .load(uri)
                        .error(R.drawable.ic_image_grey600_24dp)
                        .into(holder.getPreview());
                holder.getPreview().setImageResource(R.drawable.ic_image_grey600_24dp);
                holder.getPreview().getRootView().setOnClickListener((v) -> {
                    if (selectionTracker != null && !selectionTracker.hasSelection()) {
                        if (attachmentClickedListener != null) {
                            attachmentClickedListener.onAttachmentClicked(position);
                        }
                        Intent intent = new Intent(context, AttachmentsActivity.class);
                        intent.putExtra(BUNDLE_KEY_ACCOUNT_ID, account.getId());
                        intent.putExtra(BUNDLE_KEY_LOCAL_ID, cardLocalId);
                        intent.putExtra(BUNDLE_KEY_CURRENT_ATTACHMENT_LOCAL_ID, attachment.getLocalId());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && context instanceof Activity) {
                            String transitionName = context.getString(R.string.transition_attachment_preview, String.valueOf(attachment.getLocalId()));
                            holder.getPreview().setTransitionName(transitionName);
                            context.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.getPreview(), transitionName).toBundle());
                        } else {
                            context.startActivity(intent);
                        }
                    }
                });
            } else if (attachment.getMimetype().startsWith("audio")) {
                holder.getPreview().setImageResource(R.drawable.ic_music_note_grey600_24dp);
            } else if (attachment.getMimetype().startsWith("video")) {
                holder.getPreview().setImageResource(R.drawable.ic_local_movies_grey600_24dp);
            }
            DeckLog.info("SELECTION ===== ViewHolder: SelectionTracker is null: " + (selectionTracker == null));
            DeckLog.info("SELECTION ===== ViewHolder: Attachment: " + attachment.getLocalId() + " selectionTracker.isSelected: " + (selectionTracker != null && selectionTracker.isSelected(attachment.getLocalId())));
            holder.bind(attachment, selectionTracker != null && selectionTracker.isSelected(attachment.getLocalId()));
        }

        //noinspection SwitchStatementWithTooFewBranches
        switch (viewType) {
            case VIEW_TYPE_IMAGE: {
//                ImageAttachmentViewHolder imageHolder = (ImageAttachmentViewHolder) holder;
                break;
            }
            default: {
                DefaultAttachmentViewHolder defaultHolder = (DefaultAttachmentViewHolder) holder;
                defaultHolder.binding.filename.getRootView().setOnClickListener((event) -> {
                    Intent openURL = new Intent(Intent.ACTION_VIEW);
                    openURL.setData(Uri.parse(AttachmentUtil.getUrl(account.getUrl(), cardRemoteId, attachment.getId())));
                    context.startActivity(openURL);
                });
                defaultHolder.binding.filename.setText(attachment.getBasename());
                defaultHolder.binding.filesize.setText(Formatter.formatFileSize(context, attachment.getFilesize()));
                if (attachment.getLastModifiedLocal() != null) {
                    defaultHolder.binding.modified.setText(DateUtil.getRelativeDateTimeString(context, attachment.getLastModifiedLocal().getTime()));
                    defaultHolder.binding.modified.setVisibility(View.VISIBLE);
                } else if (attachment.getLastModified() != null) {
                    defaultHolder.binding.modified.setText(DateUtil.getRelativeDateTimeString(context, attachment.getLastModified().getTime()));
                    defaultHolder.binding.modified.setVisibility(View.VISIBLE);
                } else {
                    defaultHolder.binding.modified.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        String mimeType = attachments.get(position).getMimetype();
        return (mimeType != null && mimeType.startsWith("image")) ? VIEW_TYPE_IMAGE : VIEW_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        DeckLog.info("SELECTION ==== Adapter: Set Selection Tracker" + selectionTracker);
        this.selectionTracker = selectionTracker;
    }

}
