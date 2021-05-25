package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import android.annotation.SuppressLint;

import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class CardPropagationDataProvider extends CardDataProvider {

    public CardPropagationDataProvider(AbstractSyncDataProvider<?> parent, Board board, FullStack stack) {
        super(parent, board, stack);
    }

    @Override
    public Disposable createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<FullCard> responder, FullCard entity) {
        // make sure, all ancestors are synced properly
        if (board.getId() == null) {
            return serverAdapter.createBoard(board, new ResponseCallback<FullBoard>(responder.getAccount()) {
                @Override
                public void onResponse(FullBoard response) {
                    board.setId(response.getId());
                    board.setEtag(response.getEtag());
                    createOnServer(serverAdapter, dataBaseAdapter, accountId, responder, entity);
                }

                @SuppressLint("MissingSuperCall")
                @Override
                public void onError(Throwable throwable) {
                    responder.onError(throwable);
                }
            });
        } else  if (stack.getId() == null) {
            return serverAdapter.createStack(board, stack.getStack(), new ResponseCallback<FullStack>(responder.getAccount()) {
                @Override
                public void onResponse(FullStack response) {
                    stack.setId(response.getId());
                    stack.setEtag(response.getEtag());
                    createOnServer(serverAdapter, dataBaseAdapter, accountId, responder, entity);
                }

                @SuppressLint("MissingSuperCall")
                @Override
                public void onError(Throwable throwable) {
                    responder.onError(throwable);
                }
            });
        } else {
            Card card = entity.getCard();
            card.setStackId(stack.getId());
            return serverAdapter.createCard(board.getId(), stack.getId(), card, responder);
        }
    }


    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity, boolean setStatus) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateCard(entity.getCard(), setStatus);
    }

    @Override
    public Disposable updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<FullCard> callback, FullCard entity) {
        CardUpdate update = toCardUpdate(entity);
        update.setStackId(stack.getId());
        return serverAdapter.updateCard(board.getId(), stack.getId(), update, callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard fullCard) {
        dataBaseAdapter.deleteCard(fullCard.getCard(), true);
    }

    @Override
    public void deletePhysicallyInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard fullCard) {
        dataBaseAdapter.deleteCardPhysically(fullCard.getCard());
    }

    @Override
    public Disposable deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, FullCard entity, DataBaseAdapter dataBaseAdapter) {
        return serverAdapter.deleteCard(board.getId(), stack.getId(), entity.getCard(), callback);
    }
}
