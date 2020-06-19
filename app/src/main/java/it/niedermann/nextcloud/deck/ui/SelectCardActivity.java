package it.niedermann.nextcloud.deck.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class SelectCardActivity extends MainActivity implements SelectCardListener {

    private static final String MIMETYPE_TEXT_PREFIX = "text/";

    private boolean isFile;

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
            isFile = receivedType != null && !receivedType.startsWith(MIMETYPE_TEXT_PREFIX);
            if (isFile) {
                if (Intent.ACTION_SEND.equals(receivedIntent.getAction())) {
                    mStreamsToUpload = Collections.singletonList(receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM));
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(receivedIntent.getAction())) {
                    @Nullable List<Parcelable> listOfParcelables = receivedIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                    if (listOfParcelables != null) {
                        mStreamsToUpload.addAll(listOfParcelables);
                    }
                } else {
                    new BrandedAlertDialogBuilder(this)
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
            handleException(throwable);
        }
    }

    @Override
    public void onCardSelected(FullCard fullCard) {
        try {
            if (isFile) {
                appendAttachment(fullCard);
            } else {
                appendText(fullCard, receivedText);
            }
            syncManager.updateCard(fullCard);
            finish();
        } catch (Throwable throwable) {
            handleException(throwable);
        }
    }

    private void handleException(Throwable throwable) {
        DeckLog.logError(throwable);
        String debugInfos = ExceptionUtil.getDebugInfos(this, throwable, mainViewModel.getCurrentAccount());
        final AlertDialog dialog = new BrandedAlertDialogBuilder(this)
                .setTitle(R.string.error)
                .setMessage(debugInfos)
                .setPositiveButton(android.R.string.copy, (a, b) -> {
                    copyToClipboard(this, throwable.getMessage(), "```\n" + debugInfos + "\n```");
                    finish();
                })
                .setNeutralButton(R.string.simple_close, null)
                .create();
        dialog.show();
        ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(Typeface.MONOSPACE);
    }

    private void appendText(@NonNull FullCard fullCard, @NonNull String receivedText) {
        // TODO display dialog and ask whether the text should be appended to description or added as a new comment
        String oldDescription = fullCard.getCard().getDescription();
        DeckLog.info("Adding to card #" + fullCard.getCard().getId() + " (" + fullCard.getCard().getTitle() + "): Text \"" + receivedText + "\"");
        if (oldDescription == null || oldDescription.length() == 0) {
            fullCard.getCard().setDescription(receivedText);
        } else {
            fullCard.getCard().setDescription(oldDescription + "\n\n" + receivedText);
        }
        Toast.makeText(getApplicationContext(), getString(R.string.share_success, "\"" + receivedText + "\"", "\"" + fullCard.getCard().getTitle() + "\""), Toast.LENGTH_LONG).show();
    }

    private void appendAttachment(@NonNull FullCard fullCard) {

        List<Uri> contentUris = new ArrayList<>();

        for (Parcelable sourceStream : mStreamsToUpload) {
            Uri sourceUri = (Uri) sourceStream;
            if (sourceUri != null) {
                if (ContentResolver.SCHEME_CONTENT.equals(sourceUri.getScheme())) {
                    contentUris.add(sourceUri);
                    DeckLog.verbose("--- found content URL, remember for later: " + sourceUri.getPath());
                } else if (ContentResolver.SCHEME_FILE.equals(sourceUri.getScheme())) {
                    /// file: uris should point to a local file, should be safe let FileUploader handle them
                    DeckLog.verbose("--- found file URL, directly upload: " + sourceUri.getPath());
                    syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), Attachment.getMimetypeForUri(this, sourceUri), new File(sourceUri.getPath()));
                }
            }
        }

        if (!contentUris.isEmpty()) {
            /// content: uris will be copied to temporary files before calling {@link FileUploader}
            for (Uri contentUri : contentUris) {
                try {
                    DeckLog.verbose("---- so, now copy&upload: " + contentUri.getPath());
                    File copiedFile = copyContentUriToTempFile(contentUri, fullCard.getAccountId(), fullCard.getCard().getLocalId());
                    syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), getContentResolver().getType(contentUri), copiedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private File copyContentUriToTempFile(Uri currentUri, long accountId, Long localId) throws IOException {
        String fullTempPath = getApplicationContext().getFilesDir().getAbsolutePath() + '/' + accountId + '/' + localId + '/' + UUID.randomUUID() + '/' + currentUri.getLastPathSegment();
        DeckLog.verbose("----- fullTempPath: " + fullTempPath);
        InputStream inputStream = getContentResolver().openInputStream(currentUri);
        if (inputStream == null) {
            throw new IOException("Could not open input stream for " + currentUri.getPath());
        }
        File cacheFile = new File(fullTempPath);
        File tempDir = cacheFile.getParentFile();
        if (tempDir == null) {
            throw new FileNotFoundException("could not cacheFile.getPranetFile()");
        }
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                throw new IOException("Directory for temporary file does not exist and could not be created.");
            }
        }
        if (!cacheFile.createNewFile()) {
            throw new IOException("Failed to create cacheFile");
        }
        FileOutputStream outputStream = new FileOutputStream(fullTempPath);
        byte[] buffer = new byte[4096];

        int count;
        while ((count = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, count);
        }
        DeckLog.verbose("----- wrote");
        return cacheFile;
    }

    @Override
    protected void setCurrentBoard(@NonNull Board board) {
        super.setCurrentBoard(board);
        binding.listMenuButton.setVisibility(View.GONE);
        binding.fab.setVisibility(View.GONE);
        binding.toolbar.setTitle(R.string.simple_select);
    }

    @Override
    protected void showFabIfEditPermissionGranted() { /* Silence is gold */ }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}