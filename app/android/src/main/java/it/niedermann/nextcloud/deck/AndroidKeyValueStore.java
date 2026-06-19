package it.niedermann.nextcloud.deck;


import android.content.Context;

import androidx.datastore.core.DataStore;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import kotlin.Unit;

public class AndroidKeyValueStore implements KeyValueStore {

    final Context context;
    final DataStore<?> store;

    public AndroidKeyValueStore(Context context,
                                DataStore<?> store) {
        this.context = context;
        this.store = store;
    }

    @Override
    public void putString(@NotNull String key, @NotNull String value) {
        store.putString(key, value);
    }

    @Override
    public void putLong(@NotNull String key, long value) {
        editor.putLong(key, value);
    }

    @Override
    public void putBoolean(@NotNull String key, boolean value) {
        editor.putBoolean(key, value);
    }

    @Override
    public String getString(@NotNull String key) {
        return prefs.getString(key, null);
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
    public void registerStringChangeListener(@NotNull String key, @NotNull Consumer<@NotNull String> consumer) {

    }

    @Override
    public void registerLongChangeListener(@NotNull String key, @NotNull Consumer<@NotNull Long> consumer) {

    }

    @Override
    public void registerBooleanChangeListener(@NotNull String key, @NotNull Consumer<@NotNull Boolean> consumer) {

    }

    @Override
    public void unregisterStringChangeListener(@NotNull Consumer<@NotNull String> consumer) {

    }

    @Override
    public void unregisterLongChangeListener(@NotNull Consumer<@NotNull Long> consumer) {

    }

    @Override
    public void unregisterBooleanChangeListener(@NotNull Consumer<@NotNull Boolean> consumer) {

    }

    @Override
    public void clear() {
        return DataStoreKt.getDataStore(context).edit(
                prefs -> {
                    prefs.clear();
                    return Unit.INSTANCE;
                },
                continuation
        );
    }

    @Override
    public void remove(@NotNull String key) {
        prefs.remove(key);
    }
}
