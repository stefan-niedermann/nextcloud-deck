package it.niedermann.nextcloud.deck.ui.card.details;

import java.util.Date;

import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.User;

public interface CardDetailsListener {

    void onDescriptionChanged(String toString);

    void onDueDateChanged(Date dueDate);

    void onUserAdded(User user);

    void onUserRemoved(User user);

    void onLabelRemoved(Label label);

    void onLabelAdded(Label createdLabel);
}