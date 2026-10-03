package it.niedermann.nextcloud.deck.javafx.ui.shared.views;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DependentCardView extends HBox {

    @FXML
    CheckBox doneCheckBox;
    @FXML
    Label titleLabel;
    @FXML
    Button removeButton;

    public DependentCardView() {
        Inflater.getInstance().inflate(this);
    }

    public void bind(Card card, DependentCardActionListener listener) {
        doneCheckBox.setSelected(card.done() != null);
        titleLabel.setText((card.remoteId() != null ? card.remoteId().value() + ". " : "") + card.title());

        if (card.done() != null) {
            titleLabel.getStyleClass().add("done");
        } else {
            titleLabel.getStyleClass().remove("done");
        }

        doneCheckBox.setOnAction(event -> {
            if (doneCheckBox.isSelected()) {
                listener.onMarkAsDone(card.id());
            } else {
                listener.onMarkAsUndone(card.id());
            }
        });

        removeButton.setOnAction(event -> listener.onRemoveDependent(card.id()));
    }

    public interface DependentCardActionListener {
        void onMarkAsDone(Card.ID cardId);
        void onMarkAsUndone(Card.ID cardId);
        void onRemoveDependent(Card.ID cardId);
    }
}
