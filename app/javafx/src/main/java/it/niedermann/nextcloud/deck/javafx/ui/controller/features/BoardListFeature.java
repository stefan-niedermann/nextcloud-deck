package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.javafx.services.MainService;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.BoardListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.util.Pair;

public class BoardListFeature extends DisposableController implements ChangeListener<Board> {

    private static final Logger logger = Logger.getLogger(BoardListFeature.class.getName());

    @FXML
    ListView<Board> boardList;

    private final MainService mainService;
    private final GetBoardUseCase getBoardUseCase;
    private final ListBoardsUseCase listBoardsUseCase;

    @Inject
    public BoardListFeature(
            MainService mainService,
            GetBoardUseCase getBoardUseCase,
            ListBoardsUseCase listBoardsUseCase
    ) {
        this.mainService = mainService;
        this.getBoardUseCase = getBoardUseCase;
        this.listBoardsUseCase = listBoardsUseCase;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        boardList.setCellFactory(new BoardListItemCellFactory());

        final var listBoards = Flowable.fromPublisher(this.mainService.getState())
                .map(MainService.State::accountId)
                .switchMap(listBoardsUseCase::execute);

        final var currentBoard = Flowable.fromPublisher(this.mainService.getState())
                .map(MainService.State::boardId)
                .switchMap(getBoardUseCase::execute);

        final var disposable = Flowable.combineLatest(listBoards, currentBoard, Pair::new)
                .subscribe(args -> {
                    boardList.getItems().setAll(args.getKey());
                    boardList.getSelectionModel().select(args.getValue());
                });

        addDisposable(disposable);

        boardList.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) ->
                mainService.dispatch(new MainService.OpenBoardAction(newValue.id())));
    }

    @Override
    public void changed(ObservableValue observable, Board oldValue, Board newValue) {

        // If the oldValue is null, this change was not initiated by a user but programmatically triggered while setting a new board list
        if (oldValue == null) {
            return;
        }

        // If the newValue is null, we can't set a new currentBoardId
        if (newValue == null) {
            return;
        }

        final boolean boardIdChanged = !Objects.equals(oldValue.id(), newValue.id());

        if (boardIdChanged) {

            logger.finer("Selected board changed from " + oldValue.id() + " to " + newValue.id());
            mainService.dispatch(new MainService.OpenBoardAction(newValue.id()));

        }
    }
}
