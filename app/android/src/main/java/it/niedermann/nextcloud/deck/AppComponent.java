package it.niedermann.nextcloud.deck;

import dagger.BindsInstance;
import dagger.Component;
import it.niedermann.nextcloud.deck.data.local.DeckDatabase;
import it.niedermann.nextcloud.deck.data.local.KeyValueStore;

@Component
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder database(DeckDatabase database);

        @BindsInstance
        Builder keyValueStore(KeyValueStore keyValueStore);

        AppComponent build();
    }
}
