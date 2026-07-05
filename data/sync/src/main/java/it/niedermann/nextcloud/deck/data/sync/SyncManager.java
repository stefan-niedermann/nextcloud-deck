package it.niedermann.nextcloud.deck.data.sync;

import static java.util.Collections.emptySet;

import java.util.function.Consumer;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.SyncStatus;
import it.niedermann.nextcloud.remote.ApiProvider;
import jakarta.inject.Inject;

public class SyncManager {

    private static final Logger logger = Logger.getLogger(SyncManager.class.getName());

    private final ApiProvider.Factory apiProviderFactory;

    @Inject
    public SyncManager(
            ApiProvider.Factory apiProviderFactory
    ) {
        this.apiProviderFactory = apiProviderFactory;
    }

    public void synchronize(Account account, Consumer<SyncStatus> reporter) throws Exception {

        // TODO Implement
        //   1. Wait for Capabilities Call to verify Deck is installed and version is compatible
        //   2. Synchronize stuff

        // The following content of this method is a NoOp dummy implementation and can be replaced entirely.

        final int MOCK_DURATION_PER_BOARD = 500;
        final int MOCK_BOARD_COUNT = 10;

        Thread.sleep(MOCK_DURATION_PER_BOARD);
        reporter.accept(new SyncStatus(account, emptySet(), 0, 0));

        for (int i = 0; i <= MOCK_BOARD_COUNT; i++) {
            Thread.sleep(MOCK_DURATION_PER_BOARD);
            reporter.accept(new SyncStatus(account, emptySet(), MOCK_BOARD_COUNT, i));
        }

        final var apiProvider = apiProviderFactory.create(account.id()).join();
        final var ocsApi = apiProvider.getOcsApi();

        final var response = ocsApi.getCapabilitiesRx(null).blockingGet();
        logger.info(response.ocs().data().capabilities().toString());

    }
}