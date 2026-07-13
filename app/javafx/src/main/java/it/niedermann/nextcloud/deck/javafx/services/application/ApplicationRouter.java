package it.niedermann.nextcloud.deck.javafx.services.application;

import java.net.URL;

import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.ui.stages.EditCardStageFactory;
import it.niedermann.nextcloud.deck.javafx.ui.stages.MainStageFactory;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javafx.stage.Stage;

@FxScope
public class ApplicationRouter {

    private final Stage primaryStage;
    private final StageComponent.Factory stageComponentFactory;
    private final Provider<MainStageFactory> mainStageControllerProvider;
    private final Provider<EditCardStageFactory> editCardStageControllerProvider;
    private final Provider<BoardArgResolver> boardArgsResolverProvider;
    private final Provider<CardArgResolver> cardArgsResolverProvider;

    @Inject
    public ApplicationRouter(@NamedPrimaryStage Stage primaryStage,
                             StageComponent.Factory stageComponentFactory,
                             Provider<MainStageFactory> mainStageControllerProvider,
                             Provider<EditCardStageFactory> editCardStageControllerProvider,
                             Provider<BoardArgResolver> boardArgsResolverProvider,
                             Provider<CardArgResolver> cardArgsResolverProvider) {
        this.primaryStage = primaryStage;
        this.stageComponentFactory = stageComponentFactory;
        this.mainStageControllerProvider = mainStageControllerProvider;
        this.editCardStageControllerProvider = editCardStageControllerProvider;
        this.boardArgsResolverProvider = boardArgsResolverProvider;
        this.cardArgsResolverProvider = cardArgsResolverProvider;
    }

    // region Public API

    public void initialize() {
        launchMainStage(primaryStage, new BoardRawArgs.CurrentBoardOfCurrentAccount());
    }

    public void launchMainStage(Stage stage, URL url, long cardRemoteId) {
        launchMainStage(stage, new BoardRawArgs.RemoteServer(url, cardRemoteId));
    }

    public void launchMainStage(Stage stage, String accountName, long cardRemoteId) {
        launchMainStage(stage, new BoardRawArgs.RemoteAccount(accountName, cardRemoteId));
    }

    // endregion

    // region Internal API

    private void launchMainStage(Stage stage, BoardRawArgs args) {
        final var stageComponent = stageComponentFactory.create(stage);
        final var mainStageController = mainStageControllerProvider.get();
        final var boardArgsResolver = boardArgsResolverProvider.get();
        stageComponent.getStageManager().initialize(args, boardArgsResolver, mainStageController);
    }

    private void launchEditCardStage(Stage stage, CardRawArgs args) {
        final var stageComponent = stageComponentFactory.create(stage);
        final var editCardStageController = editCardStageControllerProvider.get();
        final var cardArgsResolver = cardArgsResolverProvider.get();
        stageComponent.getStageManager().initialize(args, cardArgsResolver, editCardStageController);
    }

    // endregion

}
