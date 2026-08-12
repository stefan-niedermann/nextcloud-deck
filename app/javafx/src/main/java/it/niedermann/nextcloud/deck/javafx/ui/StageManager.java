package it.niedermann.nextcloud.deck.javafx.ui;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.CompositeDisposable;
import io.reactivex.rxjava4.disposables.Disposable;
import io.reactivex.rxjava4.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.app.shared.args.ArgsResolver;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.application.ThemeService;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SavePromptGuarded;
import it.niedermann.nextcloud.deck.javafx.ui.controller.TitleReportable;
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

/// Lifecycle of a stage
///
/// 1. SplashScreen with loading indicator show up
/// 2. Argument resolving with recovering for example with intermediate LoginScene
/// 3. Actual content loads, displaying a `*Scene`
/// 4. (Optionally ErrorScene)
public abstract class StageManager<TRawArgs, TParsedArgs> {

    private static final Logger logger = Logger.getLogger(StageManager.class.getName());

    protected final Inflater inflater;
    protected final Stage stage;
    private final ThemeService themeService;
    private final SplashScreenScene.Factory splashScreenFactory;
    private final LoginStageContext.Factory loginStageContextFactory;
    private final Provider<LoginScene.Factory> loginFactoryProvider;
    private final Provider<ExceptionScene.Factory> exceptionFactoryProvider;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final ArgsResolver<TRawArgs, TParsedArgs> resolver;
    protected final TRawArgs args;


    protected final AtomicReference<Object> controller = new AtomicReference<>();
    protected final AtomicReference<TParsedArgs> currentParsedArgs = new AtomicReference<>();
    protected final CompositeDisposable titleDisposables = new CompositeDisposable();
    protected final CompositeDisposable lifecycleDisposables = new CompositeDisposable();

    public StageManager(Stage stage,
                        ThemeService themeService,
                        Inflater inflater,
                        SplashScreenScene.Factory splashScreenFactory,
                        LoginStageContext.Factory loginStageContextFactory,
                        Provider<LoginScene.Factory> loginFactoryProvider,
                        Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                        SetCurrentAccountUseCase setCurrentAccountUseCase,
                        ArgsResolver<TRawArgs, TParsedArgs> resolver,
                        TRawArgs args) {
        this.stage = stage;
        this.themeService = themeService;
        this.inflater = inflater;
        this.splashScreenFactory = splashScreenFactory;
        this.loginStageContextFactory = loginStageContextFactory;
        this.loginFactoryProvider = loginFactoryProvider;
        this.exceptionFactoryProvider = exceptionFactoryProvider;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.resolver = resolver;
        this.args = args;
    }

    /// @return CompletableFuture is completed when the stage had an unrecoverable error or has been closed
    public CompletableFuture<Void> initialize() {
        final var cf = new CompletableFuture<Void>();

        lifecycleDisposables.clear();

        this.showSplashScreenScene()
                .thenRunAsync(() -> {
                    final var disposable = Flowable.fromPublisher(resolver.resolve(args))
                            .subscribeOn(Schedulers.virtual())
                            .observeOn(JavaFxScheduler.platform())
                            .subscribe(this::onArgsResolved, this::onArgsResolutionError);
                    lifecycleDisposables.add(disposable);
                })
                .exceptionally(throwable -> {
                    cf.completeExceptionally(throwable);
                    return null;
                });

        this.stage.setOnCloseRequest(_ -> {

            if (!lifecycleDisposables.isDisposed()) {
                lifecycleDisposables.dispose();
            }

            if (!titleDisposables.isDisposed()) {
                titleDisposables.dispose();
            }

            final var ctrl = controller.get();
            if (ctrl instanceof Disposable disposableCtrl && !disposableCtrl.isDisposed()) {
                disposableCtrl.dispose();
            }

            if (!cf.isDone()) {
                cf.complete(null);
            }
        });

        return cf;
    }

    private void onArgsResolved(TParsedArgs parsedArgs) {
        final var oldArgs = currentParsedArgs.getAndSet(parsedArgs);

        if (Objects.equals(oldArgs, parsedArgs)) {
            return;
        }

        // TODO Check whether parsed Arguments changed: Add // TODO comment to implement SavePromptGuarded (canDeactivate(): CompletableFuture<Void>, save(): CompletableFuture<Boolean>, dismiss(): CompletableFuture<Void>)
        final var ctrl = controller.get();
        final CompletableFuture<Void> canDeactivate;
        if (ctrl instanceof SavePromptGuarded savePromptGuarded) {
            canDeactivate = savePromptGuarded.canDeactivate();
        } else {
            canDeactivate = CompletableFuture.completedFuture(null);
        }

        canDeactivate.thenCompose(_ -> showContent(parsedArgs))
                .thenCompose(this::setStageContent)
                .exceptionally(throwable -> {
                    onArgsResolutionError(throwable);
                    return null;
                });
    }

    private void onArgsResolutionError(Throwable throwable) {
        logger.log(Level.SEVERE, "Argument resolution error", throwable);

        recoverError(throwable)
                .thenRun(this::initialize)
                .exceptionally(t -> {
                    showErrorScene(t);
                    return null;
                });
    }

    protected CompletableFuture<Void> recoverError(Throwable throwable) {
        return CompletableFuture.failedFuture(throwable);
    }

    /// @return [CompletableFuture] - completed when the splashscreen is shown
    protected CompletableFuture<Void> showSplashScreenScene() {
        final var bundle = inflater.inflate(splashScreenFactory.create());
        return this.setStageContent(bundle);
    }

    /// @return [CompletableFuture] - completed when an account has successfully been imported
    protected CompletableFuture<Void> showLoginScene() {
        final var stageContext = loginStageContextFactory.create(null);
        final var bundle = inflater.inflate(loginFactoryProvider.get().create(stageContext));
        return this.setStageContent(bundle)
                .thenComposeAsync(_ -> stageContext.getImportedAccount())
                .thenComposeAsync(setCurrentAccountUseCase::execute)
                .thenApply(_ -> null);
    }

    /// @return [CompletableFuture] - completed when the content is visible
    abstract protected CompletableFuture<Inflater.FxBundle<Object>> showContent(TParsedArgs args);

    /// @return [CompletableFuture] - failed with the passed [Throwable] after it has been displayed
    private CompletableFuture<Void> showErrorScene(Throwable throwable) {
        final var exceptionScene = exceptionFactoryProvider.get().create(throwable);
        final var bundle = inflater.inflate(exceptionScene);
        return this.setStageContent(bundle)
                .thenComposeAsync(_ -> CompletableFuture.failedFuture(throwable));
    }

    /// @return [CompletableFuture] - completed when the content is visible
    protected CompletableFuture<Void> setStageContent(Inflater.FxBundle<?> controllerBundle) {

        final var cf = new CompletableFuture<Void>();
        final var controller = controllerBundle.controller();
        final var oldCtrl = this.controller.getAndSet(controller);

        if (oldCtrl instanceof Disposable oldDisposableCtrl && !oldDisposableCtrl.isDisposed()) {
            oldDisposableCtrl.dispose();
        }

        titleDisposables.clear();
        if (controller instanceof TitleReportable titleReportable) {
            final var titleDisposable = titleReportable.getTitle()
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe(stage::setTitle);

            titleDisposables.add(titleDisposable);
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