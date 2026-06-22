package it.niedermann.nextcloud.deck.javafx.ui.cellfactories;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.ui.controller.views.CardPreviewView;
import it.niedermann.nextcloud.deck.javafx.util.DeckDataFormat;
import jakarta.inject.Inject;
import javafx.beans.binding.Bindings;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.util.Callback;

public class CardPreviewCellFactory implements Callback<ListView<Card>, ListCell<Card>> {

    private CardPreviewView.CardPreviewActionListener cardPreviewActionListener;

    @Inject
    public CardPreviewCellFactory() {

    }

    public void setCardPreviewActionListener(CardPreviewView.CardPreviewActionListener cardPreviewActionListener) {
        this.cardPreviewActionListener = cardPreviewActionListener;
    }

    @Override
    public ListCell<Card> call(ListView<Card> listView) {
        final var listCell = new ListCell<Card>() {

            final CardPreviewView view = new CardPreviewView();

            {
                final var totalWidth = Bindings.createDoubleBinding(
                        () -> listView.getWidth()
                              - getPadding().getLeft()
                              - getPadding().getRight()
                              // FIXME This magic number is probably needed for some border, otherwise the items cause overflow
                              - 2,
                        listView.widthProperty(),
                        paddingProperty());

                view.maxWidthProperty().bind(totalWidth);
            }

            @Override
            protected void updateItem(Card card, boolean empty) {
                super.updateItem(card, empty);
                setText(null);

                if (empty) {

                    setGraphic(null);

                } else {

                    // TODO evaluate whether card assignees contains the user belonging to the current account
                    view.bind(card, !card.assignees().isEmpty(), cardPreviewActionListener);
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
            content.put(DeckDataFormat.CARD_DATA_FORMAT, card);
            dragboard.setContent(content);

            final var image = listCell.snapshot(null, null);
            dragboard.setDragView(image);

            event.consume();
        });

        return listCell;
    }
}
