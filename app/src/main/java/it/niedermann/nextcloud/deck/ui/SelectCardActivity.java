package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.branding.BrandedAlertDialogBuilder;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;
import it.niedermann.nextcloud.deck.util.FileUtils;

import static it.niedermann.nextcloud.deck.util.ClipboardUtil.copyToClipboard;

public class SelectCardActivity extends MainActivity implements SelectCardListener {

    private static final String MIMETYPE_TEXT_PREFIX = "text/";

    private boolean isFile;

    private String receivedText;
    private Uri receivedUri;
    private File uploadFile;

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
                receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (receivedUri != null) {
                    try {
                        String path = FileUtils.getPath(this, receivedUri);
                        if (path != null) {
                            uploadFile = new File(path);
                            binding.toolbar.setSubtitle(uploadFile.getName());
                        } else {
                            throw new IllegalArgumentException("Could not find path for given Uri " + receivedUri.toString());
                        }
                    } catch (IllegalArgumentException e) {
                        DeckLog.logError(e);
                        new BrandedAlertDialogBuilder(this)
                                .setTitle(R.string.error)
                                .setMessage(R.string.operation_not_yet_supported)
                                .setPositiveButton(R.string.simple_close, (a, b) -> finish())
                                .create().show();
                    }
                } else {
                    throw new IllegalArgumentException("Could not find any file for receivedUri = " + receivedUri);
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
        String debugInfos = ExceptionUtil.getDebugInfos(this, throwable);
        AlertDialog dialog = new BrandedAlertDialogBuilder(this)
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
        DeckLog.info("Adding to card #" + fullCard.getCard().getId() + " (" + fullCard.getCard().getTitle() + "): Attachment \"" + receivedUri.toString() + "\"");
        syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), Attachment.getMimetypeForUri(this, receivedUri), uploadFile);
        Toast.makeText(getApplicationContext(), getString(R.string.share_success, "\"" + uploadFile.getName() + "\"", "\"" + fullCard.getCard().getTitle() + "\""), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void setCurrentBoard(@NonNull Board board) {
        super.setCurrentBoard(board);
        binding.addStackButton.setVisibility(View.GONE);
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