package it.niedermann.nextcloud.deck.javafx.ui.controller.stages;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Single;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.services.stage.MainStageContext;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.ControllerFactory;
import it.niedermann.nextcloud.deck.javafx.ui.controller.StageController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.MainScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;

@StageScope
public class MainStageController extends StageController<MainStageController.Args, MainStageController.ParsedArgs> {


    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;
    private final GetCurrentBoardUseCase getCurrentBoardUseCase;
    private final MainScene.Factory mainSceneFactory;
    private final MainStageContext.Factory stageContextFactory;

    @Inject
    public MainStageController(Inflater inflater,
                               StageRouter stageRouter,
                               ControllerFactory controllerFactory,
                               HasAccountsUseCase hasAccountsUseCase,
                               GetCurrentAccountUseCase getCurrentAccountUseCase,
                               GetCurrentBoardUseCase getCurrentBoardUseCase,
                               LoginScene.Factory loginFactory,
                               ExceptionScene.Factory exceptionFactory,
                               SetCurrentAccountUseCase setCurrentAccountUseCase,
                               MainScene.Factory mainSceneFactory,
                               MainStageContext.Factory stageContextFactory) {
        super(inflater,
                stageRouter,
                controllerFactory,
                loginFactory,
                exceptionFactory,
                setCurrentAccountUseCase);

        this.hasAccountsUseCase = hasAccountsUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
        this.getCurrentBoardUseCase = getCurrentBoardUseCase;
        this.mainSceneFactory = mainSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @Override
    protected CompletableFuture<ParsedArgs> deriveInitialState(Args args) {
        return switch (args) {

            case Args.CurrentBoardOfCurrentAccount _ -> {

                final var accountId = Flowable.fromPublisher(hasAccountsUseCase.execute())
                        .subscribeOn(Schedulers.virtual())
                        .firstElement()
                        .flatMapSingle(hasAccounts -> {
                            if (hasAccounts) {
                                return Single.fromCompletionStage(getCurrentAccountUseCase.execute());
                            }

                            return Single.error(new NoAccountConfiguredException());
                        })
                        .toCompletionStage()
                        .toCompletableFuture();

                final var boardId = accountId
                        .thenComposeAsync(getCurrentBoardUseCase::execute)
                        .exceptionallyAsync(_ -> null);

                yield accountId.thenCombineAsync(boardId, ParsedArgs::new);

            }
            default -> throw new UnsupportedOperationException("Not yet implemented.");
        };
    }

    @Override
    protected CompletableFuture<Void> showContent(ParsedArgs initialState) {
        final var stageContext = stageContextFactory.createStageContext(new MainStageContext.State(
                Optional.ofNullable(initialState.accountId()),
                Optional.ofNullable(initialState.boardId()),
                Optional.ofNullable(initialState.cardId())));

        final var mainScene = mainSceneFactory.createMainScene(stageContext);
        final var bundle = inflater.inflate(mainScene);
        return this.stageRouter.setStageContent(bundle);
    }

    public record ParsedArgs(Account.ID accountId,
                             Board.ID boardId,
                             Card.ID cardId) {

        public ParsedArgs(Account.ID accountId, Board.ID boardId) {
            this(accountId, boardId, null);
        }
    }

    public sealed interface Args {
        record CurrentBoardOfCurrentAccount() implements Args {
        }

        record RemoteAccount(String accountName, long cardRemoteId) implements Args {
        }

        record RemoteServer(URL server, long cardRemoteId) implements Args {
        }
    }
}
