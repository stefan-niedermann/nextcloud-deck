package it.niedermann.nextcloud.deck.feature_import;


import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;
import static androidx.lifecycle.Transformations.map;
import static androidx.lifecycle.Transformations.switchMap;
import static it.niedermann.nextcloud.deck.feature_shared.R.string;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;

import com.nextcloud.android.sso.model.SingleSignOnAccount;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Objects;

import it.niedermann.nextcloud.deck.repository.AccountRepository;

public class ImportAccountViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AccountRepository accountRepository;

    private final LiveData<AccountRepository.ImportState> importState;
    private final LiveData<Throwable> lastError;

    public ImportAccountViewModel(@NonNull Application application,
                                  @NonNull SavedStateHandle savedStateHandle) {
        super(application);

        this.savedStateHandle = savedStateHandle;
        this.accountRepository = new AccountRepository(application);

        this.importState = savedStateHandle.getLiveData("importState");
        this.lastError = savedStateHandle.getLiveData("lastError");
    }

    public void importAccount(@NonNull SingleSignOnAccount account) {
        this.accountRepository
                .importAccount(account.name, account.url, account.userId, account.token)
                .subscribe(new Subscriber<>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        savedStateHandle.set("importState", null);
                        savedStateHandle.set("lastError", null);
                    }

                    @Override
                    public void onNext(AccountRepository.ImportState importState) {
                        savedStateHandle.set("importState", importState);
                    }

                    @Override
                    public void onError(Throwable t) {
                        savedStateHandle.set("lastError", lastError);
                    }

                    @Override
                    public void onComplete() {
                        savedStateHandle.set("importState", null);
                        savedStateHandle.set("lastError", null);
                    }
                });
    }

    public LiveData<String> getWelcomeMessage() {
        return fromPublisher(this.accountRepository
                .hasAccounts()
                .map(hasAccounts -> hasAccounts
                        ? R.string.welcome_text_further_accounts
                        : R.string.welcome_text)
                .map(getApplication()::getString));
    }

    public LiveData<AccountRepository.ImportState> getImportState() {
        return importState;
    }

    public LiveData<String> getStatusMessage() {
        return switchMap(importState, state -> {

            if (state == null) {

                return map(lastError, Throwable::getLocalizedMessage);

            } else {

                return new MutableLiveData<>(getApplication().getString(
                        string.progress_import,
                        Math.min(state.boardsImported() + 1, state.boardsTotal()),
                        state.boardsTotal()));
            }
        });
    }

    public LiveData<Boolean> isImporting() {
        return map(importState, Objects::isNull);
    }

    public LiveData<ImportProgress> getImportProgress() {
        return map(importState, state -> state == null
                ? new ImportProgress(0, 0, 0)
                : new ImportProgress(state.boardsImported(), state.boardsCurrentlyImporting(), state.boardsTotal()));
    }
}
