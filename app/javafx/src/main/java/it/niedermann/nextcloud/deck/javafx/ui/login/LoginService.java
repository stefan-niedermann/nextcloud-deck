package it.niedermann.nextcloud.deck.javafx.ui.login;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.soabase.recordbuilder.core.RecordBuilder;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava4.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Account;
import it.niedermann.nextcloud.deck.domain.model.ImportAccount;
import it.niedermann.nextcloud.deck.domain.state.SyncStatus;
import it.niedermann.nextcloud.deck.domain.usecases.accounts.ImportAccountUseCase;
import it.niedermann.nextcloud.deck.javafx.store.Store;
import it.niedermann.nextcloud.deck.javafx.store.StoreLogger;


public class LoginService extends Store<LoginService.State, LoginService.Action> implements
        AppTokenLoginFeature.ViewModel,
        WebLoginV2Feature.ViewModel {

    private final ImportAccountUseCase importAccountUseCase;
    private final CompletableFuture<Account.ID> importedAccount = new CompletableFuture<>();

    @AssistedInject
    public LoginService(
            StoreLogger storeLogger,
            ImportAccountUseCase importAccountUseCase,
            @Assisted URL initialUrl
    ) {
        super(storeLogger, State.initial(initialUrl));
        this.importAccountUseCase = importAccountUseCase;

        on(Action.SyncStatusUpdated.class, State::reduce);
        on(Action.AuthenticationStarted.class, State::reduce);
        on(Action.AuthenticationFailed.class, State::reduce);
        on(Action.ImportSuccessful.class, (state, action) -> State.reduce(state, action, initialUrl));

        effect(Action.AccountAuthenticated.class, (state, action) -> {
            return Flowable.fromPublisher(importAccountUseCase.execute(action.account()))
                    .doOnNext(status -> {
                        System.out.println(status.toString());
                        dispatch(new Action.SyncStatusUpdated(status));
                    })
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
    public void onAuthenticationStarted() {
        dispatch(new Action.AuthenticationStarted());
    }

    @Override
    public void onAccountAuthenticated(ImportAccount account) {
        dispatch(new Action.AccountAuthenticated(account));
    }

    @Override
    public void onAccountAuthenticationFailed(URL url, Throwable exception) {
        dispatch(new Action.AuthenticationFailed(url, exception));
    }

    @AssistedFactory
    public interface Factory {
        LoginService create(URL url);
    }

    public CompletableFuture<Account.ID> getImportedAccount() {
        return this.importedAccount;
    }

    @RecordBuilder
    public record State(
            Optional<URL> url,
            AuthenticationState authenticationState
    ) implements LoginServiceStateBuilder.With {
        public static State initial(URL initialUrl) {
            return new State(Optional.ofNullable(initialUrl), new AuthenticationState.Authenticating(AuthenticationMethod.WEBLOGIN_FLOW_V2));
        }

        public static State reduce(State state, Action.AuthenticationStarted action) {
            return state.withAuthenticationState(new AuthenticationState.WaitingForImportStart());
        }

        public static State reduce(State state, Action.SyncStatusUpdated action) {
            return state.withAuthenticationState(new AuthenticationState.Importing(action.syncStatus()));
        }

        public static State reduce(State state, Action.AuthenticationFailed action) {
            return state.withUrl(Optional.ofNullable(action.url()))
                    .withAuthenticationState(new AuthenticationState.Authenticating(AuthenticationMethod.APPTOKEN));
        }

        public static State reduce(State state, Action.ImportSuccessful action, URL initialUrl) {
            return initial(initialUrl);
        }
    }

    public sealed interface AuthenticationState {
        record Authenticating(AuthenticationMethod method) implements AuthenticationState {
        }

        record WaitingForImportStart() implements AuthenticationState {
        }

        record Importing(SyncStatus syncStatus) implements AuthenticationState {
        }
    }

    public enum AuthenticationMethod {
        WEBLOGIN_FLOW_V2,
        APPTOKEN,
    }

    public sealed interface Action {

        record AuthenticationStarted() implements Action {
        }

        record AccountAuthenticated(ImportAccount account) implements Action {
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
