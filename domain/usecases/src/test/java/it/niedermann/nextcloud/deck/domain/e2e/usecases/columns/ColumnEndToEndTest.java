package it.niedermann.nextcloud.deck.domain.e2e.usecases.columns;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.repository.MockData;

public class ColumnEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Board boardA;

    @BeforeEach
    public void setup() throws IOException {
        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        final var boardTitle = randomUtil.randomize("boardForColumn");
        boardA = EndToEndUtil.createBoard(DEVICE_A, boardTitle);
    }

    @Test
    public void createColumn() {
        final var boardTitleA = boardA.title();
        final var columnTitle = randomUtil.randomize(MockData.MOCK_COLUMNS[0].title());
        final var createColumn = new CreateColumn(boardA.id(), columnTitle, 0);

        DEVICE_A.virtualDevice().getAddColumnUseCase().execute(createColumn).join();

        EndToEndUtil.assertColumnExists(DEVICE_A, boardA, columnTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardTitleA);
        EndToEndUtil.assertColumnExists(DEVICE_B, boardB, columnTitle);
    }

    @Test
    public void updateColumn() {
        final var boardTitleA = boardA.title();
        final var columnTitle = randomUtil.randomize(MockData.MOCK_COLUMNS[1].title());
        var column = EndToEndUtil.createColumn(DEVICE_A, boardA, columnTitle);

        synchronize(DEVICE_A);
        column = EndToEndUtil.getColumn(DEVICE_A, boardA, columnTitle);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardTitleA);
        EndToEndUtil.assertColumnExists(DEVICE_B, boardB, columnTitle);

        final var newTitle = randomUtil.randomize("updatedColumn");
        final var updatedColumn = new Column(column.id(), column.boardId(), newTitle, column.order(), column.archived(), column.deletedAt(), column.localId(), column.accountId(), column.remoteId(), column.status(), column.lastModified(), column.lastModifiedLocal(), column.etag());

        DEVICE_A.virtualDevice().getUpdateColumnUseCase().execute(updatedColumn).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertColumnExists(DEVICE_A, boardA, newTitle);
        EndToEndUtil.assertColumnExists(DEVICE_B, boardB, newTitle);
        EndToEndUtil.assertColumnDoesNotExist(DEVICE_B, boardB, columnTitle);
    }

    @Test
    public void deleteColumn() {
        final var boardTitleA = boardA.title();
        final var columnTitle = randomUtil.randomize(MockData.MOCK_COLUMNS[2].title());
        var column = EndToEndUtil.createColumn(DEVICE_A, boardA, columnTitle);

        synchronize(DEVICE_A);
        column = EndToEndUtil.getColumn(DEVICE_A, boardA, columnTitle);
        synchronize(DEVICE_B);
        final var boardB = EndToEndUtil.getBoard(DEVICE_B, boardTitleA);
        EndToEndUtil.assertColumnExists(DEVICE_B, boardB, columnTitle);

        DEVICE_A.virtualDevice().getDeleteColumnUseCase().execute(column.id()).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertColumnDoesNotExist(DEVICE_A, boardA, columnTitle);
        EndToEndUtil.assertColumnDoesNotExist(DEVICE_B, boardB, columnTitle);
    }
}
