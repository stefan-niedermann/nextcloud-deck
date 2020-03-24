package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

@Dao
public interface CommentDao extends GenericDao<DeckComment> {

    @Query("SELECT * FROM DeckComment where accountId = :accountId and id = :remoteId")
    DeckComment getCommentByRemoteIdDirectly(long accountId, Long remoteId);

    @Query("SELECT * FROM DeckComment where accountId = :accountId and localId = :id")
    DeckComment getCommentByLocalIdDirectly(long accountId, Long id);

    @Query("SELECT * FROM DeckComment WHERE accountId = :accountId and objectId = :localCardId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<DeckComment> getLocallyChangedCommentsByLocalCardIdDirectly(long accountId, long localCardId);

    @Query("SELECT * FROM DeckComment WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<DeckComment> getLocallyChangedCommentsDirectly(long accountId);

    @Query("SELECT * FROM DeckComment WHERE accountId = :accountId and objectId = :localCardId")
    List<DeckComment> getCommentsForLocalCardIdDirectly(long accountId, Long localCardId);

    @Query("SELECT * FROM DeckComment where objectId = :localCardId")
    List<DeckComment> getCommentByLocalCardIdDirectly(Long localCardId);

    @Query("SELECT * FROM DeckComment where objectId = :localCardId order by creationDateTime desc")
    LiveData<List<DeckComment>> getCommentByLocalCardId(Long localCardId);
}