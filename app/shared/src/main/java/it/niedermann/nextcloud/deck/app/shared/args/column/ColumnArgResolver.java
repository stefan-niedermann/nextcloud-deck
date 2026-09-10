package it.niedermann.nextcloud.deck.app.shared.args.column;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;

public class ColumnArgResolver implements ArgsResolver<ColumnRawArgs, ColumnParsedArgs> {

    private final ColumnRepository columnRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    public ColumnArgResolver(ColumnRepository columnRepository, AccountRepository accountRepository, GetCurrentAccountUseCase getCurrentAccountUseCase) {
        this.columnRepository = columnRepository;
        this.accountRepository = accountRepository;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
    }

    @Override
    public Flow.Publisher<ColumnParsedArgs> resolve(ColumnRawArgs args) {
        if (args instanceof ColumnRawArgs.LocalColumn localColumn) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(accountRepository.getAnyAccount()) // TODO find account by column ID
                            .flatMap(accountId -> Flowable.just(new ColumnParsedArgs(accountId, localColumn.columnId())))
            );
        } else if (args instanceof ColumnRawArgs.RemoteColumn remoteColumn) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(getCurrentAccountUseCase.execute())
                            .subscribeOn(Schedulers.io())
                            .flatMap(accountId -> Flowable.fromCompletionStage(columnRepository.findColumnByRemoteId(accountId, remoteColumn.columnRemoteId()))
                                    .map(columnId -> new ColumnParsedArgs(accountId, columnId))
                            )
            );
        } else {
            return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented.")));
        }
    }

    public static final class ColumnDoesNotExist extends RuntimeException {
    }
}
