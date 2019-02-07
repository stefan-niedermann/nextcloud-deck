package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;
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
        if (stack.getCards() == null || stack.getCards().isEmpty()){
            responder.onResponse(result);
        }
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
        return dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.createCard(accountId, entity.getCard());
    }

    private void fixRelations(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        entity.getCard().setStackId(stack.getLocalId());
        if (entity.getOwner() != null && !entity.getOwner().isEmpty()){
            User user = entity.getOwner().get(0);
            User u = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());
            if (u == null){
                dataBaseAdapter.createUser(accountId, user);
            } else {
                user.setLocalId(u.getLocalId());
                dataBaseAdapter.updateUser(accountId, user);
            }
            u = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());

            user.setLocalId(u.getLocalId());
            entity.getCard().setUserId(u.getLocalId());
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateCard(entity.getCard());
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullCard existingEntity, FullCard entityFromServer) {
        existingEntity.setLabels(entityFromServer.getLabels());
        existingEntity.setAssignedUsers(entityFromServer.getAssignedUsers());
        syncHelper.fixRelations(new StackCardRelationshipProvider(stack.getStack(), existingEntity.getCard()));
        syncHelper.doSyncFor(new LabelDataProvider(entityFromServer.getLabels()));
        syncHelper.fixRelations(new CardLabelRelationshipProvider(existingEntity.getCard(), existingEntity.getLabels()));
        syncHelper.doSyncFor(new UserDataProvider(board, stack, existingEntity, existingEntity.getAssignedUsers()));
        syncHelper.fixRelations(new CardUserRelationshipProvider(existingEntity.getCard(), existingEntity.getAssignedUsers()));
//        syncHelper.doSyncFor(new UserDataProvider(board, stack, existingEntity, existingEntity.getOwner()));
    }
}
