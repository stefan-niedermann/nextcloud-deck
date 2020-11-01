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
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.WrappedLiveData;
import it.niedermann.nextcloud.deck.ui.card.AbstractCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

import static it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.LiveDataHelper.observeOnce;

public class ArchivedCardsAdapter extends CardAdapter {

    @SuppressWarnings("WeakerAccess")
    public ArchivedCardsAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull Account account, long boardId, boolean hasEditPermission, @NonNull SyncManager syncManager, @NonNull LifecycleOwner lifecycleOwner) {
        super(context, fragmentManager, account, boardId, 0L, 0L, hasEditPermission, syncManager, lifecycleOwner, null);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractCardViewHolder viewHolder, int position) {
        viewHolder.bind(cardList.get(position), account, boardRemoteId, hasEditPermission, R.menu.archived_card_menu, this, counterMaxValue, mainColor);
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        switch (menuItem.getItemId()) {
            case R.id.action_card_dearchive: {
                final WrappedLiveData<FullCard> liveData = syncManager.dearchiveCard(fullCard);
                observeOnce(liveData, lifecycleOwner, (next) -> {
                    if (liveData.hasError()) {
                        ExceptionDialogFragment.newInstance(liveData.getError(), account).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());
                    }
                });
                return true;
            }
            case R.id.action_card_delete: {
                final WrappedLiveData<Void> liveData = syncManager.deleteCard(fullCard.getCard());
                observeOnce(liveData, lifecycleOwner, (next) -> {
                    if (liveData.hasError() && !SyncManager.ignoreExceptionOnVoidError(liveData.getError())) {
                        ExceptionDialogFragment.newInstance(liveData.getError(), account).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());
                    }
                });
                return true;
            }
            default: {
                return super.onCardOptionsItemSelected(menuItem, fullCard);
            }
        }
    }
}
