package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.PickStackActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentAccountId;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentBoardId;
import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentStackId;

public class PrepareCreateActivity extends PickStackActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.add_card);
        }
    }

    @Override
    protected void onSubmit(Account account, long boardId, long stackId) {
        final String receivedClipData = getReceivedClipData(getIntent());
        if (receivedClipData == null) {
            startActivity(EditActivity.createNewCardIntent(this, account, boardId, stackId));
        } else {
            startActivity(EditActivity.createNewCardIntent(this, account, boardId, stackId, receivedClipData));
        }

        saveCurrentAccountId(this, account.getId());
        saveCurrentBoardId(this, account.getId(), boardId);
        saveCurrentStackId(this, account.getId(), boardId, stackId);
        applyBrand(account.getColor());

        finish();
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return false;
    }

    @Nullable
    private static String getReceivedClipData(@Nullable Intent intent) {
        if (intent == null) {
            return null;
        }
        final ClipData clipData = intent.getClipData();
        if (clipData == null) {
            return null;
        }
        final int itemCount = clipData.getItemCount();
        if (itemCount <= 0) {
            return null;
        }
        final ClipData.Item item = clipData.getItemAt(0);
        if (item == null) {
            return null;
        }
        final CharSequence text = item.getText();
        return TextUtils.isEmpty(text) ? null : text.toString();
    }
}