package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.full.FullBoard;

@SuppressWarnings("WeakerAccess")
public class BoardsRepository extends AbstractRepository {

    public BoardsRepository(@NonNull Context context) {
        super(context);
    }

    @ColorInt
    @WorkerThread
    public Integer getBoardColorDirectly(long accountId, long localBoardId) {
        return dataBaseAdapter.getBoardColorDirectly(accountId, localBoardId);
    }

    public LiveData<FullBoard> getFullBoardById(@NonNull Account account, long localId) {
        return dataBaseAdapter.getFullBoardById(account.getId(), localId);
    }

    /// @param accountId ID of the account
    /// @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
    /// @return all archived or non-archived <code>Board</code>s depending on <code>archived</code> parameter
    public LiveData<List<Board>> getBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getBoards(accountId, archived);
    }

    /// @param accountId ID of the account
    /// @param archived  Decides whether only archived or not-archived boards for the specified account will be returned
    /// @return all archived or non-archived <code>FullBoard</code>s depending on <code>archived</code> parameter
    public LiveData<List<FullBoard>> getFullBoards(long accountId, boolean archived) {
        return dataBaseAdapter.getFullBoards(accountId, archived);
    }

    public LiveData<Boolean> hasArchivedBoards(long accountId) {
        return dataBaseAdapter.hasArchivedBoards(accountId);
    }

    /// Get all non-archived  <code>FullBoard</code>s with edit permissions for the specified account.
    /// @param accountId ID of the account
    /// @return all non-archived <code>Board</code>s with edit permission
    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return dataBaseAdapter.getBoardsWithEditPermission(accountId);
    }

    public LiveData<FullBoard> getFullBoardById(Long accountId, Long localId) {
        return dataBaseAdapter.getFullBoardById(accountId, localId);
    }

    public LiveData<Board> getBoardByRemoteId(long accountId, long remoteId) {
        return dataBaseAdapter.getBoardByRemoteId(accountId, remoteId);
    }
}
