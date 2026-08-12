
package it.niedermann.nextcloud.deck.domain.usecases.accounts;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.AuthenticatedAccount;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import jakarta.inject.Inject;

public class ImportAccountUseCase {

    private static final Logger logger = Logger.getLogger(ImportAccountUseCase.class.getName());

    private final AccountRepository accountRepository;
    private final SyncScheduler syncScheduler;

    @Inject
    public ImportAccountUseCase(
            AccountRepository accountRepository,
            SyncScheduler syncScheduler
    ) {
        this.accountRepository = accountRepository;
        this.syncScheduler = syncScheduler;
    }

    public Flow.Publisher<SyncStatus> execute(AuthenticatedAccount authenticatedAccount) {
        final var accountId = new AtomicReference<Account.ID>();
        final var result = Flowable.fromFuture(accountRepository.addAccount(
                        authenticatedAccount.url(),
                        authenticatedAccount.username(),
                        authenticatedAccount.token()))

                .doOnNext(accountId::set)
                .doOnNext(v -> {
                    logger.info("Workaround for first Board is different. Call endpoint, expect HTTP 200. See https://github.com/nextcloud/deck/issues/3229");
                    // TODO Workaround for first Board is different. Call endpoint, expect HTTP 200
                })
                .map(syncScheduler::scheduleSynchronization)

                .flatMap(FlowAdapters::toPublisher)

                .map(syncStatus -> {
                    logger.info("ImportAccountUseCase :: SyncStatus :: " + syncStatus);
                    return syncStatus;
                })
                .doOnError(e -> {
                    logger.log(Level.SEVERE, "ImportAccountUseCase :: Error during import", e);
                    accountRepository.removeAccount(accountId.get());
                });

        return FlowAdapters.toFlowPublisher(result);
    }
}