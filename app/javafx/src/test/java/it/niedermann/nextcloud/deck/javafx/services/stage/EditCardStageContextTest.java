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
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;

@ExtendWith(ApplicationExtension.class)
class EditCardStageContextTest {

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

    private EditCardStageContext editCardStageContext;

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

        editCardStageContext = new EditCardStageContext(
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
                new EditCardStageContext.State(Optional.empty(), false),
                onClose
        );
    }

    @Test
    void testInitialState() {
        final var testSubscriber = new TestSubscriber<EditCardStageContext.State>();
        editCardStageContext.getState().subscribe(testSubscriber);
        testSubscriber.assertValue(new EditCardStageContext.State(Optional.empty(), false));
    }

    @Test
    void testGetCard() {
        final var cardId = new Card.ID(1);
        final var card = mock(Card.class);
        when(getCardUseCase.execute(cardId)).thenReturn(Flowable.just(card));

        editCardStageContext.dispatch(new EditCardStageContext.Action.SelectCard(Optional.of(cardId)));

        final var testSubscriber = new TestSubscriber<Card>();
        editCardStageContext.getCard().subscribe(testSubscriber);

        testSubscriber.assertValue(card);
    }

    @Test
    void testOnCardSaved() throws Exception {
        final var card = mock(Card.class);
        when(updateCardUseCase.execute(card)).thenReturn(CompletableFuture.completedFuture(null));

        editCardStageContext.onCardSaved(card).get();

        verify(updateCardUseCase).execute(card);
        verify(onClose).run();
    }

    @Test
    void testOnPopOut() {
        final var cardId = new Card.ID(1);
        
        editCardStageContext.dispatch(new EditCardStageContext.Action.SelectCard(Optional.of(cardId)));

        editCardStageContext.onPopOut();

        verify(applicationRouter).launchEditCardStage(cardId);
    }
}
