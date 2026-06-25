package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import androidx.sqlite.SQLiteException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.reactivex.rxjava4.core.Single;
import io.reactivex.rxjava4.processors.BehaviorProcessor;
import io.reactivex.rxjava4.processors.FlowableProcessor;
import it.niedermann.nextcloud.auth.apptoken.AppTokenAuthProvider;
import it.niedermann.nextcloud.auth.webloginflowv2.AuthenticatedAccount;
import it.niedermann.nextcloud.auth.webloginflowv2.WebLoginFlowV2AuthProvider;
import it.niedermann.nextcloud.deck.domain.model.SyncStatus;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.SetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import jakarta.inject.Inject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputControl;

public class LoginScene extends SceneController {

    private static final Logger logger = Logger.getLogger(LoginScene.class.getName());

    @FXML
    private TextInputControl url;
    @FXML
    private TextInputControl username;
    @FXML
    private TextInputControl password;
    @FXML
    private ProgressBar progress;
    @FXML
    private Button submit;

    private final StageRouter router;
    private final ImportAccountUseCase importAccountUseCase;
    private final SetCurrentAccountUseCase setCurrentAccountUseCase;
    private final WebLoginFlowV2AuthProvider webLoginV2AuthProvider;
    private final AppTokenAuthProvider appTokenAuthProvider;

    private final FlowableProcessor<SyncStatus> syncStatus = BehaviorProcessor.create();
    private final FlowableProcessor<Boolean> importInProgress = BehaviorProcessor.create();

    @Inject
    public LoginScene(
            StageRouter router,
            ImportAccountUseCase importAccountUseCase,
            SetCurrentAccountUseCase setCurrentAccountUseCase,
            WebLoginFlowV2AuthProvider webLoginV2AuthProvider,
            AppTokenAuthProvider appTokenAuthProvider
    ) {
        this.router = router;
        this.importAccountUseCase = importAccountUseCase;
        this.setCurrentAccountUseCase = setCurrentAccountUseCase;
        this.webLoginV2AuthProvider = webLoginV2AuthProvider;
        this.appTokenAuthProvider = appTokenAuthProvider;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        final var importInProgressDisposable = importInProgress
                .observeOn(JavaFxScheduler.platform())
                .subscribe(importInProgress -> {

                    if (importInProgress) {

                        this.submit.setDisable(true);
                        this.progress.setDisable(false);
                        this.url.setDisable(true);
                        this.username.setDisable(true);
                        this.password.setDisable(true);

                        this.progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

                    } else {

                        this.submit.setDisable(false);
                        this.progress.setDisable(true);
                        this.url.setDisable(false);
                        this.username.setDisable(false);
                        this.password.setDisable(false);

                        this.progress.setProgress(0);

                    }
                });

        addDisposable(importInProgressDisposable);

        final var progressDisposable = importInProgress
                .filter(Boolean.TRUE::equals)
                .switchMap(_ -> syncStatus)
                .observeOn(JavaFxScheduler.platform())
                .subscribe(syncStatus -> {
                    if (syncStatus.boardsFinishedCount() > 0) {
                        this.progress.setProgress(Math.min(1, (double) syncStatus.boardsFinishedCount() / syncStatus.boardsTotalCount()));
                    }
                });

        addDisposable(progressDisposable);

//        Platform.runLater(() -> {
//            this.url.textProperty().setValue();
//            this.username.textProperty().setValue();
//            this.password.textProperty().setValue();
//            this.submit.fire();
//        });
    }

    public void submit() {

        importInProgress.onNext(true);

        final var currentlyImportingAccountId = new AtomicLong();

        final var syncStatusDisposable = Single.fromCompletionStage(
                        authenticateAccount(
                                this.url.getText(),
                                this.username.getText(),
                                this.password.getText()))

                .flatMapPublisher(authenticatedAccount ->
                        importAccountUseCase.execute(
                                authenticatedAccount.url(),
                                authenticatedAccount.username(),
                                authenticatedAccount.token()))

                .observeOn(JavaFxScheduler.platform())

                .doOnNext(status -> {
                    logger.fine(status.toString());
                    currentlyImportingAccountId.set(status.account().id());
                    syncStatus.onNext(status);
                })

                .doOnError(throwable -> {

                    importInProgress.onNext(false);

                    if (throwable.getCause() instanceof SQLiteException) {
                        // TODO Handle more gracefully: Just silently switch to this account?
                        throw new IllegalArgumentException("This account has already been imported");
                    }

                    throw throwable;
                })
                .ignoreElements()
                .subscribe(() -> setCurrentAccountUseCase.execute(currentlyImportingAccountId.get())
                        .thenApplyAsync(_ -> MainScene.class)
                        .whenCompleteAsync((route, exception) -> {

                            importInProgress.onNext(false);

                            if (exception == null) {
                                router.navigateTo(route);
                            }

                        }, Platform::runLater)
                );

        addDisposable(syncStatusDisposable);
    }

    private CompletableFuture<AuthenticatedAccount> authenticateAccount(String url,
                                                                        String username,
                                                                        String password) {
        final URL parsedUrl;
        try {
            parsedUrl = URI.create(url).toURL();

        } catch (MalformedURLException e) {
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.supplyAsync(() -> {

            try {
                return webLoginV2AuthProvider.initializeAuthentication(parsedUrl);

            } catch (IOException | URISyntaxException | UnsupportedOperationException e) {

                logger.log(Level.WARNING, e.getMessage(), e);

                final String token;
                try {
                    token = appTokenAuthProvider.generateToken(parsedUrl, username, password);
                } catch (IOException ex) {
                    throw new CompletionException(ex);
                }

                return new AuthenticatedAccount(parsedUrl, username, token);
            }
        });
    }
}
