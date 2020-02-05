package it.niedermann.nextcloud.deck.ui.card;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.util.DeleteDialogBuilder;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.AttachmentViewHolder> {

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
        View v = LayoutInflater.from(context).inflate(R.layout.item_attachment, parent, false);
        return new AttachmentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        Attachment attachment = attachments.get(position);
        holder.notSyncedYet.setVisibility(attachment.getStatusEnum() == DBStatus.UP_TO_DATE ? View.GONE: View.VISIBLE);
        if (attachment.getMimetype().startsWith("image")) {
            // TODO Glide is currently not yet able to use SSO and fails on authentication
//            String uri = account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId();
//            Glide.with(context)
//                    .load(uri)
//                    .error(R.drawable.ic_image_grey600_24dp)
//                    .apply(RequestOptions.circleCropTransform())
//                    .into(holder.filetype);
            holder.filetype.setImageResource(R.drawable.ic_image_grey600_24dp);
        } else if (attachment.getMimetype().startsWith("audio")) {
            holder.filetype.setImageResource(R.drawable.ic_music_note_grey600_24dp);
        } else if (attachment.getMimetype().startsWith("video")) {
            holder.filetype.setImageResource(R.drawable.ic_local_movies_grey600_24dp);
        }
        holder.filename.setText(attachment.getBasename());
        holder.filesize.setText(Formatter.formatFileSize(context, attachment.getFilesize()));
        if (attachment.getLastModifiedLocal() != null) {
            holder.modified.setText(DateUtils.getRelativeTimeSpanString(context, attachment.getLastModifiedLocal().getTime()));
        }
        holder.filename.getRootView().setOnClickListener((event) -> {
            Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
            openURL.setData(Uri.parse(account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId()));
            context.startActivity(openURL);
        });
        holder.deleteButton.setOnClickListener((v) -> {
            new DeleteDialogBuilder(context)
                    .setTitle(context.getString(R.string.delete_something, attachment.getFilename()))
                    .setMessage(R.string.attachment_delete_message)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(R.string.simple_delete, (dialog, which) -> attachmentDeletedListener.onAttachmentDeleted(attachment))
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return attachments.size();
    }

    static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.not_synced_yet)
        AppCompatImageView notSyncedYet;
        @BindView(R.id.filetype)
        AppCompatImageView filetype;
        @BindView(R.id.filename)
        TextView filename;
        @BindView(R.id.filesize)
        TextView filesize;
        @BindView(R.id.modified)
        TextView modified;
        @BindView(R.id.deleteButton)
        ImageButton deleteButton;

        private AttachmentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface AttachmentDeletedListener {
        void onAttachmentDeleted(Attachment attachment);
    }
}
