package it.niedermann.nextcloud.deck.javafx.ui.controller.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import io.reactivex.rxjava4.core.Flowable;
import io.reactivex.rxjava4.core.Maybe;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.HasAccountsUseCase;
import it.niedermann.nextcloud.deck.domain.usecases.state.GetCurrentAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.RouteProvider;
import it.niedermann.nextcloud.deck.javafx.router.Router;
import it.niedermann.nextcloud.deck.javafx.ui.controller.SceneController;
import jakarta.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

public class SplashScreenScene extends SceneController {

    @FXML
    ProgressIndicator progressIndicator;

    private final Router router;
    private final RouteProvider routeProvider;
    private final HasAccountsUseCase hasAccountsUseCase;
    private final GetCurrentAccountUseCase getCurrentAccountUseCase;

    @Inject
    public SplashScreenScene(
            Router router,
            RouteProvider routeProvider,
            HasAccountsUseCase hasAccountsUseCase,
            GetCurrentAccountUseCase getCurrentAccountUseCase
    ) {
        this.router = router;
        this.routeProvider = routeProvider;
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
                                .map(Account::id)
                                .map(routeProvider::getMainRoute);
                    } else {
                        return Flowable.just(routeProvider.getLoginRoute());
                    }
                })
                .firstElement()
                .map(router::navigateTo)
                .flatMap(Maybe::fromCompletionStage)
                .ignoreElement()
                .subscribe();

        addDisposable(disposable);

    }
}
