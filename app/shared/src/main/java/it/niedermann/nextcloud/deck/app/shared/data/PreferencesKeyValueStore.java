package it.niedermann.nextcloud.deck.app.shared.data;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.reactivestreams.FlowAdapters;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Flow;
import java.util.function.Supplier;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;

public class PreferencesKeyValueStore implements KeyValueStore {

    private final Preferences prefs;

    private final Map<String, Flow.Publisher<?>> flowableValuesByKey = new HashMap<>();

    public PreferencesKeyValueStore(Preferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void putString(@NotNull String key, @NotNull String value) {
        prefs.put(key, value);
    }

    @Override
    public void putLong(@NotNull String key, long value) {
        prefs.putLong(key, value);
    }

    @Override
    public void putBoolean(@NotNull String key, boolean value) {
        prefs.putBoolean(key, value);
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
    public boolean containsKey(@NotNull String key) {
        try {
            for (var k : prefs.keys()) {
                if (Objects.equals(k, key)) {
                    return true;
                }
            }

            return false;

        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try {
            prefs.clear();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(@NotNull String key) {
        prefs.remove(key);
    }

    private <T> Flow.Publisher<T> get(String key, Supplier<T> supplier) {
        final var result = Flowable.<T>create(emitter -> {

                    emitter.onNext(supplier.get());

                    final var listener = new PreferenceChangeListener() {
                        @Override
                        public void preferenceChange(PreferenceChangeEvent event) {

                            if (emitter.isCancelled() || !Objects.equals(event.getKey(), key)) {
                                return;
                            }

                            final var newValue = event.getNewValue();
                            if (newValue != null) {
                                try {
                                    //noinspection unchecked
                                    emitter.onNext((T) newValue);
                                } catch (ClassCastException e) {
                                    emitter.onError(e);
                                }
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
