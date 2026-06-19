package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class CardPreviewView extends BorderPane {

    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    ContextMenu contextMenu;

    public CardPreviewView() {
        Inflater.getInstance().inflateAndBind(this);
    }

    public void bind(Card card, CardPreviewActionListener cardPreviewActionListener) {

        title.setText(card.title());
        description.setText(card.description());

        setOnMouseClicked(event -> {
            cardPreviewActionListener.onOpenCard(card);
            event.consume();
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                cardPreviewActionListener.onOpenCard(card);
                event.consume();
            }
        });

        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public interface CardPreviewActionListener {
        void onOpenCard(Card card);
    }
}
