package it.niedermann.nextcloud.deck.cli.di;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import jakarta.inject.Singleton;

@Module
public class AppModule {

    @Provides
    @Singleton
    public AppTokenAuthProvider provideAuthProvider() {
        return new AppTokenAuthProvider();
    }
}
