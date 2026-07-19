package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.MainStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardListFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.HeaderFeature;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class MainScene extends DisposableController {

    @FXML
    Parent root;
    @FXML
    Pane accountSwitcherHost;
    @FXML
    SplitPane splitPane;

    private final MainStageContext mainStageContext;
    private final GetBoardUseCase getBoardUseCase;

    private final Inflater.FxBundle<?> boardListBundle;
    private final Inflater.FxBundle<?> accountSwitcherBundle;
    private final Inflater.FxBundle<?> boardBundle;
    private final Inflater.FxBundle<EditCardFeature> editCardBundle;

    private double[] dividerPositions;

    @AssistedInject
    public MainScene(
            GetBoardUseCase getBoardUseCase,
            Inflater inflater,
            BoardListFeature.Factory boardListFactory,
            HeaderFeature.Factory accountSwitcherFactory,
            BoardFeature.Factory boardFactory,
            EditCardFeature.Factory editCardFactory,
            @Assisted MainStageContext mainStageContext
    ) {
        this.mainStageContext = mainStageContext;
        this.getBoardUseCase = getBoardUseCase;

        this.boardListBundle = inflater.inflate(boardListFactory.create(mainStageContext));
        this.accountSwitcherBundle = inflater.inflate(accountSwitcherFactory.create(mainStageContext));
        this.boardBundle = inflater.inflate(boardFactory.create(mainStageContext));
        this.editCardBundle = inflater.inflate(editCardFactory.create(mainStageContext));
    }

    @AssistedFactory
    public interface Factory {
        MainScene createMainScene(MainStageContext mainStageContext);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        accountSwitcherHost.getChildren().add(accountSwitcherBundle.view());
        splitPane.getItems().addAll(boardListBundle.view(), boardBundle.view());

        final var accentColorDisposable = Flowable.fromPublisher(this.mainStageContext.getState())
                .map(MainStageContext.State::boardId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this.getBoardUseCase::execute)
                .switchMap(Flowable::fromPublisher)
                .map(Board::color)
                .map(FxUtils::createAccentColorCss)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(root.styleProperty()::setValue);

        final var cardSidebarDisposable = Flowable.fromPublisher(mainStageContext.getState())
                .subscribe(state -> {
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
                mainStageContext.dispatch(new MainStageContext.Action.CloseCardAction());

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
}

