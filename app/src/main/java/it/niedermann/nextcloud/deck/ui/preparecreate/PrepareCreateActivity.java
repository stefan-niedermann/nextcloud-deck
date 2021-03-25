package it.niedermann.nextcloud.deck.ui.preparecreate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.ui.PickStackActivity;
import it.niedermann.nextcloud.deck.ui.card.EditActivity;

import static it.niedermann.nextcloud.deck.DeckApplication.saveCurrentAccount;
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
        final Intent intent = getIntent();
        if (intent == null) {
            startActivity(EditActivity.createNewCardIntent(this, account, boardId, stackId));
        } else {
            startActivity(EditActivity.createNewCardIntent(this, account, boardId, stackId,
                    intent.getStringExtra(Intent.EXTRA_TITLE),
                    intent.getStringExtra(Intent.EXTRA_TEXT)));
        }

        saveCurrentAccount(this, account);
        saveCurrentBoardId(this, account.getId(), boardId);
        saveCurrentStackId(this, account.getId(), boardId, stackId);
        applyBrand(account.getColor());

        finish();
    }

    @Override
    protected boolean showBoardsWithoutEditPermission() {
        return false;
    }
}