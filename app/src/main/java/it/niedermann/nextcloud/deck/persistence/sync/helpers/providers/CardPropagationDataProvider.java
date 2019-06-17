package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class CardPropagationDataProvider extends CardDataProvider {

    public CardPropagationDataProvider(AbstractSyncDataProvider<?> parent, Board board, FullStack stack) {
        super(parent, board, stack);
    }


    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateCard(entity.getCard(), true);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullCard> callback, FullCard entity) {
        serverAdapter.updateCard(board.getId(), stack.getId(), toCardUpdate(entity), callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard fullCard) {
        dataBaseAdapter.deleteCard(fullCard.getCard(), true);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullCard entity) {
        serverAdapter.deleteCard(board.getId(), stack.getId(), entity.getCard(), callback);
    }
}
