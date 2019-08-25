package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
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
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullCard> responder, FullCard entity) {
        Card card = entity.getCard();
        card.setStackId(stack.getId());
        serverAdapter.createCard(board.getId(), stack.getId(), card, responder);
    }


    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateCard(entity.getCard(), true);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullCard> callback, FullCard entity) {
        CardUpdate update = toCardUpdate(entity);
        update.setStackId(stack.getId());
        serverAdapter.updateCard(board.getId(), stack.getId(), update, callback);
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
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullCard entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteCard(board.getId(), stack.getId(), entity.getCard(), callback);
    }
}
