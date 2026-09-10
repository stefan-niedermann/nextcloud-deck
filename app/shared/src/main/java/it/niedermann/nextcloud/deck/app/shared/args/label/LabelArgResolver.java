package it.niedermann.nextcloud.deck.app.shared.args.label;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import jakarta.inject.Inject;

public class LabelArgResolver implements ArgsResolver<LabelRawArgs, LabelParsedArgs> {

    private final LabelRepository labelRepository;
    private final AccountRepository accountRepository;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    public LabelArgResolver(LabelRepository labelRepository, AccountRepository accountRepository, GetCurrentAccountUseCase getCurrentAccountUseCase) {
        this.labelRepository = labelRepository;
        this.accountRepository = accountRepository;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
    }

    @Override
    public Flow.Publisher<LabelParsedArgs> resolve(LabelRawArgs args) {
        if (args instanceof LabelRawArgs.LocalLabel localLabel) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(accountRepository.getAnyAccount()) // TODO find account by label ID
                            .flatMap(accountId -> Flowable.just(new LabelParsedArgs(accountId, localLabel.labelId())))
            );
        } else if (args instanceof LabelRawArgs.RemoteLabel remoteLabel) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromCompletionStage(getCurrentAccountUseCase.execute())
                            .subscribeOn(Schedulers.io())
                            .flatMap(accountId -> Flowable.fromCompletionStage(labelRepository.findLabelByRemoteId(accountId, remoteLabel.labelRemoteId()))
                                    .map(labelId -> new LabelParsedArgs(accountId, labelId))
                            )
            );
        } else {
            return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented.")));
        }
    }
}
