package it.niedermann.nextcloud.deck.javafx.ui.editcard;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.CreateComment;
import it.niedermann.nextcloud.deck.domain.model.query.Attachment;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewActivity;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewComment;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.javafx.services.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.editcard.features.EditCardFeature;

public class EditCardService extends Store<EditCardService.State, EditCardService.Action> implements
        EditCardFeature.ViewModel {

    private static final Logger logger = Logger.getLogger(EditCardService.class.getName());

    private final ApplicationRouter applicationRouter;
    private final GetCardUseCase getCardUseCase;
    private final UpdateCardUseCase updateCardUseCase;
    private final GetColumnUseCase getColumnUseCase;
    private final GetBoardUseCase getBoardUseCase;
    private final ListAttachmentsUseCase listAttachmentsUseCase;
    private final ListPreviewCommentsUseCase listPreviewCommentsUseCase;
    private final ListPreviewActivitiesUseCase listPreviewActivitiesUseCase;
    private final AddCommentUseCase addCommentUseCase;

    private final Runnable onClose;

    @AssistedInject
    public EditCardService(
            StoreLogger storeLogger,
            ApplicationRouter applicationRouter,
            GetCardUseCase getCardUseCase,
            UpdateCardUseCase updateCardUseCase,
            GetColumnUseCase getColumnUseCase,
            GetBoardUseCase getBoardUseCase,
            ListAttachmentsUseCase listAttachmentsUseCase,
            ListPreviewCommentsUseCase listPreviewCommentsUseCase,
            ListPreviewActivitiesUseCase listPreviewActivitiesUseCase,
            AddCommentUseCase addCommentUseCase,
            @Assisted State initialState,
            @Assisted Runnable onClose
    ) {
        super(storeLogger, initialState);
        this.applicationRouter = applicationRouter;
        this.getCardUseCase = getCardUseCase;
        this.updateCardUseCase = updateCardUseCase;
        this.getColumnUseCase = getColumnUseCase;
        this.getBoardUseCase = getBoardUseCase;
        this.listAttachmentsUseCase = listAttachmentsUseCase;
        this.listPreviewCommentsUseCase = listPreviewCommentsUseCase;
        this.listPreviewActivitiesUseCase = listPreviewActivitiesUseCase;
        this.addCommentUseCase = addCommentUseCase;
        this.onClose = onClose;

        on(Action.Initialize.class, (_, action) -> action.initialState());
        on(Action.SelectCard.class, (state, action) -> new State(action.cardId(), state.standalone()));
    }

    @AssistedFactory
    public interface Factory {
        EditCardService create(State initialState, Runnable onClose);
    }

    @Override
    public Flowable<Card> getCard() {
        return getCardId()
                .switchMap(id -> Flowable.fromPublisher(getCardUseCase.execute(id)));
    }

    public Flowable<Board> getBoard() {
        return getCard()
                .switchMap(card -> Flowable.fromPublisher(getColumnUseCase.execute(card.columnId())))
                .switchMap(column -> Flowable.fromPublisher(getBoardUseCase.execute(column.boardId())));
    }

    @Override
    public Flowable<java.util.List<Attachment>> getAttachments() {
        return getCardId()
                .switchMap(id -> Flowable.fromPublisher(listAttachmentsUseCase.execute(id)));
    }

    @Override
    public Flowable<java.util.List<PreviewComment>> getComments() {
        return getCardId().switchMap(id -> Flowable.fromPublisher(listPreviewCommentsUseCase.execute(id)));
    }

    @Override
    public Flowable<java.util.List<PreviewActivity>> getActivities() {
        return getCardId().switchMap(id -> Flowable.fromPublisher(listPreviewActivitiesUseCase.execute(id)));
    }

    @Override
    public CompletableFuture<Void> onAddComment(String content) {
        return getCardId()
                .firstElement()
                .toCompletionStage()
                .toCompletableFuture()
                .thenComposeAsync(id -> addCommentUseCase.execute(new CreateComment(id, content)));
    }

    @Override
    public CompletableFuture<Void> onCardSaved(Card card) {
        return updateCardUseCase.execute(card).thenRunAsync(onClose);
    }

    @Override
    public void onCloseSidebar() {
        onClose.run();
    }

    @Override
    public Disposable onPopOut() {
        return getCardId()
                .firstElement()
                .subscribe(applicationRouter::launchEditCardStage);
    }

    @Override
    public Flowable<Boolean> isStandalone() {
        return Flowable.fromPublisher(getState())
                .map(State::standalone)
                .distinctUntilChanged();
    }

    @Override
    public Flowable<Card.ID> getCardId() {
        return Flowable.fromPublisher(getState())
                .map(State::cardId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .distinctUntilChanged();
    }

    @Override
    public Flowable<Board.Permissions> getPermissions() {
        return getBoard()
                .map(Board::permissions)
                .distinctUntilChanged();
    }

    public record State(Optional<Card.ID> cardId, boolean standalone) {
    }

    public sealed interface Action {
        record Initialize(State initialState) implements Action {
        }
        record SelectCard(Optional<Card.ID> cardId) implements Action {
        }
    }
}
