package it.niedermann.nextcloud.deck.feature.setup;


import static androidx.core.content.ContextCompat.getMainExecutor;
import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;
import static androidx.lifecycle.Transformations.map;
import static java.util.Objects.requireNonNull;
import static it.niedermann.nextcloud.deck.feature.shared.R.string;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.nextcloud.android.sso.model.SingleSignOnAccount;

import io.reactivex.rxjava3.disposables.Disposable;
import it.niedermann.nextcloud.deck.feature.shared.util.LiveDataWrapper;
import it.niedermann.nextcloud.deck.repository.AccountRepository;

/// @noinspection UnusedReturnValue
public class ImportAccountViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AccountRepository accountRepository;

    private static final String key_importState = "importState";
    private final LiveData<LiveDataWrapper<AccountRepository.ImportState>> importState;

    public ImportAccountViewModel(@NonNull Application application,
                                  @NonNull SavedStateHandle savedStateHandle) {
        super(application);

        this.savedStateHandle = savedStateHandle;
        this.accountRepository = new AccountRepository(application);

        this.importState = Transformations.distinctUntilChanged(savedStateHandle.getLiveData(key_importState, LiveDataWrapper.create()));
    }

    public Disposable importAccount(@NonNull SingleSignOnAccount account) {
        final var importState = requireNonNull(this.importState.getValue());
        if (!importState.isPristine()) {
            assert importState.getValue() != null;
            throw new IllegalStateException("Can not import two accounts at the same time. Already in progress: " + importState.getValue().accountName());
        }

        return this.accountRepository
                .importAccount(account.name, account.url, account.userId, account.token)
                .subscribe(state -> getMainExecutor(getApplication()).execute(() -> savedStateHandle.set(key_importState, LiveDataWrapper.next(state))),
                        error -> getMainExecutor(getApplication()).execute(() -> savedStateHandle.set(key_importState, LiveDataWrapper.error(error))),
                        () -> getMainExecutor(getApplication()).execute(() -> savedStateHandle.set(key_importState, LiveDataWrapper.completed())));
    }

    public LiveData<String> getWelcomeMessage() {
        return fromPublisher(this.accountRepository
                .hasAccounts()
                .map(hasAccounts -> hasAccounts
                        ? R.string.welcome_text_further_accounts
                        : R.string.welcome_text)
                .map(getApplication()::getString));
    }

    public LiveData<LiveDataWrapper<AccountRepository.ImportState>> getImportState() {
        return importState;
    }

    public LiveData<String> getStatusMessage() {
        return map(importState, state -> {

            if (state.hasError()) {
                assert state.getError() != null;
                return state.getError().getLocalizedMessage();

            } else if (state.hasValue()) {
                assert state.getValue() != null;
                return getApplication().getString(
                        string.progress_import,
                        Math.min(state.getValue().boardsDone() + 1, state.getValue().boardsTotal()),
                        state.getValue().boardsTotal());

            } else {
                return null;
            }
        });
    }

    public LiveData<Boolean> isImporting() {
        return map(importState, state ->
                !state.isPristine() &&
                !state.isCompleted() &&
                !state.hasError());
    }

    public LiveData<Boolean> isImportSuccessful() {
        return map(importState, state ->
                !state.isPristine() &&
                state.isCompleted() &&
                !state.hasError());
    }
}
