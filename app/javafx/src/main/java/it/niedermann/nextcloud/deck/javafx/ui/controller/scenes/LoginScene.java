package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.disposables.Disposable;
import it.niedermann.nextcloud.deck.javafx.services.stage.LoginStageContext;
import it.niedermann.nextcloud.deck.javafx.ui.controller.DisposableController;
import it.niedermann.nextcloud.deck.javafx.ui.controller.TitleReportable;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AppTokenLoginFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.WebLoginV2Feature;
import it.niedermann.nextcloud.deck.javafx.ui.fxml.Inflater;
import it.niedermann.nextcloud.deck.javafx.util.JavaFxScheduler;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class LoginScene extends DisposableController implements TitleReportable {

    private static final Logger logger = Logger.getLogger(LoginScene.class.getName());

    @FXML
    private StackPane featureHost;
    @FXML
    private Pane progressHost;
    @FXML
    private ProgressBar progress;

    private final LoginStageContext stageContext;

    private final Inflater inflater;
    private final WebLoginV2Feature.Factory webLoginFactory;
    private final AppTokenLoginFeature.Factory appTokenFactory;

    @AssistedFactory
    public interface Factory {
        LoginScene create(LoginStageContext stageContext);
    }

    @AssistedInject
    public LoginScene(
            Inflater inflater,
            WebLoginV2Feature.Factory webLoginFactory,
            AppTokenLoginFeature.Factory appTokenFactory,
            @Assisted LoginStageContext stageContext
    ) {
        this.stageContext = stageContext;
        this.inflater = inflater;
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
                        if (syncStatus.boardsFinishedCount() > 0) {
                            this.progress.setProgress(Math.min(1, (double) syncStatus.boardsFinishedCount() / syncStatus.boardsTotalCount()));
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
                                    final Inflater.FxBundle<AppTokenLoginFeature> fxmlBundle = inflater.inflate(appTokenFactory.create(stageContext));
                                    featureHost.setUserData(fxmlBundle.controller());
                                    state.url().ifPresent(fxmlBundle.controller()::setUrl);
                                    featureHost.getChildren().setAll(fxmlBundle.view());
                                }
                            }
                            case WEBLOGIN_FLOW_V2 -> {
                                final var oldDisposable = (Disposable) featureHost.getUserData();
                                if (oldDisposable != null && !(oldDisposable instanceof WebLoginV2Feature)) {
                                    oldDisposable.dispose();
                                    featureHost.setUserData(null);
                                }
                                if (featureHost.getUserData() == null) {
                                    final Inflater.FxBundle<WebLoginV2Feature> fxmlBundle = inflater.inflate(webLoginFactory.create(stageContext));
                                    featureHost.setUserData(fxmlBundle.controller());
                                    state.url().ifPresent(fxmlBundle.controller()::setUrl);
                                    featureHost.getChildren().setAll(fxmlBundle.view());
                                }
                            }
                        }
                    }
                });

        addDisposable(uiStateDisposable);
    }

    @Override
    public Flowable<String> getTitle() {
        return Flowable.just("Login");
    }
}
