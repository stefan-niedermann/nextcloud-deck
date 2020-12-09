package it.niedermann.nextcloud.deck.ui.attachments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

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
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                binding.preview.setTransitionName(parentContext.getString(R.string.transition_attachment_preview, String.valueOf(attachment.getLocalId())));
            }
            binding.preview.setImageResource(R.drawable.ic_image_grey600_24dp);
            binding.preview.post(() -> {
                final String uri = AttachmentUtil.getThumbnailUrl(account.getServerDeckVersionAsObject(), account.getUrl(), cardRemoteId, attachment, binding.preview.getWidth());
                Glide.with(parentContext)
                        .load(uri)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                if (parentContext instanceof FragmentActivity) {
                                    ((FragmentActivity) parentContext).supportStartPostponedEnterTransition();
                                }
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                if (parentContext instanceof FragmentActivity) {
                                    ((FragmentActivity) parentContext).supportStartPostponedEnterTransition();
                                }
                                return false;
                            }
                        })
                        .error(R.drawable.ic_image_grey600_24dp)
                        .into(binding.preview);
            });
        }
    }
}