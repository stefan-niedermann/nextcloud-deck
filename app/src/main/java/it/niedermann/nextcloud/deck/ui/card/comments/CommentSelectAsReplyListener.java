package it.niedermann.nextcloud.deck.ui.card.comments;

import it.niedermann.nextcloud.deck.model.ocs.comment.full.FullDeckComment;

public interface CommentSelectAsReplyListener {
    void onSelectAsReply(FullDeckComment comment);
}
