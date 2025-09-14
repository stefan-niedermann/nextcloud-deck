package it.niedermann.nextcloud.deck.database.entity.ocs.comment.full;

import androidx.room.Embedded;
import androidx.room.Relation;

import it.niedermann.nextcloud.deck.database.entity.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.database.entity.ocs.comment.DeckComment;

public class FullDeckComment implements IRemoteEntity {

    @Embedded
    private DeckComment comment;

    @Relation(entity = DeckComment.class, parentColumn = "parentId", entityColumn = "localId")
    public DeckComment parent;

    public DeckComment getParent() {
        return parent;
    }

    public void setParent(DeckComment parent) {
        this.parent = parent;
    }

    public DeckComment getComment() {
        return comment;
    }

    public void setComment(DeckComment comment) {
        this.comment = comment;
    }

    @Override
    public IRemoteEntity getEntity() {
        return comment;
    }
}
