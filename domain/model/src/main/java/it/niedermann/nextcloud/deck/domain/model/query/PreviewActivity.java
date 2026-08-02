package it.niedermann.nextcloud.deck.domain.model.query;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Activity;

public record PreviewActivity(
        Activity activity,
        Account account
) {
}
