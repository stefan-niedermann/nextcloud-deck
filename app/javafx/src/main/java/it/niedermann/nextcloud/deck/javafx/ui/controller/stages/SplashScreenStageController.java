package it.niedermann.nextcloud.deck.javafx.ui.controller.stages;

import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import jakarta.inject.Inject;

public class SplashScreenStageController {

    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final StageRouter stageRouter;

    @Inject
    public SplashScreenStageController(
            HasAccountsUseCase hasAccountsUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            StageRouter stageRouter) {
        this.hasAccountsUseCase = hasAccountsUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.stageRouter = stageRouter;
    }

    public static class Factory {

        private final HasAccountsUseCase hasAccountsUseCase;
        private final GetCurrentAccountUseCase getCurrentAccountUseCase;

        @Inject
        public Factory(HasAccountsUseCase hasAccountsUseCase,
                       GetCurrentAccountUseCase getCurrentAccountUseCase) {
            this.hasAccountsUseCase = hasAccountsUseCase;
            this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        }

        public SplashScreenStageController create(StageRouter stageRouter) {
            return new SplashScreenStageController(
                    hasAccountsUseCase,
                    getCurrentAccountUseCase,
                    stageRouter);
        }
    }

    public CompletableFuture<Void> initialize() {
        return this.stageRouter.navigateTo(SplashScreenScene.class);
    }

    public CompletableFuture<Void> routeResolutionFailed(RouteResolveException exception) {
        return this.stageRouter.navigateTo(ExceptionScene.class);
        // TODO display scene that handles this error
        // TODO Recoverable:
        //  ACCOUNT_NOT_CONFIGURED -> EmptyContentView with Add Account
        //  NO_INTERNET_CONNECTION -> EmptyContentView with Refresh-Button
        //  ROUTE_NOT_FOUND -> ExceptionScene (displaying an EmptyContentView with a exceptionDetail)
    }

    public static class RouteResolveException extends Exception {

    }
}
