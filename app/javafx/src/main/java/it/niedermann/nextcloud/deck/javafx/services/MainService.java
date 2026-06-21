package it.niedermann.nextcloud.deck.javafx.services;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Action;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import jakarta.inject.Inject;

public class MainService extends Store<MainService.State> {

    private static final Logger logger = Logger.getLogger(MainService.class.getName());

    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;

    @Inject
    public MainService(
            StoreLogger storeLogger,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            GetCurrentBoardUseCase getCurrentBoardUseCase,
            SetCurrentBoardUseCase setCurrentBoardUseCase
    ) {
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.setCurrentBoardUseCase = setCurrentBoardUseCase;

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
            OpenBoardAction,
            OpenCardAction,
            CloseCardAction {
    }

    public record SwitchAccountAction(long accountId) implements MainAction {
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

            case SwitchAccountAction switchAccountAction ->
                    state.withAccountId(switchAccountAction.accountId());
            case OpenBoardAction openBoardAction -> state.withBoardId(openBoardAction.boardId);
            case OpenCardAction openCardAction -> state.withCardId(openCardAction.cardId());
            case CloseCardAction _ -> state.withCardId(null);

            default -> state;
        };
    }

    @Override
    protected CompletableFuture<Optional<Action>> handleEffects(State state, Action action) {
        return switch (action) {

            case SwitchAccountAction switchAccountAction -> {
                final var persistCurrentAccountCf = this.setCurrentAccountUseCase.execute(state.accountId());
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
                                        .map(OpenBoardAction::new);
                            }
                        });
            }
            case OpenBoardAction _ -> {
                this.setCurrentBoardUseCase.execute(state.accountId(), state.boardId());
                yield CompletableFuture.completedFuture(Optional.empty());
            }

            default -> super.handleEffects(state, action);
        };
    }
}
