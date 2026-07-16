package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.Optional;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.MainStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Provider;
import javafx.stage.Stage;

public class MainStageManager extends StageManager<BoardRawArgs, BoardParsedArgs> {

    private final MainScene.Factory mainSceneFactory;
    private final MainStageContext.Factory stageContextFactory;

    @AssistedInject
    public MainStageManager(Inflater inflater,
                            Stage stage,
                            ThemeService themeService,
                            SplashScreenScene.Factory splashScreenFactory,
                            HasAccountsUseCase hasAccountsUseCase,
                            Provider<LoginScene.Factory> loginFactoryProvider,
                            Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                            SetCurrentAccountUseCase setCurrentAccountUseCase,
                            MainScene.Factory mainSceneFactory,
                            MainStageContext.Factory stageContextFactory,
                            BoardArgResolver boardArgResolver,
                            @Assisted BoardRawArgs args) {
        super(stage,
                themeService,
                inflater,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                args,
                boardArgResolver);
        this.mainSceneFactory = mainSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @AssistedFactory
    public interface Factory {
        MainStageManager create(BoardRawArgs args);
    }

    @Override
    public Inflater.FxBundle<?> inflateContent(BoardParsedArgs initialState) {

        final var stageContext = stageContextFactory.createStageContext(new MainStageContext.State(
                Optional.ofNullable(initialState.accountId()),
                Optional.ofNullable(initialState.boardId()),
                Optional.empty()
        ));

        final var mainScene = mainSceneFactory.createMainScene(stageContext);
        return inflater.inflate(mainScene);
    }
}
