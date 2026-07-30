package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
import it.niedermann.nextcloud.deck.javafx.util.DeckDataFormat;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class CardPreviewCellFactory implements Callback<ListView<PreviewCard>, ListCell<PreviewCard>> {

    private CardPreviewView.CardPreviewActionListener cardPreviewActionListener;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;

    private Account currentAccount;
    private Disposable disposable;

    @Inject
    public CardPreviewCellFactory(GetCurrentAccountUseCase getCurrentAccountUseCase, GetAccountUseCase getAccountUseCase) {
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getAccountUseCase = getAccountUseCase;

        this.disposable = Flowable.fromCompletionStage(getCurrentAccountUseCase.execute())
                .switchMap(id -> Flowable.fromPublisher(getAccountUseCase.execute(id)))
                .observeOn(JavaFxScheduler.platform())
                .subscribe(account -> this.currentAccount = account);
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
                final var totalWidth = Bindings.createDoubleBinding(
                        () -> listView.getWidth()
                              - getPadding().getLeft()
                              - getPadding().getRight()
                              // FIXME This magic number is probably needed for some border, otherwise the items cause overflow
                              - 2,
                        listView.widthProperty(),
                        paddingProperty());

                view.maxWidthProperty().bind(totalWidth);
                placeholder.maxWidthProperty().bind(totalWidth);
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

                    view.bind(card, currentAccount, cardPreviewActionListener);
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

            listCell.setOpacity(0.5);
            final var image = listCell.snapshot(null, null);
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
