package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Maybe;
import io.reactivex.rxjava4.disposables.CompositeDisposable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.PreferencesStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
import it.niedermann.nextcloud.deck.javafx.util.DeckDataFormat;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import jakarta.inject.Inject;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class CardPreviewCellFactory implements Callback<ListView<PreviewCard>, ListCell<PreviewCard>> {

    private CardPreviewView.CardPreviewActionListener cardPreviewActionListener;
    private final ColorUtil colorUtil;

    private Account currentAccount;
    private final BooleanProperty compactMode = new SimpleBooleanProperty(this, "compactMode", false);
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Inject
    public CardPreviewCellFactory(GetCurrentAccountUseCase getCurrentAccountUseCase,
                                  GetAccountUseCase getAccountUseCase,
                                  KeyValueStore keyValueStore,
                                  ColorUtil colorUtil) {
        this.colorUtil = colorUtil;

        disposables.add(Maybe.fromCompletionStage(getCurrentAccountUseCase.execute())
                .toFlowable()
                .switchMap(id -> Flowable.fromPublisher(getAccountUseCase.execute(id)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(account -> this.currentAccount = account));

        disposables.add(Flowable.fromPublisher(keyValueStore.getBoolean(PreferencesStageContext.KEY_COMPACT_MODE))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(compactMode::set));
    }

    public void setCardPreviewActionListener(CardPreviewView.CardPreviewActionListener cardPreviewActionListener) {
        this.cardPreviewActionListener = cardPreviewActionListener;
    }

    @Override
    public ListCell<PreviewCard> call(ListView<PreviewCard> listView) {
        final var listCell = new ListCell<PreviewCard>() {

            final CardPreviewView view = new CardPreviewView();
            final Region placeholder = new Region();

            {
                placeholder.getStyleClass().add("card-placeholder");
                view.compactProperty().bind(compactMode);
            }

            @Override
            protected void updateItem(PreviewCard card, boolean empty) {
                super.updateItem(card, empty);
                setText(null);

                if (empty) {

                    setGraphic(null);

                } else if (card == null) {

                    setGraphic(placeholder);

                } else {

                    view.bind(card, currentAccount, cardPreviewActionListener, colorUtil);
                    setGraphic(view);

                }
            }
        };

        listCell.setOnDragDetected(event -> {
            final var card = listCell.getItem();

            if (card == null) {
                return;
            }

            final var dragboard = listCell.startDragAndDrop(TransferMode.MOVE);

            final var content = new ClipboardContent();

            content.put(DataFormat.PLAIN_TEXT, card.title());
            // TODO Add card URL as DragContent
            // content.put(DataFormat.URL, card.title());
            content.put(DeckDataFormat.CARD_ID_PRIMITIVE, card.id().value());
            dragboard.setContent(content);

            final var graphic = listCell.getGraphic();
            final var image = graphic != null ? graphic.snapshot(null, null) : listCell.snapshot(null, null);
            listCell.setOpacity(0.5);
            dragboard.setDragView(image);

            event.consume();
        });

        listCell.setOnDragDone(event -> {
            listCell.setOpacity(1.0);
            event.consume();
        });

        return listCell;
    }
}
