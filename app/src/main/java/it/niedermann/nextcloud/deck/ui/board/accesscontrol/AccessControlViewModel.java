package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.model.AccessControl;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.repository.AccessControlRepository;
import it.niedermann.nextcloud.deck.repository.BoardRepository;

public class AccessControlViewModel extends AndroidViewModel {

    private final BoardRepository boardRepository;
    private final AccessControlRepository accessControlRepository;

    public AccessControlViewModel(@NonNull Application application) {
        super(application);
        this.boardRepository = new BoardRepository(application);
        this.accessControlRepository = new AccessControlRepository(application);
    }

    public LiveData<FullBoard> getFullBoardById(long accountId, long localId) {
        return boardRepository.getFullBoardById(accountId, localId);
    }

    public LiveData<List<AccessControl>> getAccessControlByLocalBoardId(long accountId, long id) {
        return accessControlRepository.getAccessControlByLocalBoardId(accountId, id);
    }

    public CompletableFuture<Integer> getCurrentBoardColor(long accountId, long boardId) {
        return baseRepository.getCurrentBoardColor(accountId, boardId);
    }

    public void createAccessControl(@NonNull Account account, @NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        accessControlRepository.createAccessControl(account.getId(), entity, callback);
    }

    public void updateAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<AccessControl> callback) {
        accessControlRepository.updateAccessControl(entity, callback);
    }

    public void deleteAccessControl(@NonNull AccessControl entity, @NonNull IResponseCallback<EmptyResponse> callback) {
        accessControlRepository.deleteAccessControl(entity, callback);
    }
}
