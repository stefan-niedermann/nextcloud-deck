package it.niedermann.nextcloud.deck.javafx.ui.preferences;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import io.soabase.recordbuilder.core.RecordBuilder;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ExceptionService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;

public class PreferencesService extends Store<PreferencesService.State, PreferencesService.Action> {

    private static final Logger logger = Logger.getLogger(PreferencesService.class.getName());

    public static final String KEY_COMPACT_MODE = "compact_mode";

    private final KeyValueStore keyValueStore;

    @AssistedInject
    public PreferencesService(
            StoreLogger storeLogger,
            KeyValueStore keyValueStore,
            GetAccountsUseCase getAccountsUseCase,
            @Assisted State initialState
    ) {
        super(storeLogger, initialState);
        this.keyValueStore = keyValueStore;

        on(Action.Initialize.class, (state, action) -> action.initialState());
        on(Action.SetTheme.class, (state, action) -> state.withTheme(action.theme()));
        on(Action.SetCompactMode.class, (state, action) -> state.withCompactMode(action.enabled()));
        on(Action.SetDebugMode.class, (state, action) -> state.withDebugMode(action.enabled()));
        on(Action.SetAccounts.class, (state, action) -> state.withAccounts(action.accounts()));
        on(Action.SwitchSection.class, (state, action) -> state.withSelectedSection(action.section()).withSelectedAccount(action.account()));

        effect(Action.SetTheme.class, (_, action) ->
                keyValueStore.putString(ThemeService.KEY_THEME, action.theme().name())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        effect(Action.SetCompactMode.class, (_, action) ->
                keyValueStore.putBoolean(KEY_COMPACT_MODE, action.enabled())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        effect(Action.SetDebugMode.class, (_, action) ->
                keyValueStore.putBoolean(ExceptionService.KEY_DEBUG_MODE, action.enabled())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        addDisposable(
                Flowable.fromPublisher(keyValueStore.getString(ThemeService.KEY_THEME))
                        .subscribeOn(Schedulers.virtual())
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(s -> dispatch(new Action.SetTheme(ThemeService.Theme.fromName(s))), throwable -> logger.log(Level.SEVERE, "Error while loading theme preference", throwable)),

                Flowable.fromPublisher(keyValueStore.getBoolean(KEY_COMPACT_MODE))
                        .subscribeOn(Schedulers.virtual())
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(enabled -> dispatch(new Action.SetCompactMode(enabled)), throwable -> logger.log(Level.SEVERE, "Error while loading compact mode preference", throwable)),

                Flowable.fromPublisher(keyValueStore.getBoolean(ExceptionService.KEY_DEBUG_MODE))
                        .subscribeOn(Schedulers.virtual())
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(enabled -> dispatch(new Action.SetDebugMode(enabled)), throwable -> logger.log(Level.SEVERE, "Error while loading debug mode preference", throwable)),

                Flowable.fromPublisher(getAccountsUseCase.execute())
                        .subscribeOn(Schedulers.virtual())
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(accounts -> dispatch(new Action.SetAccounts(accounts)), throwable -> logger.log(Level.SEVERE, "Error while loading accounts", throwable))
        );

        // Handle defaults for keys that don't exist yet
        keyValueStore.containsKey(ExceptionService.KEY_DEBUG_MODE).thenAccept(exists -> {
            if (!exists) {
                dispatch(new Action.SetDebugMode(false));
            }
        });

        // Store disposables if context is ever disposed, although StageManager handles controller disposal.
    }

    @AssistedFactory
    public interface Factory {
        PreferencesService create(State initialState);
    }

    public enum Section {
        GENERAL, ACCOUNT
    }

    @RecordBuilder
    public record State(
            ThemeService.Theme theme,
            boolean compactMode,
            boolean debugMode,
            Collection<Account> accounts,
            Section selectedSection,
            Optional<Account> selectedAccount
    ) implements PreferencesServiceStateBuilder.With {
        public State() {
            this(ThemeService.Theme.AUTO,
                    false,
                    false,
                    Collections.emptyList(),
                    Section.GENERAL,
                    Optional.empty());
        }
    }

    public sealed interface Action {
        record Initialize(State initialState) implements Action {
        }

        record SetTheme(ThemeService.Theme theme) implements Action {
        }

        record SetCompactMode(boolean enabled) implements Action {
        }

        record SetDebugMode(boolean enabled) implements Action {
        }

        record SetAccounts(Collection<Account> accounts) implements Action {
        }

        record SwitchSection(Section section, Optional<Account> account) implements Action {
        }
    }
}
