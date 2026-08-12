package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.ActivityDao;
import it.niedermann.nextcloud.deck.data.local.mapper.ActivityMapper;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import jakarta.inject.Inject;

public class ActivityRepositoryImpl implements ActivityRepository {

    private final AccountRepository accountRepository;
    private final ActivityDao activityDao;
    private final ActivityMapper activityMapper;

    @Inject
    public ActivityRepositoryImpl(AccountRepository accountRepository,
                                 ActivityDao activityDao,
                                 ActivityMapper activityMapper) {
        this.accountRepository = accountRepository;
        this.activityDao = activityDao;
        this.activityMapper = activityMapper;
    }

    @Override
    public Flow.Publisher<List<Activity>> getNotDeletedActivities(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                activityDao.getActivitiesByCard(cardId.value())
                        .map(activityMapper::toTOList)
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<List<PreviewActivity>> getNotDeletedActivityPreviews(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Maybe.fromCompletionStage(accountRepository.findAccountIdByCardId(cardId))
                        .toFlowable()
                        .switchMap(accountId -> Maybe.fromCompletionStage(accountRepository.getAccountSync(accountId)).toFlowable())
                        .switchMap(account -> activityDao.getActivitiesByCard(cardId.value())
                                .map(entities -> entities.stream()
                                        .map(entity -> new PreviewActivity(activityMapper.toTO(entity), account))
                                        .collect(Collectors.toList())))
                        .subscribeOn(Schedulers.io())
        );
    }
}
