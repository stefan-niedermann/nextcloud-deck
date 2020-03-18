package it.niedermann.nextcloud.deck.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.util.Objects;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.util.ExceptionUtil;
import it.niedermann.nextcloud.deck.util.FileUtils;

public class SelectCardActivity extends MainActivity implements CardAdapter.SelectCardListener {

    Intent receivedIntent;
    String receivedAction;
    String receivedType;

    boolean isFile;

    String receivedText;
    Uri receivedUri;
    File uploadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            receivedIntent = getIntent();
            receivedAction = receivedIntent.getAction();
            receivedType = receivedIntent.getType();
            DeckLog.info(receivedAction);
            DeckLog.info(receivedType);
            isFile = !receivedType.startsWith("text/");
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
                        new AlertDialog.Builder(this)
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error)
                .setMessage(debugInfos)
                .setPositiveButton(android.R.string.copy, (a, b) -> {
                    final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    final ClipData clipData = ClipData.newPlainText(throwable.getMessage(), "```\n" + debugInfos + "\n```");
                    Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
                    Toast.makeText(getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton(R.string.simple_close, (d, w) -> finish())
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
    protected void displayStacksForBoard(@Nullable Board board, @Nullable Account account) {
        super.displayStacksForBoard(board, account);
        binding.addStackButton.setVisibility(View.GONE);
        binding.fab.setVisibility(View.GONE);
        binding.toolbar.setTitle(R.string.simple_select);
    }

    @Override
    protected void showFabIfEditPermissionGranted() {
        // Do nothing
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}