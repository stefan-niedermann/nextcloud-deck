package it.niedermann.nextcloud.deck.ui.card.assignee;

import androidx.annotation.NonNull;

import it.niedermann.nextcloud.deck.model.User;

public interface CardAssigneeListener {

    void onUnassignUser(@NonNull User user);

}