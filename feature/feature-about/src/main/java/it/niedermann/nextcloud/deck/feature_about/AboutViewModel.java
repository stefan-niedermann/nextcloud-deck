package it.niedermann.nextcloud.deck.feature_about;


import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import java.util.Collection;

import it.niedermann.nextcloud.deck.repository.AccountRepository;
import it.niedermann.nextcloud.deck.shared.model.Account;

public class AboutViewModel extends AndroidViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AccountRepository accountRepository;

    public AboutViewModel(@NonNull Application application,
                          @NonNull SavedStateHandle savedStateHandle) {
        super(application);

        this.savedStateHandle = savedStateHandle;
        this.accountRepository = new AccountRepository(application);
    }

    public LiveData<Collection<Account>> getAccounts() {
        return fromPublisher(this.accountRepository.getAccounts());
    }
}
