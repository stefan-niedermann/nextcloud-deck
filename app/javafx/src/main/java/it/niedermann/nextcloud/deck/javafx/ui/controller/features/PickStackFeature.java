package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnsUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class PickStackFeature extends DisposableController {

    private final GetAccountsUseCase getAccountsUseCase;
    private final ListBoardsUseCase listBoardsUseCase;
    private final ListColumnsUseCase listColumnsUseCase;
    private final ViewModel viewModel;

    @FXML
    VBox accountBox;
    @FXML
    Label headlineLabel;
    @FXML
    ComboBox<Account> accountSelect;
    @FXML
    ComboBox<Board> boardSelect;
    @FXML
    ListView<Column> columnSelect;
    @FXML
    Button confirmButton;

    @AssistedInject
    public PickStackFeature(
            GetAccountsUseCase getAccountsUseCase,
            ListBoardsUseCase listBoardsUseCase,
            ListColumnsUseCase listColumnsUseCase,
            @Assisted Mode mode,
            @Assisted ViewModel viewModel
    ) {
        this.getAccountsUseCase = getAccountsUseCase;
        this.listBoardsUseCase = listBoardsUseCase;
        this.listColumnsUseCase = listColumnsUseCase;
        this.viewModel = viewModel;
        this.mode = mode;
    }

    @AssistedFactory
    public interface Factory {
        PickStackFeature create(Mode mode, ViewModel viewModel);
    }

    private final Mode mode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        headlineLabel.setText(resources.getString(mode == Mode.MOVE ? "moveCard" : "copyCard"));

        accountSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(Account account) {
                return account == null ? "" : account.accountName();
            }

            @Override
            public Account fromString(String string) {
                return null;
            }
        });

        boardSelect.setConverter(new StringConverter<>() {
            @Override
            public String toString(Board board) {
                return board == null ? "" : board.title();
            }

            @Override
            public Board fromString(String string) {
                return null;
            }
        });

        final var toggleGroup = new ToggleGroup();

        columnSelect.setCellFactory(lv -> new ListCell<>() {
            private final RadioButton radioButton = new RadioButton();

            {
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setMouseTransparent(true);
                radioButton.setFocusTraversable(false);
                radioButton.selectedProperty().bind(selectedProperty());
            }

            @Override
            protected void updateItem(Column item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    radioButton.setText(item.title());
                    setGraphic(radioButton);
                }
            }
        });

        columnSelect.getSelectionModel().selectedItemProperty().addListener((_, _, column) -> {
            confirmButton.setDisable(column == null);
        });

        addDisposable(Flowable.fromPublisher(getAccountsUseCase.execute())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(accounts -> {
                    accountSelect.setItems(FXCollections.observableArrayList(accounts.stream().toList()));
                    accountBox.setVisible(accounts.size() > 1);
                    accountBox.setManaged(accounts.size() > 1);
                    if (!accounts.isEmpty()) {
                        accountSelect.getSelectionModel().select(0);
                    }
                }));

        accountSelect.getSelectionModel().selectedItemProperty().addListener((_, _, account) -> {
            if (account != null) {
                addDisposable(Flowable.fromPublisher(listBoardsUseCase.execute(account.id()))
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(boards -> {
                            boardSelect.setItems(FXCollections.observableArrayList(boards));
                            if (!boards.isEmpty()) {
                                boardSelect.getSelectionModel().select(0);
                            }
                        }));
            }
        });

        boardSelect.getSelectionModel().selectedItemProperty().addListener((_, _, board) -> {
            if (board != null) {
                addDisposable(Flowable.fromPublisher(listColumnsUseCase.execute(board.id()))
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(columns -> {
                            columnSelect.setItems(FXCollections.observableArrayList(columns));
                        }));
            }
        });


        confirmButton.setDisable(true);
        confirmButton.setOnAction(event -> {
            final var column = columnSelect.getSelectionModel().getSelectedItem();
            if (column != null) {
                viewModel.onColumnSelected(column);
            }
            event.consume();
        });
    }

    public interface ViewModel {
        void onColumnSelected(Column column);
    }

    public enum Mode {
        MOVE, COPY
    }
}
