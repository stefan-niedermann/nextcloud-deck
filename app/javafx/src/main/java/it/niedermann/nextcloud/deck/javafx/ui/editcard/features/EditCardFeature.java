package it.niedermann.nextcloud.deck.javafx.ui.editcard.features;

import com.dlsc.gemsfx.CalendarPicker;
import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.dlsc.gemsfx.TimePicker;

import java.net.URL;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.ActivityCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.AttachmentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.CommentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.DependentCardCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.CardSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.LabelSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.UserSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.CardSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.LabelSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.shared.tagviewfactories.LabelTagViewFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.tagviewfactories.UserTagViewFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.DependentCardView;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.SubmitTextField;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import one.jpro.platform.mdfx.MarkdownView;

public class EditCardFeature extends AbstractFeature {

    private static final Logger logger = Logger.getLogger(EditCardFeature.class.getName());

    private final CommentCellFactory commentCellFactory;
    private final ActivityCellFactory activityCellFactory = new ActivityCellFactory();
    private final DependentCardCellFactory dependentCardCellFactory;

    private final UserSuggestionProvider userSuggestionProvider;
    private final UserSearchViewConverter userSearchViewConverter;
    private final UserTagViewFactory userTagViewFactory;

    private final LabelSuggestionProvider labelSuggestionProvider;
    private final LabelSearchViewConverter labelSearchViewConverter;
    private final LabelTagViewFactory labelTagViewFactory;
    private final CardSuggestionProvider cardSuggestionProvider;
    private final CardSearchViewConverter cardSearchViewConverter;
    private final UpdateCardUseCase updateCardUseCase;
    private final GetCardUseCase getCardUseCase;

    private final ViewModel viewModel;

    @FXML
    TextField title;
    @FXML
    BorderPane detailsPane;
    @FXML
    VBox metadataContainer;
    @FXML
    Label createdAt;
    @FXML
    Label editedAt;
    @FXML
    TagsField<it.niedermann.nextcloud.deck.domain.model.Label> labels;
    @FXML
    TagsField<User> assignees;
    @FXML
    CalendarPicker startDateDate;
    @FXML
    TimePicker startDateTime;
    @FXML
    CalendarPicker dueDateDate;
    @FXML
    TimePicker dueDateTime;
    @FXML
    SearchField<Card> dependentCards;
    @FXML
    TextArea descriptionEditor;
    @FXML
    MarkdownView descriptionPreview;
    @FXML
    ToggleButton descriptionEditModeToggleButton;
    @FXML
    Button cancelBtn;
    @FXML
    Button saveBtn;
    @FXML
    Button popOutBtn;
    @FXML
    Button closeSidebar;
    @FXML
    ListView<PreviewComment> comments;
    @FXML
    SubmitTextField addComment;
    @FXML
    ListView<PreviewActivity> activities;
    @FXML
    ListView<Attachment> attachments;
    @FXML
    ListView<Card> dependentCardsList;

    private final List<it.niedermann.nextcloud.deck.domain.model.Label> allBoardLabels = new ArrayList<>();
    private final List<User> allBoardUsers = new ArrayList<>();
    private Card card;
    private final Flowable<Board.Permissions> permissions;
    private final ObservableList<Card> localDependentCards = FXCollections.observableArrayList();

    private <T> void updateTags(TagsField<T> tagsField, List<T> newTags) {
        if (!newTags.equals(tagsField.getTags())) {
            tagsField.getTags().setAll(newTags);
        }
    }

    @AssistedInject
    public EditCardFeature(
            Inflater inflater,
            CommentCellFactory commentCellFactory,
            LabelSuggestionProvider labelSuggestionProvider,
            UserSuggestionProvider userSuggestionProvider,
            LabelSearchViewConverter labelSearchViewConverter,
            LabelTagViewFactory labelTagViewFactory,
            UserSearchViewConverter userSearchViewConverter,
            UserTagViewFactory userTagViewFactory,
            CardSuggestionProvider cardSuggestionProvider,
            CardSearchViewConverter cardSearchViewConverter,
            DependentCardCellFactory dependentCardCellFactory,
            UpdateCardUseCase updateCardUseCase,
            GetCardUseCase getCardUseCase,
            @Assisted ViewModel viewModel
    ) {
        super(inflater);

        this.commentCellFactory = commentCellFactory;
        this.labelSuggestionProvider = labelSuggestionProvider;
        this.userSuggestionProvider = userSuggestionProvider;
        this.labelSearchViewConverter = labelSearchViewConverter;
        this.labelTagViewFactory = labelTagViewFactory;
        this.userSearchViewConverter = userSearchViewConverter;
        this.userTagViewFactory = userTagViewFactory;
        this.cardSuggestionProvider = cardSuggestionProvider;
        this.cardSearchViewConverter = cardSearchViewConverter;
        this.dependentCardCellFactory = dependentCardCellFactory;
        this.updateCardUseCase = updateCardUseCase;
        this.getCardUseCase = getCardUseCase;
        this.viewModel = viewModel;

        this.permissions = viewModel.getPermissions();
    }

    @AssistedFactory
    public interface Factory {
        EditCardFeature create(ViewModel viewModel);
    }

    @FXML
    private OffsetDateTime getOffsetDateTime(CalendarPicker datePicker, TimePicker timePicker) {
        final var date = datePicker.getValue();
        if (date == null) {
            return null;
        }
        final var time = timePicker.getTime() != null ? timePicker.getTime() : LocalTime.MIDNIGHT;
        return OffsetDateTime.of(date, time, OffsetDateTime.now().getOffset());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        detailsPane.widthProperty().addListener((_, _, newValue) -> {
            if (newValue.doubleValue() > 600) {
                if (detailsPane.getTop() != null) {
                    detailsPane.setTop(null);
                    detailsPane.setRight(metadataContainer);
                    metadataContainer.setPrefWidth(300);
                }
            } else {
                if (detailsPane.getRight() != null) {
                    detailsPane.setRight(null);
                    detailsPane.setTop(metadataContainer);
                    metadataContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
                }
            }
        });

        final var editModeEnabled = descriptionEditModeToggleButton.selectedProperty();
        final var previewModeEnabled = editModeEnabled.map(enabled -> !enabled);

        previewModeEnabled.subscribe(descriptionEditor::setVisible);
        previewModeEnabled.subscribe(descriptionEditor::setManaged);
        editModeEnabled.subscribe(descriptionPreview::setVisible);
        editModeEnabled.subscribe(descriptionPreview::setManaged);

        descriptionPreview.mdStringProperty().bind(descriptionEditor.textProperty());


        comments.setCellFactory(commentCellFactory);
        activities.setCellFactory(activityCellFactory);
        attachments.setCellFactory(new AttachmentCellFactory());

        labels.setSuggestionProvider(labelSuggestionProvider);
        labels.setTagViewFactory(labelTagViewFactory);
        labels.setConverter(labelSearchViewConverter);

        assignees.setSuggestionProvider(userSuggestionProvider);
        assignees.setTagViewFactory(userTagViewFactory);
        assignees.setConverter(userSearchViewConverter);

        dependentCards.setSuggestionProvider(cardSuggestionProvider);
        dependentCards.setConverter(cardSearchViewConverter);
        dependentCards.setAutoCommitOnFocusLost(false);

        dependentCardCellFactory.setListener(new DependentCardView.DependentCardActionListener() {
            @Override
            public void onMarkAsDone(Card.ID cardId) {
                updateCardUseCase.markAsDone(cardId);
            }

            @Override
            public void onMarkAsUndone(Card.ID cardId) {
                updateCardUseCase.markAsUndone(cardId);
            }

            @Override
            public void onRemoveDependent(Card.ID cardId) {
                if (card != null) {
                    final var newDependents = new ArrayList<>(card.dependents());
                    newDependents.remove(cardId);
                    card = card.withDependents(newDependents);
                    localDependentCards.removeIf(c -> c.id().equals(cardId));
                }
            }
        });
        dependentCardsList.setCellFactory(dependentCardCellFactory);
        dependentCardsList.setItems(localDependentCards);

        dependentCards.setOnCommit(newValue -> {
            if (newValue != null && card != null) {
                final var newDependents = new ArrayList<>(card.dependents());
                if (!newDependents.contains(newValue.id())) {
                    newDependents.add(newValue.id());
                    card = card.withDependents(newDependents);
                    if (localDependentCards.stream().noneMatch(c -> c.id().equals(newValue.id()))) {
                        localDependentCards.add(newValue);
                    }
                }
                Platform.runLater(() -> {
                    dependentCards.setSelectedItem(null);
                    dependentCards.getEditor().clear();
                });
            }
        });

        final var permissionsDisposable = Flowable.fromPublisher(permissions)
                .distinctUntilChanged()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(p -> {
                    final var editableFields = new Node[]{
                            title, labels, assignees, startDateDate, startDateTime, dueDateDate, dueDateTime,
                            dependentCards, descriptionEditor, descriptionPreview, saveBtn, addComment,
                    };

                    for (final var node : editableFields) {
                        if (node.isDisable() != (!p.permissionEdit())) {
                            node.setDisable(!p.permissionEdit());
                        }
                    }
                });

        addDisposable(permissionsDisposable);

        addDisposable(viewModel.getBoard().observeOn(JavaFxScheduler.platform()).subscribe(board -> {
            labelSuggestionProvider.setBoardId(board.id());
            cardSuggestionProvider.setBoardId(board.id());
        }));

        addDisposable(viewModel.getBoardLabels().observeOn(JavaFxScheduler.platform()).subscribe(allLabels -> {
            this.allBoardLabels.clear();
            this.allBoardLabels.addAll(allLabels);
            if (this.card != null) {
                labels.getTags().setAll(this.card.labels().stream()
                        .map(id -> allBoardLabels.stream().filter(l -> l.id().value() == id.value()).findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
            }
        }));
        addDisposable(viewModel.getBoardUsers().observeOn(JavaFxScheduler.platform()).subscribe(allUsers -> {
            this.allBoardUsers.clear();
            this.allBoardUsers.addAll(allUsers);
            if (this.card != null) {
                assignees.getTags().setAll(this.card.assignees().stream()
                        .map(id -> allBoardUsers.stream().filter(u -> u.id().equals(id)).findFirst())
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
            }
        }));

        final var cardDisposable = viewModel.getCard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(card -> {
                    this.card = card;
                    cardSuggestionProvider.setExcludeId(card.id());
                    if (!Objects.equals(title.getText(), card.title())) {
                        title.setText(card.title());
                    }
                    final var createdAtText = MessageFormat.format(resources.getString("editcard.label.created-at"),
                            card.createdAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                            "John Doe"
                    );
                    if (!Objects.equals(createdAt.getText(), createdAtText)) {
                        createdAt.setText(createdAtText);
                    }
                    final var editedAtText = MessageFormat.format(resources.getString("editcard.label.last-edited"),
                            card.createdAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                            "John Doe"
                    );
                    if (!Objects.equals(editedAt.getText(), editedAtText)) {
                        editedAt.setText(editedAtText);
                    }
                    if (!Objects.equals(descriptionEditor.getText(), card.description())) {
                        descriptionEditor.setText(card.description());
                    }

                    updateTags(labels, card.labels().stream()
                            .map(id -> allBoardLabels.stream().filter(l -> l.id().value() == id.value()).findFirst())
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList()));

                    updateTags(assignees, card.assignees().stream()
                            .map(id -> allBoardUsers.stream().filter(u -> u.id().equals(id)).findFirst())
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList()));

                    if (card.startDate() != null) {
                        if (!Objects.equals(startDateDate.getValue(), card.startDate().toLocalDate())) {
                            startDateDate.setValue(card.startDate().toLocalDate());
                        }
                        if (!Objects.equals(startDateTime.getTime(), card.startDate().toLocalTime())) {
                            startDateTime.setTime(card.startDate().toLocalTime());
                        }
                    } else {
                        if (startDateDate.getValue() != null) {
                            startDateDate.setValue(null);
                        }
                        if (startDateTime.getTime() != null) {
                            startDateTime.setTime(null);
                        }
                    }

                    if (card.dueDate() != null) {
                        if (!Objects.equals(dueDateDate.getValue(), card.dueDate().toLocalDate())) {
                            dueDateDate.setValue(card.dueDate().toLocalDate());
                        }
                        if (!Objects.equals(dueDateTime.getTime(), card.dueDate().toLocalTime())) {
                            dueDateTime.setTime(card.dueDate().toLocalTime());
                        }
                    } else {
                        if (dueDateDate.getValue() != null) {
                            dueDateDate.setValue(null);
                        }
                        if (dueDateTime.getTime() != null) {
                            dueDateTime.setTime(null);
                        }
                    }
                });

        final var dependentsDisposable = viewModel.getCard()
                .map(Card::dependents)
                .distinctUntilChanged()
                .observeOn(Schedulers.io())
                .switchMapSingle(ids ->
                        Flowable.fromIterable(ids)
                                .flatMapSingle(id -> Flowable.fromPublisher(getCardUseCase.execute(id)).firstOrError())
                                .toList()
                )
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> {
                    if (!list.equals(localDependentCards)) {
                        localDependentCards.setAll(list);
                    }
                });

        addDisposable(cardDisposable, dependentsDisposable);

        saveBtn.setOnAction(event -> {
            if (this.card != null) {
                final var updatedCard = this.card.with()
                        .title(this.title.getText())
                        .description(descriptionEditor.getText())
                        .labels(labels.getTags().stream().map(it.niedermann.nextcloud.deck.domain.model.Label::id).collect(Collectors.toSet()))
                        .assignees(assignees.getTags().stream().map(User::id).collect(Collectors.toSet()))
                        .startDate(getOffsetDateTime(startDateDate, startDateTime))
                        .dueDate(getOffsetDateTime(dueDateDate, dueDateTime))
                        .dependents(localDependentCards.stream().map(Card::id).collect(Collectors.toList()))
                        .build();
                viewModel.onCardSaved(updatedCard);
                event.consume();
            }
        });

        cancelBtn.setOnAction(event -> {
            viewModel.onCloseSidebar();
            event.consume();
        });

        final var attachmentsDisposable = viewModel.getAttachments()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> {
                    if (!list.equals(attachments.getItems())) {
                        attachments.getItems().setAll(list);
                    }
                });

        addDisposable(attachmentsDisposable);

        final var commentsDisposable = viewModel.getComments()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> {
                    if (!list.equals(comments.getItems())) {
                        comments.getItems().setAll(list);
                    }
                });

        addDisposable(commentsDisposable);

        final var activitiesDisposable = viewModel.getActivities()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(list -> {
                    if (!list.equals(activities.getItems())) {
                        activities.getItems().setAll(list);
                    }
                });

        addDisposable(activitiesDisposable);

        closeSidebar.setOnMouseClicked(event -> {
            viewModel.onCloseSidebar();
            event.consume();
        });

        final var isSidebarDisposable = viewModel.isStandalone()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(standalone -> {
                    final boolean visible = !standalone;
                    popOutBtn.setVisible(visible);
                    popOutBtn.setManaged(visible);
                    closeSidebar.setVisible(visible);
                    closeSidebar.setManaged(visible);
                });

        addDisposable(isSidebarDisposable);

        popOutBtn.setOnAction(_ -> {
            final var dispoable = viewModel.onPopOut();
            addDisposable(dispoable);
        });

        addComment.setOnSubmit(content -> {

            addComment.setDisable(true);

            viewModel.onAddComment(content)
                    .whenCompleteAsync((_, exception) -> {

                        if (exception == null) {
                            addComment.setContent(null);
                        } else {
                            throw new RuntimeException(exception);
                        }

                        addComment.setDisable(false);
                        addComment.requestFocus();

                    }, Platform::runLater);
        });

        attachments.setOnDragOver(this::onDragCardOver);
        attachments.setOnDragDropped(this::onCardDropped);

        // FIXME Disable drag and drop
    }

    private void onDragCardOver(DragEvent event) {
        final var dragboard = event.getDragboard();
        if (!dragboard.getContentTypes().contains(DataFormat.FILES)) {
            return;
        }

        event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }

    public void onCardDropped(DragEvent event) {

    }

    public interface ViewModel {
        Flowable<Card> getCard();

        Flowable<Board> getBoard();

        Flowable<List<Attachment>> getAttachments();

        Flowable<List<PreviewComment>> getComments();

        Flowable<List<PreviewActivity>> getActivities();

        Flowable<List<it.niedermann.nextcloud.deck.domain.model.Label>> getBoardLabels();

        Flowable<List<User>> getBoardUsers();

        CompletableFuture<Void> onCardSaved(Card card);

        CompletableFuture<Void> onAddComment(String content);

        void onCloseSidebar();

        Disposable onPopOut();

        Flowable<Boolean> isStandalone();

        Flowable<Card.ID> getCardId();

        Flowable<Board.Permissions> getPermissions();
    }
}
