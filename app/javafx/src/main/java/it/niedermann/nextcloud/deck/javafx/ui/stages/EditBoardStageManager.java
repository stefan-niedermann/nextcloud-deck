package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditBoardStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditBoardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Provider;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class EditBoardStageManager extends StageManager<BoardRawArgs, BoardParsedArgs> {

    private final EditBoardScene.Factory editBoardSceneFactory;
    private final EditBoardStageContext.Factory stageContextFactory;

    @AssistedInject
    public EditBoardStageManager(Inflater inflater,
                                 Stage stage,
                                 ThemeService themeService,
                                 SplashScreenScene.Factory splashScreenFactory,
                                 LoginStageContext.Factory loginStageContextFactory,
                                 Provider<LoginScene.Factory> loginFactoryProvider,
                                 Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                                 SetCurrentAccountUseCase setCurrentAccountUseCase,
                                 EditBoardScene.Factory editBoardSceneFactory,
                                 EditBoardStageContext.Factory stageContextFactory,
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
        this.editBoardSceneFactory = editBoardSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditBoardStageManager create(BoardRawArgs args);
    }

    @Override
    protected CompletableFuture<Inflater.FxBundle<Object>> showContent(BoardParsedArgs parsedArgs) {
        final var stageContext = stageContextFactory.create(new EditBoardStageContext.State(
                parsedArgs.accountId(),
                parsedArgs.boardId()
        ));

        final var editBoardScene = editBoardSceneFactory.create(stageContext);
        return CompletableFuture.completedFuture(inflater.inflate(editBoardScene));
    }

}
