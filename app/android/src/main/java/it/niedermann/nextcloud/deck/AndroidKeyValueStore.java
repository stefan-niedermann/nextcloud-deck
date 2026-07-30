package it.niedermann.nextcloud.deck;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.FlowAdapters;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Single;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;

public class AndroidKeyValueStore implements KeyValueStore {

    private final RxDataStore<Preferences> dataStore;

    public AndroidKeyValueStore(RxDataStore<Preferences> dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public CompletableFuture<Void> putString(@NotNull String key, @NotNull String value) {
        return update(PreferencesKeys.stringKey(key), value);
    }

    @Override
    public CompletableFuture<Void> putLong(@NotNull String key, long value) {
        return update(PreferencesKeys.longKey(key), value);
    }

    @Override
    public CompletableFuture<Void> putBoolean(@NotNull String key, boolean value) {
        return update(PreferencesKeys.booleanKey(key), value);
    }

    @NonNull
    @Override
    public Flow.Publisher<String> getString(@NotNull String key) {
        return FlowAdapters.toFlowPublisher(
                dataStore.data().map(prefs -> {
                    String value = prefs.get(PreferencesKeys.stringKey(key));
                    return value != null ? value : "";
                })
        );
    }

    @NonNull
    @Override
    public Flow.Publisher<Long> getLong(@NotNull String key) {
        return FlowAdapters.toFlowPublisher(
                dataStore.data().map(prefs -> {
                    Long value = prefs.get(PreferencesKeys.longKey(key));
                    return value != null ? value : -1L;
                })
        );
    }

    @NonNull
    @Override
    public Flow.Publisher<Boolean> getBoolean(@NotNull String key) {
        return FlowAdapters.toFlowPublisher(
                dataStore.data().map(prefs -> {
                    Boolean value = prefs.get(PreferencesKeys.booleanKey(key));
                    return value != null ? value : false;
                })
        );
    }

    @Override
    public CompletableFuture<Boolean> containsKey(@NotNull String key) {
        return dataStore.data()
                .firstOrError()
                .map(prefs -> prefs.contains(PreferencesKeys.stringKey(key)) ||
                        prefs.contains(PreferencesKeys.longKey(key)) ||
                        prefs.contains(PreferencesKeys.booleanKey(key)))
                .toCompletionStage()
                .toCompletableFuture();
    }

    @Override
    public CompletableFuture<Void> clear() {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePreferences = prefs.toMutablePreferences();
            mutablePreferences.clear();
            return Single.just(mutablePreferences);
        }).toCompletionStage().toCompletableFuture().thenApply(p -> null);
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull String key) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePreferences = prefs.toMutablePreferences();
            mutablePreferences.remove(PreferencesKeys.stringKey(key));
            mutablePreferences.remove(PreferencesKeys.longKey(key));
            mutablePreferences.remove(PreferencesKeys.booleanKey(key));
            return Single.just(mutablePreferences);
        }).toCompletionStage().toCompletableFuture().thenApply(p -> null);
    }

    private <T> CompletableFuture<Void> update(Preferences.Key<T> key, T value) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePreferences = prefs.toMutablePreferences();
            mutablePreferences.set(key, value);
            return Single.just(mutablePreferences);
        }).toCompletionStage().toCompletableFuture().thenApply(p -> null);
    }
}
