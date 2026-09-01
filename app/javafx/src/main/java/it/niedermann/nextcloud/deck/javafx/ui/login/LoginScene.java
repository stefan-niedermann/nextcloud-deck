package it.niedermann.nextcloud.deck.javafx.ui.login;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.app.shared.di.model.BuildConfig;
import it.niedermann.nextcloud.deck.domain.sync.SyncScheduler;
import it.niedermann.nextcloud.deck.javafx.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.ui.shared.AbstractScene;
import it.niedermann.nextcloud.deck.javafx.ui.shared.services.ThemeService;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class LoginScene extends AbstractScene {

    private static final Logger logger = Logger.getLogger(LoginScene.class.getName());

    @FXML
    private StackPane featureHost;
    @FXML
    private Pane progressHost;
    @FXML
    private ProgressBar progress;

    private final LoginService stageContext;

    private final WebLoginV2Feature.Factory webLoginFactory;
    private final AppTokenLoginFeature.Factory appTokenFactory;

    @AssistedFactory
    public interface Factory {
        LoginScene create(LoginService stageContext);
    }

    @AssistedInject
    public LoginScene(
            Inflater inflater,
            WebLoginV2Feature.Factory webLoginFactory,
            AppTokenLoginFeature.Factory appTokenFactory,
            ThemeService themeService,
            HostServices hostServices,
            BuildConfig buildConfig,
            SyncScheduler syncScheduler,
            @Assisted LoginService stageContext
    ) {
        super(themeService, hostServices, buildConfig, syncScheduler, inflater);

        this.stageContext = stageContext;
        this.webLoginFactory = webLoginFactory;
        this.appTokenFactory = appTokenFactory;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        featureHost.managedProperty().bind(featureHost.visibleProperty());
        progressHost.managedProperty().bind(progressHost.visibleProperty());

        final var uiStateDisposable = Flowable.fromPublisher(stageContext.getState())
                .observeOn(JavaFxScheduler.platform())
                .subscribe(state -> {
                    if (state.syncStatus().isPresent()) {
                        featureHost.setVisible(false);
                        progressHost.setVisible(true);
                        final var oldDisposable = (Disposable) featureHost.getUserData();
                        if (oldDisposable != null) {
                            oldDisposable.dispose();
                            featureHost.setUserData(null);
                        }
                        featureHost.getChildren().clear();

                        final var syncStatus = state.syncStatus().get();
                        if (syncStatus.boardsFinished() > 0) {
                            this.progress.setProgress(Math.min(1, (double) syncStatus.boardsFinished() / syncStatus.boardsTotal()));
                        } else {
                            progress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
                        }

                    } else {
                        featureHost.setVisible(true);
                        progressHost.setVisible(false);
                        progress.setProgress(0);

                        switch (state.method()) {
                            case APPTOKEN -> {
                                final var oldDisposable = (Disposable) featureHost.getUserData();
                                if (oldDisposable != null && !(oldDisposable instanceof AppTokenLoginFeature)) {
                                    oldDisposable.dispose();
                                    featureHost.setUserData(null);
                                }
                                if (featureHost.getUserData() == null) {
                                    final var appTokenLoginFeature = appTokenFactory.create(stageContext);
                                    featureHost.setUserData(appTokenLoginFeature);
                                    featureHost.getChildren().setAll(appTokenLoginFeature.getRoot());
                                    state.url().ifPresent(appTokenLoginFeature::setUrl);
                                }
                            }
                            case WEBLOGIN_FLOW_V2 -> {
                                final var oldDisposable = (Disposable) featureHost.getUserData();
                                if (oldDisposable != null && !(oldDisposable instanceof WebLoginV2Feature)) {
                                    oldDisposable.dispose();
                                    featureHost.setUserData(null);
                                }
                                if (featureHost.getUserData() == null) {
                                    final var webLoginV2Feature = webLoginFactory.create(stageContext);
                                    featureHost.setUserData(webLoginV2Feature);
                                    featureHost.getChildren().setAll(webLoginV2Feature.getRoot());
                                    state.url().ifPresent(webLoginV2Feature::setUrl);
                                }
                            }
                        }
                    }
                });

        addDisposable(uiStateDisposable);
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just(resources.getString("login.title"));
    }
}
