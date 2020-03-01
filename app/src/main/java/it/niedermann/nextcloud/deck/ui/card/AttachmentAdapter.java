package it.niedermann.nextcloud.deck.ui.card;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.DeleteDialogBuilder;

import static android.content.Context.CLIPBOARD_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;

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
                return new ImageAttachmentViewHolder(ItemAttachmentImageBinding.inflate(LayoutInflater.from(context), parent, false));
            }
            default: {
                return new DefaultAttachmentViewHolder(ItemAttachmentDefaultBinding.inflate(LayoutInflater.from(context), parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        Attachment attachment = attachments.get(position);
        int viewType = getItemViewType(position);
        String uri = account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId();
        holder.setNotSyncedYetStatus(attachment.getStatusEnum() == DBStatus.UP_TO_DATE);
        holder.getRootView().setOnCreateContextMenuListener((menu, v, menuInfo) -> {
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
            menu.findItem(android.R.id.copyUrl).setOnMenuItemClickListener(item -> {
                final android.content.ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(attachment.getFilename(), uri);
                if (clipboardManager != null) {
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, R.string.simple_copied, Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "clipboardManager is null");
                }
                return false;
            });
        });

        if (attachment.getMimetype() != null) {
            if (attachment.getMimetype().startsWith("image")) {
                // TODO Glide is currently not yet able to use SSO and fails on authentication
                Glide.with(context)
                        .load(uri)
                        .transform(new CenterCrop())
                        .error(R.drawable.ic_image_grey600_24dp)
                        .into(holder.getPreview());
                holder.getPreview().setImageResource(R.drawable.ic_image_grey600_24dp);
                holder.getPreview().getRootView().setOnClickListener((v) -> AttachmentDialogFragment.newInstance(uri, attachment.getBasename()).show(((AppCompatActivity) context).getSupportFragmentManager(), "preview"));
            } else if (attachment.getMimetype().startsWith("audio")) {
                holder.getPreview().setImageResource(R.drawable.ic_music_note_grey600_24dp);
            } else if (attachment.getMimetype().startsWith("video")) {
                holder.getPreview().setImageResource(R.drawable.ic_local_movies_grey600_24dp);
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
                defaultHolder.binding.filename.getRootView().setOnClickListener((event) -> {
                    Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
                    openURL.setData(Uri.parse(account.getUrl() + "/index.php/apps/deck/cards/" + cardRemoteId + "/attachment/" + attachment.getId()));
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

    static abstract class AttachmentViewHolder extends RecyclerView.ViewHolder {
        AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        abstract View getRootView();

        abstract ImageView getPreview();

        abstract void setNotSyncedYetStatus(boolean synced);
    }

    static class DefaultAttachmentViewHolder extends AttachmentViewHolder {
        ItemAttachmentDefaultBinding binding;

        private DefaultAttachmentViewHolder(ItemAttachmentDefaultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        View getRootView() {
            return binding.getRoot();
        }

        @Override
        ImageView getPreview() {
            return binding.preview;
        }

        @Override
        void setNotSyncedYetStatus(boolean synced) {
            binding.notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
        }
    }

    static class ImageAttachmentViewHolder extends AttachmentViewHolder {
        ItemAttachmentImageBinding binding;

        private ImageAttachmentViewHolder(ItemAttachmentImageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        View getRootView() {
            return binding.getRoot();
        }

        @Override
        ImageView getPreview() {
            return binding.preview;
        }

        @Override
        void setNotSyncedYetStatus(boolean synced) {
            binding.notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
        }
    }

    public interface AttachmentDeletedListener {
        void onAttachmentDeleted(Attachment attachment);
    }
}
