package it.niedermann.nextcloud.deck.javafx.ui.controller.stages;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.StageController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import jakarta.inject.Inject;

@StageScope
public class MainStageController extends StageController<MainStageController.Args> {

    @Inject
    public MainStageController(StageContext context,
                               StageRouter stageRouter) {
        super(context, stageRouter);
    }

    public CompletableFuture<Void> showLogin() {
        return this.stageRouter.navigateTo(LoginScene.class);
    }

    public CompletableFuture<Void> showMain() {
        return this.stageRouter.navigateTo(MainScene.class);
    }

    public CompletableFuture<Void> showError() {
        return this.stageRouter.navigateTo(ExceptionScene.class);
    }

    public sealed interface Args {
        record CurrentBoardOfCurrentAccount() implements Args {
        }

        record CurrentBoardOfAccount(Account.ID accountId) implements Args {
        }

        record RemoteAccount(String accountName, long cardRemoteId) implements Args {
        }

        record RemoteServer(URL server, long cardRemoteId) implements Args {
        }
    }

    @Override
    protected CompletableFuture<StageContext.State> deriveInitialState(Args args) {
        // TODO Mock implementation
        return CompletableFuture.completedFuture(
                new StageContext.State(
                        Optional.of(new Account.ID(1L)),
                        Optional.of(new Board.ID(1L)),
                        Optional.empty()));
    }
}
