package it.niedermann.nextcloud.deck.javafx.di;

import java.util.Map;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.auth.webloginflowv2.WebLoginFlowV2AuthProvider;
import it.niedermann.nextcloud.deck.javafx.router.Router;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.ControllerFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.FeatureFactory;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Provider;
import jakarta.inject.Singleton;
import javafx.stage.Stage;

@Module(includes = {
        ControllerModule.class,
        ServiceModule.class
})
public class AppModule {

    @Provides
    @Singleton
    Inflater inflater() {
        return Inflater.getInstance();
    }

    @Provides
    @Singleton
    ControllerFactory provideControllerFactory(Map<Class<?>, Provider<Object>> controllerProviderMap) {
        return new ControllerFactory(controllerProviderMap);
    }

    @Provides
    @Singleton
    Router provideRouter(FeatureFactory featureFactory, Stage primaryStage, ThemeService themeService) {
        return new Router(featureFactory, primaryStage, themeService);
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
}
