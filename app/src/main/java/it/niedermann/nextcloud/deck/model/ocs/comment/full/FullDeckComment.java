package it.niedermann.nextcloud.deck.model.ocs.comment.full;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;
import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

public class FullDeckComment implements IRemoteEntity {

    @Embedded
    private DeckComment comment;

    @Relation(entity = DeckComment.class, parentColumn = "localId", entityColumn = "parentId")
    public List<FullDeckComment> children = new ArrayList<>();

    public List<FullDeckComment> getChildren() {
        return children;
    }

    public void setChildren(List<FullDeckComment> children) {
        this.children = children;
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
