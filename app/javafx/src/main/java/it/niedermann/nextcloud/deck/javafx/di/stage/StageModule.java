package it.niedermann.nextcloud.deck.javafx.di.stage;

import java.util.Optional;

import dagger.Binds;
import dagger.Module;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.account.AccountArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.account.AccountParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.account.AccountRawArgs;

@Module
public interface StageModule {
    @Binds
    ArgsResolver<AccountRawArgs, AccountParsedArgs> bindAccountArgResolver(AccountArgResolver resolver);
}
