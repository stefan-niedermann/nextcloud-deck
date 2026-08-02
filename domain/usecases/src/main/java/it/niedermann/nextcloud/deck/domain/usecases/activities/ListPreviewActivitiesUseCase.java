package it.niedermann.nextcloud.deck.domain.usecases.activities;

import java.util.List;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import jakarta.inject.Inject;

public class ListPreviewActivitiesUseCase {

    private final ActivityRepository activityRepository;

    @Inject
    public ListPreviewActivitiesUseCase(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public Flow.Publisher<List<PreviewActivity>> execute(Card.ID cardId) {
        return activityRepository.getNotDeletedActivityPreviews(cardId);
    }
}
