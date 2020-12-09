package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.DeckException;
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

    private Set<Long> syncedStacks = new ConcurrentSkipListSet<>();

    public StackDataProvider(AbstractSyncDataProvider<?> parent, FullBoard board) {
        super(parent);
        this.board = board;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullStack>> responder, Instant lastSync) {
        serverAdapter.getStacks(board.getId(), responder);
    }

    @Override
    public FullStack getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        return dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, board.getLocalId(), entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        entity.getStack().setBoardId(board.getLocalId());
        entity.getStack().setAccountId(accountId);
        return dataBaseAdapter.createStack(accountId, entity.getStack());
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity, boolean setStatus) {
        entity.getStack().setBoardId(board.getLocalId());
        dataBaseAdapter.updateStack(entity.getStack(), setStatus);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullStack existingEntity, FullStack entityFromServer, IResponseCallback<Boolean> callback) {
        boolean serverHasCards = entityFromServer.getCards() != null && !entityFromServer.getCards().isEmpty();
        boolean weHaveCards = existingEntity.getCards() != null && !existingEntity.getCards().isEmpty();
        if (serverHasCards || weHaveCards) {
            existingEntity.setCards(entityFromServer.getCards());
            List<Card> cards = existingEntity.getCards();
            if (cards != null) {
                for (Card card : cards) {
                    card.setStackId(existingEntity.getLocalId());
                }
            }
            syncHelper.doSyncFor(new CardDataProvider(this, board.getBoard(), existingEntity));
        } else {
            childDone(this, callback, true);
        }
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullStack> responder, FullStack entity) {
        if (board.getId() == null) {
            throw new DeckException(DeckException.Hint.DEPENDENCY_NOT_SYNCED_YET, "Board for this stack is not synced yet. Perform a full sync (pull to referesh) as soon as you are online again.");
        }
        entity.getStack().setBoardId(board.getId());
        serverAdapter.createStack(board.getBoard(), entity.getStack(), responder);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullStack entity) {
        entity.getStack().setBoardId(board.getId());
        dataBaseAdapter.deleteStackPhysically(entity.getStack());
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullStack entity, DataBaseAdapter dataBaseAdapter) {
        entity.getStack().setBoardId(board.getId());
        serverAdapter.deleteStack(board.getBoard(), entity.getStack(), callback);
    }

    @Override
    public List<FullStack> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        if (board == null) {
            // no stacks changed!
            // (see call from BoardDataProvider: goDeeperForUpSync called with null for board.)
            // so we can just skip this one and proceed with cards.
            return Collections.emptyList();
        }
        List<FullStack> changedStacks = dataBaseAdapter.getLocallyChangedStacksForBoard(accountId, board.getLocalId());
        return changedStacks;
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        List<FullCard> changedCards = dataBaseAdapter.getLocallyChangedCardsDirectly(callback.getAccount().getId());
        if (changedCards != null && !changedCards.isEmpty()) {
            for (FullCard changedCard : changedCards) {
                long stackId = changedCard.getCard().getStackId();
                boolean alreadySynced = syncedStacks.contains(stackId);
                if (!alreadySynced) {
                    FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackId);
                    // already synced and known to server?
                    if (stack.getStack().getId() != null) {
                        syncedStacks.add(stackId);
                        Board board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
                        changedCard.getCard().setStackId(stack.getId());
                        syncHelper.doUpSyncFor(new CardDataProvider(this, board, stack));
                    }
                }
            }
        } else {
            // no changed cards? maybe users or Labels! So we have to go deeper!
            new CardDataProvider(this, null, null).goDeeperForUpSync(syncHelper, serverAdapter, dataBaseAdapter, callback);
        }
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullStack> callback, FullStack entity) {
        entity.getStack().setBoardId(board.getId());
        serverAdapter.updateStack(board.getBoard(), entity.getStack(), callback);
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<FullStack> entitiesFromServer) {
        List<FullStack> localStacks = dataBaseAdapter.getFullStacksForBoardDirectly(accountId, board.getLocalId());
        List<FullStack> delta = findDelta(entitiesFromServer, localStacks);
        for (FullStack stackToDelete : delta) {
            if (stackToDelete.getId() == null) {
                // not pushed up yet so:
                continue;
            }
            dataBaseAdapter.deleteStackPhysically(stackToDelete.getStack());
        }
    }
}
