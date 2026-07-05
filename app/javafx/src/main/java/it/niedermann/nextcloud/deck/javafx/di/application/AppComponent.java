package it.niedermann.nextcloud.deck.javafx.di.application;


import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule;
import it.niedermann.nextcloud.deck.javafx.di.fx.FxComponent;
import it.niedermann.nextcloud.deck.javafx.services.application.PurgeService;
import jakarta.inject.Singleton;

@Singleton
@Component(modules = {
        AppModule.class,
        ConfigModule.class,
        SharedModule.class,
})
public interface AppComponent {

    @Component.Factory
    interface Factory {
        AppComponent create();
    }

    FxComponent.Factory getFxComponentFactory();

    PurgeService getPurgeService();
}
