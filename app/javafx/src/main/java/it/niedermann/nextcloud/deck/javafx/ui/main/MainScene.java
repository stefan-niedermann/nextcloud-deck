package it.niedermann.nextcloud.deck.javafx.ui.main;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.EditCardService;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.BoardListFeature;
import it.niedermann.nextcloud.deck.javafx.ui.main.features.HeaderFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class MainScene extends AbstractScene {

    @FXML
    Pane root;
    @FXML
    Pane headerHost;
    @FXML
    SplitPane splitPane;

    private final MainService mainService;
    private final EditCardService sidebarContext;
    private final StageTitleResolver stageTitleResolver;

    private final Inflater.FxBundle<?> boardListBundle;
    private final Inflater.FxBundle<?> headerBundle;
    private final Inflater.FxBundle<?> boardBundle;
    private final Inflater.FxBundle<EditCardFeature> editCardBundle;

    private double[] dividerPositions;

    @AssistedInject
    public MainScene(
            Inflater inflater,
            BoardListFeature.Factory boardListFactory,
            HeaderFeature.Factory headerFactory,
            BoardFeature.Factory boardFactory,
            EditCardFeature.Factory editCardFactory,
            EditCardService.Factory editCardStageContextFactory,
            StageTitleResolver stageTitleResolver,
            @Assisted MainService mainService
    ) {
        this.mainService = mainService;
        this.stageTitleResolver = stageTitleResolver;

        this.sidebarContext = editCardStageContextFactory.create(new EditCardService.State(Optional.empty(), false), () -> mainService.dispatch(new MainService.Action.CloseCardAction()));

        this.boardListBundle = inflater.inflate(boardListFactory.create(mainService));
        this.headerBundle = inflater.inflate(headerFactory.create(mainService));
        this.boardBundle = inflater.inflate(boardFactory.create(mainService));
        this.editCardBundle = inflater.inflate(editCardFactory.create(sidebarContext));
    }

    @AssistedFactory
    public interface Factory {
        MainScene createMainScene(MainService mainService);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        root.prefWidthProperty().bind(root.sceneProperty().flatMap(Scene::widthProperty));
        root.prefHeightProperty().bind(root.sceneProperty().flatMap(Scene::heightProperty));

        headerHost.getChildren().add(headerBundle.view());
        splitPane.getItems().addAll(boardListBundle.view(), boardBundle.view());

        final var accentColorDisposable = mainService.getBoard()
                .map(Board::color)
                .map(FxUtils::createAccentColorCss)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(root.styleProperty()::setValue);

        final var cardSidebarDisposable = Flowable.fromPublisher(mainService.getState())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(state -> {
                    sidebarContext.dispatch(new EditCardService.Action.SelectCard(state.cardId()));
                    if (state.cardId().isEmpty()) {
                        splitPane.getItems().remove(editCardBundle.view());

                    } else {
                        if (!splitPane.getItems().contains(editCardBundle.view())) {
                            splitPane.getItems().add(editCardBundle.view());
                            splitPane.setDividerPositions(splitPane.getDividerPositions()[0], .8);
                        }
                    }
                });

        addDisposable(accentColorDisposable, cardSidebarDisposable);

        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                mainService.dispatch(new MainService.Action.CloseCardAction());

            } else if (event.getCode() == KeyCode.F5) {
//                accountSwitcherController.scheduleSync();

            }
//            else if (event.getCode() == KeyCode.S && event.isControlDown()) {
//                if (splitPane.getItems().contains(editCardScrollpane)) {
//                    onCardSaved(card);
//                }
//            }
        });
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.fromPublisher(mainService.getState())
                .switchMap(state -> stageTitleResolver.resolve(state.accountId().orElse(null), state.boardId().orElse(null), state.cardId().orElse(null)));
    }
}

