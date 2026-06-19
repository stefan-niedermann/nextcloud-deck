package it.niedermann.nextcloud.deck.model.ocs.comment;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.model.interfaces.IRemoteEntity;

public class OcsComment implements IRemoteEntity {
    private List<DeckComment> comments;

    public OcsComment() {
        comments = new ArrayList<>();
    }

    public OcsComment(List<DeckComment> comments) {
        this.comments = comments;
    }

    public List<DeckComment> getComments() {
        return comments;
    }

    public void addComment(DeckComment comment) {
        comments.add(comment);
    }

    @Override
    public IRemoteEntity getEntity() {
        return comments.size() > 0 ? comments.get(0) : null;
    }

    public List<OcsComment> split() {
        List<OcsComment> commentsList = new ArrayList<>();
        for (DeckComment comment : comments) {
            commentsList.add(OcsComment.of(comment));
        }
        return commentsList;
    }

    public static OcsComment of(DeckComment comment) {
        OcsComment ret = new OcsComment();
        ret.addComment(comment);
        return ret;
    }

    public DeckComment getSingle(){
        if (comments.size() != 1) {
            throw new IllegalStateException("Expected to have a single item but has " + comments.size());
        }
        return getFirst();
    }

    public DeckComment getFirst() {
        return comments.get(0);
    }
}
