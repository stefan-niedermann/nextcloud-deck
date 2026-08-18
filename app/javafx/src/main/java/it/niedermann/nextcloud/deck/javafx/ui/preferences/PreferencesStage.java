package it.niedermann.nextcloud.deck.javafx.ui.preferences;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.EmptyArgs;
import it.niedermann.nextcloud.deck.app.shared.args.StaticArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractStage;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import jakarta.inject.Provider;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class PreferencesStage extends AbstractStage<EmptyArgs, EmptyArgs> {

    private final PreferencesScene.Factory preferencesSceneFactory;
    private final PreferencesService.Factory stageContextFactory;

    @AssistedInject
    public PreferencesStage(Inflater inflater,
                            Stage stage,
                            ThemeService themeService,
                            SplashScreenScene.Factory splashScreenFactory,
                            LoginService.Factory loginStageContextFactory,
                            Provider<LoginScene.Factory> loginFactoryProvider,
                            Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                            SetCurrentAccountUseCase setCurrentAccountUseCase,
                            PreferencesScene.Factory preferencesSceneFactory,
                            PreferencesService.Factory stageContextFactory,
                            HostServices hostServices,
                            BuildConfig buildConfig,
                            @Assisted EmptyArgs args) {
        super(stage,
                themeService,
                inflater,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                new StaticArgsResolver<>(),
                hostServices,
                buildConfig,
                args);
        this.preferencesSceneFactory = preferencesSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        PreferencesStage create(EmptyArgs args);
    }

    @Override
    protected CompletableFuture<Inflater.FxBundle<AbstractScene>> showContent(EmptyArgs args) {
        final var stageContext = stageContextFactory.create(new PreferencesService.State());
        final AbstractScene preferencesScene = preferencesSceneFactory.create(stageContext);
        return CompletableFuture.completedFuture(inflater.inflate(preferencesScene));
    }
}
