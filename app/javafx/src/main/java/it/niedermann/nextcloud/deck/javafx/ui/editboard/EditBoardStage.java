package it.niedermann.nextcloud.deck.javafx.ui.editboard;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.board.BoardRawArgs;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractStage;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import jakarta.inject.Provider;
import javafx.stage.Stage;

public class EditBoardStage extends AbstractStage<BoardRawArgs, BoardParsedArgs> {

    private final EditBoardScene.Factory editBoardSceneFactory;
    private final EditBoardService.Factory stageContextFactory;

    @AssistedInject
    public EditBoardStage(Inflater inflater,
                          Stage stage,
                          SplashScreenScene.Factory splashScreenFactory,
                          LoginService.Factory loginStageContextFactory,
                          Provider<LoginScene.Factory> loginFactoryProvider,
                          Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                          SetCurrentAccountUseCase setCurrentAccountUseCase,
                          EditBoardScene.Factory editBoardSceneFactory,
                          EditBoardService.Factory stageContextFactory,
                          BoardArgResolver boardArgResolver,
                          @Assisted BoardRawArgs args) {
        super(stage,
                inflater,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                boardArgResolver,
                args);
        this.editBoardSceneFactory = editBoardSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        EditBoardStage create(BoardRawArgs args);
    }

    @Override
    protected CompletableFuture<AbstractScene> showContent(BoardParsedArgs parsedArgs) {
        final var stageContext = stageContextFactory.create(new EditBoardService.State(
                parsedArgs.accountId(),
                parsedArgs.boardId()
        ));

        final var editBoardScene = editBoardSceneFactory.create(stageContext);
        return CompletableFuture.completedFuture(editBoardScene);
    }

}
