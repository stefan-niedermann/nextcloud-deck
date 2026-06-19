package it.niedermann.nextcloud.deck.javafx.di;

import com.jthemedetecor.OsThemeDetector;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.javafx.services.ThemeService;
import jakarta.inject.Singleton;

@Module()
public class ServiceModule {

    @Provides
    @Singleton
    OsThemeDetector provideOsThemeDetector() {
        return OsThemeDetector.getDetector();
    }

    @Provides
    @Singleton
    ThemeService provideThemeService(OsThemeDetector detector) {
        return new ThemeService(detector);
    }

}
