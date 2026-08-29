package it.niedermann.nextcloud.deck.javafx.ui.preferences;

import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.StaticArgsResolver;
import it.niedermann.nextcloud.deck.app.shared.args.account.AccountParsedArgs;
import it.niedermann.nextcloud.deck.app.shared.args.account.AccountRawArgs;
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

public class PreferencesStage extends AbstractStage<AccountRawArgs, AccountParsedArgs> {

    private final PreferencesScene.Factory preferencesSceneFactory;
    private final PreferencesService.Factory stageContextFactory;

    @AssistedInject
    public PreferencesStage(Inflater inflater,
                            Stage stage,
                            SplashScreenScene.Factory splashScreenFactory,
                            LoginService.Factory loginStageContextFactory,
                            Provider<LoginScene.Factory> loginFactoryProvider,
                            Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                            SetCurrentAccountUseCase setCurrentAccountUseCase,
                            PreferencesScene.Factory preferencesSceneFactory,
                            PreferencesService.Factory stageContextFactory,
                            ArgsResolver<AccountRawArgs, AccountParsedArgs> resolver,
                            @Assisted AccountRawArgs args) {
        super(stage,
                inflater,
                splashScreenFactory,
                loginStageContextFactory,
                loginFactoryProvider,
                exceptionFactoryProvider,
                setCurrentAccountUseCase,
                resolver,
                args);
        this.preferencesSceneFactory = preferencesSceneFactory;
        this.stageContextFactory = stageContextFactory;
    }

    @StageScope
    @AssistedFactory
    public interface Factory {
        PreferencesStage create(AccountRawArgs args);
    }

    @Override
    protected CompletableFuture<AbstractScene> showContent(AccountParsedArgs parsedArgs) {
        final var stageContext = stageContextFactory.create(new PreferencesService.State().withInitialSelectedAccountId(parsedArgs.accountId()));
        final var preferencesScene = preferencesSceneFactory.create(stageContext);
        return CompletableFuture.completedFuture(preferencesScene);
    }
}
