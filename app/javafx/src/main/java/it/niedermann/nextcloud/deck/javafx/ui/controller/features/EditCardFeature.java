package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import com.dlsc.gemsfx.CalendarPicker;
import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.dlsc.gemsfx.TimePicker;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Maybe;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Comment;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListActivityUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.AddAttachmentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListCommentsUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.ActivityCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.AttachmentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CommentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.SubmitTextField;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.LabelSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.UserSearchViewConverter;
import it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.LabelSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories.LabelTagViewFactory;
import it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories.UserTagViewFactory;
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
import one.jpro.platform.mdfx.MarkdownView;

public class EditCardFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(EditCardFeature.class.getName());

    private final GetCardUseCase getCardUseCase;

    private final AddAttachmentUseCase addAttachmentUseCase;
    private final ListAttachmentsUseCase listAttachmentsUseCase;

    private final AddCommentUseCase addCommentUseCase;
    private final ListCommentsUseCase listCommentsUseCase;
    private final CommentCellFactory commentCellFactory;

    private final ListActivityUseCase listActivityUseCase;

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
    Button closeSidebar;
    @FXML
    ListView<Comment> comments;
    @FXML
    SubmitTextField addComment;
    @FXML
    ListView<Activity> activities;
    @FXML
    ListView<Attachment> attachments;

    private final Flow.Publisher<Board.Permissions> permissions;

    @AssistedInject
    public EditCardFeature(
            GetCardUseCase getCardUseCase,
            ListAttachmentsUseCase listAttachmentsUseCase,
            AddAttachmentUseCase addAttachmentUseCase,
            ListCommentsUseCase listCommentsUseCase,
            AddCommentUseCase addCommentUseCase,
            CommentCellFactory commentCellFactory,
            ListActivityUseCase listActivityUseCase,
            LabelSuggestionProvider labelSuggestionProvider,
            UserSuggestionProvider userSuggestionProvider,
            LabelSearchViewConverter labelSearchViewConverter,
            LabelTagViewFactory labelTagViewFactory,
            UserSearchViewConverter userSearchViewConverter,
            UserTagViewFactory userTagViewFactory,
            @Assisted ViewModel viewModel
    ) {
        this.getCardUseCase = getCardUseCase;
        this.listAttachmentsUseCase = listAttachmentsUseCase;
        this.addAttachmentUseCase = addAttachmentUseCase;
        this.listCommentsUseCase = listCommentsUseCase;
        this.addCommentUseCase = addCommentUseCase;
        this.commentCellFactory = commentCellFactory;
        this.listActivityUseCase = listActivityUseCase;
        this.labelSuggestionProvider = labelSuggestionProvider;
        this.userSuggestionProvider = userSuggestionProvider;
        this.labelSearchViewConverter = labelSearchViewConverter;
        this.labelTagViewFactory = labelTagViewFactory;
        this.userSearchViewConverter = userSearchViewConverter;
        this.userTagViewFactory = userTagViewFactory;
        this.viewModel = viewModel;

        this.permissions = Flowable.fromPublisher(viewModel.getPermissions());
    }

    @AssistedFactory
    public interface Factory {
        EditCardFeature create(ViewModel viewModel);
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var editModeEnabled = descriptionEditModeToggleButton.selectedProperty();
        final var previewModeEnabled = editModeEnabled.map(enabled -> !enabled);

        previewModeEnabled.subscribe(descriptionEditor::setVisible);
        previewModeEnabled.subscribe(descriptionEditor::setManaged);
        editModeEnabled.subscribe(descriptionPreview::setVisible);
        editModeEnabled.subscribe(descriptionPreview::setManaged);

        descriptionPreview.mdStringProperty().bind(descriptionEditor.textProperty());


        comments.setCellFactory(commentCellFactory);
        activities.setCellFactory(new ActivityCellFactory());
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

        final var cardId = Flowable.fromPublisher(viewModel.getCardId());

        final var cardDisposable = cardId.switchMap(getCardUseCase::execute)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(card -> {
                    title.setText(card.title());
                    createdAt.setText(String.format("Created at %1$s by %2$s",
                            card.createdAt().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)),
                            "John Doe"
                    ));
                    editedAt.setText(String.format("Last edited at %1$s by %2$s",
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

        final var attachmentsDisposable = cardId.switchMap(listAttachmentsUseCase::execute)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(attachments -> this.attachments.getItems().setAll(attachments));

        addDisposable(attachmentsDisposable);

        final var commentsDisposable = cardId.switchMap(listCommentsUseCase::execute)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(comments -> this.comments.getItems().setAll(comments));

        addDisposable(commentsDisposable);

        final var activitiesDisposable = cardId.switchMap(listActivityUseCase::execute)
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(activities -> this.activities.getItems().setAll(activities));

        addDisposable(activitiesDisposable);

        closeSidebar.setOnMouseClicked(event -> {
            viewModel.onCloseSidebar();
            event.consume();
        });

        addComment.setOnSubmit(content -> {

            addComment.setDisable(true);

            Maybe.fromPublisher(cardId)
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenComposeAsync(id -> addCommentUseCase.execute(new CreateComment(id, content)))
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
        CompletableFuture<Void> onCardSaved(Card card);

        void onCloseSidebar();

        Flow.Publisher<Card.ID> getCardId();

        Flow.Publisher<Board.Permissions> getPermissions();
    }
}
