package it.niedermann.nextcloud.remote;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.remote.deck.DeckApi;
import it.niedermann.nextcloud.remote.ocs.OcsApi;


public interface ApiProvider {

    OcsApi getOcsApi();

    DeckApi getDeckApi();

    interface Factory {

        CompletableFuture<ApiProvider> create(Account.ID accountId);

    }
}
