package it.niedermann.nextcloud.deck.app.shared.args.account;

import org.reactivestreams.FlowAdapters;

import java.util.Optional;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.repository.AccountRepository;
import jakarta.inject.Inject;

public class AccountArgResolver implements ArgsResolver<AccountRawArgs, AccountParsedArgs> {

    private final AccountRepository accountRepository;

    @Inject
    public AccountArgResolver(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Flow.Publisher<AccountParsedArgs> resolve(AccountRawArgs args) {
        if (args instanceof AccountRawArgs.None) {
            return FlowAdapters.toFlowPublisher(Flowable.just(new AccountParsedArgs(Optional.empty())));
        } else if (args instanceof AccountRawArgs.ExplicitAccount explicitAccount) {
            return FlowAdapters.toFlowPublisher(
                    Flowable.fromPublisher(FlowAdapters.toPublisher(accountRepository.accountExists(explicitAccount.accountId())))
                            .subscribeOn(Schedulers.io())
                            .map(exists -> exists ? Optional.of(explicitAccount.accountId()) : Optional.<it.niedermann.nextcloud.deck.domain.model.Account.ID>empty())
                            .map(AccountParsedArgs::new)
                            .onErrorReturnItem(new AccountParsedArgs(Optional.empty()))
            );
        } else {
            return FlowAdapters.toFlowPublisher(Flowable.just(new AccountParsedArgs(Optional.empty())));
        }
    }
}
