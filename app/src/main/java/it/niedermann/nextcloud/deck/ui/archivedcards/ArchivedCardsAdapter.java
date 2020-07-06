package it.niedermann.nextcloud.deck.ui.archivedcards;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.card.CardViewHolder;

public class ArchivedCardsAdapter extends CardAdapter {

    @SuppressWarnings("WeakerAccess")
    public ArchivedCardsAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull Account account, long boardId, boolean canEdit, @NonNull SyncManager syncManager, @NonNull LifecycleOwner lifecycleOwner) {
        super(context, fragmentManager, account, boardId, 0L, 0L, canEdit, syncManager, lifecycleOwner, null);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder viewHolder, int position) {
        viewHolder.bind(cardList.get(position), this, position, account, boardLocalId, boardRemoteId, hasEditPermission, fullCard -> {
        }, R.menu.archived_card_menu, this, counterMaxValue, mainColor);
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        switch (menuItem.getItemId()) {
            case R.id.action_card_dearchive: {
                // TODO error handling
                new Thread(() -> syncManager.dearchiveCard(fullCard)).start();
                return true;
            }
            case R.id.action_card_delete: {
                // TODO error handling
                syncManager.deleteCard(fullCard.getCard());
                return true;
            }
            default: {
                return super.onCardOptionsItemSelected(menuItem, fullCard);
            }
        }
    }
}
