package it.niedermann.nextcloud.deck.cli.commands.account.subcommands;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;

@Command(name = "list",
        description = "Get the currently selected account")
public class AccountListCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AccountListCmd.class.getName());

    @Inject
    GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    GetAccountsUseCase getAccountsUseCase;

    @Override
    public Integer call() {
        try {

            final var currentAccountId = getCurrentAccountUseCase.execute().join();
            final var accounts = Maybe.fromPublisher(getAccountsUseCase.execute()).blockingGet();

            final var sb = new StringBuilder();

            for (final var account : accounts) {
                final char state = Objects.equals(account.id(), currentAccountId) ? '*' : ' ';
                final var line = String.format(" %1$s %2$s@%3$s", state, account.username(), account.url().getHost());
                sb.append(line);
            }

            System.out.println(sb);
            return 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
