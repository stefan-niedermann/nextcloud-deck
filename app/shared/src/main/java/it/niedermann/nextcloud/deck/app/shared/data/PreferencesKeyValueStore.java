package it.niedermann.nextcloud.deck.app.shared.data;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import it.niedermann.nextcloud.deck.data.local.KeyValueStore;

public class PreferencesKeyValueStore implements KeyValueStore {

    final Preferences prefs;
    final Map<Consumer<String>, String> stringChangelisteners = new HashMap<>();
    final Map<Consumer<Long>, String> longChangelisteners = new HashMap<>();
    final Map<Consumer<Boolean>, String> booleanChangelisteners = new HashMap<>();

    public PreferencesKeyValueStore(Preferences prefs) {
        this.prefs = prefs;
        this.prefs.addPreferenceChangeListener(new DefaultPreferenceChangeListener<>(stringChangelisteners, this::getString));
        this.prefs.addPreferenceChangeListener(new DefaultPreferenceChangeListener<>(longChangelisteners, this::getLong));
        this.prefs.addPreferenceChangeListener(new DefaultPreferenceChangeListener<>(booleanChangelisteners, this::getBoolean));
    }

    @Override
    public void putString(@NotNull String key, @NotNull String value) {
        prefs.put(key, value);
    }

    @Override
    public void putLong(@NotNull String key, long value) {
        prefs.putLong(key, value);
        try {
            prefs.flush();
            prefs.sync();
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putBoolean(@NotNull String key, boolean value) {
        prefs.putBoolean(key, value);
    }

    @Override
    public String getString(@NotNull String key) {
        return prefs.get(key, null);
    }

    @Override
    public Long getLong(@NotNull String key) {
        return prefs.getLong(key, -1L);
    }

    @Override
    public Boolean getBoolean(@NotNull String key) {
        return prefs.getBoolean(key, false);
    }

    @Override
    public void registerStringChangeListener(@NotNull String key, @NotNull Consumer<String> consumer) {
        stringChangelisteners.put(consumer, key);
        consumer.accept(getString(key));
    }

    @Override
    public void registerLongChangeListener(@NotNull String key, @NotNull Consumer<Long> consumer) {
        longChangelisteners.put(consumer, key);
        consumer.accept(getLong(key));

    }

    @Override
    public void registerBooleanChangeListener(@NotNull String key, @NotNull Consumer<Boolean> consumer) {
        booleanChangelisteners.put(consumer, key);
        consumer.accept(getBoolean(key));
    }

    @Override
    public void unregisterStringChangeListener(@NotNull Consumer<String> consumer) {
        stringChangelisteners.remove(consumer);
    }

    @Override
    public void unregisterLongChangeListener(@NotNull Consumer<Long> consumer) {
        longChangelisteners.remove(consumer);
    }

    @Override
    public void unregisterBooleanChangeListener(@NotNull Consumer<Boolean> consumer) {
        booleanChangelisteners.remove(consumer);
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
}
