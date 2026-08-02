package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;

public interface ActivityRepository {

    Flow.Publisher<List<Activity>> getNotDeletedActivities(Card.ID cardId);

    Flow.Publisher<List<PreviewActivity>> getNotDeletedActivityPreviews(Card.ID cardId);

}
