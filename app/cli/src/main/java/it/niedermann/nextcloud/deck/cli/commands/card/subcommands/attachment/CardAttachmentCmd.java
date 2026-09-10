package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment;

import picocli.CommandLine.Command;

@Command(name = "attachment",
        mixinStandardHelpOptions = true,
        description = "Manage attachments",
        subcommands = {
                AttachmentAddCmd.class,
                AttachmentDownloadCmd.class,
                AttachmentListCmd.class
        })
public class CardAttachmentCmd {
}
