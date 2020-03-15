package it.niedermann.nextcloud.deck.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;

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

    @Override
    public void onCardSelected(FullCard fullCard) {
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        DeckLog.info(receivedAction);
        DeckLog.info(receivedType);
        try {
            if (receivedType == null) {
                throw new IllegalArgumentException("receivedType must not be null for " + SelectCardActivity.class.getCanonicalName());
            }
            if (receivedType.startsWith("text/")) {
                String receivedText = receivedIntent.getStringExtra(Intent.EXTRA_TEXT);
                if (receivedText != null) {
                    appendText(fullCard, receivedText);
                } else {
                    DeckLog.warn("Did not receive any text.");
                }
            } else if (receivedType.startsWith("image/")) {
                Uri receivedUri = receivedIntent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (receivedUri != null) {
                    appendAttachment(fullCard, receivedUri);
                } else {
                    DeckLog.warn("Did not receive any extra.");
                }
                syncManager.updateCard(fullCard);
            }
            finish();
        } catch (Exception e) {
            DeckLog.logError(e);
            String debugInfos = ExceptionUtil.getDebugInfos(this, e);
            Snackbar.make(binding.coordinatorLayout, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.simple_more, v -> {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(R.string.error)
                                .setMessage(debugInfos)
                                .setPositiveButton(android.R.string.copy, (a, b) -> {
                                    final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    final ClipData clipData = ClipData.newPlainText(e.getMessage(), "```\n" + debugInfos + "\n```");
                                    Objects.requireNonNull(clipboardManager).setPrimaryClip(clipData);
                                    Toast.makeText(getApplicationContext(), R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show();
                                    a.dismiss();
                                })
                                .setNegativeButton(R.string.simple_close, null)
                                .create();
                        dialog.show();
                        ((TextView) Objects.requireNonNull(dialog.findViewById(android.R.id.message))).setTypeface(Typeface.MONOSPACE);
                    })
                    .show();
        }
    }

    private void appendText(@NonNull FullCard fullCard, @NonNull String receivedText) {
        DeckLog.log(receivedText);
        String oldDescription = fullCard.getCard().getDescription();
        DeckLog.info("Adding to card #" + fullCard.getCard().getId() + " (" + fullCard.getCard().getTitle() + "): Text \"" + receivedText + "\"");
        if (oldDescription == null || oldDescription.length() == 0) {
            fullCard.getCard().setDescription(receivedText);
        } else {
            fullCard.getCard().setDescription(oldDescription + "\n\n" + receivedText);
        }
    }

    private void appendAttachment(@NonNull FullCard fullCard, @NonNull Uri uri) {
        String path = FileUtils.getPath(this, uri);
        if (path != null) {
            File uploadFile = new File(path);
            DeckLog.info("Adding to card #" + fullCard.getCard().getId() + " (" + fullCard.getCard().getTitle() + "): Attachment \"" + uri.toString() + "\"");
            syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), Attachment.getMimetypeForUri(this, uri), uploadFile);
        } else {
            throw new IllegalArgumentException("Could not find path for given Uri " + uri.toString());
        }
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