package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.DeleteDialogBuilder;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

    public static final int VIEW_TYPE_DEFAULT = 2;
    public static final int VIEW_TYPE_IMAGE = 1;

    private final Account account;
    private final long cardRemoteId;
    @NonNull
    private List<Attachment> attachments;
    @NonNull
    private AttachmentDeletedListener attachmentDeletedListener;
    private Context context;

    AttachmentAdapter(@NonNull AttachmentDeletedListener attachmentDeletedListener, @NonNull Account account, long cardRemoteId, @NonNull List<Attachment> attachments) {
        super();
        this.attachmentDeletedListener = attachmentDeletedListener;
        this.attachments = attachments;
        this.account = account;
        this.cardRemoteId = cardRemoteId;
    }

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        //noinspection SwitchStatementWithTooFewBranches
        switch (viewType) {
            case VIEW_TYPE_IMAGE: {
                return new ImageAttachmentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_attachment_image, parent, false));
            }
            default: {
                return new DefaultAttachmentViewHolder(LayoutInflater.from(context).inflate(R.layout.item_attachment_default, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        Attachment attachment = attachments.get(position);
        int viewType = getItemViewType(position);
        holder.notSyncedYet.setVisibility(attachment.getStatusEnum() == DBStatus.UP_TO_DATE ? View.GONE : View.VISIBLE);
        holder.preview.getRootView().setOnCreateContextMenuListener((menu, v, menuInfo) -> {
            ((Activity) context).getMenuInflater().inflate(R.menu.attachment_menu, menu);
            menu.findItem(R.id.delete).setOnMenuItemClickListener(item -> {
                new DeleteDialogBuilder(context)
                        .setTitle(context.getString(R.string.delete_something, attachment.getFilename()))
                        .setMessage(R.string.attachment_delete_message)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(R.string.simple_delete, (dialog, which) -> attachmentDeletedListener.onAttachmentDeleted(attachment))
                        .show();
                return false;
            });
        });

        if (attachment.getMimetype() != null) {
            if (attachment.getMimetype().startsWith("image")) {
                // TODO Glide is currently not yet able to use SSO and fails on authentication
                String uri = account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId();
                Glide.with(context)
                        .load(uri)
                        .transform(new CenterCrop())
                        .error(R.drawable.ic_image_grey600_24dp)
                        .into(holder.preview);
                holder.preview.setImageResource(R.drawable.ic_image_grey600_24dp);
                holder.preview.getRootView().setOnClickListener((v) -> AttachmentDialogFragment.newInstance(uri, attachment.getBasename()).show(((AppCompatActivity) context).getSupportFragmentManager(), "preview"));
            } else if (attachment.getMimetype().startsWith("audio")) {
                holder.preview.setImageResource(R.drawable.ic_music_note_grey600_24dp);
            } else if (attachment.getMimetype().startsWith("video")) {
                holder.preview.setImageResource(R.drawable.ic_local_movies_grey600_24dp);
            }
        }

        //noinspection SwitchStatementWithTooFewBranches
        switch (viewType) {
            case VIEW_TYPE_IMAGE: {
                ImageAttachmentViewHolder imageHolder = (ImageAttachmentViewHolder) holder;
                break;
            }
            default: {
                DefaultAttachmentViewHolder defaultHolder = (DefaultAttachmentViewHolder) holder;
                defaultHolder.filename.getRootView().setOnClickListener((event) -> {
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    openURL.setData(Uri.parse(account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId()));
                    context.startActivity(openURL);
                });
                defaultHolder.filename.setText(attachment.getBasename());
                defaultHolder.filesize.setText(Formatter.formatFileSize(context, attachment.getFilesize()));
                if (attachment.getLastModifiedLocal() != null) {
                    defaultHolder.modified.setText(DateUtil.getRelativeDateTimeString(context, attachment.getLastModifiedLocal().getTime()));
                    defaultHolder.modified.setVisibility(View.VISIBLE);
                } else if (attachment.getLastModified() != null) {
                    defaultHolder.modified.setText(DateUtil.getRelativeDateTimeString(context, attachment.getLastModified().getTime()));
                    defaultHolder.modified.setVisibility(View.VISIBLE);
                } else {
                    defaultHolder.modified.setVisibility(View.GONE);
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

    static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.preview)
        AppCompatImageView preview;
        @BindView(R.id.not_synced_yet)
        AppCompatImageView notSyncedYet;

        AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class DefaultAttachmentViewHolder extends AttachmentViewHolder {
        @BindView(R.id.filename)
        TextView filename;
        @BindView(R.id.filesize)
        TextView filesize;
        @BindView(R.id.modified)
        TextView modified;

        private DefaultAttachmentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ImageAttachmentViewHolder extends AttachmentViewHolder {

        private ImageAttachmentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface AttachmentDeletedListener {
        void onAttachmentDeleted(Attachment attachment);
    }
}
