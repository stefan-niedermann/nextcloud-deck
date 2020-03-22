package it.niedermann.nextcloud.deck.ui.card.comments;

import it.niedermann.nextcloud.deck.model.ocs.comment.DeckComment;

public interface CommentAddedListener {
    void onCommentAdded(DeckComment comment);
}
