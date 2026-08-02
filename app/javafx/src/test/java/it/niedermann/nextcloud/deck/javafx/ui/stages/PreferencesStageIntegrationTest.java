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

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.PreferencesStageContext;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.PreferencesScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class PreferencesStageIntegrationTest {

    private PreferencesStageContext stageContext;

    @Start
    void start(Stage stage) {
        final var hasAccountsUseCase = mock(HasAccountsUseCase.class);
        final var storeLogger = mock(StoreLogger.class);
        final var keyValueStore = mock(KeyValueStore.class);
        final var detector = mock(OsThemeDetector.class);
        final var setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class);

        when(hasAccountsUseCase.execute()).thenReturn(Flowable.just(true));
        when(detector.isDark()).thenReturn(false);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.just("AUTO"));
        when(keyValueStore.getBoolean(PreferencesStageContext.KEY_BACKGROUND_SYNC)).thenReturn(Flowable.just(true));
        when(keyValueStore.getBoolean(PreferencesStageContext.KEY_COMPACT_MODE)).thenReturn(Flowable.just(false));
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

        final var realStageContext = new PreferencesStageContext(storeLogger, keyValueStore, new PreferencesStageContext.State());
        stageContext = spy(realStageContext);
        final PreferencesStageContext.Factory stageContextFactory = initialState -> stageContext;
        final PreferencesScene.Factory preferencesSceneFactory = PreferencesScene::new;

        new PreferencesStageManager(
                Inflater.getInstance(),
                stage,
                themeService,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                preferencesSceneFactory,
                stageContextFactory,
                null
        );
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
        verify(stageContext, atLeastOnce()).dispatch(any(PreferencesStageContext.Action.SetBackgroundSync.class));
        verify(stageContext, atLeastOnce()).dispatch(any(PreferencesStageContext.Action.SetCompactMode.class));
        verify(stageContext, atLeastOnce()).dispatch(any(PreferencesStageContext.Action.SetTheme.class));
    }
}
