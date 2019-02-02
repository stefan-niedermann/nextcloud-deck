package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class CardDataProvider implements IDataProvider<FullCard> {

    private Board board;
    private FullStack stack;

    public CardDataProvider(Board board, FullStack stack) {
        this.board = board;
        this.stack = stack;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullCard>> responder) {
        List<FullCard> result = new ArrayList<>();
        for (Long card : stack.getCards()) {
            serverAdapter.getCard(accountId, board.getId(), stack.getId(), card, new IResponseCallback<FullCard>(responder.getAccount()) {
                @Override
                public void onResponse(FullCard response) {
                    result.add(response);
                    if (result.size() == stack.getCards().size()) {
                        responder.onResponse(result);
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    responder.onError(throwable);
                }
            });
        }
    }

    @Override
    public FullCard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        DeckLog.log("cardFromDB: "+accountId+" | "+remoteId);
        return dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        dataBaseAdapter.createCard(accountId, entity.getCard());
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        dataBaseAdapter.updateCard(entity.getCard());
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullCard existingEntity, FullCard entityFromServer) {
        syncHelper.doSyncFor(new LabelDataProvider(board, stack, existingEntity));
        syncHelper.fixRelations(new CardLabelRelationshipProvider(existingEntity.getCard(), existingEntity.getLabels()));
        syncHelper.doSyncFor(new UserDataProvider(board, stack, existingEntity, existingEntity.getAssignedUsers()));
        syncHelper.fixRelations(new CardUserRelationshipProvider(existingEntity.getCard(), existingEntity.getAssignedUsers()));
        syncHelper.doSyncFor(new UserDataProvider(board, stack, existingEntity, existingEntity.getOwner()));
    }
}
