package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.dao;

import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

import it.niedermann.nextcloud.deck.model.ocs.comment.Mention;

@Dao
public interface MentionDao extends GenericDao<Mention> {

    @Query("delete from mention WHERE commentId = :commentID")
    void clearMentionsForCommentId(long commentID);

    @Query("select * from mention WHERE commentId = :commentID")
    List<Mention> getMentionsForCommentIdDirectly(long commentID);
}