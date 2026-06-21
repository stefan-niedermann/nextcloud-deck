package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.javafx.services.scene.ContextService;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.BoardListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.util.Pair;

public class BoardListFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(BoardListFeature.class.getName());

    @FXML
    ListView<Board> boardList;

    private final ContextService contextService;
    private final GetBoardUseCase getBoardUseCase;
    private final ListBoardsUseCase listBoardsUseCase;

    @Inject
    public BoardListFeature(
            ContextService contextService,
            GetBoardUseCase getBoardUseCase,
            ListBoardsUseCase listBoardsUseCase
    ) {
        this.contextService = contextService;
        this.getBoardUseCase = getBoardUseCase;
        this.listBoardsUseCase = listBoardsUseCase;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final ChangeListener<Board> changeListener = (_, _, newValue) ->
                contextService.dispatch(new ContextService.OpenBoardAction(newValue.id()));

        boardList.setCellFactory(new BoardListItemCellFactory());

        final var listBoards = Flowable.fromPublisher(this.contextService.getState())
                .map(ContextService.State::accountId)
                .switchMap(listBoardsUseCase::execute);

        final var currentBoard = Flowable.fromPublisher(this.contextService.getState())
                .map(ContextService.State::boardId)
                .switchMap(getBoardUseCase::execute);

        final var disposable = Flowable.combineLatest(listBoards, currentBoard, Pair::new)
                .subscribe(args -> {
                    boardList.getSelectionModel().selectedItemProperty().removeListener(changeListener);
                    boardList.getItems().setAll(args.getKey());
                    boardList.getSelectionModel().select(args.getValue());
                    boardList.getSelectionModel().selectedItemProperty().addListener(changeListener);
                });

        addDisposable(disposable);

    }
}
