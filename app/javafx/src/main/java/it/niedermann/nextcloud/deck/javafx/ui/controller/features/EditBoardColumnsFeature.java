package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class EditBoardColumnsFeature extends DisposableController {

    @FXML
    ListView<Column> columns;
    @FXML
    TextField newColumnTitle;
    @FXML
    Button addColumnButton;

    private Board board;
    private final ViewModel viewModel;

    @AssistedInject
    public EditBoardColumnsFeature(@Assisted ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardColumnsFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        addColumnButton.setOnAction(_ -> onAddColumn());

        columns.setCellFactory(_ -> new ColumnListCell());

        final var columnsDisposable = viewModel.getColumns()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> columns.getItems().setAll(list));

        final var permissionsDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    this.board = board;
                    final boolean disable = !board.permissions().permissionManage();
                    newColumnTitle.setDisable(disable);
                    addColumnButton.setDisable(disable);
                    columns.refresh(); // Refresh cells to update their internal buttons
                });

        addDisposable(columnsDisposable, permissionsDisposable);
    }

    private void onAddColumn() {
        final var title = newColumnTitle.getText();
        if (title != null && !title.isBlank()) {
            viewModel.onAddColumn(title);
            newColumnTitle.clear();
        }
    }

    private class ColumnListCell extends ListCell<Column> {
        private final HBox root = new HBox(10);
        private final TextField titleField = new TextField();
        private final Button deleteButton = new Button("Delete");

        public ColumnListCell() {
            HBox.setHgrow(titleField, Priority.ALWAYS);
            final var spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            root.getChildren().addAll(titleField, spacer, deleteButton);

            titleField.setOnAction(_ -> {
                if (getItem() != null) {
                    viewModel.onUpdateColumn(getItem(), titleField.getText());
                }
            });

            deleteButton.setOnAction(_ -> {
                if (getItem() != null) {
                    final var disposable = viewModel.onDeleteColumn(getItem());
                    addDisposable(disposable);
                }
            });
        }

        @Override
        protected void updateItem(Column item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || board == null) {
                setGraphic(null);
            } else {
                final boolean disable = !board.permissions().permissionManage();

                titleField.setText(item.title());
                titleField.setDisable(disable);
                deleteButton.setDisable(disable);
                setGraphic(root);
            }
        }
    }

    public interface ViewModel {
        Flowable<Board> getBoard();
        Flowable<List<Column>> getColumns();
        void onAddColumn(String title);
        Disposable onDeleteColumn(Column column);
        void onUpdateColumn(Column column, String newTitle);
    }
}
