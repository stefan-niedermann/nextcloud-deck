package it.niedermann.nextcloud.deck.javafx.ui.splashscreen;

import java.net.URL;
import java.util.ResourceBundle;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import javafx.application.HostServices;

public class SplashScreenScene extends AbstractScene {

    @AssistedInject
    public SplashScreenScene(
            Inflater inflater,
            ThemeService themeService,
            HostServices hostServices,
            BuildConfig buildConfig,
            SyncScheduler syncScheduler) {
        super(themeService, hostServices, buildConfig, syncScheduler, inflater);
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
