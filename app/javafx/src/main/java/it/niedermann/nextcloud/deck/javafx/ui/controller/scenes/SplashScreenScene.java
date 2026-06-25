package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.services.stage.StageRouter;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class SplashScreenScene extends SceneController {

    @FXML
    ProgressIndicator progressIndicator;

    private final StageRouter stageRouter;
    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    public SplashScreenScene(
            StageRouter stageRouter,
            HasAccountsUseCase hasAccountsUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase
    ) {
        this.stageRouter = stageRouter;
        this.hasAccountsUseCase = hasAccountsUseCase;
        this.getCurrentAccountUseCase = getCurrentAccountUseCase;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        super.initialize(location, resources);

        progressIndicator.setProgress(ProgressBar.INDETERMINATE_PROGRESS);

        final var disposable = Flowable.fromPublisher(hasAccountsUseCase.execute())
                .switchMap(hasAccounts -> {
                    if (hasAccounts) {
                        return Flowable.fromPublisher(getCurrentAccountUseCase.execute("SplashScreen"))
                                .map(_ -> MainScene.class);
                    } else {
                        return Flowable.just(LoginScene.class);
                    }
                })
                .firstElement()
                .map(stageRouter::navigateTo)
                .flatMap(Maybe::fromCompletionStage)
                .ignoreElement()
                .subscribe();

        addDisposable(disposable);

    }
}
