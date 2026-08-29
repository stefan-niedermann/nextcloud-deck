package it.niedermann.nextcloud.deck.app.shared.args.account;

import java.util.Optional;

import it.niedermann.nextcloud.deck.domain.model.Account;

public record AccountParsedArgs(Optional<Account.ID> accountId) {
}
