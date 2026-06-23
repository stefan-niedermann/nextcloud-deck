package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.RouteProvider;
import it.niedermann.nextcloud.deck.javafx.router.Router;
import it.niedermann.nextcloud.deck.javafx.services.scene.ContextService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AccountSwitcherFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;

public class MainScene extends SceneController implements EditCardFeature.EditCardListener {

    @FXML
    AccountSwitcherFeature accountSwitcherController;

    @FXML
    SplitPane splitPane;

    @FXML
    Node editCard;

    @FXML
    EditCardFeature editCardController;

    private final ContextService contextService;
    private final Router router;
    private final RouteProvider routeProvider;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final GetBoardUseCase getBoardUseCase;

    private double[] dividerPositions;

    @Inject
    public MainScene(
            ContextService contextService,
            Router router,
            RouteProvider routeProvider,
            ScheduleSyncUseCase scheduleSyncUseCase,
            GetBoardUseCase getBoardUseCase
    ) {
        this.contextService = contextService;
        this.router = router;
        this.routeProvider = routeProvider;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.getBoardUseCase = getBoardUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var accentColorDisposable = Flowable.fromPublisher(this.contextService.getState())
                .map(ContextService.State::boardId)
                .map(this.getBoardUseCase::execute)
                .switchMap(Flowable::fromPublisher)
                .map(Board::color)
                .map(FxUtils::createAccentColorCss)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(root.styleProperty()::setValue);

        final var cardSidebarDisposable = Flowable.fromPublisher(contextService.getState())
                .subscribe(state -> {
                    if (state.cardId() == null) {
                        splitPane.getItems().remove(editCard);

                    } else {
                        if (!splitPane.getItems().contains(editCard)) {
                            splitPane.getItems().add(editCard);
                            splitPane.setDividerPositions(splitPane.getDividerPositions()[0], .8);
                        }

                        editCardController.setCardId(state.cardId());
                        editCardController.setEditCardListener(this);
                    }
                });

        addDisposable(accentColorDisposable, cardSidebarDisposable);

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                contextService.dispatch(new ContextService.CloseCardAction());

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
        System.out.println(card);
        return CompletableFuture.completedFuture(null);
    }
}

