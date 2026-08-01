package it.niedermann.nextcloud.deck.app.shared.data;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.function.Supplier;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;

public class PreferencesKeyValueStore implements KeyValueStore {

    private final Preferences prefs;

    private final Map<String, Flow.Publisher<?>> flowableValuesByKey = new HashMap<>();

    public PreferencesKeyValueStore(Preferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public CompletableFuture<Void> putString(@NotNull String key, @NotNull String value) {
        prefs.put(key, value);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> putLong(@NotNull String key, long value) {
        prefs.putLong(key, value);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> putBoolean(@NotNull String key, boolean value) {
        prefs.putBoolean(key, value);
        return CompletableFuture.completedFuture(null);
    }

    @NonNull
    @Override
    public Flow.Publisher<String> getString(@NotNull String key) {
        //noinspection unchecked
        return (Flow.Publisher<String>) flowableValuesByKey.computeIfAbsent(key, k -> get(key, () -> prefs.get(key, null)));
    }

    @NonNull
    @Override
    public Flow.Publisher<Long> getLong(@NotNull String key) {
        //noinspection unchecked
        return (Flow.Publisher<Long>) flowableValuesByKey.computeIfAbsent(key, k -> get(key, () -> prefs.getLong(key, -1L)));
    }

    @NonNull
    @Override
    public Flow.Publisher<Boolean> getBoolean(@NotNull String key) {
        //noinspection unchecked
        return (Flow.Publisher<Boolean>) flowableValuesByKey.computeIfAbsent(key, k -> get(key, () -> prefs.getBoolean(key, false)));
    }

    @Override
    public CompletableFuture<Boolean> containsKey(@NotNull String key) {
        try {
            for (var k : prefs.keys()) {
                if (Objects.equals(k, key)) {
                    return CompletableFuture.completedFuture(true);
                }
            }

            return CompletableFuture.completedFuture(false);

        } catch (BackingStoreException e) {
            final var future = new CompletableFuture<Boolean>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> clear() {
        try {
            prefs.clear();
            return CompletableFuture.completedFuture(null);
        } catch (BackingStoreException e) {
            final var future = new CompletableFuture<Void>();
            future.completeExceptionally(e);
            return future;
        }
    }

    @Override
    public CompletableFuture<Void> remove(@NotNull String key) {
        prefs.remove(key);
        return CompletableFuture.completedFuture(null);
    }

    private <T> Flow.Publisher<T> get(String key, Supplier<T> supplier) {
        final var result = Flowable.<T>create(emitter -> {

                    final var newValue = supplier.get();
                    if (newValue != null) {
                        emitter.onNext(newValue);
                    }

                    final var listener = new PreferenceChangeListener() {
                        @Override
                        public void preferenceChange(PreferenceChangeEvent event) {

                            if (emitter.isCancelled() || !Objects.equals(event.getKey(), key)) {
                                return;
                            }

                            final var newValue = event.getNewValue();
                            if (newValue != null) {
                                emitter.onNext(supplier.get());
                            }
                        }
                    };

                    prefs.addPreferenceChangeListener(listener);
                    emitter.setCancellable(() -> prefs.removePreferenceChangeListener(listener));

                }, BackpressureStrategy.LATEST)
                .distinctUntilChanged();

        return FlowAdapters.toFlowPublisher(result);
    }
}
