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
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateCard;
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageContext;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Popup;

public class ColumnFeature extends DisposableController implements CardPreviewView.CardPreviewActionListener {

    private static final Logger logger = Logger.getLogger(ColumnFeature.class.getName());

    private final StageContext stageContext;
    private final ListCardsUseCase listCardsUseCase;
    private final MoveCardUseCase moveCardUseCase;
    private final AddCardUseCase addCardUseCase;
    private final ThemeService themeService;

    private final FlowableProcessor<Column.ID> columnId = ReplayProcessor.create();
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
            StageContext stageContext,
            ListCardsUseCase listCardsUseCase,
            MoveCardUseCase moveCardUseCase,
            CardPreviewCellFactory cardPreviewCellFactory,
            AddCardUseCase addCardUseCase,
            ThemeService themeService
    ) {
        this.stageContext = stageContext;
        this.listCardsUseCase = listCardsUseCase;
        this.moveCardUseCase = moveCardUseCase;
        this.cardPreviewCellFactory = cardPreviewCellFactory;
        this.addCardUseCase = addCardUseCase;
        this.themeService = themeService;
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
                .observeOn(Schedulers.virtual())
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
        return addCardUseCase.execute(new CreateCard(columnId.blockingFirst(), cardTitle));
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
        if (!dragboard.getContentTypes().contains(DeckDataFormat.CARD_ID_PRIMITIVE)) {
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
        if (!dragboard.getContentTypes().contains(DeckDataFormat.CARD_ID_PRIMITIVE)) {
            return;
        }

        final var targetOrder = getDropTargetOrderOfListView(event);
        final var cardId = new Card.ID((long) dragboard.getContent(DeckDataFormat.CARD_ID_PRIMITIVE));

        logger.info("Dropped: " + cardId);

        columnId.firstElement()
                .toCompletionStage()
                .toCompletableFuture()
                .thenComposeAsync(targetColumnId -> moveCardUseCase.execute(cardId, targetColumnId, targetOrder))
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

    public void render(Column column) {
        this.title.setText(column.title());
        // Order of setting listener and columnId matters because columnId Flowable triggers rebinding the listener to the cards
        this.cardPreviewCellFactory.setCardPreviewActionListener(this);
        this.columnId.onNext(column.id());
    }

    @Override
    public void onOpenCard(Card card) {
        stageContext.dispatch(new StageContext.Action.EditCardAction(card.id()));
    }

    @Override
    public void onAssignCard(Card card) {
        System.out.println("[Mock] onAssignCard " + card);
    }

    @Override
    public void onUnassignCard(Card card) {
        System.out.println("[Mock] onUnassignCard " + card);
    }

    @Override
    public void onMoveCard(Card card) {
        System.out.println("[Mock] onMoveCard " + card);
    }

    @Override
    public void onCopyCard(Card card) {
        System.out.println("[Mock] onCopyCard " + card);
    }

    @Override
    public void onDeleteCard(Card card) {
        final var alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to delete the card \"" + card.title() + "\" permanently? This operation can not be undone.", ButtonType.CANCEL, ButtonType.YES);
        alert.setTitle("Delete");
        alert.setHeaderText("Delete \"" + card.title() + "\"?");
        themeService.bind(alert);
        alert.showAndWait()
                .map(ButtonType::getButtonData)
                .map(ButtonBar.ButtonData::isDefaultButton)
                .filter(Boolean.TRUE::equals).ifPresent(_ -> stageContext.dispatch(new StageContext.Action.DeleteCardAction(card.id())));
    }
}
