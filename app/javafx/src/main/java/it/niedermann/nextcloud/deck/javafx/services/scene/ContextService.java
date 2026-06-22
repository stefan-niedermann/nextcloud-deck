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
import it.niedermann.nextcloud.deck.javafx.store.Action;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import jakarta.inject.Inject;

public class ContextService extends Store<ContextService.State> {

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

    public sealed interface MainAction extends Action permits
            SwitchAccountAction,
            DisplayBoardAction,
            EditCardAction,
            CloseCardAction,
            DeleteCardAction {
    }

    public record SwitchAccountAction(long accountId) implements MainAction {
    }

    public record DisplayBoardAction(long boardId) implements MainAction {
    }

    public record EditCardAction(long cardId) implements MainAction {
    }

    public record CloseCardAction() implements MainAction {
    }

    public record DeleteCardAction(long cardId) implements MainAction {
    }

    @Override
    protected State reduce(State state, Action action) {
        return switch (action) {

            case SwitchAccountAction switchAccountAction ->
                    state.withAccountId(switchAccountAction.accountId());
            case DisplayBoardAction displayBoardAction -> state.withBoardId(displayBoardAction.boardId);
            case EditCardAction editCardAction -> state.withCardId(editCardAction.cardId());
            case CloseCardAction _ -> state.withCardId(null);

            default -> state;
        };
    }

    @Override
    protected CompletableFuture<Optional<Action>> handleEffects(State state, Action action) {
        return switch (action) {

            case SwitchAccountAction switchAccountAction -> {
                setCurrentAccountUseCase.execute(state.accountId());
                yield Flowable.fromPublisher(this.getCurrentBoardUseCase.execute(state.accountId()))
                        .firstElement()
                        .toCompletionStage()
                        .toCompletableFuture()
                        .handleAsync((board, exception) -> {
                            if (exception == null) {
                                return Optional.<Action>empty();

                            } else {
                                return Optional.ofNullable(board)
                                        .map(Board::id)
                                        .map(DisplayBoardAction::new);
                            }
                        });
            }
            case DisplayBoardAction _ -> {
                setCurrentBoardUseCase.execute(state.accountId(), state.boardId());
                yield CompletableFuture.completedFuture(Optional.empty());
            }
            case DeleteCardAction deleteCardAction -> {
                deleteCardUseCase.execute(deleteCardAction.cardId());
                if (Objects.equals(deleteCardAction.cardId(), state.cardId())) {
                    yield CompletableFuture.completedFuture(Optional.of(new CloseCardAction()));
                } else {
                    yield CompletableFuture.completedFuture(Optional.empty());
                }
            }

            default -> super.handleEffects(state, action);
        };
    }
}
