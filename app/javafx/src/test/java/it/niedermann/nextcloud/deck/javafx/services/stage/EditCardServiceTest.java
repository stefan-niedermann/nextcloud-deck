package it.niedermann.nextcloud.deck.javafx.services.stage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.subscribers.TestSubscriber;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.repository.MockData;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.javafx.services.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.EditCardService;

@ExtendWith(ApplicationExtension.class)
class EditCardServiceTest {

    private StoreLogger storeLogger;
    private ApplicationRouter applicationRouter;
    private GetCardUseCase getCardUseCase;
    private UpdateCardUseCase updateCardUseCase;
    private GetColumnUseCase getColumnUseCase;
    private GetBoardUseCase getBoardUseCase;
    private ListAttachmentsUseCase listAttachmentsUseCase;
    private ListPreviewCommentsUseCase listPreviewCommentsUseCase;
    private ListPreviewActivitiesUseCase listPreviewActivitiesUseCase;
    private AddCommentUseCase addCommentUseCase;
    private Runnable onClose;

    private EditCardService editCardService;

    @BeforeEach
    void setUp() {
        storeLogger = mock(StoreLogger.class);
        
        applicationRouter = mock(ApplicationRouter.class);
        
        getCardUseCase = mock(GetCardUseCase.class);
        updateCardUseCase = mock(UpdateCardUseCase.class);
        getColumnUseCase = mock(GetColumnUseCase.class);
        getBoardUseCase = mock(GetBoardUseCase.class);
        listAttachmentsUseCase = mock(ListAttachmentsUseCase.class);
        listPreviewCommentsUseCase = mock(ListPreviewCommentsUseCase.class);
        listPreviewActivitiesUseCase = mock(ListPreviewActivitiesUseCase.class);
        addCommentUseCase = mock(AddCommentUseCase.class);
        onClose = mock(Runnable.class);

        editCardService = new EditCardService(
                storeLogger,
                applicationRouter,
                getCardUseCase,
                updateCardUseCase,
                getColumnUseCase,
                getBoardUseCase,
                listAttachmentsUseCase,
                listPreviewCommentsUseCase,
                listPreviewActivitiesUseCase,
                addCommentUseCase,
                new EditCardService.State(Optional.empty(), false),
                onClose
        );
    }

    @Test
    void testInitialState() {
        final var testSubscriber = new TestSubscriber<EditCardService.State>();
        editCardService.getState().subscribe(testSubscriber);
        testSubscriber.assertValue(new EditCardService.State(Optional.empty(), false));
    }

    @Test
    void testGetCard() {
        final var card = MockData.MOCK_CARDS.get(0);
        final var cardId = card.id();
        when(getCardUseCase.execute(cardId)).thenReturn(Flowable.just(card));

        editCardService.dispatch(new EditCardService.Action.SelectCard(Optional.of(cardId)));

        final var testSubscriber = new TestSubscriber<Card>();
        editCardService.getCard().subscribe(testSubscriber);

        testSubscriber.assertValue(card);
    }

    @Test
    void testOnCardSaved() throws Exception {
        final var card = mock(Card.class);
        when(updateCardUseCase.execute(card)).thenReturn(CompletableFuture.completedFuture(null));

        editCardService.onCardSaved(card).get();

        verify(updateCardUseCase).execute(card);
        verify(onClose).run();
    }

    @Test
    void testOnPopOut() {
        final var cardId = MockData.MOCK_CARDS.get(0).id();
        
        editCardService.dispatch(new EditCardService.Action.SelectCard(Optional.of(cardId)));

        editCardService.onPopOut();

        verify(applicationRouter).launchEditCardStage(cardId);
    }
}
