package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "get",
        mixinStandardHelpOptions = true,
        description = "Get a column by its ID")
public class ColumnGetCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ColumnGetCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the column")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the column")
    Long remoteId;

    @Inject
    ColumnArgResolver columnArgResolver;

    @Inject
    GetColumnUseCase getColumnUseCase;

    @Override
    public Integer call() {
        try {
            final Column.ID columnId;
            if (localId != null) {
                columnId = new Column.ID(localId);
            } else if (remoteId != null) {
                final var parsedArgs = Maybe.fromPublisher(columnArgResolver.resolve(new ColumnRawArgs.RemoteColumn(new Column.RemoteID(remoteId)))).blockingGet();
                columnId = parsedArgs.columnId();
            } else {
                System.err.println("Provide either --localId or --remoteId");
                return 2;
            }

            final var column = Maybe.fromPublisher(getColumnUseCase.execute(columnId)).blockingGet();
            System.out.println(column);

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
