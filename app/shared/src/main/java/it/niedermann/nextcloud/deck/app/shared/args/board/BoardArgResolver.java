package it.niedermann.nextcloud.deck.app.shared.args.board;

import org.reactivestreams.FlowAdapters;

import java.util.Collection;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import jakarta.inject.Inject;

public class BoardArgResolver implements ArgsResolver<BoardRawArgs, BoardParsedArgs> {

    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final AccountRepository accountRepository;

    @Inject
    public BoardArgResolver(
            HasAccountsUseCase hasAccountsUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            AccountRepository accountRepository
    ) {
        this.hasAccountsUseCase = hasAccountsUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<BoardParsedArgs> resolve(BoardRawArgs args) {
        if (args instanceof BoardRawArgs.CurrentBoardOfCurrentAccount) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromPublisher(FlowAdapters.toPublisher(hasAccountsUseCase.execute()))
                            .subscribeOn(Schedulers.io())
                            .switchMap(hasAccounts -> {
                                if (hasAccounts) {
                                    return Flowable.fromCompletionStage(getCurrentAccountUseCase.execute())
                                            .flatMap(accountId ->
                                                    Flowable.fromCompletionStage(getCurrentBoardUseCase.execute(accountId))
                                                            .map(boardId -> new BoardParsedArgs(accountId, boardId))
                                                            .onErrorReturnItem(new BoardParsedArgs(accountId, null))
                                            );
                                }

                                return Flowable.error(new BoardArgResolver.NoAccountConfiguredException());
                            })
            );

        } else if (args instanceof BoardRawArgs.ExplicitBoard explicitArgs) {

            return FlowAdapters.toFlowPublisher(
                    Flowable.fromPublisher(FlowAdapters.toPublisher(accountRepository.accountExists(explicitArgs.accountId())))
                            .subscribeOn(Schedulers.io())
                            .switchMap(exists -> {
                                if (exists) {
                                    return Flowable.just(new BoardParsedArgs(explicitArgs.accountId(), explicitArgs.boardId()));
                                } else {
                                    return Flowable.error(new BoardArgResolver.NoAccountConfiguredException());
                                }
                            })
            );

        } else {
            return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented.")));
        }
    }

    abstract sealed static class BoardArgResolveException extends RuntimeException permits MultipleAccountsOnRequestedInstanceException, NoAccountConfiguredException, RequestedAccountNotConfiguredException {
    }

    /// No account is configured at all
    public static final class NoAccountConfiguredException extends BoardArgResolveException {

    }

    /// There is no account on the requested instance
    public static final class RequestedAccountNotConfiguredException extends BoardArgResolveException {

    }

    /// Args are not specific enough to identify one matching account
    public static final class MultipleAccountsOnRequestedInstanceException extends BoardArgResolveException {

        private final Collection<Account> matchingAccounts;

        public MultipleAccountsOnRequestedInstanceException(Collection<Account> matchingAccounts) {
            this.matchingAccounts = matchingAccounts;
        }

        public Collection<Account> getMatchingAccounts() {
            return this.matchingAccounts;
        }
    }
}
