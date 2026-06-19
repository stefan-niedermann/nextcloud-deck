package it.niedermann.nextcloud.deck.javafx.ui.controller.features;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.processors.BehaviorProcessor;
import io.reactivex.rxjava4.processors.FlowableProcessor;
import io.reactivex.rxjava4.processors.ReplayProcessor;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CardPreviewCellFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.SubmitTextField;
import it.niedermann.nextcloud.deck.javafx.util.DeckDataFormat;
import it.niedermann.nextcloud.deck.javafx.util.FxUtils;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Popup;

public class ColumnFeature extends DisposableController {

    private static final Logger logger = Logger.getLogger(ColumnFeature.class.getName());

    private final ListCardsUseCase listCardsUseCase;
    private final MoveCardUseCase moveCardUseCase;
    private final AddCardUseCase addCardUseCase;

    private final FlowableProcessor<Long> columnId = ReplayProcessor.create();
    private FlowableProcessor<Integer> draggingCardIndex;

    private final CardPreviewCellFactory cardPreviewCellFactory;

    @FXML
    Label title;
    @FXML
    ListView<Card> cards;
    @FXML
    Button addCard;
    @FXML
    Popup addCardPopup;
    @FXML
    SubmitTextField addCardSubmitTextField;

    @Inject
    public ColumnFeature(
            ListCardsUseCase listCardsUseCase,
            MoveCardUseCase moveCardUseCase,
            CardPreviewCellFactory cardPreviewCellFactory,
            AddCardUseCase addCardUseCase
    ) {
        this.listCardsUseCase = listCardsUseCase;
        this.moveCardUseCase = moveCardUseCase;
        this.cardPreviewCellFactory = cardPreviewCellFactory;
        this.addCardUseCase = addCardUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        cards.setCellFactory(cardPreviewCellFactory);
        cards.setOnDragEntered(this::onDragCardEntered);
        cards.setOnDragExited(this::onDragCardExited);
        cards.setOnDragOver(this::onDragCardOver);
        cards.setOnDragDropped(this::onCardDropped);

        final var disposable = this.columnId
                .distinctUntilChanged()
                .map(listCardsUseCase::execute)
                .switchMap(Flowable::fromPublisher)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(cards -> this.cards.getItems().setAll(cards));

        addDisposable(disposable);

        addCard.setOnAction(event -> {

            if (addCardPopup.isShowing()) {
                addCardPopup.hide();

            } else {
                final var bounds = addCard.localToScreen(addCard.getBoundsInLocal());
                addCardPopup.show(addCard, bounds.getMinX(), bounds.getMaxY() + 5);
                addCardSubmitTextField.requestFocus();
            }

            event.consume();
        });

        addCardSubmitTextField.setOnSubmit(cardTitle -> {

            addCardPopup.hide();
            addCardSubmitTextField.setDisable(true);

            addCard(cardTitle)
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

    private CompletableFuture<Void> addCard(String cardTitle) {
        return addCardUseCase.execute(columnId.blockingFirst(), cardTitle);
    }

    private void onDragCardEntered(DragEvent event) {
        if (this.draggingCardIndex != null) {
            throw new IllegalStateException("Expected draggingCardIndex to be null onDragCardEntered");
        }

        this.draggingCardIndex = BehaviorProcessor.create();
    }

    private void onDragCardExited(DragEvent event) {
        if (this.draggingCardIndex == null) {
            throw new IllegalStateException("Expected draggingCardIndex to be not null onDragCardEntered");
        }

        this.draggingCardIndex = null;
    }

    private void onDragCardOver(DragEvent event) {
        if (!TransferMode.MOVE.equals(event.getTransferMode())) {
            return;
        }

        final var dragboard = event.getDragboard();
        if (!dragboard.getContentTypes().contains(DeckDataFormat.CARD_DATA_FORMAT)) {
            return;
        }

        final var targetIndex = getDropTargetOrderOfListView(event);
        // TODO Display hint at targetIndex (e. g. colored bar or a semi-transparent card)

        logger.finest("Dragging over index " + targetIndex + ", targetIndex: " + targetIndex);

        event.acceptTransferModes(TransferMode.MOVE);
        event.consume();
    }

    public void onCardDropped(DragEvent event) {
        if (!TransferMode.MOVE.equals(event.getTransferMode())) {
            return;
        }

        final var dragboard = event.getDragboard();
        if (!dragboard.getContentTypes().contains(DeckDataFormat.CARD_DATA_FORMAT)) {
            return;
        }

        final var targetOrder = getDropTargetOrderOfListView(event);
        final var card = (Card) dragboard.getContent(DeckDataFormat.CARD_DATA_FORMAT);

        logger.info("Dropped: " + card);

        columnId.firstElement()
                .toCompletionStage()
                .toCompletableFuture()
                .thenComposeAsync(targetColumnId -> moveCardUseCase.execute(card, targetColumnId, targetOrder))
                .whenCompleteAsync((_, exception) -> {
                    if (exception != null) {
                        logger.log(Level.SEVERE, exception.getMessage(), exception);
                    }
                });

        event.consume();
    }

    private int getDropTargetOrderOfListView(DragEvent event) {
        final var intersectedNode = event.getPickResult().getIntersectedNode();
        final var intersectedListCellOrListView = FxUtils.findListCellOrListViewParent(intersectedNode)
                .orElseThrow(() -> new IllegalStateException("intersectedNode " + intersectedNode + " is not a child of the ListView"));

        return FxUtils.identifyClosestListViewIndex(intersectedListCellOrListView, event.getSceneY());
    }

    public void render(Args args) {
        this.title.setText(args.column().title());
        // Order of setting listener and columnId matters because columnId Flowable triggers rebinding the listener to the cards
        this.cardPreviewCellFactory.setCardPreviewActionListener(args.cardPreviewActionListener());
        this.columnId.onNext(args.column().id());
    }

    public record Args(Column column,
                       CardPreviewView.CardPreviewActionListener cardPreviewActionListener) {
    }
}
