package it.niedermann.nextcloud.deck.cli.commands.sync;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.GetSyncStatusUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "status",
        mixinStandardHelpOptions = true,
        description = "Get synchronization status")
public class SyncStatusCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(SyncStatusCmd.class.getName());

    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    GetSyncStatusUseCase getSyncStatusUseCase;

    @Override
    public Integer call() {
        try {
            final var accountId = getCurrentAccountUseCase.execute().join();
            final var status = Maybe.fromPublisher(getSyncStatusUseCase.execute(accountId)).blockingGet();
            System.out.println(status.orElse(null));
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
