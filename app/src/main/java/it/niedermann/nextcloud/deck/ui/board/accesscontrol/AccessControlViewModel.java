package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

public class AccessControlViewModel extends SyncViewModel {

    public AccessControlViewModel(@NonNull Application application, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        super(application, account);
    }

    public LiveData<FullBoard> getFullBoardById(long accountId, long localId) {
        return baseRepository.getFullBoardById(accountId, localId);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, long id) {
        return baseRepository.getAccessControlByLocalBoardId(accountId, id);
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return baseRepository.getCurrentBoardColor(accountId, boardId);
    }

    public void createAccessControl(@NonNull Account account, @NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        syncRepository.createAccessControl(account.getId(), entity, callback);
    }

    public void updateAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        syncRepository.updateAccessControl(entity, callback);
    }

    public void deleteAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<Void> callback) {
        syncRepository.deleteAccessControl(entity, callback);
    }
}
