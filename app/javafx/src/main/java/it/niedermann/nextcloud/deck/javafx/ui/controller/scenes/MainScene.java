package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AccountSwitcherFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;

public class MainScene extends DisposableController implements EditCardFeature.EditCardListener {

    @FXML
    Parent root;

    @FXML
    AccountSwitcherFeature accountSwitcherController;

    @FXML
    SplitPane splitPane;

    @FXML
    Node editCard;

    @FXML
    EditCardFeature editCardController;

    private final StageContext stageContext;
    private final GetBoardUseCase getBoardUseCase;

    private double[] dividerPositions;

    @Inject
    public MainScene(
            StageContext stageContext,
            GetBoardUseCase getBoardUseCase
    ) {
        this.stageContext = stageContext;
        this.getBoardUseCase = getBoardUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var accentColorDisposable = Flowable.fromPublisher(this.stageContext.getState())
                .map(StageContext.State::boardId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this.getBoardUseCase::execute)
                .switchMap(Flowable::fromPublisher)
                .map(Board::color)
                .map(FxUtils::createAccentColorCss)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(root.styleProperty()::setValue);

        final var cardSidebarDisposable = Flowable.fromPublisher(stageContext.getState())
                .subscribe(state -> {
                    if (state.cardId().isEmpty()) {
                        splitPane.getItems().remove(editCard);

                    } else {
                        if (!splitPane.getItems().contains(editCard)) {
                            splitPane.getItems().add(editCard);
                            splitPane.setDividerPositions(splitPane.getDividerPositions()[0], .8);
                        }

                        editCardController.setCardId(state.cardId().get());
                        editCardController.setEditCardListener(this);
                    }
                });

        addDisposable(accentColorDisposable, cardSidebarDisposable);

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stageContext.dispatch(new StageContext.Action.CloseCardAction());

            } else if (event.getCode() == KeyCode.F5) {
                accountSwitcherController.scheduleSync();

            }
//            else if (event.getCode() == KeyCode.S && event.isControlDown()) {
//                if (splitPane.getItems().contains(editCardScrollpane)) {
//                    onCardSaved(card);
//                }
//            }
        });
    }

    @Override
    public CompletableFuture<Void> onCardSaved(Card card) {
        System.out.println("[MOCK] onCardSaved " + card);
        return CompletableFuture.completedFuture(null);
    }
}

