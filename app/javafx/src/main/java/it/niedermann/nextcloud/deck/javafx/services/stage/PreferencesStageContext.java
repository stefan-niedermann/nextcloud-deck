package it.niedermann.nextcloud.deck.javafx.services.stage;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.services.application.Theme;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;

public class PreferencesStageContext extends Store<PreferencesStageContext.State, PreferencesStageContext.Action> {

    private static final Logger logger = Logger.getLogger(PreferencesStageContext.class.getName());

    public static final String KEY_BACKGROUND_SYNC = "background_sync";
    public static final String KEY_COMPACT_MODE = "compact_mode";

    private final KeyValueStore keyValueStore;

    @AssistedInject
    public PreferencesStageContext(
            StoreLogger storeLogger,
            KeyValueStore keyValueStore,
            @Assisted State initialState
    ) {
        super(storeLogger, initialState);
        this.keyValueStore = keyValueStore;

        on(Action.Initialize.class, (state, action) -> action.initialState());
        on(Action.SetTheme.class, (state, action) -> state.withTheme(action.theme()));
        on(Action.SetBackgroundSync.class, (state, action) -> state.withBackgroundSync(action.enabled()));
        on(Action.SetCompactMode.class, (state, action) -> state.withCompactMode(action.enabled()));

        effect(Action.SetTheme.class, (_, action) ->
                keyValueStore.putString(ThemeService.KEY_THEME, action.theme().name())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        effect(Action.SetBackgroundSync.class, (_, action) ->
                keyValueStore.putBoolean(KEY_BACKGROUND_SYNC, action.enabled())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        effect(Action.SetCompactMode.class, (_, action) ->
                keyValueStore.putBoolean(KEY_COMPACT_MODE, action.enabled())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        final var themeDisposable = Flowable.fromPublisher(keyValueStore.getString(ThemeService.KEY_THEME))
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(s -> dispatch(new Action.SetTheme(Theme.fromName(s))), throwable -> logger.log(Level.SEVERE, "Error while loading theme preference", throwable));

        final var syncDisposable = Flowable.fromPublisher(keyValueStore.getBoolean(KEY_BACKGROUND_SYNC))
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(enabled -> dispatch(new Action.SetBackgroundSync(enabled)), throwable -> logger.log(Level.SEVERE, "Error while loading background sync preference", throwable));

        final var compactDisposable = Flowable.fromPublisher(keyValueStore.getBoolean(KEY_COMPACT_MODE))
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(enabled -> dispatch(new Action.SetCompactMode(enabled)), throwable -> logger.log(Level.SEVERE, "Error while loading compact mode preference", throwable));

        // Handle defaults for keys that don't exist yet
        keyValueStore.containsKey(KEY_BACKGROUND_SYNC).thenAccept(exists -> {
            if (!exists) {
                dispatch(new Action.SetBackgroundSync(true));
            }
        });

        // Store disposables if context is ever disposed, although StageManager handles controller disposal.
    }

    @AssistedFactory
    public interface Factory {
        PreferencesStageContext create(State initialState);
    }

    public record State(Theme theme, boolean backgroundSync, boolean compactMode) {
        public State() {
            this(Theme.AUTO, true, false);
        }

        public State withTheme(Theme theme) {
            return new State(theme, backgroundSync, compactMode);
        }

        public State withBackgroundSync(boolean enabled) {
            return new State(theme, enabled, compactMode);
        }

        public State withCompactMode(boolean enabled) {
            return new State(theme, backgroundSync, enabled);
        }
    }

    public sealed interface Action {
        record Initialize(State initialState) implements Action {
        }

        record SetTheme(Theme theme) implements Action {
        }

        record SetBackgroundSync(boolean enabled) implements Action {
        }

        record SetCompactMode(boolean enabled) implements Action {
        }
    }
}
