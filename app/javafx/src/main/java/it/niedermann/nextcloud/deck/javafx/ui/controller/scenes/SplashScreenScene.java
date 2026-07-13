package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;

public class SplashScreenScene extends DisposableController {

    @AssistedInject
    public SplashScreenScene() {
    }

    @AssistedFactory
    public interface Factory {
        SplashScreenScene create();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);
    }
}
