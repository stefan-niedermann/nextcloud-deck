package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.comment;

import picocli.CommandLine.Command;

@Command(name = "comment",
        mixinStandardHelpOptions = true,
        description = "Manage comments",
        subcommands = {
                CommentAddCmd.class,
                CommentDeleteCmd.class,
                CommentListCmd.class,
                CommentUpdateCmd.class
        })
public class CardCommentCmd {
}
