package it.niedermann.nextcloud.deck.domain.usecases.activities;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import jakarta.inject.Inject;

public class ListActivityUseCase {

    private final ActivityRepository activityRepository;

    @Inject
    public ListActivityUseCase(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public Flow.Publisher<List<Activity>> execute(Card.ID cardId) {
        return activityRepository.getNotDeletedActivities(cardId);
    }
}
