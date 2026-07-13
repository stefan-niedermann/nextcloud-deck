package it.niedermann.nextcloud.deck.javafx.services.application;

import java.net.URL;

import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.EditCardStageController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.stages.MainStageController;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@FxScope
public class ApplicationRouter {

    private final Stage primaryStage;
    private final StageComponent.Factory stageComponentFactory;
    private final ThemeService themeService;

    @Inject
    public ApplicationRouter(@NamedPrimaryStage Stage primaryStage,
                             StageComponent.Factory stageComponentFactory,
                             ThemeService themeService) {
        this.primaryStage = primaryStage;
        this.stageComponentFactory = stageComponentFactory;
        this.themeService = themeService;
    }

    // region Public API

    public void initialize() {
        launchMainStage(primaryStage, new MainStageController.Args.CurrentBoardOfCurrentAccount());
    }

    public void launchMainStage(Stage stage, URL url, long cardRemoteId) {
        launchMainStage(stage, new MainStageController.Args.RemoteServer(url, cardRemoteId));
    }

    public void launchMainStage(Stage stage, String accountName, long cardRemoteId) {
        launchMainStage(stage, new MainStageController.Args.RemoteAccount(accountName, cardRemoteId));
    }

    // endregion

    // region Internal API

    private void launchMainStage(Stage stage, MainStageController.Args args) {
        final var stageComponent = stageComponentFactory.create(stage);
        stageComponent.getMainStageController().initialize(args);
    }

    private void launchEditCardStage(Stage stage, EditCardStageController.Args args) {
        final var stageComponent = stageComponentFactory.create(stage);
        stageComponent.getEditCardStageController().initialize(args);
    }

    // endregion

}
