package it.niedermann.nextcloud.deck.javafx.services.scene;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.cards.DeleteCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import jakarta.inject.Inject;

public class ContextService extends Store<ContextService.State, ContextService.Action> {

    private static final Logger logger = Logger.getLogger(ContextService.class.getName());

    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;

    @Inject
    public ContextService(
            StoreLogger storeLogger,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            SetCurrentBoardUseCase setCurrentBoardUseCase,
            DeleteCardUseCase deleteCardUseCase
    ) {
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.setCurrentBoardUseCase = setCurrentBoardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;

        // TODO Write Factory for MainService and pass initialState
        super(storeLogger, new State(1L, 1L, null));

        on(SwitchAccountAction.class, (state, action) -> state.withAccountId(action.accountId()));
        on(DisplayBoardAction.class, (state, action) -> state.withBoardId(action.boardId()));
        on(EditCardAction.class, (state, action) -> state.withCardId(action.cardId()));
        on(CloseCardAction.class, (state, _) -> state.withCardId(null));

        effect(SwitchAccountAction.class, (_, action) -> {
            setCurrentAccountUseCase.execute(action.accountId());
            return CompletableFuture.completedFuture(Optional.empty());
        });

        effect(SwitchAccountAction.class, (state, action) -> {
            setCurrentAccountUseCase.execute(state.accountId());
            return Flowable.fromPublisher(this.getCurrentBoardUseCase.execute(state.accountId()))
                    .firstElement()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApplyAsync(board -> Optional.ofNullable(board)
                            .map(Board::id)
                            .map(DisplayBoardAction::new));
        });

        effect(DisplayBoardAction.class, (state, action) -> {
            setCurrentBoardUseCase.execute(state.accountId(), state.boardId());
            return CompletableFuture.completedFuture(Optional.empty());
        });

        effect(DeleteCardAction.class, (state, action) -> deleteCardUseCase.execute(action.cardId())
                .thenComposeAsync(_ -> {
                    if (Objects.equals(action.cardId(), state.cardId())) {
                        return CompletableFuture.completedFuture(Optional.of(new CloseCardAction()));
                    } else {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                }));
    }

    public record State(
            long accountId,
            Long boardId,
            Long cardId
    ) {

        public State withAccountId(Long accountId) {
            if (Objects.equals(accountId(), accountId)) {
                return this;
            }

            return new State(accountId, null, null);
        }

        public State withBoardId(Long boardId) {
            if (Objects.equals(boardId(), boardId)) {
                return this;
            }

            return new State(accountId(), boardId, null);
        }

        public State withCardId(Long cardId) {
            return new State(accountId(), boardId(), cardId);
        }
    }

    public sealed interface Action permits
            SwitchAccountAction,
            DisplayBoardAction,
            EditCardAction,
            CloseCardAction,
            DeleteCardAction {
    }

    public record SwitchAccountAction(long accountId) implements Action {
    }

    public record DisplayBoardAction(long boardId) implements Action {
    }

    public record EditCardAction(long cardId) implements Action {
    }

    public record CloseCardAction() implements Action {
    }

    public record DeleteCardAction(long cardId) implements Action {
    }
}
