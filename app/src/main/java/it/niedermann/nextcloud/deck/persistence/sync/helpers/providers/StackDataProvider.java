package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class StackDataProvider extends AbstractSyncDataProvider<FullStack> {
    private FullBoard board;

    public StackDataProvider(AbstractSyncDataProvider<?> parent, FullBoard board) {
        super(parent);
        this.board = board;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullStack>> responder, Date lastSync) {
        serverAdapter.getStacks(board.getId(), responder);
    }

    @Override
    public FullStack getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        return dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, board.getLocalId(), entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        entity.getStack().setBoardId(board.getLocalId());
        return dataBaseAdapter.createStack(accountId, entity.getStack());
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        entity.getStack().setBoardId(board.getLocalId());
        dataBaseAdapter.updateStack(entity.getStack(), false);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullStack existingEntity, FullStack entityFromServer, IResponseCallback<Boolean> callback) {
        existingEntity.setCards(entityFromServer.getCards());
        List<Card> cards = existingEntity.getCards();
        if (cards != null && !cards.isEmpty()){
            for (Card card : cards) {
                card.setStackId(existingEntity.getLocalId());
            }
            syncHelper.doSyncFor(new CardDataProvider(this, board.getBoard(), existingEntity));
        } else {
            childDone(this, callback, true);
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullStack> responder, FullStack entity) {
        entity.getStack().setBoardId(board.getId());
        serverAdapter.createStack(entity.getStack(), responder);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack fullStack) {
        dataBaseAdapter.deleteStackPhysically(fullStack.getStack());
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullStack entity) {
        serverAdapter.deleteStack(entity.getStack(), callback);
    }

    @Override
    public List<FullStack> getAllFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return dataBaseAdapter.getLocallyChangedStacks(accountId);
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        // TODO: implement
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<FullStack> callback, FullStack entity) {
        serverAdapter.updateStack(entity.getStack(), callback);
    }
}
