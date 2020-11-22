package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import android.annotation.SuppressLint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.DeckException;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Attachment;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.JoinCardWithLabel;
import it.niedermann.nextcloud.deck.model.JoinCardWithUser;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.ocs.Version;
import it.niedermann.nextcloud.deck.model.propagation.CardUpdate;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class CardDataProvider extends AbstractSyncDataProvider<FullCard> {

    protected Board board;
    protected FullStack stack;

    public CardDataProvider(AbstractSyncDataProvider<?> parent, Board board, FullStack stack) {
        super(parent);
        this.board = board;
        this.stack = stack;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<FullCard>> responder, Instant lastSync) {

        List<FullCard> result = new ArrayList<>();
        if (stack.getCards() == null || stack.getCards().isEmpty()) {
            responder.onResponse(result);
            return;
        }
        for (Card card : stack.getCards()) {
            serverAdapter.getCard(board.getId(), stack.getId(), card.getId(), new IResponseCallback<FullCard>(responder.getAccount()) {
                @Override
                public void onResponse(FullCard response) {
                    result.add(response);
                    if (result.size() == stack.getCards().size()) {
                        responder.onResponse(result);
                    }
                }

                @SuppressLint("MissingSuperCall")
                @Override
                public void onError(Throwable throwable) {
                    responder.onError(throwable);
                }
            });
        }
    }

    @Override
    public FullCard getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        return dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        fixRelations(dataBaseAdapter, accountId, entity);
        return dataBaseAdapter.createCardDirectly(accountId, entity.getCard());
    }

    protected CardUpdate toCardUpdate(FullCard card) {
        CardUpdate c = new CardUpdate(card);
        // FIXME This causes an IndexOutOfBoundsException for the three "Example Tasks" on a fresh Deck server installation
        c.setOwner(card.getOwner().get(0));
        return c;
    }

    protected void fixRelations(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        entity.getCard().setStackId(stack.getLocalId());
        if (entity.getOwner() != null && !entity.getOwner().isEmpty()) {
            User user = entity.getOwner().get(0);
            User u = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());
            if (u == null) {
                dataBaseAdapter.createUser(accountId, user);
            } else {
                user.setLocalId(u.getLocalId());
                dataBaseAdapter.updateUser(accountId, user, false);
            }
            u = dataBaseAdapter.getUserByUidDirectly(accountId, user.getUid());

            user.setLocalId(u.getLocalId());
            entity.getCard().setUserId(u.getLocalId());
        }
    }


    @Override
    public FullCard applyUpdatesFromRemote(FullCard localEntity, FullCard remoteEntity, Long accountId) {
        if (localEntity.getCard().getUserId() != null) {
            remoteEntity.getCard().setUserId(localEntity.getCard().getUserId());
        }
        return remoteEntity;
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity, boolean setStatus) {
        fixRelations(dataBaseAdapter, accountId, entity);
        dataBaseAdapter.updateCard(entity.getCard(), setStatus);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullCard existingEntity, FullCard entityFromServer, IResponseCallback<Boolean> callback) {
        List<Label> labels = entityFromServer.getLabels();
        existingEntity.setLabels(labels);
        List<User> assignedUsers = entityFromServer.getAssignedUsers();
        existingEntity.setAssignedUsers(assignedUsers);
        List<Attachment> attachments = entityFromServer.getAttachments();
        existingEntity.setAttachments(attachments);

        syncHelper.fixRelations(new CardLabelRelationshipProvider(existingEntity.getCard(), existingEntity.getLabels()));
        if (assignedUsers != null && !assignedUsers.isEmpty()) {
            syncHelper.doSyncFor(new UserDataProvider(this, board, stack, existingEntity, existingEntity.getAssignedUsers()));
        }

        syncHelper.fixRelations(new CardUserRelationshipProvider(existingEntity.getCard(), existingEntity.getAssignedUsers()));
        if (attachments == null) {
            attachments = new ArrayList<>();
        }
        syncHelper.doSyncFor(new AttachmentDataProvider(this, board, stack.getStack(), existingEntity, attachments));

        if (callback.getAccount().getServerDeckVersionAsObject().isGreaterOrEqualTo(new Version("1.0.0", 1, 0, 0))) {
            DeckLog.verbose("Comments - Version is OK, SYNC");
            syncHelper.doSyncFor(new DeckCommentsDataProvider(this, existingEntity.getCard()));
        } else {
            DeckLog.verbose("Comments - Version is too low, DONT SYNC");
        }
        syncHelper.doSyncFor(new OcsProjectDataProvider(this, existingEntity.getCard()));
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullCard> responder, FullCard entity) {
        if (stack.getId() == null) {
            responder.onError(new DeckException(DeckException.Hint.DEPENDENCY_NOT_SYNCED_YET, "Stack \"" +
                    stack.getStack().getTitle() + "\" for Card \"" + entity.getCard().getTitle() +
                    "\" is not synced yet. Perform a full sync (pull to refresh) as soon as you are online again."));
            return;
        }
        entity.getCard().setStackId(stack.getId());
        serverAdapter.createCard(board.getId(), stack.getId(), entity.getCard(), responder);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<FullCard> callback, FullCard entity) {
        CardUpdate update = toCardUpdate(entity);
        update.setStackId(stack.getId());
        serverAdapter.updateCard(board.getId(), stack.getId(), update, callback);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullCard fullCard) {
        dataBaseAdapter.deleteCard(fullCard.getCard(), false);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, FullCard entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteCard(board.getId(), stack.getId(), entity.getCard(), callback);
    }

    @Override
    public List<FullCard> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        if (board == null || stack == null) {
            // no cards changed!
            // (see call from StackDataProvider: goDeeperForUpSync called with null for board.)
            // so we can just skip this one and proceed with anything else (users, labels).
            return Collections.emptyList();
        }
        return dataBaseAdapter.getLocallyChangedCardsByLocalStackIdDirectly(accountId, stack.getStack().getLocalId());
    }

    @Override
    public void goDeeperForUpSync(SyncHelper syncHelper, ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, IResponseCallback<Boolean> callback) {
        FullStack stack;
        Board board;
        List<JoinCardWithLabel> changedLabels;
        if (this.stack == null) {
            changedLabels = dataBaseAdapter.getAllChangedLabelJoins();
        } else {
            changedLabels = dataBaseAdapter.getAllChangedLabelJoinsForStack(this.stack.getLocalId());
        }

        Account account = callback.getAccount();
        for (JoinCardWithLabel changedLabelLocal : changedLabels) {
            Card card = dataBaseAdapter.getCardByLocalIdDirectly(account.getId(), changedLabelLocal.getCardId());
            if (this.stack == null) {
                stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            } else {
                stack = this.stack;
            }

            if (this.board == null) {
                board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            } else {
                board = this.board;
            }

            JoinCardWithLabel changedLabel = dataBaseAdapter.getAllChangedLabelJoinsWithRemoteIDs(changedLabelLocal.getCardId(), changedLabelLocal.getLabelId());
            if (changedLabel.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                if (changedLabel.getLabelId() == null || changedLabel.getCardId() == null) {
                    dataBaseAdapter.deleteJoinedLabelForCardPhysicallyByRemoteIDs(account.getId(), changedLabel.getCardId(), changedLabel.getLabelId());
                } else {
                    serverAdapter.unassignLabelFromCard(board.getId(), stack.getId(), changedLabel.getCardId(), changedLabel.getLabelId(), new IResponseCallback<Void>(account) {
                        @Override
                        public void onResponse(Void response) {
                            dataBaseAdapter.deleteJoinedLabelForCardPhysicallyByRemoteIDs(account.getId(), changedLabel.getCardId(), changedLabel.getLabelId());
                        }
                    });
                }
            } else if (changedLabel.getStatusEnum() == DBStatus.LOCAL_EDITED) {
                if (changedLabel.getLabelId() == null || changedLabel.getCardId() == null) {
                    // Sync next time, the card should be available on server then.
                    continue;
                } else {
                    serverAdapter.assignLabelToCard(board.getId(), stack.getId(), changedLabel.getCardId(), changedLabel.getLabelId(), new IResponseCallback<Void>(account) {
                        @Override
                        public void onResponse(Void response) {
                            Label label = dataBaseAdapter.getLabelByRemoteIdDirectly(account.getId(), changedLabel.getLabelId());
                            dataBaseAdapter.setStatusForJoinCardWithLabel(card.getLocalId(), label.getLocalId(), DBStatus.UP_TO_DATE.getId());
                        }
                    });
                }

            }
        }

        List<JoinCardWithUser> changedUsers;
        if (this.stack == null) {
            changedUsers = dataBaseAdapter.getAllChangedUserJoinsWithRemoteIDs();
        } else {
            changedUsers = dataBaseAdapter.getAllChangedUserJoinsWithRemoteIDsForStack(this.stack.getLocalId());
        }
        for (JoinCardWithUser changedUser : changedUsers) {
            // not already known to server?
            if (changedUser.getCardId() == null) {
                //skip for now
                continue;
            }
            Card card = dataBaseAdapter.getCardByRemoteIdDirectly(account.getId(), changedUser.getCardId());
            if (this.stack == null) {
                stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getStackId());
            } else {
                stack = this.stack;
            }

            if (this.board == null) {
                board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            } else {
                board = this.board;
            }
            User user = dataBaseAdapter.getUserByLocalIdDirectly(changedUser.getUserId());
            if (changedUser.getStatusEnum() == DBStatus.LOCAL_DELETED) {
                serverAdapter.unassignUserFromCard(board.getId(), stack.getId(), changedUser.getCardId(), user.getUid(), new IResponseCallback<Void>(account) {
                    @Override
                    public void onResponse(Void response) {
                        dataBaseAdapter.deleteJoinedUserForCardPhysicallyByRemoteIDs(account.getId(), changedUser.getCardId(), user.getUid());
                    }
                });
            } else if (changedUser.getStatusEnum() == DBStatus.LOCAL_EDITED) {
                serverAdapter.assignUserToCard(board.getId(), stack.getId(), changedUser.getCardId(), user.getUid(), new IResponseCallback<Void>(account) {
                    @Override
                    public void onResponse(Void response) {
                        dataBaseAdapter.setStatusForJoinCardWithUser(card.getLocalId(), user.getLocalId(), DBStatus.UP_TO_DATE.getId());
                    }
                });
            }
        }

        List<Attachment> attachments;
        if (this.stack == null) {
            attachments = dataBaseAdapter.getLocallyChangedAttachmentsDirectly(account.getId());
        } else {
            attachments = dataBaseAdapter.getLocallyChangedAttachmentsForStackDirectly(this.stack.getLocalId());
        }
        for (Attachment attachment : attachments) {
            FullCard card = dataBaseAdapter.getFullCardByLocalIdDirectly(account.getId(), attachment.getCardId());
            stack = dataBaseAdapter.getFullStackByLocalIdDirectly(card.getCard().getStackId());
            board = dataBaseAdapter.getBoardByLocalIdDirectly(stack.getStack().getBoardId());
            syncHelper.doUpSyncFor(new AttachmentDataProvider(this, board, stack.getStack(), card, Collections.singletonList(attachment)));
        }

        List<Card> cardsWithChangedComments;
        if (this.stack == null) {
            cardsWithChangedComments = dataBaseAdapter.getCardsWithLocallyChangedCommentsDirectly(account.getId());
        } else {
            cardsWithChangedComments = dataBaseAdapter.getCardsWithLocallyChangedCommentsForStackDirectly(this.stack.getLocalId());
        }
        for (Card card : cardsWithChangedComments) {
            syncHelper.doUpSyncFor(new DeckCommentsDataProvider(this, card));
        }

        callback.onResponse(Boolean.TRUE);
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<FullCard> entitiesFromServer) {
        List<FullCard> localCards = dataBaseAdapter.getFullCardsForStackDirectly(accountId, stack.getLocalId(), null);
        List<FullCard> delta = findDelta(entitiesFromServer, localCards);
        for (FullCard cardToDelete : delta) {
            if (cardToDelete.getId() == null) {
                // not pushed up yet so:
                continue;
            }
            if (cardToDelete.getStatus() == DBStatus.LOCAL_MOVED.getId()) {
                //only delete, if the card isn't availible on server anymore.
                serverAdapter.getCard(board.getId(), stack.getId(), cardToDelete.getId(), new IResponseCallback<FullCard>(new Account(accountId)) {
                    @Override
                    public void onResponse(FullCard response) {
                        // do not delete, it's still there and was just moved!
                    }

                    @SuppressLint("MissingSuperCall")
                    @Override
                    public void onError(Throwable throwable) {
                        if (!(throwable instanceof OfflineException)) {
                            // most likely permission denied, therefore deleted
                            dataBaseAdapter.deleteCardPhysically(cardToDelete.getCard());
                        }
                    }
                });

                continue;
            }
            dataBaseAdapter.deleteCardPhysically(cardToDelete.getCard());
        }
    }
}
