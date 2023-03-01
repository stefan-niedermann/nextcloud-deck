package it.niedermann.nextcloud.deck.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.persistence.sync.SyncManager;
import it.niedermann.nextcloud.deck.ui.archivedboards.ArchivedBoardsViewModel;
import it.niedermann.nextcloud.deck.ui.board.accesscontrol.AccessControlViewModel;
import it.niedermann.nextcloud.deck.ui.board.managelabels.LabelsViewModel;
import it.niedermann.nextcloud.deck.ui.card.NewCardViewModel;
import it.niedermann.nextcloud.deck.ui.card.comments.CommentsViewModel;
import it.niedermann.nextcloud.deck.ui.stack.StackViewModel;

/**
 * To be used for {@link ViewModel}s which need an {@link SyncManager} instance
 */
public abstract class SyncViewModel extends BaseViewModel {

    protected final Account account;
    protected final SyncManager syncManager;

    public SyncViewModel(@NonNull Application application,
                         @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        this(application, account, new SyncManager(application, account));
    }

    public SyncViewModel(@NonNull Application application,
                         @NonNull Account account,
                         @NonNull SyncManager syncManager) {
        super(application, syncManager);
        this.account = account;
        this.syncManager = syncManager;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final Application application;
        private final Account account;

        public Factory(@NonNull Application application, @NonNull Account account) {
            this.application = application;
            this.account = account;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            try {
                if (modelClass == AccessControlViewModel.class) {
                    return (T) new AccessControlViewModel(application, account);
                }
                if (modelClass == ArchivedBoardsViewModel.class) {
                    return (T) new ArchivedBoardsViewModel(application, account);
                }
                if (modelClass == CommentsViewModel.class) {
                    return (T) new CommentsViewModel(application, account);
                }
                if (modelClass == LabelsViewModel.class) {
                    return (T) new LabelsViewModel(application, account);
                }
                if (modelClass == NewCardViewModel.class) {
                    return (T) new NewCardViewModel(application, account);
                }
                if (modelClass == StackViewModel.class) {
                    return (T) new StackViewModel(application, account);
                }
                throw new IllegalArgumentException(getClass().getSimpleName() + " can not instantiate " + modelClass.getSimpleName());
            } catch (NextcloudFilesAppAccountNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
