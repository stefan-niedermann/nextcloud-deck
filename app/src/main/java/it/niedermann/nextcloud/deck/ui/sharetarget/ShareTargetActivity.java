package it.niedermann.nextcloud.deck.ui.sharetarget;

import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static it.niedermann.nextcloud.deck.util.FilesUtil.copyContentUriToTempFile;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.UploadAttachmentFailedException;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.ui.MainActivity;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;
import it.niedermann.nextcloud.deck.util.MimeTypeUtil;

public class ShareTargetActivity extends MainActivity implements SelectCardListener {

    private boolean isFile;
    private boolean cardSelected = false;

    private String receivedText;
    @NonNull
    List<Parcelable> mStreamsToUpload = new ArrayList<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            final Intent receivedIntent = getIntent();
            final String receivedAction = receivedIntent.getAction();
            final String receivedType = receivedIntent.getType();
            DeckLog.info(receivedAction);
            DeckLog.info(receivedType);
            isFile = !MimeTypeUtil.isTextPlain(receivedType);
            if (isFile) {
                if (Intent.ACTION_SEND.equals(receivedIntent.getAction())) {
                    mStreamsToUpload = Collections.singletonList(receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM));
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(receivedIntent.getAction())) {
                    @Nullable List<Parcelable> listOfParcelables = receivedIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    if (listOfParcelables != null) {
                        mStreamsToUpload.addAll(listOfParcelables);
                    }
                } else {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.error)
                            .setMessage(R.string.operation_not_yet_supported)
                            .setPositiveButton(R.string.simple_close, (a, b) -> finish())
                            .create().show();
                }
            } else {
                receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                binding.toolbar.setSubtitle(receivedText);
            }
        } catch (Throwable throwable) {
            ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public void onCardSelected(FullCard fullCard) {
        if (cardSelected) {
            return;
        }
        cardSelected = true;
        try {
            if (isFile) {
                appendFilesAndFinish(fullCard);
            } else {
                appendTextAndFinish(fullCard, receivedText);
            }
        } catch (Throwable throwable) {
            cardSelected = false;
            ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
        }
    }

    private void appendFilesAndFinish(@NonNull FullCard fullCard) {
        ShareProgressDialogFragment.newInstance().show(getSupportFragmentManager(), ShareProgressDialogFragment.class.getSimpleName());
        final var shareProgressViewModel = new ViewModelProvider(this).get(ShareProgressViewModel.class);
        shareProgressViewModel.setMax(mStreamsToUpload.size());
        shareProgressViewModel.targetCardTitle = fullCard.getCard().getTitle();

        for (Parcelable sourceStream : mStreamsToUpload) {
            if (!(sourceStream instanceof Uri)) {
                shareProgressViewModel.addException(new UploadAttachmentFailedException("Expected sourceStream to be " + Uri.class.getSimpleName() + " but was: " + (sourceStream == null ? null : sourceStream.getClass().getSimpleName())));
                return;
            }
            final Uri uri = (Uri) sourceStream;
            if (!ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
                shareProgressViewModel.addException(new UploadAttachmentFailedException("Unhandled URI scheme: " + uri.getScheme()));
                return;
            }

            new Thread(() -> {
                try {
                    final File tempFile = copyContentUriToTempFile(this, uri, fullCard.getAccountId(), fullCard.getCard().getLocalId());
                    final String mimeType = getContentResolver().getType(uri);
                    if (mimeType == null) {
                        throw new IllegalArgumentException("MimeType of uri is null. [" + uri + "]");
                    }
                    mainViewModel.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), mimeType, tempFile, new IResponseCallback<>() {
                        @Override
                        public void onResponse(Attachment response) {
                            runOnUiThread(shareProgressViewModel::increaseProgress);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            runOnUiThread(() -> {
                                if (throwable instanceof NextcloudHttpRequestFailedException && ((NextcloudHttpRequestFailedException) throwable).getStatusCode() == HTTP_CONFLICT) {
                                    IResponseCallback.super.onError(throwable);
                                    shareProgressViewModel.addDuplicateAttachment(tempFile.getName());
                                } else {
                                    shareProgressViewModel.addException(throwable);
                                }
                            });
                        }
                    });
                } catch (Throwable t) {
                    runOnUiThread(() -> shareProgressViewModel.addException(new UploadAttachmentFailedException("Error while uploading attachment for uri [" + uri + "]", t)));
                }
            }).start();
        }
    }

    private void appendTextAndFinish(@NonNull FullCard fullCard, @NonNull String receivedText) {
        final String[] targets = {getString(R.string.append_text_to_description), getString(R.string.add_text_as_comment)};
        new AlertDialog.Builder(this)
                .setOnCancelListener(dialog -> cardSelected = false)
                .setItems(targets, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            final String oldDescription = fullCard.getCard().getDescription();
                            DeckLog.info("Adding to card with id", fullCard.getCard().getId(), "(" + fullCard.getCard().getTitle() + "):", receivedText);
                            fullCard.getCard().setDescription(
                                    (oldDescription == null || oldDescription.length() == 0)
                                            ? receivedText
                                            : oldDescription + "\n\n" + receivedText
                            );
                            mainViewModel.updateCard(fullCard, new IResponseCallback<>() {
                                @Override
                                public void onResponse(FullCard response) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.share_success, "\"" + receivedText + "\"", "\"" + fullCard.getCard().getTitle() + "\""), Toast.LENGTH_LONG).show();
                                    runOnUiThread(() -> finish());
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    IResponseCallback.super.onError(throwable);
                                    runOnUiThread(() -> {
                                        cardSelected = false;
                                        ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(getSupportFragmentManager(), ExceptionDialogFragment.class.getSimpleName());
                                    });
                                }
                            });
                            break;
                        case 1:
                            final var currentAccount = mainViewModel.getCurrentAccount();
                            final var comment = new DeckComment(receivedText.trim(), currentAccount.getUserName(), Instant.now());
                            mainViewModel.addCommentToCard(currentAccount.getId(), fullCard.getLocalId(), comment);
                            Toast.makeText(getApplicationContext(), getString(R.string.share_success, "\"" + receivedText + "\"", "\"" + fullCard.getCard().getTitle() + "\""), Toast.LENGTH_LONG).show();
                            finish();
                            break;
                    }
                }).create().show();
    }

    @Override
    protected void setCurrentBoard(@NonNull Board board) {
        super.setCurrentBoard(board);
        binding.toolbar.setTitle(R.string.simple_select);

        // Show Fab so we can add new lists/cards
        showFabIfEditPermissionGranted();
    }

    // @Override
    // protected void showFabIfEditPermissionGranted() { /* Silence is gold */ }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}