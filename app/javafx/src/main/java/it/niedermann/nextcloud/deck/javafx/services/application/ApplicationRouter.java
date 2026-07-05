package it.niedermann.nextcloud.deck.javafx.services.application;

import java.net.URL;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.MainStageController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.SplashScreenStageController;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@FxScope
public class ApplicationRouter {

    private final Stage primaryStage;
    private final StageComponent.Factory stageComponentFactory;
    private final ThemeService themeService;
    private final SplashScreenStageController.Factory managerFactory;

    @Inject
    public ApplicationRouter(@NamedPrimaryStage Stage primaryStage,
                             StageComponent.Factory stageComponentFactory,
                             ThemeService themeService,
                             SplashScreenStageController.Factory managerFactory) {
        this.primaryStage = primaryStage;
        this.stageComponentFactory = stageComponentFactory;
        this.themeService = themeService;
        this.managerFactory = managerFactory;
    }

    // region Public API

    public CompletableFuture<Void> initialize() {
        return launchMainStage(primaryStage, new MainStageController.Args.CurrentBoardOfCurrentAccount());
    }

    public CompletableFuture<Void> launchMainStage(Stage stage, URL url, long cardRemoteId) {
        return launchMainStage(stage, new MainStageController.Args.RemoteServer(url, cardRemoteId));
    }

    public CompletableFuture<Void> launchMainStage(Stage stage, String accountName, long cardRemoteId) {
        return launchMainStage(stage, new MainStageController.Args.RemoteAccount(accountName, cardRemoteId));
    }

    // endregion

    // region Internal API

    private CompletableFuture<Void> launchMainStage(Stage stage, MainStageController.Args args) {
        final var stageComponent = stageComponentFactory.create(stage);
        return stageComponent.getMainStageController().initialize(args);
    }

    private CompletableFuture<Void> launchEditCardStage(Stage stage) {
        final var stageComponent = stageComponentFactory.create(stage);
        return stageComponent.getEditCardStageController().initialize();
    }

    // endregion

}
