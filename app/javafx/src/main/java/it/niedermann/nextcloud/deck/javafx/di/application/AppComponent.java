package it.niedermann.nextcloud.deck.javafx.di.application;


import dagger.BindsInstance;
import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import javafx.stage.Stage;

@Singleton
@Component(modules = {
        AppModule.class,
        SharedModule.class
})
public interface AppComponent {

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance @Named("primary") Stage primaryStage,
                            @BindsInstance DeckDatabase database,
                            @BindsInstance KeyValueStore keyValueStore);
    }

    StageComponent.Factory getStageComponentFactory();

    ApplicationRouter getApplicationRouter();
}
