package it.niedermann.nextcloud.deck.javafx.ui.main.features;

import com.dlsc.gemsfx.AdvancedTableView;
import com.dlsc.gemsfx.CircleProgressIndicator;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.EmptyContentView;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.IconCounterView;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.LabelView;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.MultiAvatarView;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class BoardTableFeature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(BoardTableFeature.class.getName());

    @FXML
    VBox root;
    @FXML
    CircleProgressIndicator progress;
    @FXML
    EmptyContentView emptyContentView;
    @FXML
    AdvancedTableView<PreviewCard> table;

    private final ListCardPreviewsUseCase listCardPreviewsUseCase;
    private final ColorUtil colorUtil;
    private final ViewModel viewModel;

    @AssistedInject
    public BoardTableFeature(
            Inflater inflater,
            ListCardPreviewsUseCase listCardPreviewsUseCase,
            ColorUtil colorUtil,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.listCardPreviewsUseCase = listCardPreviewsUseCase;
        this.colorUtil = colorUtil;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        BoardTableFeature create(ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        this.progress.managedProperty().bind(this.progress.visibleProperty());
        this.emptyContentView.managedProperty().bind(this.emptyContentView.visibleProperty());
        this.table.managedProperty().bind(this.table.visibleProperty());

        setupTable(resources);

        final var disposable = viewModel.getBoardId()
                .observeOn(JavaFxScheduler.platform())
                .doOnNext(_ -> {
                    this.progress.setVisible(true);
                    this.emptyContentView.setVisible(false);
                    this.table.setVisible(false);
                })
                .observeOn(Schedulers.virtual())
                .switchMap(boardId -> Flowable.fromPublisher(listCardPreviewsUseCase.execute(boardId)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cards -> {
                    logger.info("Updating table with " + cards.size() + " cards");
                    this.updateTable(cards);
                }, throwable -> logger.severe("Error updating table: " + throwable.getMessage()));

        addDisposable(disposable);
    }

    private void setupTable(ResourceBundle resources) {
        final TableColumn<PreviewCard, String> idColumn = new TableColumn<>("#");
        idColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().remoteId() != null ? "#" + data.getValue().remoteId().value() : ""));
        idColumn.setPrefWidth(50);

        final TableColumn<PreviewCard, String> titleColumn = new TableColumn<>(resources.getString("editcard.placeholder.title"));
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().title()));
        titleColumn.setPrefWidth(200);

        final TableColumn<PreviewCard, List<PreviewCard.LabelPreview>> labelsColumn = new TableColumn<>(resources.getString("filter.tab.labels"));
        labelsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(new ArrayList<>(data.getValue().labels())));
        labelsColumn.setCellFactory(_ -> new TableCell<>() {
            private final FlowPane flowPane = new FlowPane(5, 5);

            @Override
            protected void updateItem(List<PreviewCard.LabelPreview> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    flowPane.getChildren().clear();
                    for (final var label : item) {
                        final var labelView = new LabelView(colorUtil);
                        labelView.setLabel(label.title(), label.color());
                        flowPane.getChildren().add(labelView);
                    }
                    setGraphic(flowPane);
                }
            }
        });
        labelsColumn.setPrefWidth(200);

        final TableColumn<PreviewCard, List<User.ID>> assigneesColumn = new TableColumn<>(resources.getString("editcard.label.assignees"));
        assigneesColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(new ArrayList<>(data.getValue().assignees())));
        assigneesColumn.setCellFactory(_ -> new TableCell<>() {
            private final MultiAvatarView multiAvatarView = new MultiAvatarView();

            {
                multiAvatarView.setAvatarSize(20);
            }

            @Override
            protected void updateItem(List<User.ID> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    multiAvatarView.bind(item);
                    setGraphic(multiAvatarView);
                }
            }
        });
        assigneesColumn.setPrefWidth(100);

        final TableColumn<PreviewCard, Integer> commentsColumn = new TableColumn<>(resources.getString("editcard.tab.comments"));
        commentsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().commentCount()));
        commentsColumn.setCellFactory(_ -> new TableCell<>() {
            private final IconCounterView counterView = new IconCounterView();

            {
                counterView.setIconLiteral("fltral-comment-16");
                counterView.initialize();
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0) {
                    setGraphic(null);
                } else {
                    counterView.setCounter(item);
                    setGraphic(counterView);
                }
            }
        });
        commentsColumn.setPrefWidth(100);

        final TableColumn<PreviewCard, Integer> attachmentsColumn = new TableColumn<>(resources.getString("editcard.tab.attachments"));
        attachmentsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().attachmentCount()));
        attachmentsColumn.setCellFactory(_ -> new TableCell<>() {
            private final IconCounterView counterView = new IconCounterView();

            {
                counterView.setIconLiteral("fltfal-attach-16");
                counterView.initialize();
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item == 0) {
                    setGraphic(null);
                } else {
                    counterView.setCounter(item);
                    setGraphic(counterView);
                }
            }
        });
        attachmentsColumn.setPrefWidth(100);

        final DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

        final TableColumn<PreviewCard, String> startDateColumn = new TableColumn<>(resources.getString("editcard.label.start-date"));
        startDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().startDate() != null ? data.getValue().startDate().format(formatter) : ""));
        startDateColumn.setPrefWidth(100);

        final TableColumn<PreviewCard, String> dueDateColumn = new TableColumn<>(resources.getString("editcard.label.due-date"));
        dueDateColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().dueDate() != null ? data.getValue().dueDate().format(formatter) : ""));
        dueDateColumn.setPrefWidth(100);

        table.getColumns().setAll(List.of(idColumn, titleColumn, labelsColumn, assigneesColumn, commentsColumn, attachmentsColumn, startDateColumn, dueDateColumn));

        table.getSelectionModel().selectedItemProperty().addListener((_, _, newValue) -> {
            if (newValue != null) {
                viewModel.onOpenCard(newValue.id());
            }
        });
    }

    private void updateTable(List<PreviewCard> cards) {
        this.progress.setVisible(false);

        if (cards.isEmpty()) {
            this.emptyContentView.setVisible(true);
            this.table.setVisible(false);
        } else {
            this.emptyContentView.setVisible(false);
            this.table.setVisible(true);
            this.table.getItems().setAll(cards);
        }
    }

    public interface ViewModel extends ColumnFeature.ViewModel {
        Flowable<Board.ID> getBoardId();
    }
}
