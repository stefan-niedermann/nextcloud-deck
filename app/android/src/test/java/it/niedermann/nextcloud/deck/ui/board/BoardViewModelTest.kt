package it.niedermann.nextcloud.deck.ui.board

import android.os.Looper
import io.reactivex.rxjava3.core.Flowable
import it.niedermann.nextcloud.deck.domain.model.Account
import it.niedermann.nextcloud.deck.domain.model.Card
import it.niedermann.nextcloud.deck.domain.model.Color
import it.niedermann.nextcloud.deck.domain.model.Column
import it.niedermann.nextcloud.deck.domain.model.FilterInformation
import it.niedermann.nextcloud.deck.domain.model.Label
import it.niedermann.nextcloud.deck.domain.model.User
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.AddCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.AssignCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.ListCardPreviewsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.MoveCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.cards.UnassignCardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.AddColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase
import it.niedermann.nextcloud.deck.domain.usecases.columns.ListColumnIDsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.labels.ListLabelsUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase
import it.niedermann.nextcloud.deck.domain.usecases.sync.ScheduleSyncUseCase
import it.niedermann.nextcloud.deck.domain.usecases.users.ListUsersUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.reactivestreams.FlowAdapters
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import java.util.concurrent.CompletableFuture

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class BoardViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock lateinit var listColumnIDsUseCase: ListColumnIDsUseCase
    @Mock lateinit var getColumnUseCase: GetColumnUseCase
    @Mock lateinit var listCardPreviewsUseCase: ListCardPreviewsUseCase
    @Mock lateinit var addCardUseCase: AddCardUseCase
    @Mock lateinit var assignCardUseCase: AssignCardUseCase
    @Mock lateinit var unassignCardUseCase: UnassignCardUseCase
    @Mock lateinit var addColumnUseCase: AddColumnUseCase
    @Mock lateinit var moveCardUseCase: MoveCardUseCase
    @Mock lateinit var listLabelsUseCase: ListLabelsUseCase
    @Mock lateinit var listUsersUseCase: ListUsersUseCase
    @Mock lateinit var getCurrentAccountUseCase: GetCurrentAccountUseCase
    @Mock lateinit var getAccountUseCase: GetAccountUseCase
    @Mock lateinit var setCurrentBoardUseCase: SetCurrentBoardUseCase
    @Mock lateinit var scheduleSyncUseCase: ScheduleSyncUseCase
    @Mock lateinit var keyValueStore: KeyValueStore

    private lateinit var viewModel: BoardViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        `when`(keyValueStore.getBoolean(any())).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(false)))
        `when`(getCurrentAccountUseCase.execute()).thenReturn(CompletableFuture.completedFuture(Account.ID(1L)))
        `when`(getAccountUseCase.execute(any())).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(mock<Account>())))
        `when`(listLabelsUseCase.execute(any())).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(emptySet<Label>())))
        `when`(listUsersUseCase.execute(any())).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(emptyList<User>())))
        `when`(setCurrentBoardUseCase.execute(any(), any())).thenReturn(CompletableFuture.completedFuture(null))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateFilter triggers card reload`() = runTest(testDispatcher) {
        val boardId = 1L
        val columnId = Column.ID(10L)
        val column = mock<Column> {
            `when`(it.id).thenReturn(columnId)
        }
        val filter = FilterInformation.EMPTY
        val card = PreviewCard(
            Card.ID(1L), null, "Test Card", "", emptySet(), emptySet(),
            0, 0, 0, false, false, 0, 0, null, null, Color(0)
        )

        `when`(listColumnIDsUseCase.execute(any())).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(listOf(columnId))))
        `when`(getColumnUseCase.execute(columnId)).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(column)))
        `when`(listCardPreviewsUseCase.execute(columnId, filter)).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(listOf(card))))

        viewModel = BoardViewModel(
            listColumnIDsUseCase, getColumnUseCase, listCardPreviewsUseCase,
            addCardUseCase, assignCardUseCase, unassignCardUseCase,
            addColumnUseCase, moveCardUseCase, listLabelsUseCase,
            listUsersUseCase, getCurrentAccountUseCase, getAccountUseCase,
            setCurrentBoardUseCase, scheduleSyncUseCase, keyValueStore
        )

        val job = launch { viewModel.cardsByColumn.collect {} }

        viewModel.loadBoard(boardId)
        
        // Wait for coroutines and looper
        advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()
        
        assertEquals("Columns should be loaded", 1, viewModel.columns.value.size)
        assertNotNull("Cards map should contain column ID", viewModel.cardsByColumn.value[columnId.value()])
        assertEquals(listOf(card), viewModel.cardsByColumn.value[columnId.value()])

        // Update filter
        val newFilter = FilterInformation(setOf(Label.ID(1L)), emptySet(), FilterInformation.DoneState.ALL, FilterInformation.DueDateFilter.ALL)
        `when`(listCardPreviewsUseCase.execute(columnId, newFilter)).thenReturn(FlowAdapters.toFlowPublisher(Flowable.just(emptyList<PreviewCard>())))
        
        viewModel.updateFilter(newFilter)
        advanceUntilIdle()
        shadowOf(Looper.getMainLooper()).idle()
        
        assertEquals(emptyList<PreviewCard>(), viewModel.cardsByColumn.value[columnId.value()])
        
        job.cancel()
    }
}
