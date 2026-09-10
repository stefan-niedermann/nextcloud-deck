package it.niedermann.nextcloud.deck.app.shared.args.label;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Label;

public record LabelParsedArgs(Account.ID accountId,
                              Label.ID labelId) {
}
