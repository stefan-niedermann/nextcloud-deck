package it.niedermann.nextcloud.deck.app.shared.args.card;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import jakarta.inject.Inject;

public class CardArgResolver implements ArgsResolver<CardRawArgs, Card.ID> {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;

    @Inject
    public CardArgResolver(CardRepository cardRepository, AccountRepository accountRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<Card.ID> resolve(CardRawArgs args) {
        if (args instanceof CardRawArgs.LocalCard localCard) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(accountRepository.findAccountIdByCardId(localCard.cardId()))
                            .flatMap(accountId ->
                                    Flowable.fromPublisher(FlowAdapters.toPublisher(accountRepository.accountExists(accountId)))
                                            .switchMap(exists -> {
                                                if (exists) {
                                                    return Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.cardExists(localCard.cardId())))
                                                            .switchMap(cardExists -> {
                                                                if (cardExists) {
                                                                    return Flowable.just(localCard.cardId());
                                                                } else {
                                                                    return Flowable.error(new CardDoesNotExist());
                                                                }
                                                            });
                                                } else {
                                                    return Flowable.error(new BoardArgResolver.NoAccountConfiguredException());
                                                }
                                            })
                            )
            );
        } else {
            return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented.")));
        }
    }

    public abstract sealed static class CardArgResolveException extends RuntimeException permits CardDoesNotExist {
    }

    public static final class CardDoesNotExist extends CardArgResolveException {
    }

}
