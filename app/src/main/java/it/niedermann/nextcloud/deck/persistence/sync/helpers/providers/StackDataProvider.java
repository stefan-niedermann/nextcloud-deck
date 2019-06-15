package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
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
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullStack> responder, FullStack entity) {
        entity.getStack().setBoardId(board.getId());
        serverAdapter.createStack(entity.getStack(), responder);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack fullStack) {
        dataBaseAdapter.deleteStackPhysically(fullStack.getStack());
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullStack entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteStack(entity.getStack(), callback);
    }

    @Override
    public List<FullStack> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        if (board == null){
            // no stacks changed!
            // (see call from BoardDataProvider: goDeeperForUpSync called with null for board.)
            // so we can just skip this one and proceed with cards.
            return Collections.emptyList();
        }
        List<FullStack> changedStacks = dataBaseAdapter.getLocallyChangedStacksForBoard(accountId, board.getLocalId());
        return changedStacks;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        List<FullCard> changedCards = dataBaseAdapter.getLocallyChangedCardsDirectly(callback.getAccount().getId());
        Set<Long> syncedStacks = new HashSet<>();
        for (FullCard changedCard : changedCards) {
            long stackId = changedCard.getCard().getStackId();
            boolean added = syncedStacks.add(stackId);
            if (added) {
                FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackId);
                Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
                changedCard.getCard().setStackId(stack.getId());
                syncHelper.doUpSyncFor(new CardDataProvider(this, board, stack));
            }
        }
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullStack> callback, FullStack entity) {
        serverAdapter.updateStack(entity.getStack(), callback);
    }
}
