package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import jakarta.inject.Inject;

public class ActivityRepositoryImpl implements ActivityRepository {

    private final AccountRepository accountRepository;

    @Inject
    public ActivityRepositoryImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<List<Activity>> getNotDeletedActivities(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just( List.of(new Activity(
                new Activity.ID(1),
                cardId,
                "Something changed",
                0,
                new User(new User.ID("sample"), "Sampson Sample"),
                OffsetDateTime.now()
        ))));
    }

    @Override
    public Flow.Publisher<List<PreviewActivity>> getNotDeletedActivityPreviews(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromCompletionStage(accountRepository.findAccountIdByCardId(cardId))
                        .switchMap(accountId -> Flowable.fromCompletionStage(accountRepository.getAccountSync(accountId)))
                        .switchMap(account -> Flowable.fromCallable(() -> List.of(new PreviewActivity(new Activity(
                                new Activity.ID(1),
                                cardId,
                                "Something changed",
                                0,
                                new User(new User.ID("sample"), "Sampson Sample"),
                                OffsetDateTime.now()
                        ), account))))
        );
    }
}