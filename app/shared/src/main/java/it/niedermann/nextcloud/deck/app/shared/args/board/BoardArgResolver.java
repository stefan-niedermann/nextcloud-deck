package it.niedermann.nextcloud.deck.app.shared.args.board;

import org.reactivestreams.FlowAdapters;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import jakarta.inject.Inject;

public class BoardArgResolver implements ArgsResolver<BoardRawArgs, BoardParsedArgs> {

    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;

    @Inject
    public BoardArgResolver(
            HasAccountsUseCase hasAccountsUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase
    ) {
        this.hasAccountsUseCase = hasAccountsUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
    }

    @Override
    public CompletableFuture<BoardParsedArgs> resolve(BoardRawArgs args) {
        if (args instanceof BoardRawArgs.CurrentBoardOfCurrentAccount) {
            final var accountId = Flowable.fromPublisher(FlowAdapters.toPublisher(hasAccountsUseCase.execute()))
                    .subscribeOn(Schedulers.io())
                    .firstElement()
                    .flatMapSingle(hasAccounts -> {
                        // TODO No need to check for hasAccounts, better check fo accounts.exist(args)
                        if (hasAccounts) {
                            return Single.fromCompletionStage(getCurrentAccountUseCase.execute());
                        }

                        return Single.error(new BoardArgResolver.NoAccountConfiguredException());
                    })
                    .toCompletionStage()
                    .toCompletableFuture();

            final var boardId = accountId
                    .thenComposeAsync(getCurrentBoardUseCase::execute)
                    .exceptionally(throwable -> null);

            return accountId.thenCombineAsync(boardId, BoardParsedArgs::new);

        } else {
            final var cf = new CompletableFuture<BoardParsedArgs>();
            cf.completeExceptionally(new UnsupportedOperationException("Not yet implemented."));
            return cf;
        }
    }

    abstract sealed class BoardArgResolveException extends RuntimeException permits MultipleAccountsOnRequestedInstanceException, NoAccountConfiguredException, RequestedAccountNotConfiguredException {
    }

    /// No account is configured at all
    ///
    /// @deprecated according to [ArgsResolver#resolve(Object)] it is safe to assume that this can not happen.
    @Deprecated
    public final class NoAccountConfiguredException extends BoardArgResolveException {

    }

    /// There is no account on the requested instance
    public final class RequestedAccountNotConfiguredException extends BoardArgResolveException {

    }

    /// Args are not specific enough to identify one matching account
    public final class MultipleAccountsOnRequestedInstanceException extends BoardArgResolveException {

        private final Collection<Account> matchingAccounts;

        public MultipleAccountsOnRequestedInstanceException(Collection<Account> matchingAccounts) {
            this.matchingAccounts = matchingAccounts;
        }

        public Collection<Account> getMatchingAccounts() {
            return this.matchingAccounts;
        }
    }
}
