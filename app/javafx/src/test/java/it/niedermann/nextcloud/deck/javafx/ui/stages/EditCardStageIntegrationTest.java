package it.niedermann.nextcloud.deck.javafx.ui.stages;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jthemedetecor.OsThemeDetector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardArgResolver;
import it.niedermann.nextcloud.deck.app.shared.args.card.CardRawArgs;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.state.KeyValueStore;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.GetAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.activities.ListPreviewActivitiesUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.attachments.ListAttachmentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.boards.GetBoardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.GetCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.cards.UpdateCardUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.columns.GetColumnUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.AddCommentUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.comments.ListPreviewCommentsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageComponent;
import it.niedermann.nextcloud.deck.javafx.services.application.ApplicationRouter;
import it.niedermann.nextcloud.deck.javafx.services.application.StageTitleResolver;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.EditCardStageContext;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.EditCardFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.EditCardScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import javafx.stage.Stage;

@ExtendWith(ApplicationExtension.class)
class EditCardStageIntegrationTest {

    @Start
    void start(Stage stage) {
        final var hasAccountsUseCase = mock(HasAccountsUseCase.class);
        final var cardArgResolver = mock(CardArgResolver.class);
        final var setCurrentAccountUseCase = mock(SetCurrentAccountUseCase.class);

        when(hasAccountsUseCase.execute()).thenReturn(Flowable.just(true));
        
        final var cardId = new Card.ID(1);
        when(cardArgResolver.resolve(any())).thenReturn(CompletableFuture.completedFuture(cardId));
        
        final var storeLogger = mock(StoreLogger.class);
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.empty());
        final var themeService = new ThemeService(detector, keyValueStore);

        final var primaryStage = mock(Stage.class);
        final var stageComponentFactory = mock(StageComponent.Factory.class);
        final var applicationRouter = new ApplicationRouter(primaryStage, stageComponentFactory);

        final var stageContext = new EditCardStageContext(
                storeLogger,
                applicationRouter,
                mock(GetCardUseCase.class),
                mock(UpdateCardUseCase.class),
                mock(GetColumnUseCase.class),
                mock(GetBoardUseCase.class),
                mock(ListAttachmentsUseCase.class),
                mock(ListPreviewCommentsUseCase.class),
                mock(ListPreviewActivitiesUseCase.class),
                mock(AddCommentUseCase.class),
                new EditCardStageContext.State(Optional.empty(), false),
                () -> {}
        );
        final EditCardStageContext.Factory editCardStageContextFactory = (initialState, onClose) -> stageContext;
        
        final var inflater = Inflater.getInstance();
        
        final var getAccountUseCase = mock(GetAccountUseCase.class);
        final var getAccountsUseCase = mock(GetAccountsUseCase.class);
        when(getAccountsUseCase.execute()).thenReturn(Flowable.empty());
        final var getBoardUseCase = mock(GetBoardUseCase.class);
        when(getBoardUseCase.execute(any())).thenReturn(Flowable.empty());
        final var getCardUseCase = mock(GetCardUseCase.class);
        when(getCardUseCase.execute(any())).thenReturn(Flowable.empty());
        final var getColumnUseCase = mock(GetColumnUseCase.class);
        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        
        final var stageTitleResolver = new StageTitleResolver(
                getAccountUseCase,
                getAccountsUseCase,
                getBoardUseCase,
                getCardUseCase,
                getColumnUseCase
        );

        final EditCardScene.Factory editCardSceneFactory = context -> new EditCardScene(
                inflater,
                mock(EditCardFeature.Factory.class, Answers.RETURNS_MOCKS),
                stageTitleResolver,
                context
        );

        final SplashScreenScene.Factory splashScreenFactory = SplashScreenScene::new;
        final var loginFactoryProvider = (jakarta.inject.Provider<LoginScene.Factory>) () -> mock(LoginScene.Factory.class);
        final var exceptionFactoryProvider = (jakarta.inject.Provider<ExceptionScene.Factory>) () -> mock(ExceptionScene.Factory.class);

        new EditCardStageManager(
                inflater,
                stage,
                themeService,
                splashScreenFactory,
                hasAccountsUseCase,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                cardArgResolver,
                editCardStageContextFactory,
                editCardSceneFactory,
                new CardRawArgs.LocalCard(cardId)
        );
    }

    @Test
    void testEditCardSceneIsShown(FxRobot robot) {
    }
}
