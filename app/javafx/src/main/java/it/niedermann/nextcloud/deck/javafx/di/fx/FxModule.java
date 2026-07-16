package it.niedermann.nextcloud.deck.javafx.di.fx;

import com.jthemedetecor.OsThemeDetector;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.auth.webloginflowv2.WebLoginFlowV2AuthProvider;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;

@Module(subcomponents = StageComponent.class)
public class FxModule {

    @Provides
    @FxScope
    Inflater inflater() {
        return Inflater.getInstance();
    }

    @Provides
    @FxScope
    WebLoginFlowV2AuthProvider provideWebLoginFlowV2AuthProvider() {
        return new WebLoginFlowV2AuthProvider();
    }

    @Provides
    @FxScope
    OsThemeDetector provideOsThemeDetector() {
        return OsThemeDetector.getDetector();
    }
}
