package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;

public interface ActivityRepository {

    Flow.Publisher<List<Activity>> getNotDeletedActivities(Card.ID cardId);

}