package it.niedermann.nextcloud.deck;

import androidx.room3.Database;

import dagger.BindsInstance;
import dagger.Component;

@Component
public interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder database(Database database);

        AppComponent build();
    }
}
