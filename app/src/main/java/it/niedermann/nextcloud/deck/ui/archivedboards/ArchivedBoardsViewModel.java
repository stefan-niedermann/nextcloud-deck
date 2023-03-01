package it.niedermann.nextcloud.deck.ui.archivedboards;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.Collections;
import java.util.List;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

public class ArchivedBoardsViewModel extends SyncViewModel {

    public ArchivedBoardsViewModel(@NonNull Application application, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        super(application, account);
    }

    public LiveData<List<Board>> getArchivedBoards(long accountId) {
        return new ReactiveLiveData<>(baseRepository.getBoards(accountId, true))
                .map(boards -> boards == null ? Collections.<Board>emptyList() : boards);
    }

    public void updateBoard(@NonNull FullBoard board, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.updateBoard(board, callback);
    }

    public void deleteBoard(@NonNull Board board, @NonNull IResponseCallback<Void> callback) {
        syncManager.deleteBoard(board, callback);
    }

    public void dearchiveBoard(@NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        syncManager.dearchiveBoard(board, callback);
    }
}
