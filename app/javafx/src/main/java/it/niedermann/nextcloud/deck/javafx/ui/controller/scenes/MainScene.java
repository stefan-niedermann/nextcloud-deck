package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.awt.Color;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase;
import it.niedermann.nextcloud.deck.javafx.RouteProvider;
import it.niedermann.nextcloud.deck.javafx.router.Router;
import it.niedermann.nextcloud.deck.javafx.ui.controller.FeatureFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AccountSwitcherFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;

public class MainScene extends SceneController implements CardPreviewView.CardPreviewActionListener, EditCardFeature.EditCardListener {

    @FXML
    BoardFeature boardController;
    @FXML
    AccountSwitcherFeature accountSwitcherFeature;

    @FXML
    SplitPane splitPane;

    private ScrollPane editCardScrollpane;
    private EditCardFeature editCardFeature;

    private final FeatureFactory featureFactory;
    private final Router router;
    private final RouteProvider routeProvider;
    private final ScheduleSyncUseCase scheduleSyncUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;

    private double[] dividerPositions;

    @Inject
    public MainScene(
            FeatureFactory featureFactory,
            Router router,
            RouteProvider routeProvider,
            ScheduleSyncUseCase scheduleSyncUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase
    ) {
        this.featureFactory = featureFactory;
        this.router = router;
        this.routeProvider = routeProvider;
        this.scheduleSyncUseCase = scheduleSyncUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var currentBoard = Flowable.fromPublisher(this.getCurrentAccountUseCase.execute("Main"))
                .map(Account::id)
                .map(this.getCurrentBoardUseCase::execute)
                .switchMap(Flowable::fromPublisher);

        final var disposable = currentBoard
                .map(Board::id)
                .subscribe(boardId -> {
                    onCloseCard();
                    this.boardController.setBoard(boardId);
                });

        final var d2 = currentBoard
                .map(Board::color)
                .map(Color::getRGB)
                .map(Integer::toHexString)
                .map(str -> str.substring(2))
                .map(hexColor -> "#" + hexColor)
                .map(hexColor -> String.format("""
                        -fx-accent: %1$s;
                        -fx-default-button: derive(-fx-accent, 90%%);
                        -fx-focus-color: derive(-fx-accent, 60%%);
                        -fx-faint-focus-color: derive(-fx-accent, 65%%);
                        """, hexColor))
//                .subscribe(System.out::println);
                .subscribe(root.styleProperty()::setValue);

        addDisposable(disposable, d2);

        boardController.setCardPreviewActionListener(this);

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                onCloseCard();

            } else if (event.getCode() == KeyCode.F5) {
                accountSwitcherFeature.scheduleSync();

            }
//            else if (event.getCode() == KeyCode.S && event.isControlDown()) {
//                if (splitPane.getItems().contains(editCardScrollpane)) {
//                    onCardSaved(card);
//                }
//            }
        });
    }

    @Override
    public void onOpenCard(Card card) {

        if (editCardScrollpane == null) {
            final var fxBundle = this.featureFactory.inflateFeature(EditCardFeature.class);
            addDisposable(fxBundle.controller());

            editCardScrollpane = new ScrollPane();
            editCardScrollpane.setFitToWidth(true);
            editCardScrollpane.setFitToHeight(true);
            editCardScrollpane.setContent(fxBundle.view());
            editCardScrollpane.setMinWidth(300);

            editCardFeature = fxBundle.controller();
        }

        if (!splitPane.getItems().contains(editCardScrollpane)) {
            splitPane.getItems().add(editCardScrollpane);
            splitPane.setDividerPositions(splitPane.getDividerPositions()[0], .8);
        }

        editCardFeature.setCardId(card.id());
        editCardFeature.setEditCardListener(this);
    }

    private void onCloseCard() {
        splitPane.getItems().remove(editCardScrollpane);
    }

    @Override
    public CompletableFuture<Void> onCardSaved(Card card) {
        System.out.println(card);
        return CompletableFuture.completedFuture(null);
    }
}

