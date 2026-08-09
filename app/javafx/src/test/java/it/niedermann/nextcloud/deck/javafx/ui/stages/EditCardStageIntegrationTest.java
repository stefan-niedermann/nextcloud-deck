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
import java.util.concurrent.Flow;

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
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
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
        when(cardArgResolver.resolve(any())).thenReturn(subscriber -> subscriber.onSubscribe(new Flow.Subscription() {
            @Override
            public void request(long n) {
                subscriber.onNext(cardId);
                subscriber.onComplete();
            }

            @Override
            public void cancel() {
            }
        }));
        
        final var storeLogger = new StoreLogger(new com.google.gson.Gson());
        final var detector = mock(OsThemeDetector.class);
        when(detector.isDark()).thenReturn(false);
        final var keyValueStore = mock(KeyValueStore.class);
        when(keyValueStore.getString(ThemeService.KEY_THEME)).thenReturn(Flowable.empty());
        final var themeService = new ThemeService(detector, keyValueStore);

        final var primaryStage = mock(Stage.class);
        final var stageComponentFactory = mock(StageComponent.Factory.class);
        final var applicationRouter = new ApplicationRouter(primaryStage, stageComponentFactory);

        final var getCardUseCase = mock(GetCardUseCase.class, Answers.RETURNS_MOCKS);
        final var getColumnUseCase = mock(GetColumnUseCase.class, Answers.RETURNS_MOCKS);
        final var getBoardUseCase = mock(GetBoardUseCase.class, Answers.RETURNS_MOCKS);
        final var listAttachmentsUseCase = mock(ListAttachmentsUseCase.class, Answers.RETURNS_MOCKS);
        final var listPreviewCommentsUseCase = mock(ListPreviewCommentsUseCase.class, Answers.RETURNS_MOCKS);
        final var listPreviewActivitiesUseCase = mock(ListPreviewActivitiesUseCase.class, Answers.RETURNS_MOCKS);

        when(getCardUseCase.execute(any())).thenReturn(Flowable.empty());
        when(getColumnUseCase.execute(any())).thenReturn(Flowable.empty());
        when(getBoardUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listAttachmentsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listPreviewCommentsUseCase.execute(any())).thenReturn(Flowable.empty());
        when(listPreviewActivitiesUseCase.execute(any())).thenReturn(Flowable.empty());


        final var stageContext = new EditCardStageContext(
                storeLogger,
                applicationRouter,
                getCardUseCase,
                mock(UpdateCardUseCase.class),
                getColumnUseCase,
                getBoardUseCase,
                listAttachmentsUseCase,
                listPreviewCommentsUseCase,
                listPreviewActivitiesUseCase,
                mock(AddCommentUseCase.class),
                new EditCardStageContext.State(Optional.empty(), false),
                () -> {}
        );
        final EditCardStageContext.Factory editCardStageContextFactory = (initialState, onClose) -> stageContext;
        
        final var inflater = Inflater.getInstance();
        
        final var getAccountUseCase = mock(GetAccountUseCase.class, Answers.RETURNS_MOCKS);
        when(getAccountUseCase.execute(any())).thenReturn(Flowable.empty());
        final var getAccountsUseCase = mock(GetAccountsUseCase.class, Answers.RETURNS_MOCKS);
        when(getAccountsUseCase.execute()).thenReturn(Flowable.empty());


        
        final var stageTitleResolver = new StageTitleResolver(
                getAccountUseCase,
                getAccountsUseCase,
                getBoardUseCase,
                getCardUseCase,
                getColumnUseCase
        );

        final var userSearchViewConverter = new it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.UserSearchViewConverter();
        final var editCardFeatureFactory = (EditCardFeature.Factory) viewModel -> new EditCardFeature(
                new it.niedermann.nextcloud.deck.javafx.ui.cellfactories.CommentCellFactory(),
                new it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.LabelSuggestionProvider(mock(it.niedermann.nextcloud.deck.domain.usecases.labels.SearchLabelsUseCase.class)),
                new it.niedermann.nextcloud.deck.javafx.ui.suggestionproviders.UserSuggestionProvider(mock(it.niedermann.nextcloud.deck.domain.usecases.users.SearchUserUseCase.class)),
                new it.niedermann.nextcloud.deck.javafx.ui.searchviewconverter.LabelSearchViewConverter(),
                new it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories.LabelTagViewFactory(new it.niedermann.nextcloud.deck.util.ColorUtil()),
                userSearchViewConverter,
                new it.niedermann.nextcloud.deck.javafx.ui.tagviewfactories.UserTagViewFactory(userSearchViewConverter),
                viewModel
        );

        final EditCardScene.Factory editCardSceneFactory = context -> new EditCardScene(
                inflater,
                editCardFeatureFactory,
                stageTitleResolver,
                context
        );

        final SplashScreenScene.Factory splashScreenFactory = SplashScreenScene::new;
        final var loginFactoryProvider = (jakarta.inject.Provider<LoginScene.Factory>) () -> mock(LoginScene.Factory.class);
        final var exceptionFactoryProvider = (jakarta.inject.Provider<ExceptionScene.Factory>) () -> mock(ExceptionScene.Factory.class);
        final LoginStageContext.Factory loginStageContextFactory = url -> new LoginStageContext(
                storeLogger,
                mock(it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase.class),
                url
        );

        final var manager = new EditCardStageManager(
                inflater,
                stage,
                themeService,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                cardArgResolver,
                editCardStageContextFactory,
                editCardSceneFactory,
                new CardRawArgs.LocalCard(cardId)
        );
        manager.initialize();
    }

    @Test
    void testEditCardSceneIsShown(FxRobot robot) {
    }
}
