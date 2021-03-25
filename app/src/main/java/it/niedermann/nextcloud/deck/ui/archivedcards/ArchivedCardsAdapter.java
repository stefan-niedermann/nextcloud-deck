package it.niedermann.nextcloud.deck.ui.archivedcards;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.card.AbstractCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

public class ArchivedCardsAdapter extends CardAdapter {

    @SuppressWarnings("WeakerAccess")
    public ArchivedCardsAdapter(@NonNull Context context, @NonNull FragmentManager fragmentManager, @NonNull MainViewModel viewModel, @NonNull LifecycleOwner lifecycleOwner) {
        super(context, fragmentManager, 0L, viewModel, lifecycleOwner, null);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractCardViewHolder viewHolder, int position) {
        viewHolder.bind(cardList.get(position), mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardRemoteId(), false, R.menu.archived_card_menu, this, counterMaxValue, mainColor);
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_card_dearchive) {
            mainViewModel.dearchiveCard(fullCard, new ResponseCallback<FullCard>() {
                @Override
                public void onResponse(FullCard response) {
                    DeckLog.info("Successfully dearchived " + Card.class.getSimpleName() + " " + fullCard.getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    ResponseCallback.super.onError(throwable);
                    ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());
                }
            });
            return true;
        } else if (itemId == R.id.action_card_delete) {
            mainViewModel.deleteCard(fullCard.getCard(), new ResponseCallback<Void>() {
                @Override
                public void onResponse(Void response) {
                    DeckLog.info("Successfully deleted card " + fullCard.getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                        ResponseCallback.super.onError(throwable);
                        ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName());
                    }
                }
            });
            return true;
        }
        return super.onCardOptionsItemSelected(menuItem, fullCard);
    }
}
