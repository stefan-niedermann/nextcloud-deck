package it.niedermann.nextcloud.deck.javafx.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.BiFunction;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.processors.BehaviorProcessor;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;

public abstract class Store<TState, TAction> {

    private static final Logger logger = Logger.getLogger(Store.class.getName());

    private final StoreLogger storeLogger;

    protected TState initialState;
    private final BehaviorProcessor<TState> state = BehaviorProcessor.create();
    private final Flowable<TState> state$ = state
            .observeOn(JavaFxScheduler.platform())
            .distinctUntilChanged(TState::equals);

    private final Map<Class<?>, List<BiFunction<TState, TAction, TState>>> reducers = new HashMap<>();
    private final Map<Class<?>, List<BiFunction<TState, TAction, CompletableFuture<Optional<? extends TAction>>>>> effects = new HashMap<>();

    protected Store(StoreLogger storeLogger) {
        this(storeLogger, null);
    }

    protected Store(StoreLogger storeLogger, TState initialState) {
        this.storeLogger = storeLogger;

        if (initialState != null) {
            this.initialState = initialState;
            this.state.onNext(initialState);
        }
    }

    public final Flow.Publisher<TState> getState() {
        return this.state$;
    }

    protected final <ActionType extends TAction> void on(Class<ActionType> actionType, BiFunction<TState, ActionType, TState> reducer) {
        this.reducers.computeIfAbsent(actionType, _ -> new ArrayList<>());
        //noinspection unchecked
        this.reducers.get(actionType).add((BiFunction<TState, TAction, TState>) reducer);
    }

    protected final <ActionType extends TAction> void effect(Class<ActionType> actionType, BiFunction<TState, ActionType, CompletableFuture<Optional<? extends TAction>>> effect) {
        this.effects.computeIfAbsent(actionType, _ -> new ArrayList<>());
        //noinspection unchecked
        this.effects.get(actionType).add((BiFunction<TState, TAction, CompletableFuture<Optional<? extends TAction>>>) effect);
    }

    public final void dispatch(TAction action) {
        final var oldState = this.state.getValue();

        final var newState = reduce(oldState, action);
        handleEffects(newState, action);

        this.state.onNext(newState);
        storeLogger.log(action, oldState, newState);
    }

    private TState reduce(TState state, TAction action) {
        final var reducers = this.reducers.getOrDefault(action.getClass(), Collections.emptyList());

        for (final var reducer : reducers) {
            state = reducer.apply(state, action);
        }

        return state;
    }

    private CompletableFuture<Void> handleEffects(TState state, TAction action) {
        final var effects = this.effects.getOrDefault(action.getClass(), Collections.emptyList());
        final var futures = new ArrayList<>(effects.size());

        for (final var effect : effects) {
            final var future = effect.apply(state, action)
                    .whenCompleteAsync((resultingAction, exception) -> {
                        if (exception != null) {
                            throw new RuntimeException(exception);
                        }
                        resultingAction.ifPresent(this::dispatch);
                    });
            futures.add(future);
        }

        //noinspection SuspiciousToArrayCall
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}
