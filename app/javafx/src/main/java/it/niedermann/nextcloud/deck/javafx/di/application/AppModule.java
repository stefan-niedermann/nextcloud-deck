package it.niedermann.nextcloud.deck.javafx.di.application;

import com.google.gson.Gson;
import com.jthemedetecor.OsThemeDetector;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.auth.webloginflowv2.WebLoginFlowV2AuthProvider;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Singleton;

@Module()
public class AppModule {

    @Provides
    @Singleton
    Inflater inflater() {
        return Inflater.getInstance();
    }

    @Provides
    @Singleton
    WebLoginFlowV2AuthProvider providerAuthProvider() {
        return new WebLoginFlowV2AuthProvider();
    }

    @Provides
    @Singleton
    AppTokenAuthProvider provideAuthProvider() {
        return new AppTokenAuthProvider();
    }

    @Provides
    @Singleton
    StoreLogger provideStoreLogger(Gson gson) {
        return new StoreLogger(gson);
    }

    @Provides
    @Singleton
    OsThemeDetector provideOsThemeDetector() {
        return OsThemeDetector.getDetector();
    }

    @Provides
    @Singleton
    ThemeService provideThemeService(OsThemeDetector detector) {
        return new ThemeService(detector);
    }
}
