package it.niedermann.nextcloud.deck.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.repository.AccountRepository;
import it.niedermann.nextcloud.deck.repository.BaseRepository;
import it.niedermann.nextcloud.deck.repository.SharedExecutors;
import it.niedermann.nextcloud.deck.repository.UserRepository;

/**
 * To be used for {@link ViewModel}s which need an {@link BaseRepository} instance
 */
public abstract class BaseViewModel extends AndroidViewModel {

    protected final Application application;
    protected final BaseRepository baseRepository;
    protected final AccountRepository accountRepository;
    protected final UserRepository userRepository;
    protected final ExecutorService executor;

    public BaseViewModel(@NonNull Application application) {
        this(application, new BaseRepository(application), new AccountRepository(application), new UserRepository(application));
    }

    public BaseViewModel(@NonNull Application application,
                         @NonNull BaseRepository baseRepository,
                         @NonNull UserRepository userRepository) {
        this(application, baseRepository, userRepository, SharedExecutors.getLinkedBlockingQueueExecutor());
    }

    public BaseViewModel(@NonNull Application application,
                         @NonNull BaseRepository baseRepository,
                         @NonNull UserRepository userRepository,
                         @NonNull ExecutorService executor) {
        super(application);
        this.application = application;
        this.baseRepository = baseRepository;
        this.userRepository = userRepository;
        this.executor = executor;
    }
}
