package it.niedermann.nextcloud.deck.javafx.ui.editboard.features;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class EditBoardLabelsFeature extends AbstractFeature {

    @FXML
    ListView<Label> labels;
    @FXML
    TextField newLabelTitle;
    @FXML
    ColorPicker newLabelColor;
    @FXML
    Button addLabelButton;

    private Board board;
    private final ViewModel viewModel;

    @AssistedInject
    public EditBoardLabelsFeature(Inflater inflater,
                                  @Assisted ViewModel viewModel) {
        super(inflater);

        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        EditBoardLabelsFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        addLabelButton.setOnAction(_ -> onAddLabel());

        labels.setCellFactory(_ -> new LabelListCell());

        final var labelsDisposable = viewModel.getLabels()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> labels.getItems().setAll(list));

        final var permissionsDisposable = viewModel.getBoard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(board -> {
                    this.board = board;
                    final boolean disable = !board.permissions().permissionManage();
                    newLabelTitle.setDisable(disable);
                    newLabelColor.setDisable(disable);
                    addLabelButton.setDisable(disable);
                    labels.refresh();
                });

        addDisposable(labelsDisposable, permissionsDisposable);
    }

    private void onAddLabel() {
        final var title = newLabelTitle.getText();
        final var color = newLabelColor.getValue();
        if (title != null && !title.isBlank() && color != null) {
            viewModel.onAddLabel(title, new Color((int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255)));
            newLabelTitle.clear();
        }
    }

    private class LabelListCell extends ListCell<Label> {
        private final HBox root = new HBox(10);
        private final TextField titleField = new TextField();
        private final ColorPicker colorPicker = new ColorPicker();
        private final Button deleteButton = new Button("Delete");

        public LabelListCell() {
            HBox.setHgrow(titleField, Priority.ALWAYS);
            final var spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            root.getChildren().addAll(titleField, colorPicker, spacer, deleteButton);

            titleField.setOnAction(_ -> updateLabel());
            colorPicker.setOnAction(_ -> updateLabel());

            deleteButton.setOnAction(_ -> {
                if (getItem() != null) {
                    viewModel.onDeleteLabel(getItem());
                }
            });
        }

        private void updateLabel() {
            if (getItem() != null) {
                final var fxColor = colorPicker.getValue();
                final var color = new Color((int) (fxColor.getRed() * 255), (int) (fxColor.getGreen() * 255), (int) (fxColor.getBlue() * 255));
                viewModel.onUpdateLabel(getItem(), titleField.getText(), color);
            }
        }

        @Override
        protected void updateItem(Label item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null || board == null) {
                setGraphic(null);
            } else {
                final boolean disable = !board.permissions().permissionManage();

                titleField.setText(item.title());
                colorPicker.setValue(javafx.scene.paint.Color.rgb(item.color().getRed(), item.color().getGreen(), item.color().getBlue()));
                titleField.setDisable(disable);
                colorPicker.setDisable(disable);
                deleteButton.setDisable(disable);
                setGraphic(root);
            }
        }
    }

    public interface ViewModel {
        Flowable<Board> getBoard();

        Flowable<Collection<Label>> getLabels();

        void onAddLabel(String title, Color color);

        void onDeleteLabel(Label label);

        void onUpdateLabel(Label label, String newTitle, Color newColor);
    }
}
