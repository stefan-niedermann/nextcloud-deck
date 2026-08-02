package it.niedermann.nextcloud.remote;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.ocs.OcsApi;


public interface ApiProvider {

    OcsApi getOcsApi();

    DeckApi getDeckApi();

    interface Factory {

        ApiProvider create(Account account);

    }
}
