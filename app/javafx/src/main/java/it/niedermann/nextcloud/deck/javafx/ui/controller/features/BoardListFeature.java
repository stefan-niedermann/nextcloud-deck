package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.BoardListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.util.Pair;

public class BoardListFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(BoardListFeature.class.getName());

    @FXML
    ListView<Board> boardList;

    private final StageContext stageContext;
    private final GetBoardUseCase getBoardUseCase;
    private final ListBoardsUseCase listBoardsUseCase;

    @Inject
    public BoardListFeature(
            StageContext stageContext,
            GetBoardUseCase getBoardUseCase,
            ListBoardsUseCase listBoardsUseCase
    ) {
        this.stageContext = stageContext;
        this.getBoardUseCase = getBoardUseCase;
        this.listBoardsUseCase = listBoardsUseCase;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        boardList.setCellFactory(new BoardListItemCellFactory());

        final var listBoards = Flowable.fromPublisher(this.stageContext.getState())
                .map(StageContext.State::accountId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .switchMap(listBoardsUseCase::execute);

        final var currentBoard = Flowable.fromPublisher(this.stageContext.getState())
                .map(StageContext.State::boardId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .switchMap(getBoardUseCase::execute);

        final ChangeListener<Board> changeListener = (_, _, newValue) ->
                stageContext.dispatch(new StageContext.Action.DisplayBoardAction(newValue.id()));

        final var disposable = Flowable.combineLatest(listBoards, currentBoard, Pair::new)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(args -> {
                    boardList.getSelectionModel().selectedItemProperty().removeListener(changeListener);
                    boardList.getItems().setAll(args.getKey());
                    boardList.getSelectionModel().select(args.getValue());
                    boardList.getSelectionModel().selectedItemProperty().addListener(changeListener);
                });

        addDisposable(disposable);

    }
}
