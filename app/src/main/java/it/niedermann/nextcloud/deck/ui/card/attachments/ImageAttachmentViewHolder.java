package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.net.Uri;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentImageBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

public class ImageAttachmentViewHolder extends AttachmentViewHolder {

    @NonNull
    private final ItemAttachmentImageBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ImageAttachmentViewHolder(@NonNull ItemAttachmentImageBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    @Override
    protected ImageView getPreview() {
        return binding.preview;
    }

    @Override
    protected ImageView getNotSyncedYetStatusIcon() {
        return binding.notSyncedYet;
    }

    public void bind(@NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull FragmentManager fragmentManager, Long cardRemoteId, Attachment attachment, @Nullable View.OnClickListener onClickListener, @ColorInt int color) {
        super.bind(account, menuInflater, fragmentManager, cardRemoteId, attachment, onClickListener, color);

        getPreview().post(() -> {
            final var requestManager = Glide.with(getPreview().getContext());
            AttachmentUtil.getThumbnailUrl(account, cardRemoteId, attachment, getPreview().getWidth())
                    .map(Uri::toString)
                    .map(uri -> requestManager.load(new SingleSignOnUrl(account.getName(), uri)))
                    .orElseGet(() -> requestManager.load(R.drawable.ic_image_24dp))
                    .placeholder(R.drawable.ic_image_24dp)
                    .error(R.drawable.ic_image_24dp)
                    .into(getPreview());
        });

        itemView.setOnClickListener(onClickListener);
    }
}