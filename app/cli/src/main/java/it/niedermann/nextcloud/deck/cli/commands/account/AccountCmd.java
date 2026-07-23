package it.niedermann.nextcloud.deck.cli.commands.account;


import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Single;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountListCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountRemoveCmd;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

@Command(name = "account",
        description = "List all accounts",
        subcommands = {
                AccountAddCmd.class,
                AccountListCmd.class,
                AccountRemoveCmd.class
        })
public class AccountCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AccountCmd.class.getName());

    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Override
    public Integer call() {
        try {
            final var account = Single.fromCompletionStage(getCurrentAccountUseCase.execute()).blockingGet();
            System.out.println(account);
            return 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
