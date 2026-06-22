package it.niedermann.nextcloud.deck.domain.usecases.cards;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class UnassignCardUseCase {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    @Inject
    public UnassignCardUseCase(
            UserRepository userRepository,
            CardRepository cardRepository
    ) {
        this.userRepository = userRepository;
        this.cardRepository = cardRepository;
    }

    public CompletableFuture<Void> execute(long cardId) {
        return Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getCard(cardId)))
                .firstElement()
                .map(account -> userRepository.getUserByAccountId(account.id()))
                .map(FlowAdapters::toPublisher)
                .flatMapPublisher(Flowable::fromPublisher)
                .firstElement()
                .map(User::id)
                .flatMapSingle(userId -> Single.fromFuture(execute(cardId, userId)))
                .toCompletionStage()
                .toCompletableFuture();
    }

    public CompletableFuture<Void> execute(long cardId, String userId) {
        return cardRepository.unassignUser(cardId, userId);
    }
}
