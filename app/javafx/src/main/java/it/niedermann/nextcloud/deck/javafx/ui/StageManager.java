package it.niedermann.nextcloud.deck.javafx.ui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.di.stage.StageScope;
import it.niedermann.nextcloud.deck.javafx.exception.ExceptionUnwrapper;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

@StageScope
public class StageManager {

    private static final Logger logger = Logger.getLogger(StageManager.class.getName());

    protected final Inflater inflater;
    private final Stage stage;
    private final ThemeService themeService;
    private final SplashScreenScene.Factory splashScreenFactory;
    private final Provider<LoginScene.Factory> loginFactoryProvider;
    private final Provider<ExceptionScene.Factory> exceptionFactoryProvider;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;

    private final AtomicReference<Object> controller = new AtomicReference<>();

    @Inject
    public StageManager(Stage stage,
                        ThemeService themeService,
                        Inflater inflater,
                        SplashScreenScene.Factory splashScreenFactory,
                        Provider<LoginScene.Factory> loginFactoryProvider,
                        Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                        SetCurrentAccountUseCase setCurrentAccountUseCase) {
        this.stage = stage;
        this.themeService = themeService;
        this.inflater = inflater;
        this.splashScreenFactory = splashScreenFactory;
        this.loginFactoryProvider = loginFactoryProvider;
        this.exceptionFactoryProvider = exceptionFactoryProvider;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
    }

    public <TArgs, TParsedArgs> CompletableFuture<Void> initialize(TArgs args,
                                                                   ArgsResolver<TArgs, TParsedArgs> argsResolver,
                                                                   StageFactory<TParsedArgs> stageFactory) {

        this.stage.setOnCloseRequest(_ -> {
            final var ctrl = controller.get();
            if (ctrl instanceof Disposable disposableCtrl) {
                disposableCtrl.dispose();
            }
        });

        return this.showSplashScreenScene()
                .thenApplyAsync(_ -> args)
                .thenComposeAsync(argsResolver::resolve)

                .handleAsync((state, exception) -> switch (new ExceptionUnwrapper().unwrap(exception)) {

                    case null -> this.showContent(stageFactory, state);

                    case ArgsResolver.NoAccountConfiguredException _ -> this.showLogin()
                            .thenComposeAsync(_ -> initialize(args, argsResolver, stageFactory));

                    default -> this.showErrorScene(exception, args, state);

                })
                .thenComposeAsync(a -> a);
    }

    private CompletableFuture<Void> showSplashScreenScene() {
        final var bundle = inflater.inflate(splashScreenFactory.create());
        return this.setStageContent(bundle);
    }

    private CompletableFuture<Account.ID> showLogin() {
        final var accountImported = new CompletableFuture<Account.ID>();
        final var bundle = inflater.inflate(loginFactoryProvider.get().create(accountImported::complete));
        return this.setStageContent(bundle)
                .thenComposeAsync(_ -> accountImported)
                .thenComposeAsync(setCurrentAccountUseCase::execute);
    }

    private <T, U> CompletableFuture<U> setStageContent(Inflater.FxBundle<T> controllerBundle) {
        final var cf = new CompletableFuture<U>();
        final var controller = controllerBundle.controller();
        final var oldCtrl = this.controller.getAndSet(controller);

        if (oldCtrl instanceof Disposable oldDisposableCtrl && !oldDisposableCtrl.isDisposed()) {
            oldDisposableCtrl.dispose();
        }

        Platform.runLater(() -> {
            try {

                final var scene = new Scene(controllerBundle.view());

                themeService.bind(scene);

                // TODO Maximize stage or limit to Screen.getPrimary().getBounds();
                // TODO Perform only for first call
                stage.setWidth(1280);
                stage.setHeight(768);

                stage.setScene(scene);

                if (stage.isShowing()) {
                    cf.complete(null);
                } else {
                    stage.setOnShown(_ -> cf.complete(null));
                    stage.centerOnScreen();
                    stage.show();
                }

            } catch (UnsupportedOperationException | ClassCastException e) {
                cf.completeExceptionally(e);
            }
        });

        return cf;
    }

    private <TParsedArgs> CompletableFuture<Void> showContent(StageFactory<TParsedArgs> stageFactory, TParsedArgs initialState) {
        final var fxBundle = stageFactory.inflateContent(initialState);
        return this.setStageContent(fxBundle);
    }

    public <TArgs, TParsedArgs> CompletableFuture<Void> showErrorScene(Throwable throwable, TArgs args, TParsedArgs state) {
        // TODO Pass throwable via @AssistedFactory
        logger.log(Level.SEVERE, "Initialization error", throwable);
        final var bundle = inflater.inflate(exceptionFactoryProvider.get().create(new ExceptionScene.ViewModel() {

        }));
        return this.setStageContent(bundle);
    }

    public interface StageFactory<TParsedArgs> {
        Inflater.FxBundle<?> inflateContent(TParsedArgs initialState);
    }

}
