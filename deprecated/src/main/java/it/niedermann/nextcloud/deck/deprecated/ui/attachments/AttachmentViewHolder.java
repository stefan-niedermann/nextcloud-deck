package it.niedermann.nextcloud.deck.deprecated.ui.attachments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.deprecated.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.deprecated.util.MimeTypeUtil;
import it.niedermann.nextcloud.sso.glide.SingleSignOnUrl;

public class AttachmentViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final Context parentContext;
    @NonNull
    private final ItemAttachmentBinding binding;

    @SuppressWarnings("WeakerAccess")
    public AttachmentViewHolder(@NonNull Context parentContext, @NonNull ItemAttachmentBinding binding) {
        super(binding.getRoot());
        this.parentContext = parentContext;
        this.binding = binding;
    }

    public void bind(@NonNull Account account, @NonNull Attachment attachment, long cardRemoteId) {
        if (MimeTypeUtil.isImage(attachment.getMimetype())) {
            binding.preview.setTransitionName(parentContext.getString(R.string.transition_attachment_preview, String.valueOf(attachment.getLocalId())));
            binding.preview.setImageResource(R.drawable.ic_image_24dp);
            binding.preview.post(() -> {
                final var requestManager = Glide.with(parentContext);

                AttachmentUtil.getThumbnailUrl(account, cardRemoteId, attachment, binding.preview.getWidth(), binding.preview.getHeight())
                        .map(Uri::toString)
                        .map(uri -> requestManager.load(new SingleSignOnUrl(account.getName(), uri)))
                        .orElseGet(() -> requestManager.load(R.drawable.ic_image_24dp))
                        .listener(new RequestListener<>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, @NonNull Target<Drawable> target, boolean isFirstResource) {
                                if (parentContext instanceof FragmentActivity) {
                                    ((FragmentActivity) parentContext).supportStartPostponedEnterTransition();
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(@NonNull Drawable resource, @NonNull Object model, Target<Drawable> target, @NonNull DataSource dataSource, boolean isFirstResource) {
                                if (parentContext instanceof FragmentActivity) {
                                    ((FragmentActivity) parentContext).supportStartPostponedEnterTransition();
                                }
                                return false;
                            }
                        })
                        .error(R.drawable.ic_image_24dp)
                        .into(binding.preview);
            });
        }
    }
}