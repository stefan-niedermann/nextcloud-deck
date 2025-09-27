package it.niedermann.nextcloud.deck.setup;


import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;
import static androidx.lifecycle.Transformations.map;
import static it.niedermann.nextcloud.deck.feature_shared.R.string;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.nextcloud.android.sso.model.SingleSignOnAccount;

import java.util.ConcurrentModificationException;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import it.niedermann.nextcloud.deck.feature_shared.util.LiveDataWrapper;
import it.niedermann.nextcloud.deck.repository.AccountRepository;

/// @noinspection UnusedReturnValue
public class ImportAccountViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AccountRepository accountRepository;

    private final LiveData<ImportStateWrapper> importState;

    public ImportAccountViewModel(@NonNull Application application,
                                  @NonNull SavedStateHandle savedStateHandle) {
        super(application);

        this.savedStateHandle = savedStateHandle;
        this.accountRepository = new AccountRepository(application);

        this.importState = savedStateHandle.getLiveData("importState", new ImportStateWrapper());
    }

    public Disposable importAccount(@NonNull SingleSignOnAccount account) {
        if (Objects.requireNonNull(importState.getValue()).getValue() != null) {
            throw new ConcurrentModificationException("Can not import two accounts at the same time. Already in progress: " + importState.getValue().getValue().getAccountName());
        }

        return this.accountRepository
                .importAccount(account.name, account.url, account.userId, account.token)
                .subscribe(s -> ContextCompat.getMainExecutor(getApplication()).execute(() -> savedStateHandle.set("importState", new ImportStateWrapper(s))),
                        error -> ContextCompat.getMainExecutor(getApplication()).execute(() -> savedStateHandle.set("importState", new ImportStateWrapper(error))),
                        () -> ContextCompat.getMainExecutor(getApplication()).execute(() -> savedStateHandle.set("importState", new ImportStateWrapper())));
    }

    public LiveData<String> getWelcomeMessage() {
        return fromPublisher(this.accountRepository
                .hasAccounts()
                .map(hasAccounts -> hasAccounts
                        ? R.string.welcome_text_further_accounts
                        : R.string.welcome_text)
                .map(getApplication()::getString));
    }

    public LiveData<ImportStateWrapper> getImportState() {
        return importState;
    }

    public LiveData<String> getStatusMessage() {
        return map(importState, state -> {

            if (state.hasNoValue()) {
                return null;

            } else if (state.hasError()) {
                assert state.getError() != null;
                return state.getError().getLocalizedMessage();

            } else {
                assert state.getValue() != null;
                return getApplication().getString(
                        string.progress_import,
                        Math.min(state.getValue().getBoardsDone() + 1, state.getValue().getBoardsTotal()),
                        state.getValue().getBoardsTotal());
            }
        });
    }

    public LiveData<Boolean> isImporting() {
        return map(importState, LiveDataWrapper::hasValue);
    }
}
