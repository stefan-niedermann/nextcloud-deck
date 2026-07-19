package it.niedermann.nextcloud.deck.javafx.ui;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.controller.scenes.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Provider;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/// Takes care about having at least one account present and redirects to login scene if not.
/// Also catches error while argument parsing and redirects to error scene
public abstract class StageManager<TRawArgs> {

    private static final Logger logger = Logger.getLogger(StageManager.class.getName());

    protected final Inflater inflater;
    private final Stage stage;
    private final ThemeService themeService;
    private final HasAccountsUseCase hasAccountsUseCase;
    private final SplashScreenScene.Factory splashScreenFactory;
    private final Provider<LoginScene.Factory> loginFactoryProvider;
    private final Provider<ExceptionScene.Factory> exceptionFactoryProvider;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final TRawArgs args;

    private final AtomicReference<Object> controller = new AtomicReference<>();

    public StageManager(Stage stage,
                        ThemeService themeService,
                        Inflater inflater,
                        SplashScreenScene.Factory splashScreenFactory,
                        HasAccountsUseCase hasAccountsUseCase,
                        Provider<LoginScene.Factory> loginFactoryProvider,
                        Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                        SetCurrentAccountUseCase setCurrentAccountUseCase,
                        TRawArgs args) {
        this.stage = stage;
        this.themeService = themeService;
        this.inflater = inflater;
        this.hasAccountsUseCase = hasAccountsUseCase;
        this.splashScreenFactory = splashScreenFactory;
        this.loginFactoryProvider = loginFactoryProvider;
        this.exceptionFactoryProvider = exceptionFactoryProvider;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.args = args;

        this.stage.setOnCloseRequest(_ -> {
            final var ctrl = controller.get();
            if (ctrl instanceof Disposable disposableCtrl) {
                disposableCtrl.dispose();
            }
        });

        final var disposable = Flowable.fromPublisher(this.hasAccountsUseCase.execute())
                .subscribeOn(Schedulers.virtual())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(hasAccounts -> {
                    if (hasAccounts) {
                        initialize();
                    } else {
                        showLogin();
                    }
                });
    }

    protected CompletableFuture<Void> initialize() {
        return this.showSplashScreenScene()
                .thenApplyAsync(_ -> args)
                .thenComposeAsync(this::showContent)
                .exceptionallyComposeAsync(this::showErrorScene);
    }

    /// @return [CompletableFuture] - completed when the splashscreen is shown
    private CompletableFuture<Void> showSplashScreenScene() {
        final var bundle = inflater.inflate(splashScreenFactory.create());
        return this.setStageContent(bundle);
    }

    /// @return [CompletableFuture] - completed when an account has successfully been imported
    protected CompletableFuture<Account.ID> showLogin() {
        final var accountImported = new CompletableFuture<Account.ID>();
        final var bundle = inflater.inflate(loginFactoryProvider.get().create(accountImported::complete));
        return this.setStageContent(bundle)
                .thenComposeAsync(_ -> accountImported)
                .thenComposeAsync(setCurrentAccountUseCase::execute);
    }

    /// @return [CompletableFuture] - completed when the content is visible
    abstract protected CompletableFuture<Void> showContent(TRawArgs args);

    private CompletableFuture<Void> showErrorScene(Throwable throwable) {
        final var exceptionScene = exceptionFactoryProvider.get().create(throwable);
        final var bundle = inflater.inflate(exceptionScene);
        return this.setStageContent(bundle)
                // We will never recover from this exception
                .thenComposeAsync(_ -> new CompletableFuture<>());
    }

    /// @return [CompletableFuture] - completed when the content is visible
    protected <T> CompletableFuture<Void> setStageContent(Inflater.FxBundle<T> controllerBundle) {
        final var cf = new CompletableFuture<Void>();
        final var controller = controllerBundle.controller();
        final var oldCtrl = this.controller.getAndSet(controller);

        if (oldCtrl instanceof Disposable oldDisposableCtrl && !oldDisposableCtrl.isDisposed()) {
            oldDisposableCtrl.dispose();
        }

        Platform.runLater(() -> {
            try {

                final var scene = new Scene(controllerBundle.view());
                themeService.bind(scene);
                stage.setScene(scene);

                if (stage.isShowing()) {

                    cf.complete(null);

                } else {

                    final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
                    final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();
                    stage.setWidth(Math.min(1280, screenWidth));
                    stage.setHeight(Math.min(768, screenHeight));
                    stage.setOnShown(_ -> cf.complete(null));
                    stage.centerOnScreen();
                    stage.show();

                }

            } catch (Exception e) {
                cf.completeExceptionally(e);
            }
        });

        return cf;
    }
}
