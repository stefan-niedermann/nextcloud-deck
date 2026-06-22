package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;

public class CardPreviewView extends BorderPane {

    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    ContextMenu contextMenu;
    @FXML
    MenuItem assign;
    @FXML
    MenuItem unassign;
    @FXML
    MenuItem move;
    @FXML
    MenuItem copy;
    @FXML
    MenuItem delete;

    public CardPreviewView() {
        Inflater.getInstance().inflateAndBind(this);
    }

    public void bind(Card card, boolean isAssignedToCurrentUser, CardPreviewActionListener cardPreviewActionListener) {

        title.setText(card.title());
        description.setText(card.description());
        assign.setVisible(!isAssignedToCurrentUser);
        unassign.setVisible(isAssignedToCurrentUser);

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

        assign.setOnAction(event -> {
            cardPreviewActionListener.onDeleteCard(card);
            event.consume();
        });

        unassign.setOnAction(event -> {
            cardPreviewActionListener.onDeleteCard(card);
            event.consume();
        });

        move.setOnAction(event -> {
            cardPreviewActionListener.onDeleteCard(card);
            event.consume();
        });

        copy.setOnAction(event -> {
            cardPreviewActionListener.onDeleteCard(card);
            event.consume();
        });

        delete.setOnAction(event -> {
            cardPreviewActionListener.onDeleteCard(card);
            event.consume();
        });

        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public interface CardPreviewActionListener {
        void onOpenCard(Card card);

        void onAssignCard(Card card);

        void onUnassignCard(Card card);

        void onMoveCard(Card card);

        void onCopyCard(Card card);

        void onDeleteCard(Card card);
    }
}
