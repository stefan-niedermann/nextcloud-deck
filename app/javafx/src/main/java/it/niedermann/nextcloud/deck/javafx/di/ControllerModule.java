package it.niedermann.nextcloud.deck.javafx.di;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AccountSwitcherFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.BoardListFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.ColumnFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;

@Module
public class ControllerModule {

    // region Scenes

    @Provides
    @IntoMap
    @ClassKey(SplashScreenScene.class)
    Object provideSplashScreenController(SplashScreenScene controller) {
        return controller;
    }

    @Provides
    @IntoMap
    @ClassKey(LoginScene.class)
    Object provideLoginController(LoginScene controller) {
        return controller;
    }

    @Provides
    @IntoMap
    @ClassKey(MainScene.class)
    Object provideMainController(MainScene controller) {
        return controller;
    }

    @Provides
    @IntoMap
    @ClassKey(BoardFeature.class)
    Object provideBoardController(BoardFeature controller) {
        return controller;
    }

    // endregion

    // region Features

    @Provides
    @IntoMap
    @ClassKey(AccountSwitcherFeature.class)
    Object provideAccountSwitcherController(AccountSwitcherFeature controller) {
        return controller;
    }

    @Provides
    @IntoMap
    @ClassKey(BoardListFeature.class)
    Object provideBoardListController(BoardListFeature controller) {
        return controller;
    }

    @Provides
    @IntoMap
    @ClassKey(EditCardFeature.class)
    Object provideEditCardController(EditCardFeature controller) {
        return controller;
    }

    @Provides
    @IntoMap
    @ClassKey(ColumnFeature.class)
    Object provideColumnController(ColumnFeature controller) {
        return controller;
    }

    // endregion
}
