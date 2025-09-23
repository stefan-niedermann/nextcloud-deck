package it.niedermann.nextcloud.deck;

import static androidx.lifecycle.LiveDataReactiveStreams.fromPublisher;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import it.niedermann.nextcloud.deck.repository.AccountRepository;

public class MainViewModel extends AndroidViewModel {

    private AccountRepository accountRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> hasAccounts() {
        return fromPublisher(accountRepository.hasAccounts());
    }
}
