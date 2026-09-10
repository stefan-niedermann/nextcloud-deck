package it.niedermann.nextcloud.deck.app.shared.args.column;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Column;

public record ColumnParsedArgs(Account.ID accountId,
                               Column.ID columnId) {
}
