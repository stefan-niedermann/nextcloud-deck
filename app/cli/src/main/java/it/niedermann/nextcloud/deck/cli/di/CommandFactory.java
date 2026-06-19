package it.niedermann.nextcloud.deck.cli.di;

import it.niedermann.nextcloud.deck.cli.commands.RootCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.AccountCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountListCmd;
import it.niedermann.nextcloud.deck.cli.commands.account.subcommands.AccountRemoveCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.BoardCmd;
import it.niedermann.nextcloud.deck.cli.commands.reset.ResetCmd;
import picocli.CommandLine;

public class CommandFactory implements CommandLine.IFactory {

    private final AppComponent appComponent;

    public CommandFactory(AppComponent appComponent) {
        this.appComponent = appComponent;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        final K cmd = CommandLine.defaultFactory().create(cls);

        if (cls == RootCmd.class) {
            // Nothing to do :)

        } else if (cls == AccountCmd.class) {
            appComponent.inject((AccountCmd) cmd);
        } else if (cls == AccountAddCmd.class) {
            appComponent.inject((AccountAddCmd) cmd);
        } else if (cls == AccountListCmd.class) {
            appComponent.inject((AccountListCmd) cmd);
        } else if (cls == AccountRemoveCmd.class) {
            appComponent.inject((AccountRemoveCmd) cmd);

        } else if (cls == BoardCmd.class) {
            appComponent.inject((BoardCmd) cmd);
            
        } else if (cls == ResetCmd.class) {
            appComponent.inject((ResetCmd) cmd);

        } else {
            System.out.println("Warn: No Injection for Class " + cls.getName());
        }

        return cmd;
    }
}
