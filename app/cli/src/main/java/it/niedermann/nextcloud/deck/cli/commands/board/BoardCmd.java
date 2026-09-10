package it.niedermann.nextcloud.deck.cli.commands.board;

import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardGetCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardListCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.BoardUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column.BoardColumnCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.export.BoardExportCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label.BoardLabelCmd;
import it.niedermann.nextcloud.deck.cli.commands.board.subcommands.share.BoardShareCmd;
import picocli.CommandLine.Command;

@Command(name = "board",
        mixinStandardHelpOptions = true,
        description = "Manage boards",
        subcommands = {
                BoardAddCmd.class,
                BoardColumnCmd.class,
                BoardDeleteCmd.class,
                BoardExportCmd.class,
                BoardGetCmd.class,
                BoardLabelCmd.class,
                BoardListCmd.class,
                BoardShareCmd.class,
                BoardUpdateCmd.class
        })
public class BoardCmd {
}
