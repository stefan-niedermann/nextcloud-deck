package it.niedermann.nextcloud.deck.javafx.ui.splashscreen;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;

public class SplashScreenScene extends AbstractScene {

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

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just("Deck");
    }
}
