package it.niedermann.nextcloud.deck.app.shared.args.card;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;

public class CardArgResolver implements ArgsResolver<CardRawArgs, Card.ID> {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    public CardArgResolver(CardRepository cardRepository, AccountRepository accountRepository, GetCurrentAccountUseCase getCurrentAccountUseCase) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
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
        } else if (args instanceof CardRawArgs.RemoteCard remoteCard) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(getCurrentAccountUseCase.execute())
                            .flatMap(accountId -> Flowable.fromCompletionStage(cardRepository.findCardByRemoteId(accountId, remoteCard.cardRemoteId()))
                                    .switchIfEmpty(Flowable.error(new CardDoesNotExist()))
                            )
            );
        } else if (args instanceof CardRawArgs.RemoteAccount remoteAccount) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(accountRepository.findAccountId(remoteAccount.accountName()))
                            .flatMap(accountId -> Flowable.fromCompletionStage(cardRepository.findCardByRemoteId(accountId, remoteAccount.cardRemoteId()))
                                    .switchIfEmpty(Flowable.error(new CardDoesNotExist()))
                            )
            );
        } else if (args instanceof CardRawArgs.RemoteServer remoteServer) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(accountRepository.findAccountIdsByUrl(remoteServer.server()))
                            .flatMap(accountIds -> {
                                if (accountIds.isEmpty()) {
                                    return Flowable.error(new BoardArgResolver.RequestedAccountNotConfiguredException());
                                } else if (accountIds.size() > 1) {
                                    return Flowable.error(new UnsupportedOperationException("Multiple accounts for same URL not yet supported in CLI resolution."));
                                }
                                final var accountId = accountIds.get(0);
                                return Flowable.fromCompletionStage(cardRepository.findCardByRemoteId(accountId, remoteServer.cardRemoteId()))
                                        .switchIfEmpty(Flowable.error(new CardDoesNotExist()));
                            })
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
