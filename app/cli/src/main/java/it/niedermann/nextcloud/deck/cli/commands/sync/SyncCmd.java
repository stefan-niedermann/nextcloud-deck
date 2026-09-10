package it.niedermann.nextcloud.deck.cli.commands.sync;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "sync",
        mixinStandardHelpOptions = true,
        description = "Trigger a synchronization",
        subcommands = {
                SyncStatusCmd.class
        })
public class SyncCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(SyncCmd.class.getName());

    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    ScheduleSyncUseCase scheduleSyncUseCase;

    @Override
    public Integer call() {
        try {
            final var accountId = getCurrentAccountUseCase.execute().join();
            Maybe.fromPublisher(scheduleSyncUseCase.execute(accountId)).blockingGet();
            System.out.println("Sync scheduled.");
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
