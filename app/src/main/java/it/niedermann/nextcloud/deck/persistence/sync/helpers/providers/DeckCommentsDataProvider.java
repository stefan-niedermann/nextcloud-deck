package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import android.annotation.SuppressLint;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import io.reactivex.disposables.Disposable;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.api.ResponseCallback;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;
import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;
import it.niedermann.nextcloud.deck.model.ocs.comment.OcsComment;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class DeckCommentsDataProvider extends AbstractSyncDataProvider<OcsComment> {

    protected Card card;

    public DeckCommentsDataProvider(AbstractSyncDataProvider<?> parent, Card card) {
        super(parent);
        this.card = card;
    }

    @Override
    public Disposable getAllFromServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<List<OcsComment>> responder, Instant lastSync) {
        return serverAdapter.getCommentsForRemoteCardId(card.getId(), new ResponseCallback<>(responder.getAccount()) {
            @Override
            public void onResponse(OcsComment response) {
                if (response == null) {
                    response = new OcsComment();
                }
                List<OcsComment> comments = response.split();
                Collections.sort(comments, Comparator.comparing(o -> o.getSingle().getCreationDateTime()));
                verifyCommentListIntegrity(comments);
                responder.onResponse(comments);
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                responder.onError(throwable);
            }
        });
    }

    private void verifyCommentListIntegrity(List<OcsComment> comments) {
        List<Long> knownIDs = new ArrayList<>();
        for (OcsComment comment : comments) {
            DeckComment c = comment.getSingle();
            knownIDs.add(c.getId());
            if (c.getParentId() != null && !knownIDs.contains(c.getParentId())) {
                DeckLog.logError(new IllegalStateException("No parent comment with ID " + c.getParentId() +
                        " found for comment " + c.toString()));
                c.setParentId(null);
            }
        }
    }

    @Override
    public OcsComment getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsComment entity) {
        DeckComment comment = dataBaseAdapter.getCommentByRemoteIdDirectly(accountId, entity.getId());
        if (comment == null) {
            return null;
        }
        return OcsComment.of(comment);
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsComment ocsComment) {
        DeckComment comment = ocsComment.getSingle();
        if (comment.getParentId() != null) {
            Long localId = dataBaseAdapter.getLocalCommentIdForRemoteIdDirectly(accountId, comment.getParentId());
            comment.setParentId(localId);
        }
        comment.setObjectId(card.getLocalId());
        comment.setLocalId(dataBaseAdapter.createComment(accountId, comment));
        persistMentions(dataBaseAdapter, comment);
        return comment.getLocalId();
    }

    private void persistMentions(DataBaseAdapter dataBaseAdapter, DeckComment comment) {
        dataBaseAdapter.clearMentionsForCommentId(comment.getLocalId());
        for (Mention mention : comment.getMentions()) {
            mention.setCommentId(comment.getLocalId());
            mention.setId(dataBaseAdapter.createMention(mention));
        }

    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsComment ocsComment, boolean setStatus) {
        DeckComment comment = ocsComment.getSingle();
        comment.setAccountId(accountId);
        comment.setObjectId(card.getLocalId());
        if (comment.getParentId() != null) {
            comment.setParentId(dataBaseAdapter.getLocalCommentIdForRemoteIdDirectly(accountId, comment.getParentId()));
        }
        dataBaseAdapter.updateComment(comment, setStatus);
        persistMentions(dataBaseAdapter, comment);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsComment ocsComment) {
        DeckComment comment = ocsComment.getSingle();
        dataBaseAdapter.deleteComment(comment, false);
    }

    @Override
    public Disposable createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<OcsComment> responder, OcsComment entity) {
        DeckComment comment = entity.getSingle();
        comment.setObjectId(card.getId());
        if (comment.getParentId() != null) {
            comment.setParentId(dataBaseAdapter.getRemoteCommentIdForLocalIdDirectly(comment.getParentId()));
        }
        DeckLog.info("creating entity: "+entity.getComments().get(0).getMessage() + " with id " +entity.getComments().get(0).getLocalId());
        CountDownLatch latch = new CountDownLatch(1);
        final var disposable = serverAdapter.createCommentForCard(comment, new ResponseCallback<>(responder.getAccount()) {
            @Override
            public void onResponse(OcsComment response) {
                latch.countDown();
                responder.onResponse(response);
                DeckLog.info("CREATED entity: "+entity.getComments().get(0).getMessage() + " with id " +entity.getComments().get(0).getLocalId());
            }

            @SuppressLint("MissingSuperCall")
            @Override
            public void onError(Throwable throwable) {
                latch.countDown();
                responder.onError(throwable);
            }
        });

        try {
            latch.await();
            DeckLog.info("released latch for entity: "+entity.getComments().get(0).getMessage() + " with id " +entity.getComments().get(0).getLocalId());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return disposable;
    }

    @Override
    public Disposable updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, ResponseCallback<OcsComment> callback, OcsComment entity) {
        DeckComment comment = entity.getSingle();
        comment.setObjectId(card.getId());
        if (comment.getParentId() != null) {
            comment.setParentId(dataBaseAdapter.getRemoteCommentIdForLocalIdDirectly(comment.getParentId()));
        }
        return serverAdapter.updateCommentForCard(comment, callback);
    }

    @Override
    public Disposable deleteOnServer(ServerAdapter serverAdapter, long accountId, ResponseCallback<Void> callback, OcsComment entity, DataBaseAdapter dataBaseAdapter) {
        DeckComment comment = entity.getSingle();
        comment.setObjectId(card.getId());
        return serverAdapter.deleteCommentForCard(comment, callback);
    }

    @Override
    public List<OcsComment> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return new OcsComment(dataBaseAdapter.getLocallyChangedCommentsByLocalCardIdDirectly(accountId, card.getLocalId())).split();
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<OcsComment> entitiesFromServer) {
        List<OcsComment> deletedComments = findDelta(entitiesFromServer, new OcsComment(dataBaseAdapter.getCommentByLocalCardIdDirectly(card.getLocalId())).split());
        for (OcsComment deletedComment : deletedComments) {
            if (deletedComment.getId() != null) {
                // preserve new, unsynced comment.
                dataBaseAdapter.deleteComment(deletedComment.getSingle(), false);
            }
        }
    }
}
