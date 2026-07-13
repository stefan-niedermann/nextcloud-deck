package it.niedermann.nextcloud.deck.app.shared.args.board;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
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
                        if (hasAccounts) {
                            return Single.fromCompletionStage(getCurrentAccountUseCase.execute());
                        }

                        return Single.error(new ArgsResolver.NoAccountConfiguredException());
                    })
                    .toCompletionStage()
                    .toCompletableFuture();

            final var boardId = accountId
                    .thenComposeAsync(getCurrentBoardUseCase::execute)
                    .exceptionally(throwable -> null);

            return accountId.thenCombineAsync(boardId, BoardParsedArgs::new);

        } else {
            throw new UnsupportedOperationException("Not yet implemented.");
        }
    }
}
