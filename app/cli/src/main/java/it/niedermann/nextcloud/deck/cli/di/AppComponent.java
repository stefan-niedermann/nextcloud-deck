package it.niedermann.nextcloud.deck.cli.di;


import dagger.BindsInstance;
import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule;
import it.niedermann.nextcloud.deck.cli.commands.account.AccountCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountListCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountRemoveCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.BoardCmd;
import it.niedermann.nextcloud.deck.cli.commands.reset.ResetCmd;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;

@Component(modules = SharedModule.class)
public interface AppComponent {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance DeckDatabase database,
                            @BindsInstance KeyValueStore keyValueStore);
    }

    void inject(AccountCmd cmd);

    void inject(AccountAddCmd cmd);

    void inject(AccountListCmd cmd);

    void inject(AccountRemoveCmd cmd);

    void inject(BoardCmd cmd);

    void inject(ResetCmd cmd);
}
