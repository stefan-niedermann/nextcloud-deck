package it.niedermann.nextcloud.deck.cli.commands.card.subcommands.attachment;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.DownloadAttachmentUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "download",
        mixinStandardHelpOptions = true,
        description = "Download an attachment")
public class AttachmentDownloadCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(AttachmentDownloadCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the attachment", required = true)
    Long localId;

    @Inject
    DownloadAttachmentUseCase downloadAttachmentUseCase;

    @Override
    public Integer call() {
        try {
            Maybe.fromPublisher(downloadAttachmentUseCase.execute(new Attachment.ID(localId))).blockingGet();
            System.out.println("Download triggered.");
            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
