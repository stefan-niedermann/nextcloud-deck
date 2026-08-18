package it.niedermann.nextcloud.deck.javafx.ui.controller.views;

import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.util.ColorUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class CardPreviewView extends BorderPane {

    @FXML
    Label title;
    @FXML
    Label description;
    @FXML
    FlowPane labels;
    @FXML
    CardPropertiesView cardProperties;
    @FXML
    AvatarView avatar;
    @FXML
    ContextMenu contextMenu;
    @FXML
    MenuItem openInNewWindow;
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

    private final BooleanProperty compact = new SimpleBooleanProperty(this, "compact", false);

    public CardPreviewView() {
        Inflater.getInstance().inflate(this);
    }

    public BooleanProperty compactProperty() {
        return compact;
    }

    public void bind(PreviewCard card, Account account, CardPreviewActionListener cardPreviewActionListener, ColorUtil colorUtil) {

        title.setText(card.title());
        description.setText(card.excerpt());
        description.setManaged(!card.excerpt().isEmpty());
        description.setVisible(!card.excerpt().isEmpty());

        labels.getChildren().clear();
        for (final var label : card.labels()) {
            final var labelView = new LabelView(colorUtil);
            labelView.compactProperty().bind(compact);
            labelView.setLabel(label.title(), label.color());
            labels.getChildren().add(labelView);
        }
        labels.setManaged(!card.labels().isEmpty());
        labels.setVisible(!card.labels().isEmpty());

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

        openInNewWindow.setOnAction(event -> {
            cardPreviewActionListener.onOpenCardInNewWindow(card.id());
            event.consume();
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
            cardPreviewActionListener.onMoveCard(card.id(), this);
            event.consume();
        });

        copy.setOnAction(event -> {
            cardPreviewActionListener.onCopyCard(card.id(), this);
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

        void onOpenCardInNewWindow(Card.ID cardId);

        void onAssignCard(Card.ID cardId);

        void onUnassignCard(Card.ID cardId);

        void onMoveCard(Card.ID cardId, Node anchor);

        void onCopyCard(Card.ID cardId, Node anchor);

        void onDeleteCard(Card.ID cardId);
    }
}
