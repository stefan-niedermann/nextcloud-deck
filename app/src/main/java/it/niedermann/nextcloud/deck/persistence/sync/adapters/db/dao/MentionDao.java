package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;

@Dao
public interface MentionDao extends GenericDao<Mention> {

    @Query("delete from mention WHERE commentId = :commentID")
    void clearMentionsForCommentId(long commentID);
}