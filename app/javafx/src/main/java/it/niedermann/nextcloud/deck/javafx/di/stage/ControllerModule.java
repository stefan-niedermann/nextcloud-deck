package it.niedermann.nextcloud.deck.javafx.di.stage;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;

@Module
public class ControllerModule {

    @Provides
    @IntoMap
    @ClassKey(SplashScreenScene.class)
    Object provideSplashScreenController(SplashScreenScene controller) {
        return controller;
    }

}
