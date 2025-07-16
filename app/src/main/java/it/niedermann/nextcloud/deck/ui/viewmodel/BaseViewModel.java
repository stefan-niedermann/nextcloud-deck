package it.niedermann.nextcloud.deck.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ExecutorService;

import it.niedermann.nextcloud.deck.repository.BaseRepository;
import it.niedermann.nextcloud.deck.shared.SharedExecutors;

/**
 * To be used for {@link ViewModel}s which need an {@link BaseRepository} instance
 */
public abstract class BaseViewModel extends AndroidViewModel {

    protected final Application application;
    protected final BaseRepository baseRepository;
    protected final ExecutorService executor;

    public BaseViewModel(@NonNull Application application) {
        this(application, new BaseRepository(application));
    }

    public BaseViewModel(@NonNull Application application,
                         @NonNull BaseRepository baseRepository) {
        this(application, baseRepository, SharedExecutors.getLinkedBlockingQueueExecutor());
    }

    public BaseViewModel(@NonNull Application application,
                         @NonNull BaseRepository baseRepository,
                         @NonNull ExecutorService executor) {
        super(application);
        this.application = application;
        this.baseRepository = baseRepository;
        this.executor = executor;
    }
}
