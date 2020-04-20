package it.niedermann.nextcloud.deck.ui.archivedcards;

import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.SelectCardListener;

public class ArchivedCardsAdapter extends CardAdapter {
    public ArchivedCardsAdapter(@NonNull Context context, @NonNull Account account, long boardId, long stackId, boolean canEdit, @NonNull SyncManager syncManager, @NonNull Fragment fragment, @Nullable SelectCardListener selectCardListener) {
        super(context, account, boardId, stackId, canEdit, syncManager, fragment, selectCardListener);
    }

    protected void onOverflowIconClicked(@NotNull View view, FullCard card) {
        final Context context = view.getContext();
        final PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.card_menu);
        prepareOptionsMenu(popup.getMenu(), card);

        popup.setOnMenuItemClickListener(item -> optionsItemSelected(context, item, card));
        popup.show();
    }

    protected void prepareOptionsMenu(Menu menu, @NotNull FullCard card) {
        // Nothing to do
    }

    protected boolean optionsItemSelected(@NonNull Context context, @NotNull MenuItem item, FullCard card) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (item.getItemId()) {
            case R.id.action_card_dearchive: {
                new Thread(() -> syncManager.dearchiveCard(card)).start();
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
