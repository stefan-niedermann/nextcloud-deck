package it.niedermann.nextcloud.deck.javafx.ui.shared;

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
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.exception.ExceptionScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginScene;
import it.niedermann.nextcloud.deck.javafx.ui.login.LoginService;
import it.niedermann.nextcloud.deck.javafx.ui.splashscreen.SplashScreenScene;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Provider;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.stage.Stage;

/// ```mermaid
/// ---
/// title: Lifecycle of a Stage
/// ---
/// flowchart TD
///     initializing((Initialize StageManager)) --> loading[Show splashscreen]
///     loading --> resolving[Subscribe to ArgsResolver]
///
///     subgraph Watching ["Watching (Reactive Stream)"]
///         resolving --> resolved{Resolving successful?}
///         resolved -- success --> detectingchanges[Check if Args changed]
///         resolved -- failure --> recovering[Recover from error]
///
///         detectingchanges -- changed --> savepromptevaluation{SavePromptGuarded}
///
///         savepromptevaluation -- allowed --> showcontent((Show content))
///     end
///
///     recovering -- success --> loading
///     recovering -- failure --> stopped((Show Error Scene))
/// ```
public abstract class AbstractStage<TRawArgs, TParsedArgs> {

    private static final Logger logger = Logger.getLogger(AbstractStage.class.getName());

    protected final Inflater inflater;
    protected final Stage stage;
    private final SplashScreenScene.Factory splashScreenFactory;
    private final LoginService.Factory loginStageContextFactory;
    private final Provider<LoginScene.Factory> loginFactoryProvider;
    private final Provider<ExceptionScene.Factory> exceptionFactoryProvider;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final ArgsResolver<TRawArgs, TParsedArgs> resolver;
    protected final TRawArgs args;

    private boolean closed = false;

    protected final AtomicReference<Object> controller = new AtomicReference<>();
    protected final AtomicReference<TParsedArgs> currentParsedArgs = new AtomicReference<>();
    protected final CompositeDisposable titleDisposables = new CompositeDisposable();
    protected final CompositeDisposable lifecycleDisposables = new CompositeDisposable();

    public AbstractStage(Stage stage,
                         Inflater inflater,
                         SplashScreenScene.Factory splashScreenFactory,
                         LoginService.Factory loginStageContextFactory,
                         Provider<LoginScene.Factory> loginFactoryProvider,
                         Provider<ExceptionScene.Factory> exceptionFactoryProvider,
                         SetCurrentAccountUseCase setCurrentAccountUseCase,
                         ArgsResolver<TRawArgs, TParsedArgs> resolver,
                         TRawArgs args) {
        this.stage = stage;
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

        this.stage.setOnHidden(_ -> closed = true);

        stage.setFullScreenExitHint("Press ESC to exit full screen");

        return cf;
    }

    private void onArgsResolved(TParsedArgs parsedArgs) {
        final var oldArgs = currentParsedArgs.getAndSet(parsedArgs);

        if (Objects.equals(oldArgs, parsedArgs)) {
            return;
        }

        // TODO Check whether parsed Arguments changed: Add // TODO comment to implement SavePromptGuarded (canDeactivate(): CompletableFuture<Void>, save(): CompletableFuture<Boolean>, dismiss(): CompletableFuture<Void>)
        final var ctrl = controller.get();
        final CompletableFuture<Boolean> canDeactivate;
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
        return this.setStageContent(splashScreenFactory.create());
    }

    /// @return [CompletableFuture] - completed when an account has successfully been imported
    protected CompletableFuture<Void> showLoginScene() {
        final var stageContext = loginStageContextFactory.create(null);
        return this.setStageContent(loginFactoryProvider.get().create(stageContext))
                .thenComposeAsync(_ -> stageContext.getImportedAccount())
                .thenComposeAsync(setCurrentAccountUseCase::execute)
                .thenApply(_ -> null);
    }

    /// @return [CompletableFuture] - completed when the content is visible
    abstract protected CompletableFuture<AbstractScene> showContent(TParsedArgs args);

    /// @return [CompletableFuture] - failed with the passed [Throwable] after it has been displayed
    private CompletableFuture<Void> showErrorScene(Throwable throwable) {
        return this.setStageContent(exceptionFactoryProvider.get().create(throwable))
                .thenComposeAsync(_ -> CompletableFuture.failedFuture(throwable));
    }

    /// @return [CompletableFuture] - completed when the content is visible
    protected CompletableFuture<Void> setStageContent(AbstractScene controller) {

        final var cf = new CompletableFuture<Void>();
        final var oldCtrl = this.controller.getAndSet(controller);

        if (oldCtrl instanceof Disposable oldDisposableCtrl && !oldDisposableCtrl.isDisposed()) {
            oldDisposableCtrl.dispose();
        }

        titleDisposables.clear();
        final var titleDisposable = controller.getTitle()
                .observeOn(JavaFxScheduler.platform())
                .subscribe(stage::setTitle);

        titleDisposables.add(titleDisposable);

        Platform.runLater(() -> {
            if (closed) {
                cf.complete(null);
                return;
            }
            try {
                stage.setScene(controller.getScene());

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