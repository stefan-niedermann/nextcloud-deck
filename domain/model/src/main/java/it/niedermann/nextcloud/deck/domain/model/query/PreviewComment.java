package it.niedermann.nextcloud.deck.domain.model.query;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Comment;

public record PreviewComment(
        Comment comment,
        Account account
) {
}
