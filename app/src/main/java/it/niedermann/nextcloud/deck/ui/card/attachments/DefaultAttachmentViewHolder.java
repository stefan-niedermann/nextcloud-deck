package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.text.format.Formatter;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.databinding.ItemAttachmentDefaultBinding;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.util.AttachmentUtil;
import it.niedermann.nextcloud.deck.util.DateUtil;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

public class DefaultAttachmentViewHolder extends AttachmentViewHolder {
    private ItemAttachmentDefaultBinding binding;

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

    public void bind(@NonNull Account account, @NonNull MenuInflater menuInflater, @NonNull FragmentManager fragmentManager, Long cardRemoteId, Attachment attachment, @Nullable View.OnClickListener onClickListener, @ColorInt int mainColor) {
        super.bind(account, menuInflater, fragmentManager, cardRemoteId, attachment, onClickListener, mainColor);

        if (MimeTypeUtil.isAudio(attachment.getMimetype())) {
            getPreview().setImageResource(R.drawable.ic_music_note_grey600_24dp);
        } else if (MimeTypeUtil.isVideo(attachment.getMimetype())) {
            getPreview().setImageResource(R.drawable.ic_local_movies_grey600_24dp);
        } else if (MimeTypeUtil.isPdf(attachment.getMimetype())) {
            getPreview().setImageResource(R.drawable.ic_baseline_picture_as_pdf_24);
        } else if (MimeTypeUtil.isContact(attachment.getMimetype())) {
            getPreview().setImageResource(R.drawable.ic_baseline_contact_mail_24);
        } else {
            getPreview().setImageResource(R.drawable.ic_attach_file_grey600_24dp);
        }

        itemView.setOnClickListener((event) -> AttachmentUtil.openAttachmentInBrowser(itemView.getContext(), account.getUrl(), cardRemoteId, attachment.getId()));
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
    }
}