package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;

public class ActivityRepositoryImpl implements ActivityRepository {

    @Override
    public Flow.Publisher<List<Activity>> getNotDeletedActivities(Card.ID cardId) {
        try {
            return FlowAdapters.toFlowPublisher(Flowable.just(List.of(new Activity(
                    new Activity.ID(1),
                    cardId,
                    "Something changed",
                    new User(new User.ID("sample"), "Sampson Sample"),
                    new URL("https://placehold.co/150x150"),
                    LocalDateTime.now()
            ))));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}