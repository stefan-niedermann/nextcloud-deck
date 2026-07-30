package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
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
    CardPropertiesView cardProperties;
    @FXML
    AvatarView avatar;
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
        Inflater.getInstance().inflate(this);
    }

    public void bind(PreviewCard card, Account account, CardPreviewActionListener cardPreviewActionListener) {

        title.setText(card.title());
        description.setText(card.excerpt());
        assign.setVisible(!card.assignedToMe());
        unassign.setVisible(card.assignedToMe());
        avatar.setVisible(card.assignedToMe());
        avatar.setManaged(card.assignedToMe());

        if (card.assignedToMe() && account != null) {
            avatar.setAvatar(account);
        }

        cardProperties.setArgs(new CardPropertiesView.Args(
                card.remoteId(),
                card.excerpt(),
                card.labels().size(),
                0,
                card.commentCount(),
                card.attachmentCount(),
                card.assigneeCount()
        ));

        setOnMouseClicked(event -> {
            cardPreviewActionListener.onOpenCard(card.id());
            event.consume();
        });

        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                cardPreviewActionListener.onOpenCard(card.id());
                event.consume();
            }
        });

        assign.setOnAction(event -> {
            cardPreviewActionListener.onAssignCard(card.id());
            event.consume();
        });

        unassign.setOnAction(event -> {
            cardPreviewActionListener.onUnassignCard(card.id());
            event.consume();
        });

        move.setOnAction(event -> {
            cardPreviewActionListener.onMoveCard(card.id());
            event.consume();
        });

        copy.setOnAction(event -> {
            cardPreviewActionListener.onCopyCard(card.id());
            event.consume();
        });

        delete.setOnAction(event -> {
            cardPreviewActionListener.onDeleteCard(card.id());
            event.consume();
        });

        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
            event.consume();
        });
    }

    public interface CardPreviewActionListener {
        void onOpenCard(Card.ID cardId);

        void onAssignCard(Card.ID cardId);

        void onUnassignCard(Card.ID cardId);

        void onMoveCard(Card.ID cardId);

        void onCopyCard(Card.ID cardId);

        void onDeleteCard(Card.ID cardId);
    }
}
