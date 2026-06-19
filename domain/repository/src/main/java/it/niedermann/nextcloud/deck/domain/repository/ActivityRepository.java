package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Activity;

public interface ActivityRepository {

    Flow.Publisher<List<Activity>> getNotDeletedActivities(long cardId);

}