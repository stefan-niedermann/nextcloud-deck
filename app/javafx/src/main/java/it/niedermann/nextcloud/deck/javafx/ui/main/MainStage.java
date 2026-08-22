package it.niedermann.nextcloud.deck.javafx.ui.main;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractStage;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.util.ExceptionUnwrapper;
import jakarta.inject.Provider;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class MainStage extends AbstractStage<BoardRawArgs, BoardParsedArgs> {

    private final MainScene.Factory mainSceneFactory;
    private final MainService.Factory stageContextFactory;
    private final ExceptionUnwrapper exceptionUnwrapper;

    @AssistedInject
    public MainStage(Inflater inflater,
                     Stage stage,
                     ThemeService themeService,
                     SplashScreenScene.Factory splashScreenFactory,
                     LoginService.Factory loginStageContextFactory,
                     Provider<LoginScene.Factory> loginFactoryProvider,
                     Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                     SetCurrentAccountUseCase setCurrentAccountUseCase,
                     MainScene.Factory mainSceneFactory,
                     MainService.Factory stageContextFactory,
                     ExceptionUnwrapper exceptionUnwrapper,
                     BoardArgResolver boardArgResolver,
                     HostServices hostServices,
                     BuildConfig buildConfig,
                     @Assisted BoardRawArgs args) {
        super(stage,
                themeService,
                inflater,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                boardArgResolver,
                hostServices,
                buildConfig,
                args);
        this.mainSceneFactory = mainSceneFactory;
        this.stageContextFactory = stageContextFactory;
        this.exceptionUnwrapper = exceptionUnwrapper;
    }

    @AssistedFactory
    public interface Factory {
        MainStage create(BoardRawArgs args);
    }

    @Override
    protected CompletableFuture<Inflater.FxBundle<AbstractScene>> showContent(BoardParsedArgs parsedArgs) {
        final var stageContext = stageContextFactory.createStageContext(new MainService.State(
                Optional.ofNullable(parsedArgs.accountId()),
                Optional.ofNullable(parsedArgs.boardId()),
                Optional.empty(),
                FilterInformation.EMPTY,
                MainService.ViewMode.KANBAN
        ));

        final AbstractScene mainScene = mainSceneFactory.createMainScene(stageContext);
        return CompletableFuture.completedFuture(inflater.inflate(mainScene));
    }

    @Override
    protected CompletableFuture<Void> recoverError(Throwable throwable) {
        if (exceptionUnwrapper.unwrap(throwable) instanceof BoardArgResolver.NoAccountConfiguredException) {
            return showLoginScene();
        }
        return CompletableFuture.failedFuture(throwable);
    }
}
