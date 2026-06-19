package it.niedermann.nextcloud.deck.javafx.di;


import dagger.BindsInstance;
import dagger.Component;
import it.niedermann.nextcloud.deck.app.shared.di.SharedModule;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;
import it.niedermann.nextcloud.deck.javafx.RouteProvider;
import it.niedermann.nextcloud.deck.javafx.router.Router;
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
        AppComponent create(@BindsInstance Stage stage,
                            @BindsInstance DeckDatabase database,
                            @BindsInstance KeyValueStore keyValueStore);
    }

    Router getRouter();

    RouteProvider getRouteProvider();

}
