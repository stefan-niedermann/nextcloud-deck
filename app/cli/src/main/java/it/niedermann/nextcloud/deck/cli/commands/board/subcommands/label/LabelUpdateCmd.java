package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.label;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.label.LabelArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.label.LabelRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import it.niedermann.nextcloud.deck.domain.usecases.labels.UpdateLabelUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "Update a label")
public class LabelUpdateCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(LabelUpdateCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the label")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the label")
    Long remoteId;

    @Option(names = {"-t", "--title"}, description = "New title of the label")
    String title;

    @Option(names = {"-c", "--color"}, description = "New color of the label (hex)")
    String colorHex;

    @Inject
    LabelArgResolver labelArgResolver;

    @Inject
    LabelRepository labelRepository;

    @Inject
    UpdateLabelUseCase updateLabelUseCase;

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

            final var label = Maybe.fromPublisher(labelRepository.getLabel(labelId)).blockingGet().iterator().next();

            final Color newColor;
            if (colorHex != null) {
                final int r = Integer.valueOf(colorHex.substring(0, 2), 16);
                final int g = Integer.valueOf(colorHex.substring(2, 4), 16);
                final int b = Integer.valueOf(colorHex.substring(4, 6), 16);
                newColor = new Color(r, g, b);
            } else {
                newColor = label.color();
            }

            final var updatedLabel = new Label(
                    label.id(),
                    label.boardId(),
                    title != null ? title : label.title(),
                    newColor,
                    label.remoteId(),
                    label.status(),
                    label.lastModified(),
                    label.lastModifiedLocal(),
                    label.etag()
            );

            updateLabelUseCase.execute(updatedLabel).join();
            System.out.println("Label updated.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
