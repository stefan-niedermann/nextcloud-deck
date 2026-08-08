package it.niedermann.nextcloud.deck.javafx.services.stage;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.AuthenticatedAccount;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.AppTokenLoginFeature;
import it.niedermann.nextcloud.deck.javafx.ui.controller.features.WebLoginV2Feature;


public class LoginStageContext extends Store<LoginStageContext.State, LoginStageContext.Action> implements
        AppTokenLoginFeature.ViewModel,
        WebLoginV2Feature.ViewModel {

    private final ImportAccountUseCase importAccountUseCase;
    private final CompletableFuture<Account.ID> importedAccount = new CompletableFuture<>();

    @AssistedInject
    public LoginStageContext(
            StoreLogger storeLogger,
            ImportAccountUseCase importAccountUseCase,
            @Assisted URL initialUrl
    ) {
        super(storeLogger, new State(AuthenticationMethod.WEBLOGIN_FLOW_V2, Optional.ofNullable(initialUrl), Optional.empty()));
        this.importAccountUseCase = importAccountUseCase;

        on(Action.SyncStatusUpdated.class, (state, action) -> new State(state.method(), state.url(), Optional.of(action.syncStatus())));
        on(Action.AuthenticationFailed.class, (state, action) -> new State(AuthenticationMethod.APPTOKEN, Optional.ofNullable(action.url()), Optional.empty()));
        on(Action.ImportSuccessful.class, (state, action) -> new State(AuthenticationMethod.WEBLOGIN_FLOW_V2, Optional.ofNullable(initialUrl), Optional.empty()));

        effect(Action.AccountAuthenticated.class, (state, action) -> {
            return Flowable.fromPublisher(importAccountUseCase.execute(action.account()))
                    .doOnNext(status -> dispatch(new Action.SyncStatusUpdated(status)))
                    .lastOrError()
                    .<Optional<? extends Action>>map(syncStatus -> Optional.of(new Action.ImportSuccessful(syncStatus.account().id())))
                    .onErrorReturn(throwable -> Optional.of(new Action.ImportFailed(throwable)))
                    .toCompletionStage()
                    .toCompletableFuture();
        });

        effect(Action.ImportFailed.class, (state, action) -> {
            // TODO Show Exception Dialog
            return CompletableFuture.completedFuture(Optional.empty());
        });

        effect(Action.ImportSuccessful.class, (state, action) -> {
            importedAccount.complete(action.accountId());
            return CompletableFuture.completedFuture(Optional.empty());
        });
    }

    @Override
    public void onAccountAuthenticated(AuthenticatedAccount account) {
        dispatch(new LoginStageContext.Action.AccountAuthenticated(account));
    }

    @Override
    public void onAccountAuthenticationFailed(URL url, Throwable exception) {
        dispatch(new LoginStageContext.Action.AuthenticationFailed(url, exception));
    }

    @AssistedFactory
    public interface Factory {
        LoginStageContext create(URL url);
    }

    public CompletableFuture<Account.ID> getImportedAccount() {
        return this.importedAccount;
    }

    public record State(
            AuthenticationMethod method,
            Optional<URL> url,
            Optional<SyncStatus> syncStatus
    ) {
    }

    public enum AuthenticationMethod {
        WEBLOGIN_FLOW_V2,
        APPTOKEN,
    }

    public sealed interface Action {

        record AccountAuthenticated(AuthenticatedAccount account) implements Action {
        }

        record AuthenticationFailed(URL url, Throwable exception) implements Action {
        }

        record SyncStatusUpdated(SyncStatus syncStatus) implements Action {
        }

        record ImportSuccessful(Account.ID accountId) implements Action {
        }

        record ImportFailed(Throwable throwable) implements Action {
        }
    }
}
