package it.niedermann.nextcloud.deck.javafx.ui.controller;

import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;

public class FeatureFactory {

    private final ControllerFactory controllerFactory;
    private final Inflater inflater;

    @Inject
    public FeatureFactory(ControllerFactory controllerFactory,
                          Inflater inflater) {
        this.controllerFactory = controllerFactory;
        this.inflater = inflater;
    }

    public <TController extends DisposableController> Inflater.FxBundle<TController> inflateFeature(Class<TController> controllerClass) {
        return inflater.inflateFxBundle(controllerClass, controllerFactory);
    }
}
