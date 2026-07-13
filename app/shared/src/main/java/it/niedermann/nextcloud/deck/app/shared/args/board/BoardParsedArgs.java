package it.niedermann.nextcloud.deck.app.shared.args.board;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;

public record BoardParsedArgs(Account.ID accountId,
                              Board.ID boardId) {
}
