package it.niedermann.nextcloud.deck.javafx.ui.preferences.features;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;

public class AccountPreferencesService extends Store<AccountPreferencesService.State, AccountPreferencesService.Action> implements AccountPreferencesFeature.ViewModel {

    private static final Logger logger = Logger.getLogger(AccountPreferencesService.class.getName());

    private final Account account;
    private final KeyValueStore keyValueStore;

    @AssistedInject
    public AccountPreferencesService(
            StoreLogger storeLogger,
            KeyValueStore keyValueStore,
            @Assisted Account account
    ) {
        super(storeLogger, new State(false));
        this.account = account;
        this.keyValueStore = keyValueStore;

        on(Action.Initialize.class, (state, action) -> action.initialState());
        on(Action.SetBackgroundSync.class, (state, action) -> state.withBackgroundSync(action.enabled()));

        effect(Action.SetBackgroundSync.class, (_, action) ->
                keyValueStore.putBoolean(getKey("background_sync"), action.enabled())
                        .thenApplyAsync(_ -> Optional.empty())
        );

        addDisposable(
                Flowable.fromPublisher(keyValueStore.getBoolean(getKey("background_sync")))
                        .subscribeOn(Schedulers.virtual())
                        .observeOn(JavaFxScheduler.platform())
                        .subscribe(enabled -> dispatch(new Action.SetBackgroundSync(enabled)), throwable -> logger.log(Level.SEVERE, "Error while loading background sync preference for account " + account.username(), throwable))
        );
    }

    private String getKey(String key) {
        return account.username() + "." + key;
    }

    @Override
    public Account getAccount() {
        return account;
    }

    @Override
    public Flowable<Boolean> getBackgroundSync() {
        return Flowable.fromPublisher(getState()).map(State::backgroundSync);
    }

    @Override
    public void setBackgroundSync(boolean enabled) {
        dispatch(new Action.SetBackgroundSync(enabled));
    }

    @AssistedFactory
    public interface Factory {
        AccountPreferencesService create(Account account);
    }

    public record State(boolean backgroundSync) {
        public State withBackgroundSync(boolean backgroundSync) {
            return new State(backgroundSync);
        }
    }

    public sealed interface Action {
        record Initialize(State initialState) implements Action {}
        record SetBackgroundSync(boolean enabled) implements Action {}
    }
}
