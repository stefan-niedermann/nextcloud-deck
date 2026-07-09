package it.niedermann.nextcloud.deck.cli.commands.account.subcommands;


import static org.reactivestreams.FlowAdapters.toPublisher;
import static io.reactivex.rxjava3.core.Flowable.fromPublisher;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "add",
        description = "Add a new account to the local database")
public class AccountAddCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AccountAddCmd.class.getName());

    public static final String AUTHENTICATION_METHOD = "it.niedermann.nextcloud.deck.cli.apptoken";
    public static final String ENV_VAR_PASSWORD = "it.niedermann.nextcloud.deck.cli.password";

    @Option(names = "-s",
            description = "Server address",
            required = true)
    URL url;

    @Option(names = "-u",
            description = "Username",
            required = true)
    String username;

    @Option(names = "-p",
            description = "Password - can also be provided as environment variable \"" + ENV_VAR_PASSWORD + "\"")
    String password;

    @Inject
    ImportAccountUseCase importAccountUseCase;

    @Inject
    SetCurrentAccountUseCase setCurrentAccountUseCase;

    @Inject
    AppTokenAuthProvider appTokenAuthProvider;

    @Override
    public Integer call() {
        try {
            final var password = Optional.ofNullable(this.password)
                    .orElse(System.getenv(ENV_VAR_PASSWORD));

            if (password == null) {
                throw new NoPasswordEnvVarException(username);
            }

            final var token = appTokenAuthProvider.generateToken(url, username, password);
            final var accountId = fromPublisher(toPublisher(importAccountUseCase.execute(url, username, token)))
                    .lastElement()
                    .blockingGet()
                    .account()
                    .id();

            setCurrentAccountUseCase.execute(accountId);

            System.out.println("Successfully connected to user \"" + username + "\" on " + url + " with local accountId " + accountId);

            return 0;

        } catch (NoPasswordEnvVarException e) {
            System.err.println(e.getMessage());
            return 2;

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }

    private static class NoPasswordEnvVarException extends IllegalArgumentException {
        NoPasswordEnvVarException(String username) {
            super("Provide environment variable \"" + ENV_VAR_PASSWORD + "\" containing the password for \"" + username + "\"");
        }
    }
}
