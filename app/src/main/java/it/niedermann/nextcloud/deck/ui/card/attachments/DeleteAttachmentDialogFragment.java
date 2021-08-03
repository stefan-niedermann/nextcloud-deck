package it.niedermann.nextcloud.deck.ui.card.attachments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.ui.branding.DeleteAlertDialogBuilder;

public class DeleteAttachmentDialogFragment extends DialogFragment {

    private static final String KEY_ATTACHMENT = "attachment";

    private AttachmentDeletedListener deleteAttachmentListener;
    private Attachment attachment;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof AttachmentDeletedListener) {
            this.deleteAttachmentListener = (AttachmentDeletedListener) getParentFragment();
        } else if (context instanceof AttachmentDeletedListener) {
            this.deleteAttachmentListener = (AttachmentDeletedListener) context;
        } else {
            throw new ClassCastException("Context or parent fragment must implement " + AttachmentDeletedListener.class.getCanonicalName());
        }

        if (getArguments() == null || !getArguments().containsKey(KEY_ATTACHMENT)) {
            throw new IllegalArgumentException("Please provide at least " + KEY_ATTACHMENT + " as an argument");
        } else {
            this.attachment = (Attachment) getArguments().getSerializable(KEY_ATTACHMENT);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DeleteAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_something, attachment.getFilename()))
                .setMessage(R.string.attachment_delete_message)
                .setPositiveButton(R.string.simple_delete, (dialog, whichButton) -> deleteAttachmentListener.onAttachmentDeleted(attachment))
                .setNeutralButton(android.R.string.cancel, null)
                .create();
    }

    public static DialogFragment newInstance(Attachment attachment) {
        final var dialog = new DeleteAttachmentDialogFragment();

        final var args = new Bundle();
        args.putSerializable(KEY_ATTACHMENT, attachment);
        dialog.setArguments(args);

        return dialog;
    }
}
