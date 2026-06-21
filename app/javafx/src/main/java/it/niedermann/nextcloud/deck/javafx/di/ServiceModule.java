package it.niedermann.nextcloud.deck.javafx.di;

import com.google.gson.Gson;
import com.jthemedetecor.OsThemeDetector;

import dagger.Module;
import dagger.Provides;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.MainService;
import it.niedermann.nextcloud.deck.javafx.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import jakarta.inject.Singleton;

@Module()
public class ServiceModule {

    @Provides
    @Singleton
    StoreLogger provideStoreLogger(Gson gson) {
        return new StoreLogger(gson);
    }


    /// TODO This service must be scoped (Max one instance per Scene)
    @Provides
    @Singleton
    MainService provideMainService(StoreLogger storeLogger,
                                   SetCurrentAccountUseCase setCurrentAccountUseCase,
                                   GetCurrentBoardUseCase getCurrentBoardUseCase,
                                   SetCurrentBoardUseCase setCurrentBoardUseCase) {
        return new MainService(storeLogger,
                setCurrentAccountUseCase,
                getCurrentBoardUseCase,
                setCurrentBoardUseCase);
    }

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
