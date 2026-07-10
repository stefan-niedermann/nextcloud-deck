package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import com.dlsc.gemsfx.CalendarPicker;
import com.dlsc.gemsfx.SearchField;
import com.dlsc.gemsfx.TagsField;
import com.dlsc.gemsfx.TimePicker;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava4.processors.BehaviorProcessor;
import io.reactivex.rxjava4.processors.FlowableProcessor;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Attachment;
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
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.ActivityCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.AttachmentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CommentCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.SubmitTextField;
import it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.LabelSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.UserSuggestionProvider;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.util.StringConverter;
import one.jpro.platform.mdfx.MarkdownView;

public class EditCardFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(EditCardFeature.class.getName());

    private final StageContext context;
    private final GetCardUseCase getCardUseCase;
    private final AddAttachmentUseCase addAttachmentUseCase;
    private final ListAttachmentsUseCase listAttachmentsUseCase;
    private final AddCommentUseCase addCommentUseCase;
    private final ListCommentsUseCase listCommentsUseCase;
    private final ListActivityUseCase listActivityUseCase;
    private final LabelSuggestionProvider labelSuggestionProvider;
    private final UserSuggestionProvider userSuggestionProvider;

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
    SearchField dependentCards;
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

    private final FlowableProcessor<Card.ID> cardId = BehaviorProcessor.create();

    private EditCardListener editCardListener;

    @Inject
    public EditCardFeature(
            StageContext context,
            GetCardUseCase getCardUseCase,
            ListAttachmentsUseCase listAttachmentsUseCase,
            AddAttachmentUseCase addAttachmentUseCase,
            ListCommentsUseCase listCommentsUseCase,
            AddCommentUseCase addCommentUseCase,
            ListActivityUseCase listActivityUseCase,
            LabelSuggestionProvider labelSuggestionProvider,
            UserSuggestionProvider userSuggestionProvider
    ) {
        this.context = context;
        this.getCardUseCase = getCardUseCase;
        this.listAttachmentsUseCase = listAttachmentsUseCase;
        this.addAttachmentUseCase = addAttachmentUseCase;
        this.listCommentsUseCase = listCommentsUseCase;
        this.addCommentUseCase = addCommentUseCase;
        this.listActivityUseCase = listActivityUseCase;
        this.labelSuggestionProvider = labelSuggestionProvider;
        this.userSuggestionProvider = userSuggestionProvider;
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

        comments.setCellFactory(new CommentCellFactory());
        activities.setCellFactory(new ActivityCellFactory());
        attachments.setCellFactory(new AttachmentCellFactory());

        labels.setSuggestionProvider(labelSuggestionProvider);
        labels.setConverter(new StringConverter<>() {
            @Override
            public String toString(it.niedermann.nextcloud.deck.domain.model.Label object) {
                return object.title();
            }

            @Override
            public it.niedermann.nextcloud.deck.domain.model.Label fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

        assignees.setSuggestionProvider(userSuggestionProvider);
        assignees.setConverter(new StringConverter<>() {
            @Override
            public String toString(User user) {
                return user.displayName();
            }

            @Override
            public User fromString(String string) {
                throw new UnsupportedOperationException();
            }
        });

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
                        editCardListener.onCardSaved(card);
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
            context.dispatch(new StageContext.Action.CloseCardAction());
            event.consume();
        });

        addComment.setOnSubmit(content -> {

            addComment.setDisable(true);

            addCommentUseCase.execute(new CreateComment(cardId.blockingFirst(), content))
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

//        attachments.setOnDragEntered(this::onDragCardEntered);
//        attachments.setOnDragExited(this::onDragCardExited);
        attachments.setOnDragOver(this::onDragCardOver);
        attachments.setOnDragDropped(this::onCardDropped);
    }

    public void setCardId(Card.ID id) {
        cardId.onNext(id);
    }

    public void setEditCardListener(EditCardListener editCardListener) {
        this.editCardListener = editCardListener;
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
        if (!TransferMode.COPY.equals(event.getTransferMode())) {
            return;
        }

        final var dragboard = event.getDragboard();
        if (!dragboard.getContentTypes().contains(DataFormat.FILES)) {
            return;
        }

        final var content = dragboard.getContent(DataFormat.FILES);

        if (content instanceof List<?> list) {
            final var paths = new ArrayList<Path>(list.size());
            for (final var item : list) {
                if (item instanceof File file) {
                    paths.add(file.toPath());
                } else {
                    throw new IllegalArgumentException("Expected all items to be of type " + File.class.getName() + " but got " + item.getClass().getName());
                }
            }

            addAttachmentUseCase.execute(cardId.blockingFirst(), paths);
        }

        throw new IllegalArgumentException("Expected List<File> but got: " + content.getClass().getName());
    }

    public interface EditCardListener {
        CompletableFuture<Void> onCardSaved(Card card);
    }
}
