package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import io.reactivex.rxjava4.processors.FlowableProcessor;
import io.reactivex.rxjava4.processors.ReplayProcessor;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.BoardListItemCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class BoardListFeature extends DisposableController implements ChangeListener<Board> {

    private static final Logger logger = Logger.getLogger(BoardListFeature.class.getName());

    @FXML
    ListView<Board> boardList;

    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;
    private final ListBoardsUseCase listBoardsUseCase;

    @Inject
    public BoardListFeature(
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            SetCurrentBoardUseCase setCurrentBoardUseCase,
            ListBoardsUseCase listBoardsUseCase
    ) {
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.setCurrentBoardUseCase = setCurrentBoardUseCase;
        this.listBoardsUseCase = listBoardsUseCase;
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        boardList.setCellFactory(new BoardListItemCellFactory());

        final var accountPublisher = getCurrentAccountUseCase.execute("BoardListController");

        final var currentAccountFLowable = ReplayProcessor.fromPublisher(accountPublisher)
                .map(Account::id);

        final var listBoardsDisposable = currentAccountFLowable
                .switchMap(listBoardsUseCase::execute)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(boards -> {

                    boardList.getSelectionModel()
                            .selectedItemProperty()
                            .removeListener(BoardListFeature.this);

                    boardList.getItems().setAll(boards);

                    boardList.getSelectionModel()
                            .selectedItemProperty()
                            .addListener(BoardListFeature.this);
                });

        addDisposable(listBoardsDisposable);

        final var currentBoardsDisposable = currentAccountFLowable
                .switchMap(getCurrentBoardUseCase::execute)
                .subscribe(board -> boardList.getSelectionModel().select(board));

        addDisposable(currentBoardsDisposable);
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

            final var disposable = FlowableProcessor.fromPublisher(getCurrentAccountUseCase.execute("BoardListController for setting board"))
                    .firstElement()
                    .map(Account::id)
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(accountId -> {

                        logger.finer("Set current board to: " + accountId + " / " + newValue.id());
                        this.setCurrentBoardUseCase.execute(accountId, newValue.id());

                    });

            addDisposable(disposable);
        }
    }
}
