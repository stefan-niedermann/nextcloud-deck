package it.niedermann.nextcloud.deck.deprecated.repository;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.BoardDataProvider;

@SuppressWarnings("WeakerAccess")
public class BoardRepository extends AbstractRepository {

    public BoardRepository(@NonNull Context context) {
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

    public Long getBoardLocalIdByAccountAndCardRemoteIdDirectly(long accountId, long cardRemoteId) {
        return dataBaseAdapter.getBoardLocalIdByAccountAndCardRemoteIdDirectly(accountId, cardRemoteId);
    }

    public void createBoard(@NonNull Account account, @NonNull Board board, @NonNull IResponseCallback<FullBoard> callback) {
        final var serverAdapter = getServerAdapter(account);
        dbWriteHighPriorityExecutor.submit(() -> {
            final User owner = dataBaseAdapter.getUserByUidDirectly(account.getId(), account.getUserName());
            if (owner == null) {
                StringBuilder sb = buildOwnerNullMessage(account);
                callback.onError(new IllegalStateException(sb.toString()));
            } else {
                final FullBoard fullBoard = new FullBoard();
                board.setOwnerId(owner.getLocalId());
                fullBoard.setOwner(owner);
                fullBoard.setBoard(board);
                board.setAccountId(account.getId());
                fullBoard.setAccountId(account.getId());
                new DataPropagationHelper(serverAdapter, dataBaseAdapter, dbWriteHighPriorityExecutor)
                        .createEntity(new BoardDataProvider(), fullBoard, ResponseCallback.from(account, callback));
            }
        });
    }

    @NonNull
    private StringBuilder buildOwnerNullMessage(@NonNull Account account) {
        StringBuilder sb = new StringBuilder("Owner is null. This can be the case if the Deck app has never before been opened in the webinterface. More:");
        sb.append("\naccount_id:");
        sb.append(account.getId());
        sb.append("\nusername:");
        sb.append(account.getUserName());

        sb.append("\nList of available Users:");
        sb.append(account.getUserName());
        List<User> allUsers = dataBaseAdapter.getAllUsersDirectly();
        if (allUsers != null) {
            for (User u : allUsers) {
                sb.append("\nuid:");
                sb.append(u.getUid());
                sb.append(" | account_id:");
                sb.append(u.getAccountId());
            }
        } else {
            sb.append("[none]");
        }
        return sb;
    }

    public void deleteBoard(@NonNull Board board, @NonNull IResponseCallback<EmptyResponse> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            long accountId = board.getAccountId();
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, board.getLocalId());
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).deleteEntity(new BoardDataProvider(), fullBoard, ResponseCallback.from(account, callback));
        });
    }

    public void updateBoard(@NonNull FullBoard board, @NonNull IResponseCallback<FullBoard> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            long accountId = board.getAccountId();
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).updateEntity(new BoardDataProvider(), board, ResponseCallback.from(account, callback));
        });
    }
}
