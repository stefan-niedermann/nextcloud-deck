package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
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
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<OcsComment>> responder, Date lastSync) {
        serverAdapter.getCommentsForRemoteCardId(card.getId(), new IResponseCallback<OcsComment>(responder.getAccount()) {
            @Override
            public void onResponse(OcsComment response) {
                responder.onResponse(response.split());
            }

            @Override
            public void onError(Throwable throwable) {
                responder.onError(throwable);
            }
        });
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
        dataBaseAdapter.updateComment(comment, setStatus);
        persistMentions(dataBaseAdapter, comment);
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, OcsComment ocsComment) {
        DeckComment comment = ocsComment.getSingle();
        dataBaseAdapter.deleteComment(comment, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<OcsComment> responder, OcsComment entity) {
        DeckComment comment = entity.getSingle();
        comment.setObjectId(card.getId());
        serverAdapter.createCommentForCard(comment, responder);
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<OcsComment> callback, OcsComment entity) {
        DeckComment comment = entity.getSingle();
        comment.setObjectId(card.getId());
        serverAdapter.updateCommentForCard(comment, callback);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, OcsComment entity, DataBaseAdapter dataBaseAdapter) {
        DeckComment comment = entity.getSingle();
        comment.setObjectId(card.getId());
        serverAdapter.deleteCommentForCard(comment, callback);
    }

    @Override
    public List<OcsComment> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return new OcsComment(dataBaseAdapter.getLocallyChangedCommentsByLocalCardIdDirectly(accountId, card.getLocalId())).split();
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<OcsComment> entitiesFromServer) {
        List<OcsComment> deletedComments = findDelta(entitiesFromServer, new OcsComment(dataBaseAdapter.getCommentByLocalCardIdDirectly(card.getLocalId())).split());
        for (OcsComment deletedComment : deletedComments) {
            if (deletedComment.getId()!=null){
                // preserve new, unsynced comment.
                dataBaseAdapter.deleteComment(deletedComment.getSingle(), false);
            }
        }
    }
}
