package it.niedermann.nextcloud.deck.ui.card.attachments;

import static it.niedermann.nextcloud.deck.util.AttachmentUtil.generateOpenAttachmentIntent;
import static it.niedermann.nextcloud.deck.util.AttachmentUtil.getIconForMimeType;

import android.text.format.Formatter;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.nextcloud.android.common.ui.theme.utils.ColorRole;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.ui.theme.ThemeUtils;
import it.niedermann.nextcloud.deck.util.DateUtil;

public class DefaultAttachmentViewHolder extends AttachmentViewHolder {
    private final ItemAttachmentDefaultBinding binding;

    @SuppressWarnings("WeakerAccess")
    public DefaultAttachmentViewHolder(ItemAttachmentDefaultBinding binding) {
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
        getPreview().setImageResource(getIconForMimeType(attachment.getMimetype()));
        itemView.setOnClickListener((event) -> {
            final var intent = generateOpenAttachmentIntent(account, itemView.getContext(), cardRemoteId, attachment);
            if (intent.isPresent()) {
                itemView.getContext().startActivity(intent.get());
            } else {
                Toast.makeText(itemView.getContext(), R.string.attachment_does_not_yet_exist, Toast.LENGTH_LONG).show();
                DeckLog.logError(new IllegalArgumentException("attachmentRemoteId must not be null."));
            }
        });
        binding.filename.setText(attachment.getBasename());
        binding.filesize.setText(Formatter.formatFileSize(binding.filesize.getContext(), attachment.getFilesize()));
        if (attachment.getLastModifiedLocal() != null) {
            binding.modified.setText(DateUtil.getRelativeDateTimeString(binding.modified.getContext(), attachment.getLastModifiedLocal().toEpochMilli()));
            binding.modified.setVisibility(View.VISIBLE);
        } else if (attachment.getLastModified() != null) {
            binding.modified.setText(DateUtil.getRelativeDateTimeString(binding.modified.getContext(), attachment.getLastModified().toEpochMilli()));
            binding.modified.setVisibility(View.VISIBLE);
        } else {
            binding.modified.setVisibility(View.GONE);
        }

        applyTheme(color);
    }

    protected void applyTheme(@ColorInt int color) {
        super.applyTheme(color);

        final var utils = ThemeUtils.of(color, getPreview().getContext());

        utils.platform.colorTextView(binding.filename, ColorRole.ON_SURFACE);
        utils.platform.colorImageView(getPreview(), ColorRole.ON_SURFACE_VARIANT);
        utils.platform.colorTextView(binding.filesize, ColorRole.ON_SURFACE_VARIANT);
        utils.platform.colorTextView(binding.modified, ColorRole.ON_SURFACE_VARIANT);
    }
}