package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Attachment;

@Dao
public interface AttachmentDao extends GenericDao<Attachment> {
    @Query("SELECT * FROM attachment where cardId = :cardId")
    LiveData<List<Attachment>> getAttachmentsForCard(long cardId);

    @Query("SELECT * FROM attachment where accountId = :accountId and id = :remoteId")
    Attachment getAttachmentByRemoteIdDirectly(long accountId, Long remoteId);
}