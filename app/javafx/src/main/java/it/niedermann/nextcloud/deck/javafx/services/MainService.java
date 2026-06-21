package it.niedermann.nextcloud.deck.javafx.services;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Action;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import jakarta.inject.Inject;

public class MainService extends Store<MainService.State> {

    private static final Logger logger = Logger.getLogger(MainService.class.getName());

    private final SetCurrentBoardUseCase setCurrentBoardUseCase;

    @Inject
    public MainService(
            StoreLogger storeLogger,
            SetCurrentBoardUseCase setCurrentBoardUseCase
    ) {
        // TODO Write Factory for MainService and pass initialState
        super(storeLogger, new State(1L, 1L, null));
        this.setCurrentBoardUseCase = setCurrentBoardUseCase;
    }

    public record State(
            long accountId,
            Long boardId,
            Long cardId
    ) {

        public State withBoardId(Long boardId) {
            return new State(accountId(), boardId, Objects.equals(boardId(), boardId) ? cardId() : null);
        }

        public State withCardId(Long cardId) {
            return new State(accountId(), boardId(), cardId);
        }
    }

    public sealed interface MainAction extends Action permits
            OpenBoardAction,
            OpenCardAction,
            CloseCardAction {
    }

    public record OpenBoardAction(long boardId) implements MainAction {
    }

    public record OpenCardAction(long cardId) implements MainAction {
    }

    public record CloseCardAction() implements MainAction {
    }

    @Override
    protected State reduce(State state, Action action) {
        return switch (action) {

            case OpenBoardAction openBoardAction -> state.withBoardId(openBoardAction.boardId);
            case OpenCardAction openCardAction -> state.withCardId(openCardAction.cardId());
            case CloseCardAction _ -> state.withCardId(null);

            default -> state;
        };
    }

    @Override
    protected CompletableFuture<Optional<Action>> handleEffects(State state, Action action) {
        return switch (action) {

            case OpenBoardAction _ -> {
                logger.finer("Set current board to: " + state.accountId() + " / " + state.boardId());
                this.setCurrentBoardUseCase.execute(state.accountId(), state.boardId());
                yield CompletableFuture.completedFuture(Optional.empty());
            }

            default -> super.handleEffects(state, action);
        };
    }
}
