package it.niedermann.nextcloud.deck.ui.card.attachments;

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

public class ImageAttachmentViewHolder extends AttachmentViewHolder {
    private final ItemAttachmentImageBinding binding;

    @SuppressWarnings("WeakerAccess")
    public ImageAttachmentViewHolder(ItemAttachmentImageBinding binding) {
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

    public void bind(@NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull FragmentManager fragmentManager, Long cardRemoteId, Attachment attachment, @Nullable View.OnClickListener onClickListener, @ColorInt int mainColor) {
        super.bind(menuInflater, fragmentManager, cardRemoteId, attachment, onClickListener, mainColor, AttachmentUtil.getRemoteOrLocalUrl(account.getUrl(), cardRemoteId, attachment));

        getPreview().post(() -> {
            @Nullable final String uri = AttachmentUtil.getThumbnailUrl(account.getServerDeckVersionAsObject(), account.getUrl(), cardRemoteId, attachment, getPreview().getWidth());
            Glide.with(getPreview().getContext())
                    .load(uri)
                    .placeholder(R.drawable.ic_image_grey600_24dp)
                    .error(R.drawable.ic_image_grey600_24dp)
                    .into(getPreview());
        });

        itemView.setOnClickListener(onClickListener);
    }
}