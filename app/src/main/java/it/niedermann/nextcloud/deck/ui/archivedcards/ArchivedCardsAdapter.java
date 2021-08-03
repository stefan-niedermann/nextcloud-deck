package it.niedermann.nextcloud.deck.ui.archivedcards;

import android.app.Activity;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.MainViewModel;
import it.niedermann.nextcloud.deck.ui.card.AbstractCardViewHolder;
import it.niedermann.nextcloud.deck.ui.card.CardAdapter;
import it.niedermann.nextcloud.deck.ui.exception.ExceptionDialogFragment;

public class ArchivedCardsAdapter extends CardAdapter {

    @SuppressWarnings("WeakerAccess")
    public ArchivedCardsAdapter(@NonNull Activity activity, @NonNull FragmentManager fragmentManager, @NonNull MainViewModel viewModel) {
        super(activity, fragmentManager, 0L, viewModel, null);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractCardViewHolder viewHolder, int position) {
        viewHolder.bind(cardList.get(position), mainViewModel.getCurrentAccount(), mainViewModel.getCurrentBoardRemoteId(), false, R.menu.archived_card_menu, this, counterMaxValue, mainColor);
    }

    @Override
    public boolean onCardOptionsItemSelected(@NonNull MenuItem menuItem, @NonNull FullCard fullCard) {
        int itemId = menuItem.getItemId();
        if (itemId == R.id.action_card_dearchive) {
            mainViewModel.dearchiveCard(fullCard, new IResponseCallback<>() {
                @Override
                public void onResponse(FullCard response) {
                    DeckLog.info("Successfully dearchived", Card.class.getSimpleName(), fullCard.getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    IResponseCallback.super.onError(throwable);
                    activity.runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName()));
                }
            });
            return true;
        } else if (itemId == R.id.action_card_delete) {
            mainViewModel.deleteCard(fullCard.getCard(), new IResponseCallback<>() {
                @Override
                public void onResponse(Void response) {
                    DeckLog.info("Successfully deleted card", fullCard.getCard().getTitle());
                }

                @Override
                public void onError(Throwable throwable) {
                    if (!SyncManager.ignoreExceptionOnVoidError(throwable)) {
                        IResponseCallback.super.onError(throwable);
                        activity.runOnUiThread(() -> ExceptionDialogFragment.newInstance(throwable, mainViewModel.getCurrentAccount()).show(fragmentManager, ExceptionDialogFragment.class.getSimpleName()));
                    }
                }
            });
            return true;
        }
        return super.onCardOptionsItemSelected(menuItem, fullCard);
    }
}
