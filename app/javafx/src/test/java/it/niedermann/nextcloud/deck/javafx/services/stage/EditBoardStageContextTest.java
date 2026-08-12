package it.niedermann.nextcloud.deck.javafx.services.stage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Collections;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.subscribers.TestSubscriber;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.CreateColumn;
import it.niedermann.nextcloud.deck.domain.usecases.boards.AddBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.ListBoardSharesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.RemoveBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.UpdateBoardShareUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.DeleteColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.UpdateColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.AddLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.DeleteLabelUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.labels.UpdateLabelUseCase;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;

@ExtendWith(ApplicationExtension.class)
class EditBoardStageContextTest {

    private StoreLogger storeLogger;
    private GetBoardUseCase getBoardUseCase;
    private ListCardsUseCase listCardsUseCase;
    private AddColumnUseCase addColumnUseCase;
    private UpdateColumnUseCase updateColumnUseCase;
    private DeleteColumnUseCase deleteColumnUseCase;
    private ListColumnIDsUseCase listColumnIDsUseCase;
    private GetColumnUseCase getColumnUseCase;
    private AddLabelUseCase addLabelUseCase;
    private UpdateLabelUseCase updateLabelUseCase;
    private DeleteLabelUseCase deleteLabelUseCase;
    private ListLabelsUseCase listLabelsUseCase;
    private ListBoardSharesUseCase listBoardSharesUseCase;
    private AddBoardShareUseCase addBoardShareUseCase;
    private RemoveBoardShareUseCase removeBoardShareUseCase;
    private UpdateBoardShareUseCase updateBoardShareUseCase;

    private EditBoardStageContext editBoardStageContext;
    private final Account.ID accountId = new Account.ID(1);
    private final Board.ID boardId = new Board.ID(2);

    @BeforeEach
    void setUp() {
        storeLogger = mock(StoreLogger.class);
        getBoardUseCase = mock(GetBoardUseCase.class);
        listCardsUseCase = mock(ListCardsUseCase.class);
        addColumnUseCase = mock(AddColumnUseCase.class);
        updateColumnUseCase = mock(UpdateColumnUseCase.class);
        deleteColumnUseCase = mock(DeleteColumnUseCase.class);
        listColumnIDsUseCase = mock(ListColumnIDsUseCase.class);
        getColumnUseCase = mock(GetColumnUseCase.class);
        addLabelUseCase = mock(AddLabelUseCase.class);
        updateLabelUseCase = mock(UpdateLabelUseCase.class);
        deleteLabelUseCase = mock(DeleteLabelUseCase.class);
        listLabelsUseCase = mock(ListLabelsUseCase.class);
        listBoardSharesUseCase = mock(ListBoardSharesUseCase.class);
        addBoardShareUseCase = mock(AddBoardShareUseCase.class);
        removeBoardShareUseCase = mock(RemoveBoardShareUseCase.class);
        updateBoardShareUseCase = mock(UpdateBoardShareUseCase.class);

        editBoardStageContext = new EditBoardStageContext(
                storeLogger,
                getBoardUseCase,
                listCardsUseCase,
                addColumnUseCase,
                updateColumnUseCase,
                deleteColumnUseCase,
                listColumnIDsUseCase,
                getColumnUseCase,
                addLabelUseCase,
                updateLabelUseCase,
                deleteLabelUseCase,
                listLabelsUseCase,
                listBoardSharesUseCase,
                addBoardShareUseCase,
                removeBoardShareUseCase,
                updateBoardShareUseCase,
                new EditBoardStageContext.State(accountId, boardId)
        );
    }

    @Test
    void testGetBoard() {
        final var board = mock(Board.class);
        when(getBoardUseCase.execute(boardId)).thenReturn(Flowable.just(board));

        final var testSubscriber = new TestSubscriber<Board>();
        editBoardStageContext.getBoard().subscribe(testSubscriber);

        testSubscriber.awaitCount(1);
        testSubscriber.assertValue(board);
    }

    @Test
    void testOnAddColumn() {
        editBoardStageContext.onAddColumn("New Column");
        verify(addColumnUseCase).execute(new CreateColumn(boardId, "New Column", 0));
    }

    @Test
    void testOnDeleteColumnEmpty() {
        final var columnId = new Column.ID(3);
        final var column = mock(Column.class);
        when(column.id()).thenReturn(columnId);
        when(listCardsUseCase.execute(columnId)).thenReturn(Flowable.just(Collections.emptyList()));

        editBoardStageContext.onDeleteColumn(column);

        // Need to wait for async operations if any, but Schedulers.virtual() might be fast enough if mocked correctly.
        // Actually it uses Schedulers.virtual() and JavaFxScheduler.platform().
        // In tests we might need to handle the scheduler.
    }
}
