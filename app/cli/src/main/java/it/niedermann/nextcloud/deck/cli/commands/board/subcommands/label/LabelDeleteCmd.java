package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.label.LabelArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.label.LabelRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.usecases.labels.DeleteLabelUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "delete",
        mixinStandardHelpOptions = true,
        description = "Delete a label")
public class LabelDeleteCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(LabelDeleteCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the label")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the label")
    Long remoteId;

    @Inject
    LabelArgResolver labelArgResolver;

    @Inject
    DeleteLabelUseCase deleteLabelUseCase;

    @Override
    public Integer call() {
        try {
            final Label.ID labelId;
            if (localId != null) {
                labelId = new Label.ID(localId);
            } else if (remoteId != null) {
                final var parsedArgs = Maybe.fromPublisher(labelArgResolver.resolve(new LabelRawArgs.RemoteLabel(new Label.RemoteID(remoteId)))).blockingGet();
                labelId = parsedArgs.labelId();
            } else {
                System.err.println("Provide either --localId or --remoteId");
                return 2;
            }

            deleteLabelUseCase.execute(labelId).join();
            System.out.println("Label deleted.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
