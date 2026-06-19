package it.niedermann.nextcloud.deck.javafx;

import it.niedermann.nextcloud.deck.javafx.router.RouteConfig;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import jakarta.inject.Inject;

public class RouteProvider {

    @Inject
    public RouteProvider() {

    }

    public RouteConfig<SplashScreenScene> getSplashScreenRoute() {
        return new RouteConfig<>(SplashScreenScene.class);
    }

    public RouteConfig<LoginScene> getLoginRoute() {
        return new RouteConfig<>(LoginScene.class);
    }

    public RouteConfig<MainScene> getMainRoute(long accountId) {
        return new RouteConfig<>(MainScene.class);
    }
}
