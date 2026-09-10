package it.niedermann.nextcloud.deck.cli.commands.card;

import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardAddCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardDeleteCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardGetCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardListCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.CardUpdateCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.activity.CardActivityCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.assign.CardAssignCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment.CardAttachmentCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.copy.CardCopyCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.export.CardExportCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.move.CardMoveCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.search.CardSearchCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.unassign.CardUnassignCmd;
import it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment.CardCommentCmd;
import picocli.CommandLine.Command;

@Command(name = "card",
        mixinStandardHelpOptions = true,
        description = "Manage cards",
        subcommands = {
                CardActivityCmd.class,
                CardAddCmd.class,
                CardAssignCmd.class,
                CardAttachmentCmd.class,
                CardCommentCmd.class,
                CardCopyCmd.class,
                CardDeleteCmd.class,
                CardExportCmd.class,
                CardGetCmd.class,
                CardListCmd.class,
                CardMoveCmd.class,
                CardSearchCmd.class,
                CardUnassignCmd.class,
                CardUpdateCmd.class
        })
public class CardCmd {
}
