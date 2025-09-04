package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.User;

public class UserRepository extends AbstractRepository {

    public UserRepository(@NonNull Context context) {
        super(context);
    }

    @WorkerThread
    public User getUserByUidDirectly(long accountId, String uid) {
        return dataBaseAdapter.getUserByUidDirectly(accountId, uid);
    }

    public long createUser(long accountId, User user) {
        return dataBaseAdapter.createUser(accountId, user);
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForACL(final long accountId, long boardId, final int topX) {
        return dataBaseAdapter.findProposalsForUsersToAssignForACL(accountId, boardId, topX);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForACL(final long accountId, final long notYetAssignedToACL, final String constraint) {
        return dataBaseAdapter.searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedToACL, constraint);
    }

    public LiveData<List<User>> findProposalsForUsersToAssignForCards(final long accountId, long boardId, long notAssignedToLocalCardId, final int topX) {
        return dataBaseAdapter.findProposalsForUsersToAssign(accountId, boardId, notAssignedToLocalCardId, topX);
    }

    public LiveData<List<User>> searchUserByUidOrDisplayNameForCards(final long accountId, final long boardId, final long notYetAssignedToLocalCardId, final String searchTerm) {
        return dataBaseAdapter.searchUserByUidOrDisplayName(accountId, boardId, notYetAssignedToLocalCardId, searchTerm);
    }
}
