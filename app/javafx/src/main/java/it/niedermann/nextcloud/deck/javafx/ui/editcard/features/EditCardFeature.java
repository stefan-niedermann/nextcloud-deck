package it.niedermann.nextcloud.deck.javafx.ui.editcard.features;

import com.dlsc.gemsfx.CalendarPicker;
import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.dlsc.gemsfx.TimePicker;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractFeature;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.ActivityCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.AttachmentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.cellfactories.CommentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.LabelSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.searchviewconverter.UserSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.LabelSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.shared.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.shared.tagviewfactories.LabelTagViewFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.tagviewfactories.UserTagViewFactory;
import it.niedermann.nextcloud.deck.javafx.ui.shared.views.SubmitTextField;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.application.Platform;
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

    private final UserSuggestionProvider userSuggestionProvider;
    private final UserSearchViewConverter userSearchViewConverter;
    private final UserTagViewFactory userTagViewFactory;

    private final LabelSuggestionProvider labelSuggestionProvider;
    private final LabelSearchViewConverter labelSearchViewConverter;
    private final LabelTagViewFactory labelTagViewFactory;

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

    private final Flowable<Board.Permissions> permissions;

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
        this.viewModel = viewModel;

        this.permissions = viewModel.getPermissions();
    }

    @AssistedFactory
    public interface Factory {
        EditCardFeature create(ViewModel viewModel);
    }

    @FXML
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

        final var permissionsDisposable = Flowable.fromPublisher(permissions).subscribe(p -> {
            final var editableFields = new Node[]{
                    title, labels, assignees, startDateDate, startDateTime, dueDateDate, dueDateTime,
                    dependentCards, descriptionEditor, descriptionPreview, saveBtn, addComment,
            };

            for (final var node : editableFields) {
                node.setDisable(!p.permissionEdit());
            }
        });

        addDisposable(permissionsDisposable);

        final var cardDisposable = viewModel.getCard()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(card -> {
                    title.setText(card.title());
                    createdAt.setText(java.text.MessageFormat.format(resources.getString("editcard.label.created-at"),
                            card.createdAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                            "John Doe"
                    ));
                    editedAt.setText(java.text.MessageFormat.format(resources.getString("editcard.label.last-edited"),
                            card.createdAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                            "John Doe"
                    ));
                    descriptionEditor.setText(card.description());

                    saveBtn.setOnMouseClicked(event -> {
                        viewModel.onCardSaved(card);
                        event.consume();
                    });
                });

        addDisposable(cardDisposable);

        final var attachmentsDisposable = viewModel.getAttachments()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(attachments -> this.attachments.getItems().setAll(attachments));

        addDisposable(attachmentsDisposable);

        final var commentsDisposable = viewModel.getComments()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(comments -> this.comments.getItems().setAll(comments));

        addDisposable(commentsDisposable);

        final var activitiesDisposable = viewModel.getActivities()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(activities -> this.activities.getItems().setAll(activities));

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

        Flowable<List<Attachment>> getAttachments();

        Flowable<List<PreviewComment>> getComments();

        Flowable<List<PreviewActivity>> getActivities();

        CompletableFuture<Void> onCardSaved(Card card);

        CompletableFuture<Void> onAddComment(String content);

        void onCloseSidebar();

        Disposable onPopOut();

        Flowable<Boolean> isStandalone();

        Flowable<Card.ID> getCardId();

        Flowable<Board.Permissions> getPermissions();
    }
}
