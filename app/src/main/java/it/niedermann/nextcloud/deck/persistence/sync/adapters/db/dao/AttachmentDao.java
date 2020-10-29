package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Attachment;

@Dao
public interface AttachmentDao extends GenericDao<Attachment> {
    @Query("SELECT * FROM attachment where cardId = :cardId")
    LiveData<List<Attachment>> getAttachmentsForCard(long cardId);

    @Query("SELECT * FROM attachment where accountId = :accountId and id = :remoteId")
    Attachment getAttachmentByRemoteIdDirectly(long accountId, Long remoteId);

    @Query("SELECT * FROM attachment where accountId = :accountId and localId = :id")
    Attachment getAttachmentByLocalIdDirectly(long accountId, Long id);

    @Query("SELECT * FROM attachment WHERE accountId = :accountId and cardId = :localCardId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<Attachment> getLocallyChangedAttachmentsByLocalCardIdDirectly(long accountId, long localCardId);

    @Query("SELECT * FROM attachment WHERE accountId = :accountId and (status<>1 or id is null or lastModified <> lastModifiedLocal)")
    List<Attachment> getLocallyChangedAttachmentsDirectly(long accountId);

    @Query("SELECT a.* FROM attachment a inner join card c on c.localId = a.cardId " +
            "WHERE c.stackId = :localStackId and (a.status<>1 or a.id is null or a.lastModified <> a.lastModifiedLocal)")
    List<Attachment> getLocallyChangedAttachmentsForStackDirectly(long localStackId);

    @Query("SELECT * FROM attachment WHERE accountId = :accountId and cardId = :localCardId")
    List<Attachment> getAttachmentsForLocalCardIdDirectly(long accountId, Long localCardId);
}