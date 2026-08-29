package it.niedermann.nextcloud.deck.app.shared.args.account;

import it.niedermann.nextcloud.deck.domain.model.Account;

public sealed interface AccountRawArgs {
    record None() implements AccountRawArgs {
    }

    record ExplicitAccount(Account.ID accountId) implements AccountRawArgs {
    }
}
