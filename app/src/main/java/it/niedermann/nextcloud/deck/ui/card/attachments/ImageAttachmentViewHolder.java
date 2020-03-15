package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.List;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;

class ImageAttachmentViewHolder extends AttachmentViewHolder {
    @NonNull
    public final ItemAttachmentImageBinding binding;
    @NonNull
    private final Context context;
    @NonNull
    private final String accountUrl;
    private final long cardRemoteId;

    ImageAttachmentViewHolder(
            @NonNull ItemAttachmentImageBinding binding,
            @NonNull List<Attachment> attachments,
            @NonNull Context context,
            @NonNull String accountUrl,
            long cardRemoteId
    ) {
        super(binding.getRoot(), attachments);
        this.binding = binding;
        this.context = context;
        this.accountUrl = accountUrl;
        this.cardRemoteId = cardRemoteId;
    }

    @Override
    public void bind(Attachment attachment, boolean selected) {
        super.bind(attachment, selected);
        @Nullable final String uri = attachment.getId() == null ? null : AttachmentUtil.getUrl(accountUrl, cardRemoteId, attachment.getId());
        Glide.with(context)
                .load(uri)
                .error(R.drawable.ic_image_grey600_24dp)
                .into(getPreview());
    }

    @Override
    protected ImageView getPreview() {
        return binding.preview;
    }

    @Override
    protected void setNotSyncedYetStatus(boolean synced) {
        binding.notSyncedYet.setVisibility(synced ? View.GONE : View.VISIBLE);
    }

}