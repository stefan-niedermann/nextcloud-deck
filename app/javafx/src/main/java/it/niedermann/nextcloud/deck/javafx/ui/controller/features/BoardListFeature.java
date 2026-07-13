package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.BoardListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.util.Pair;

public class BoardListFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(BoardListFeature.class.getName());

    @FXML
    ListView<Board> boardList;

    private final GetBoardUseCase getBoardUseCase;
    private final ListBoardsUseCase listBoardsUseCase;
    private final ViewModel viewModel;

    @AssistedInject
    public BoardListFeature(
            GetBoardUseCase getBoardUseCase,
            ListBoardsUseCase listBoardsUseCase,
            @Assisted ViewModel viewModel
    ) {
        this.getBoardUseCase = getBoardUseCase;
        this.listBoardsUseCase = listBoardsUseCase;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        BoardListFeature create(ViewModel viewModel);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        boardList.setCellFactory(new BoardListItemCellFactory());

        final var listBoards = viewModel.getAccountId()
                .switchMap(listBoardsUseCase::execute);

        final var currentBoard = viewModel.getBoardId()
                .switchMap(getBoardUseCase::execute);

        final ChangeListener<Board> changeListener = (_, _, newValue) ->
                viewModel.onBoardSelected(newValue.id());

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

    public interface ViewModel {
        void onBoardSelected(Board.ID boardId);

        Flowable<Account.ID> getAccountId();

        Flowable<Board.ID> getBoardId();
    }
}
