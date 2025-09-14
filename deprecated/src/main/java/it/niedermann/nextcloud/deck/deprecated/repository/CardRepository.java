package it.niedermann.nextcloud.deck.deprecated.repository;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.CardDataProvider;
import it.niedermann.nextcloud.deck.remote.helpers.providers.CardPropagationDataProvider;

public class CardRepository extends AbstractRepository {

    public CardRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<Card> getCardByRemoteID(long accountId, long remoteId) {
        return dataBaseAdapter.getCardByRemoteID(accountId, remoteId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return dataBaseAdapter.getFullCardsForStack(accountId, localStackId, filter);
    }

    public LiveData<Map<Stack, List<FullCard>>> searchCards(final long accountId, final long localBoardId, @NonNull String term, int limit) {
        return dataBaseAdapter.searchCards(accountId, localBoardId, term, limit);
    }

    @WorkerThread
    public Long getBoardLocalIdByLocalCardIdDirectly(long localCardId) {
        return dataBaseAdapter.getBoardLocalIdByLocalCardIdDirectly(localCardId);
    }

    @WorkerThread
    public Optional<Card> getCardByRemoteIDDirectly(long accountId, long remoteId) {
        return Optional.ofNullable(dataBaseAdapter.getCardByRemoteIDDirectly(accountId, remoteId));
    }

    @AnyThread
    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<EmptyResponse> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            FullCard fullCard = dataBaseAdapter.getFullCardByLocalIdDirectly(card.getAccountId(), card.getLocalId());
            if (fullCard == null) {
                throw new IllegalArgumentException("card with id " + card.getLocalId() + " to delete does not exist.");
            }
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).deleteEntity(new CardPropagationDataProvider(null, board, stack), fullCard, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            card.getCard().setArchived(true);
            updateCardForArchive(stack, board, card, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void dearchiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(card.getAccountId());
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            card.getCard().setArchived(false);
            updateCardForArchive(stack, board, card, ResponseCallback.from(account, callback));
        });
    }

    private void updateCardForArchive(FullStack stack, Board board, FullCard card, @NonNull ResponseCallback<FullCard> callback) {
        new DataPropagationHelper(getServerAdapter(callback.getAccount()), dataBaseAdapter, dbWriteHighPriorityExecutor).updateEntity(new CardDataProvider(null, board, stack), card, callback);
    }
}
