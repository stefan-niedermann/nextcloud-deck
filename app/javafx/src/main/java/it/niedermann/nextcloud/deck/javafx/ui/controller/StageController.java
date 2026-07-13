package it.niedermann.nextcloud.deck.javafx.ui.controller;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;

public abstract class StageController<TArgs, TParsedArgs> {

    private static final Logger logger = Logger.getLogger(StageController.class.getName());

    protected final Inflater inflater;
    protected final StageRouter stageRouter;
    protected final ControllerFactory controllerFactory;
    private final LoginScene.Factory loginFactory;
    private final ExceptionScene.Factory exceptionFactory;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;

    public StageController(Inflater inflater,
                           StageRouter stageRouter,
                           ControllerFactory controllerFactory,
                           LoginScene.Factory loginFactory,
                           ExceptionScene.Factory exceptionFactory,
                           SetCurrentAccountUseCase setCurrentAccountUseCase) {
        this.inflater = inflater;
        this.stageRouter = stageRouter;
        this.controllerFactory = controllerFactory;
        this.loginFactory = loginFactory;
        this.exceptionFactory = exceptionFactory;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
    }

    public CompletableFuture<Void> initialize(TArgs args) {
        return this.showSplashScreenScene()
                .thenComposeAsync(_ -> deriveInitialState(args))
                .handleAsync((state, exception) ->
                        switch (new ExceptionUnwrapper().unwrap(exception)) {
                            case null -> this.showContent(state);
                            case NoAccountConfiguredException _ ->
                                    this.showLogin().thenComposeAsync(_ -> {
                                        return initialize(args);
                                    });
                            default -> this.showErrorScene(exception, args, state);
                        })
                .thenComposeAsync(a -> a);
    }

    private CompletableFuture<Void> showSplashScreenScene() {
        final var bundle = inflater.inflate(controllerFactory.call(SplashScreenScene.class));
        return this.stageRouter.setStageContent(bundle);
    }

    private CompletableFuture<Account.ID> showLogin() {
        final var accountImported = new CompletableFuture<Account.ID>();
        final var bundle = inflater.inflate(loginFactory.create(accountImported::complete));
        return this.stageRouter.setStageContent(bundle)
                .thenComposeAsync(_ -> accountImported)
                .thenComposeAsync(setCurrentAccountUseCase::execute);
    }

    protected abstract CompletableFuture<Void> showContent(TParsedArgs initialState);

    public CompletableFuture<Void> showErrorScene(Throwable throwable, TArgs args, TParsedArgs state) {
        // TODO Pass throwable via @AssistedFactory
        logger.log(Level.SEVERE, "Initialization error", throwable);
        final var bundle = inflater.inflate(exceptionFactory.create(new ExceptionScene.ViewModel() {

        }));
        return this.stageRouter.setStageContent(bundle);
    }

    abstract protected CompletableFuture<TParsedArgs> deriveInitialState(TArgs args);

    /// No account is configured at all
    public static class NoAccountConfiguredException extends RuntimeException {

    }

    /// Args are not specific enough to identify one matching account
    protected static class MultipleAccountsOnRequestedInstanceException extends RuntimeException {

        private final Collection<Account> matchingAccounts;

        protected MultipleAccountsOnRequestedInstanceException(Collection<Account> matchingAccounts) {
            this.matchingAccounts = matchingAccounts;
        }

        public Collection<Account> getMatchingAccounts() {
            return this.matchingAccounts;
        }
    }
}
