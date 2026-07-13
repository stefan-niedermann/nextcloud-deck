package it.niedermann.nextcloud.deck.javafx.ui.stages;

import java.util.Optional;

import it.niedermann.nextcloud.deck.app.shared.args.board.BoardParsedArgs;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.MainStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.StageManager;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;

public class MainStageFactory implements StageManager.StageFactory<BoardParsedArgs> {

    private final Inflater inflater;
    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final MainScene.Factory mainSceneFactory;
    private final MainStageContext.Factory stageContextFactory;

    @Inject
    public MainStageFactory(Inflater inflater,
                            HasAccountsUseCase hasAccountsUseCase,
                            GetCurrentAccountUseCase getCurrentAccountUseCase,
                            GetCurrentBoardUseCase getCurrentBoardUseCase,
                            MainScene.Factory mainSceneFactory,
                            MainStageContext.Factory stageContextFactory) {
        this.inflater = inflater;
        this.hasAccountsUseCase = hasAccountsUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.mainSceneFactory = mainSceneFactory;
        this.stageContextFactory = stageContextFactory;
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
