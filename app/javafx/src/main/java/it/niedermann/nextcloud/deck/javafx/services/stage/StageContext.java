package it.niedermann.nextcloud.deck.javafx.services.stage;

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
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import jakarta.inject.Inject;

@StageScope
public class StageContext extends Store<StageContext.State, StageContext.Action> {

    private static final Logger logger = Logger.getLogger(StageContext.class.getName());

    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final SetCurrentBoardUseCase setCurrentBoardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;

    @Inject
    public StageContext(
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
        super(storeLogger);

        on(Action.Initialize.class, (_, action) -> action.initialState());
        on(Action.SwitchAccountAction.class, (state, action) -> state.withAccountId(action.accountId()));
        on(Action.DisplayBoardAction.class, (state, action) -> state.withBoardId(action.boardId()));
        on(Action.EditCardAction.class, (state, action) -> state.withCardId(action.cardId()));
        on(Action.CloseCardAction.class, (state, _) -> state.withCardId(null));

        effect(Action.SwitchAccountAction.class, (_, action) -> {
            setCurrentAccountUseCase.execute(action.accountId());
            return CompletableFuture.completedFuture(Optional.empty());
        });

        effect(Action.SwitchAccountAction.class, (state, action) -> {
            final var accountId = state.accountId();
            if (accountId.isEmpty()) {
                return CompletableFuture.failedFuture(new IllegalStateException());
            }
            setCurrentAccountUseCase.execute(accountId.get());
            return Flowable.fromPublisher(this.getCurrentBoardUseCase.execute(accountId.get()))
                    .firstElement()
                    .toCompletionStage()
                    .toCompletableFuture()
                    .thenApplyAsync(board -> Optional.ofNullable(board)
                            .map(Board::id)
                            .map(Action.DisplayBoardAction::new));
        });

        effect(Action.DisplayBoardAction.class, (state, action) -> {
            final var accountId = state.accountId();
            final var boardId = state.boardId();
            if (accountId.isEmpty() || boardId.isEmpty()) {
                return CompletableFuture.failedFuture(new IllegalStateException());
            }
            setCurrentBoardUseCase.execute(accountId.get(), boardId.get());
            return CompletableFuture.completedFuture(Optional.empty());
        });

        effect(Action.DeleteCardAction.class, (state, action) -> deleteCardUseCase.execute(action.cardId())
                .thenComposeAsync(_ -> {
                    if (Objects.equals(action.cardId(), state.cardId().orElse(null))) {
                        return CompletableFuture.completedFuture(Optional.of(new Action.CloseCardAction()));
                    } else {
                        return CompletableFuture.completedFuture(Optional.empty());
                    }
                }));
    }

    public record State(
            Optional<Long> accountId,
            Optional<Long> boardId,
            Optional<Long> cardId
    ) {

        public State withAccountId(Long accountId) {
            if (Objects.equals(accountId().orElse(null), accountId)) {
                return this;
            }

            return new State(Optional.ofNullable(accountId), Optional.empty(), Optional.empty());
        }

        public State withBoardId(Long boardId) {
            if (Objects.equals(boardId().orElse(null), boardId)) {
                return this;
            }

            return new State(accountId(), Optional.ofNullable(boardId), Optional.empty());
        }

        public State withCardId(Long cardId) {
            return new State(accountId(), boardId(), Optional.ofNullable(cardId));
        }
    }

    public sealed interface Action {

        record Initialize(State initialState) implements Action {
        }

        record SwitchAccountAction(long accountId) implements Action {
        }

        record DisplayBoardAction(long boardId) implements Action {
        }

        record EditCardAction(long cardId) implements Action {
        }

        record CloseCardAction() implements Action {
        }

        record DeleteCardAction(long cardId) implements Action {
        }
    }
}
