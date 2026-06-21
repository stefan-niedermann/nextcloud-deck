package it.niedermann.nextcloud.deck.javafx.store;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.processors.BehaviorProcessor;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;

public abstract class Store<State> {

    private static final Logger logger = Logger.getLogger(Store.class.getName());

    private final StoreLogger storeLogger;
    protected final State initialState;
    private final BehaviorProcessor<State> state = BehaviorProcessor.create();
    private final Flowable<State> state$ = state
            .observeOn(JavaFxScheduler.platform())
            .distinctUntilChanged();

    protected Store(StoreLogger storeLogger,
                    State initialState) {
        this.storeLogger = storeLogger;
        this.initialState = initialState;
        this.state.onNext(initialState);
    }

    public Flow.Publisher<State> getState() {
        return this.state$;
    }

    public void dispatch(Action action) {
        final var oldState = this.state.getValue();

        // Reducers
        final var newState = reduce(oldState, action);

        // Effects
        handleEffects(newState, action)
                .whenCompleteAsync((resultingAction, exception) -> {

                    if (exception != null) {
                        throw new RuntimeException(exception);
                    }

                    resultingAction.ifPresent(this::dispatch);
                });

        this.state.onNext(newState);

        storeLogger.log(action, oldState, newState);
    }

    protected abstract State reduce(State state, Action action);

    protected CompletableFuture<Optional<Action>> handleEffects(State state, Action action) {
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
