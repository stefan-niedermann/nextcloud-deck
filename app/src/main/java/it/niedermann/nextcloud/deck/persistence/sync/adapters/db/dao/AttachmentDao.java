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
}