package it.niedermann.nextcloud.deck.javafx.ui.stages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jthemedetecor.OsThemeDetector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.matcher.control.LabeledMatchers;

import java.net.URI;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.EmptyArgs;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.PreferencesScene;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.PreferencesService;
import it.niedermann.nextcloud.deck.javafx.ui.preferences.PreferencesStage;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ExceptionService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import javafx.application.HostServices;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class PreferencesStageIntegrationTest {

    private PreferencesService stageContext;

    @Start
    void start(Stage stage) {
        final var hasAccountsUseCase = mock(HasAccountsUseCase.class);
        final var keyValueStore = mock(KeyValueStore.class);
        final var detector = mock(OsThemeDetector.class);
        final var setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class);

        when(hasAccountsUseCase.execute()).thenReturn(Flowable.just(true));
        when(detector.isDark()).thenReturn(false);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.just("AUTO"));
        when(keyValueStore.getBoolean(PreferencesService.KEY_BACKGROUND_SYNC)).thenReturn(Flowable.just(true));
        when(keyValueStore.getBoolean(PreferencesService.KEY_COMPACT_MODE)).thenReturn(Flowable.just(false));
        when(keyValueStore.getBoolean(ExceptionService.KEY_DEBUG_MODE)).thenReturn(Flowable.just(false));
        when(keyValueStore.containsKey(anyString())).thenReturn(java.util.concurrent.CompletableFuture.completedFuture(true));

        final var getAccountsUseCase = mock(GetAccountsUseCase.class);
        when(getAccountsUseCase.execute()).thenReturn(Flowable.empty());
        final var getAccountUseCase = mock(GetAccountUseCase.class);
        final var getBoardUseCase = mock(GetBoardUseCase.class);
        final var getCardUseCase = mock(GetCardUseCase.class);
        final var getColumnUseCase = mock(GetColumnUseCase.class);

        final var stageTitleResolver = new StageTitleResolver(
                getAccountUseCase,
                getAccountsUseCase,
                getBoardUseCase,
                getCardUseCase,
                getColumnUseCase
        );

        final var themeService = new ThemeService(detector, keyValueStore);
        final SplashScreenScene.Factory splashScreenFactory = SplashScreenScene::new;
        
        final var loginFactoryProvider = (jakarta.inject.Provider<LoginScene.Factory>) () -> mock(LoginScene.Factory.class);
        final var exceptionFactoryProvider = (jakarta.inject.Provider<ExceptionScene.Factory>) () -> mock(ExceptionScene.Factory.class);
        final var storeLogger = new StoreLogger(new com.google.gson.Gson());
        final LoginService.Factory loginStageContextFactory = url -> new LoginService(
                storeLogger,
                mock(it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase.class),
                url
        );

        final var realStageContext = new PreferencesService(storeLogger, keyValueStore, new PreferencesService.State());
        stageContext = spy(realStageContext);
        final PreferencesService.Factory stageContextFactory = initialState -> stageContext;
        final PreferencesScene.Factory preferencesSceneFactory = PreferencesScene::new;

        final var manager = new PreferencesStage(
                Inflater.getInstance(),
                stage,
                themeService,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                preferencesSceneFactory,
                stageContextFactory,
                mock(HostServices.class),
                new BuildConfig(URI.create("https://example.com/help-uri")),
                EmptyArgs.INSTANCE
        );
        manager.initialize();
    }

    @Test
    void testPreferencesSceneIsShown(FxRobot robot) {
        robot.lookup("Preferences").query();
        robot.lookup("#themeComboBox").query();
        robot.lookup("#backgroundSyncCheckBox").query();
        robot.lookup("#compactModeCheckBox").query();
    }

    @Test
    void testChangePreferences(FxRobot robot) {
        // Interaction with CheckBoxes
        robot.clickOn("#backgroundSyncCheckBox");
        robot.clickOn("#compactModeCheckBox");

        // Interaction with ComboBox
        robot.clickOn("#themeComboBox");
        robot.clickOn(LabeledMatchers.hasText("DARK"));

        // Verification
        verify(stageContext, atLeastOnce()).dispatch(any(PreferencesService.Action.SetBackgroundSync.class));
        verify(stageContext, atLeastOnce()).dispatch(any(PreferencesService.Action.SetCompactMode.class));
        verify(stageContext, atLeastOnce()).dispatch(any(PreferencesService.Action.SetTheme.class));
    }
}
