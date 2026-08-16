package it.niedermann.nextcloud.deck.javafx.services.application;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxScope;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;

@FxScope
public class ExceptionService {

    public static final String KEY_DEBUG_MODE = "debug_mode";

    private final Flowable<Boolean> debugMode;
    private boolean currentDebugMode = false;

    @Inject
    public ExceptionService(KeyValueStore keyValueStore) {
        this.debugMode = Flowable.fromPublisher(keyValueStore.getBoolean(KEY_DEBUG_MODE))
                .distinctUntilChanged()
                .replay(1)
                .refCount();
        this.debugMode
                .observeOn(JavaFxScheduler.platform())
                .subscribe(mode -> this.currentDebugMode = mode);
    }

    public Flowable<Boolean> debugMode() {
        return debugMode;
    }

    public boolean isDebugMode() {
        return currentDebugMode;
    }
}
