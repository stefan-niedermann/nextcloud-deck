package it.niedermann.nextcloud.deck.app.shared.data;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

class DefaultPreferenceChangeListener<T> implements PreferenceChangeListener {

    private final Map<Consumer<T>, String> listeners;
    private final Function<String, T> loader;

    protected DefaultPreferenceChangeListener(
            Map<Consumer<T>, String> listeners,
            Function<String, T> loader) {
        this.listeners = listeners;
        this.loader = loader;
    }

    @Override
    public final void preferenceChange(PreferenceChangeEvent event) {
        listeners.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(event.getKey()))
                .map(Map.Entry::getKey)
                .forEach(listener -> {
                    final var newValue = loader.apply(event.getKey());
                    listener.accept(newValue);
                });
    }
}