package it.niedermann.nextcloud.deck.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.annotation.NonNull;

import java.io.File;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.util.FileUtils;

public class SelectCardActivity extends MainActivity implements CardAdapter.SelectCardListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding.addStackButton.setVisibility(View.GONE);
    }

    @Override
    public void onCardSelected(FullCard fullCard) {
        Intent receivedIntent = getIntent();
        String receivedAction = receivedIntent.getAction();
        String receivedType = receivedIntent.getType();
        DeckLog.info(receivedAction);
        DeckLog.info(receivedType);
        if (receivedType != null) {
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
            }
            syncManager.updateCard(fullCard);
        } else {
            DeckLog.logError(new IllegalArgumentException("receivedType must not be null for " + SelectCardActivity.class.getCanonicalName()));
        }
        finish();
    }

    private void appendText(@NonNull FullCard fullCard, @NonNull String receivedText) {
        DeckLog.log(receivedText);
        String oldDescription = fullCard.getCard().getDescription();
        if (oldDescription == null || oldDescription.length() == 0) {
            fullCard.getCard().setDescription(receivedText);
        } else {
            fullCard.getCard().setDescription(oldDescription + "\n\n" + receivedText);
        }
    }

    private void appendAttachment(@NonNull FullCard fullCard, @NonNull Uri uri) {
        DeckLog.info("Uri: " + uri.toString());
        String path = FileUtils.getPath(this, uri);
        if (path != null) {
            File uploadFile = new File(path);
            syncManager.addAttachmentToCard(fullCard.getAccountId(), fullCard.getCard().getLocalId(), Attachment.getMimetypeForUri(this, uri), uploadFile);
        } else {
            DeckLog.warn("path to file is null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}