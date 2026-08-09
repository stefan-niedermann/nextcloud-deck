package it.niedermann.nextcloud.deck.javafx.services.application;

import java.net.URL;

import it.niedermann.nextcloud.deck.app.shared.args.EmptyArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.di.named.NamedPrimaryStage;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import jakarta.inject.Inject;
import javafx.stage.Stage;

@FxScope
public class ApplicationRouter {

    private final Stage primaryStage;
    private final StageComponent.Factory stageComponentFactory;

    @Inject
    public ApplicationRouter(@NamedPrimaryStage Stage primaryStage,
                             StageComponent.Factory stageComponentFactory) {
        this.primaryStage = primaryStage;
        this.stageComponentFactory = stageComponentFactory;
    }

    // region Public API

    public void initialize() {
        launchMainStage(primaryStage, new BoardRawArgs.CurrentBoardOfCurrentAccount());
    }

    public void launchMainStage(URL url, long cardRemoteId) {
        launchMainStage(new Stage(), new BoardRawArgs.RemoteServer(url, cardRemoteId));
    }

    public void launchMainStage(String accountName, long cardRemoteId) {
        launchMainStage(new Stage(), new BoardRawArgs.RemoteAccount(accountName, cardRemoteId));
    }

    public void launchEditBoardStage(Account.ID accountId, Board.ID boardId) {
        launchEditBoardStage(new Stage(), new BoardRawArgs.ExplicitBoard(accountId, boardId));
    }

    public void launchEditCardStage(Card.ID cardId) {
        launchEditCardStage(new Stage(), new CardRawArgs.LocalCard(cardId));
    }

    public void launchPreferencesStage() {
        launchPreferencesStage(new Stage());
    }

    // endregion

    // region Internal API

    private void launchMainStage(Stage stage, BoardRawArgs args) {
        final var stageComponent = stageComponentFactory.create(stage);
        stageComponent.getMainStageFactory().create(args).initialize();
    }

    private void launchEditCardStage(Stage stage, CardRawArgs args) {
        final var stageComponent = stageComponentFactory.create(stage);
        stageComponent.getEditCardStageFactory().create(args).initialize();
    }

    private void launchEditBoardStage(Stage stage, BoardRawArgs args) {
        final var stageComponent = stageComponentFactory.create(stage);
        stageComponent.getEditBoardStageFactory().create(args).initialize();
    }

    private void launchPreferencesStage(Stage stage) {
        final var stageComponent = stageComponentFactory.create(stage);
        stageComponent.getPreferencesStageFactory().create(EmptyArgs.INSTANCE).initialize();
    }

    // endregion

}
