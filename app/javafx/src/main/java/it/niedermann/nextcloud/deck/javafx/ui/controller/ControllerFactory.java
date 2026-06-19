package it.niedermann.nextcloud.deck.javafx.ui.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javafx.util.Callback;

public class ControllerFactory implements Callback<Class<?>, Object> {

    private final Map<Class<?>, Provider<Object>> controllerProviderMap;

    @Inject
    public ControllerFactory(Map<Class<?>, Provider<Object>> controllerProviderMap) {
        this.controllerProviderMap = controllerProviderMap;
    }

    @Override
    public Object call(Class<?> cls) {

        final var provider = controllerProviderMap.get(cls);
        if (provider != null) {
            return provider.get();
        }

        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
