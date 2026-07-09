package it.niedermann.nextcloud.deck.cli.commands.account.subcommands;


import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.domain.usecases.accounts.RemoveAccountUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "remove",
        description = "Get the currently selected account")
public class AccountRemoveCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AccountRemoveCmd.class.getName());

    @Option(names = "-i",
            description = "Local cardId of the account to delete")
    Long id;

    @Option(names = "-n",
            description = "Account name (user@example.org)")
    String accountName;

    @Inject
    RemoveAccountUseCase removeAccountUseCase;

    @Override
    public Integer call() {
        try {
            if (id != null) {
                removeAccountUseCase.execute(id);
            } else {
                removeAccountUseCase.execute(accountName);
            }
            return 0;

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
