package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.PreferencesStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.PreferencesScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Provider;
import javafx.stage.Stage;

public class PreferencesStageManager extends StageManager<Void> {

    private final PreferencesScene.Factory preferencesSceneFactory;
    private final PreferencesStageContext.Factory stageContextFactory;

    @AssistedInject
    public PreferencesStageManager(Inflater inflater,
                                   Stage stage,
                                   ThemeService themeService,
                                   SplashScreenScene.Factory splashScreenFactory,
                                   HasAccountsUseCase hasAccountsUseCase,
                                   Provider<LoginScene.Factory> loginFactoryProvider,
                                   Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                                   SetCurrentAccountUseCase setCurrentAccountUseCase,
                                   PreferencesScene.Factory preferencesSceneFactory,
                                   PreferencesStageContext.Factory stageContextFactory,
                                   @Assisted Void args) {
        super(stage,
                themeService,
                inflater,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                args);
        this.preferencesSceneFactory = preferencesSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        PreferencesStageManager create(Void args);
    }

    @Override
    protected CompletableFuture<Void> showContent(Void args) {
        return CompletableFuture.runAsync(() -> {
        }, JavaFxScheduler.platform().toExecutorService())
                .thenApplyAsync(_ -> inflateContent(), JavaFxScheduler.platform().toExecutorService())
                .thenComposeAsync(this::setStageContent);
    }

    private Inflater.FxBundle<?> inflateContent() {
        final var stageContext = stageContextFactory.create(new PreferencesStageContext.State());
        final var preferencesScene = preferencesSceneFactory.create(stageContext);
        return inflater.inflate(preferencesScene);
    }
}
