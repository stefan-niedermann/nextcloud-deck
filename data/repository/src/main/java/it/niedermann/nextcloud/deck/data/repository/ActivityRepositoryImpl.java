package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.ActivityDao;
import it.niedermann.nextcloud.deck.data.local.dao.CardDao;
import it.niedermann.nextcloud.deck.data.local.entity.ActivityEntity;
import it.niedermann.nextcloud.deck.data.local.mapper.ActivityMapper;
import it.niedermann.nextcloud.deck.domain.model.Activity;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.DBStatus;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ActivityRepository;
import it.niedermann.nextcloud.remote.ApiProvider;
import it.niedermann.nextcloud.remote.deck.mapper.ActivityRemoteMapper;
import jakarta.inject.Inject;

public class ActivityRepositoryImpl implements ActivityRepository {

    private static final Logger logger = Logger.getLogger(ActivityRepositoryImpl.class.getName());

    private final AccountRepository accountRepository;
    private final ActivityDao activityDao;
    private final CardDao cardDao;
    private final ActivityMapper activityMapper;
    private final ApiProvider.Factory apiFactory;

    @Inject
    public ActivityRepositoryImpl(AccountRepository accountRepository,
                                 ActivityDao activityDao,
                                 CardDao cardDao,
                                 ActivityMapper activityMapper,
                                 ApiProvider.Factory apiFactory) {
        this.accountRepository = accountRepository;
        this.activityDao = activityDao;
        this.cardDao = cardDao;
        this.activityMapper = activityMapper;
        this.apiFactory = apiFactory;
    }

    @Override
    public Flow.Publisher<List<Activity>> getNotDeletedActivities(Card.ID cardId) {
        // Trigger refresh in background
        refresh(cardId);
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

    @Override
    public CompletableFuture<Void> refresh(Card.ID cardId) {
        return cardDao.getCardById(cardId.value())
                .thenCompose(card -> {
                    if (card == null || card.getRemoteId() == null) return CompletableFuture.completedFuture(null);
                    return accountRepository.getAccountSync(new it.niedermann.nextcloud.deck.domain.model.Account.ID(card.getAccountId()))
                            .thenCompose(account -> apiFactory.create(account).getOcsApi().getActivitiesForCard(card.getRemoteId()))
                            .thenCompose(response -> {
                                if (response == null || response.getOcs() == null || response.getOcs().getData() == null)
                                    return CompletableFuture.completedFuture(null);
                                CompletableFuture<?>[] futures = response.getOcs().getData().stream()
                                        .map(dto -> {
                                            Activity activity = ActivityRemoteMapper.INSTANCE.toTO(dto);
                                            ActivityEntity entity = new ActivityEntity(
                                                    0,
                                                    card.getAccountId(),
                                                    activity.id().value(),
                                                    DBStatus.UP_TO_DATE.getId(),
                                                    activity.lastModified(),
                                                    OffsetDateTime.now(),
                                                    null,
                                                    card.getLocalId(),
                                                    activity.subject(),
                                                    activity.type(),
                                                    dto.getUser(),
                                                    activity.createdAt()
                                            );
                                            return activityDao.insertOrReplace(entity);
                                        }).toArray(CompletableFuture[]::new);
                                return CompletableFuture.allOf(futures);
                            });
                });
    }
}
