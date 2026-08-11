package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import com.dlsc.gemsfx.PopOver;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.processors.BehaviorProcessor;
import io.reactivex.rxjava4.processors.FlowableProcessor;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CardPreviewCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.SubmitTextField;
import it.niedermann.nextcloud.deck.javafx.util.DeckDataFormat;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.PopupWindow;

public class ColumnFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(ColumnFeature.class.getName());

    private final ListCardPreviewsUseCase listCardPreviewsUseCase;
    private final MoveCardUseCase moveCardUseCase;
    private final AddCardUseCase addCardUseCase;
    private final GetColumnUseCase getColumnUseCase;
    private final Column.ID columnId;
    private final ViewModel viewModel;

    private Double lastDragSceneY;
    private Integer placeholderIndex;
    private FlowableProcessor<Integer> draggingCardIndex;

    private final CardPreviewCellFactory cardPreviewCellFactory;
    private PopOver addCardPopOver;

    private boolean shouldRequestInitialFocus = false;

    @FXML
    Label title;
    @FXML
    ListView<PreviewCard> cards;
    @FXML
    Button addCard;
    @FXML
    SubmitTextField addCardSubmitTextField;

    @AssistedInject
    public ColumnFeature(
            ListCardPreviewsUseCase listCardPreviewsUseCase,
            MoveCardUseCase moveCardUseCase,
            CardPreviewCellFactory cardPreviewCellFactory,
            AddCardUseCase addCardUseCase,
            GetColumnUseCase getColumnUseCase,
            @Assisted Column.ID columnId,
            @Assisted ViewModel viewModel
    ) {
        this.listCardPreviewsUseCase = listCardPreviewsUseCase;
        this.moveCardUseCase = moveCardUseCase;
        this.cardPreviewCellFactory = cardPreviewCellFactory;
        this.addCardUseCase = addCardUseCase;
        this.getColumnUseCase = getColumnUseCase;
        this.columnId = columnId;
        this.viewModel = viewModel;
    }

    @AssistedFactory
    public interface Factory {
        ColumnFeature create(Column.ID columnId, ViewModel viewModel);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        cards.setCellFactory(cardPreviewCellFactory);
        cards.setOnDragEntered(this::onDragCardEntered);
        cards.setOnDragExited(this::onDragCardExited);
        cards.setOnDragOver(this::onDragCardOver);
        cards.setOnDragDropped(this::onCardDropped);

        cards.setOnKeyPressed(event -> {
            final var selectedItem = cards.getSelectionModel().getSelectedItem();

            if (selectedItem == null) {
                return;
            }

            switch (event.getCode()) {
                case ENTER, SPACE -> {
                    viewModel.onOpenCard(selectedItem.id());
                    event.consume();
                }
                case DELETE -> {
                    viewModel.onDeleteCard(selectedItem.id());
                    event.consume();
                }
                default -> {
                    // Ignored
                }
            }
        });

        final var disposable = Flowable.fromPublisher(listCardPreviewsUseCase.execute(columnId))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cards -> {
                    this.cards.getItems().setAll(cards);
                    if (shouldRequestInitialFocus && !cards.isEmpty()) {
                        this.cards.requestFocus();
                        this.cards.getFocusModel().focus(0);
                        shouldRequestInitialFocus = false;
                    }
                });

        addDisposable(disposable);

        final var d2 = Flowable.fromPublisher(getColumnUseCase.execute(columnId))
                .subscribe(column -> {
                    this.title.setText(column.title());
                    // Order of setting listener and columnId matters because columnId Flowable triggers rebinding the listener to the cards
                    this.cardPreviewCellFactory.setCardPreviewActionListener(viewModel);
                });

        addDisposable(d2);

        addCard.setOnAction(event -> {

            addCardPopOver = new PopOver(addCardSubmitTextField);
            addCardPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_RIGHT);
            addCardPopOver.setAnchorLocation(PopupWindow.AnchorLocation.CONTENT_TOP_RIGHT);
            addCardPopOver.show(addCard);

            addCardSubmitTextField.requestFocus();

            event.consume();
        });

        addCardSubmitTextField.setOnSubmit(cardTitle -> {

            addCardPopOver.hide();
            addCardSubmitTextField.setDisable(true);

            addCardUseCase.execute(new CreateCard(columnId, cardTitle))
                    .whenCompleteAsync((_, exception) -> {
                        if (exception == null) {
                            addCardSubmitTextField.setContent(null);
                        } else {
                            throw new RuntimeException(exception);
                        }

                        addCardSubmitTextField.setDisable(false);
                    }, Platform::runLater);
        });
    }

    private void onDragCardEntered(DragEvent event) {
        if (this.draggingCardIndex != null) {
            throw new IllegalStateException("Expected draggingCardIndex to be null onDragCardEntered");
        }

        this.draggingCardIndex = BehaviorProcessor.create();
        this.placeholderIndex = null;
        this.lastDragSceneY = null;
    }

    private void onDragCardExited(DragEvent event) {
        if (this.draggingCardIndex == null) {
            throw new IllegalStateException("Expected draggingCardIndex to be not null onDragCardEntered");
        }

        removePlaceholder();
        this.draggingCardIndex = null;
        this.lastDragSceneY = null;
    }

    private void onDragCardOver(DragEvent event) {
        if (!TransferMode.MOVE.equals(event.getTransferMode())) {
            return;
        }

        final var dragboard = event.getDragboard();
        if (!dragboard.getContentTypes().contains(DeckDataFormat.CARD_ID_PRIMITIVE)) {
            return;
        }

        final double currentSceneY = event.getSceneY();
        if (lastDragSceneY != null && Math.abs(currentSceneY - lastDragSceneY) < 15) {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
            return;
        }

        final var targetIndex = getDropTargetOrderOfListView(event);
        updatePlaceholder(targetIndex);
        this.lastDragSceneY = currentSceneY;

        logger.finest("Dragging over index " + targetIndex + ", targetIndex: " + targetIndex);

        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    }

    public void onCardDropped(DragEvent event) {
        if (!TransferMode.MOVE.equals(event.getTransferMode())) {
            return;
        }

        final var dragboard = event.getDragboard();
        if (!dragboard.getContentTypes().contains(DeckDataFormat.CARD_ID_PRIMITIVE)) {
            return;
        }

        final int rawTargetOrder = getDropTargetOrderOfListView(event);
        final int targetOrder;
        if (placeholderIndex != null && rawTargetOrder > placeholderIndex) {
            targetOrder = rawTargetOrder - 1;
        } else {
            targetOrder = rawTargetOrder;
        }

        final var cardId = new Card.ID((long) dragboard.getContent(DeckDataFormat.CARD_ID_PRIMITIVE));

        removePlaceholder();

        logger.info("Dropped: " + cardId + " at " + targetOrder);

        moveCardUseCase.execute(cardId, columnId, targetOrder)
                .whenCompleteAsync((_, exception) -> {
                    if (exception != null) {
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                    }
                });

        event.consume();
    }

    private void updatePlaceholder(int targetIndex) {
        final var items = cards.getItems();
        final int adjustedIndex;
        if (placeholderIndex != null && placeholderIndex < targetIndex) {
            adjustedIndex = targetIndex - 1;
        } else {
            adjustedIndex = targetIndex;
        }

        if (placeholderIndex != null && placeholderIndex == adjustedIndex) {
            return;
        }

        removePlaceholder();

        final int safeIndex = Math.min(adjustedIndex, items.size());
        items.add(safeIndex, null);
        placeholderIndex = safeIndex;
    }

    private void removePlaceholder() {
        cards.getItems().removeIf(Objects::isNull);
        placeholderIndex = null;
    }

    private int getDropTargetOrderOfListView(DragEvent event) {
        final var intersectedNode = event.getPickResult().getIntersectedNode();
        final var intersectedListCellOrListView = FxUtils.findListCellOrListViewParent(intersectedNode)
                .orElseThrow(() -> new IllegalStateException("intersectedNode " + intersectedNode + " is not a child of the ListView"));

        return FxUtils.identifyClosestListViewIndex(intersectedListCellOrListView, event.getSceneY());
    }

    public void setShouldRequestInitialFocus(boolean shouldRequestInitialFocus) {
        this.shouldRequestInitialFocus = shouldRequestInitialFocus;
    }

    public interface ViewModel extends CardPreviewView.CardPreviewActionListener {
    }
}
