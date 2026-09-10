package it.niedermann.nextcloud.deck.cli.commands.board.subcommands.column;

import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.column.ColumnRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.UpdateColumnUseCase;
import jakarta.inject.Inject;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Command(name = "update",
        mixinStandardHelpOptions = true,
        description = "Update a column")
public class ColumnUpdateCmd implements Callable<Integer> {

    private static final Logger logger = Logger.getLogger(ColumnUpdateCmd.class.getName());

    @Option(names = "--localId", description = "Local ID of the column")
    Long localId;

    @Option(names = "--remoteId", description = "Remote ID of the column")
    Long remoteId;

    @Option(names = {"-t", "--title"}, description = "New title of the column")
    String title;

    @Option(names = {"-o", "--order"}, description = "New order of the column")
    Integer order;

    @Inject
    ColumnArgResolver columnArgResolver;

    @Inject
    GetColumnUseCase getColumnUseCase;

    @Inject
    UpdateColumnUseCase updateColumnUseCase;

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

            final var updatedColumn = new Column(
                    column.id(),
                    column.boardId(),
                    title != null ? title : column.title(),
                    order != null ? order : column.order(),
                    column.archived(),
                    column.deletedAt(),
                    column.localId(),
                    column.accountId(),
                    column.remoteId(),
                    column.status(),
                    column.lastModified(),
                    column.lastModifiedLocal(),
                    column.etag()
            );

            updateColumnUseCase.execute(updatedColumn).join();
            System.out.println("Column updated.");

            return 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            return 1;
        }
    }
}
