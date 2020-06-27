package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.extrawurst;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.enums.DBStatus;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUser;
import it.niedermann.nextcloud.deck.model.ocs.user.OcsUserList;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class UserSearchLiveData extends MediatorLiveData<List<User>> implements Debouncer.Callback<Long> {

    private static final int DEBOUNCE_TIME = 300; // ms
    private DataBaseAdapter db;
    private ServerAdapter server;
    private List<User> foundInDB;
    long accountId;
    String searchTerm;
    long notYetAssignedInACL;
    private Debouncer<Long> debouncer = new Debouncer<>(this, DEBOUNCE_TIME);

    public UserSearchLiveData(DataBaseAdapter db, ServerAdapter server) {
        this.db = db;
        this.server = server;
    }

    public UserSearchLiveData search(long accountId, long notYetAssignedInACL, String searchTerm) {
        this.accountId = accountId;
        this.searchTerm = searchTerm;
        this.notYetAssignedInACL = notYetAssignedInACL;
        new Thread(() -> debouncer.call(notYetAssignedInACL)).start();
        return this;
    }


    @Override
    public void call(Long key) {
        if (key!=notYetAssignedInACL){
            return;
        }


        Account account = db.getAccountByIdDirectly(accountId);
        server.searchUser(searchTerm, new IResponseCallback<OcsUserList>(account) {
            @Override
            public void onResponse(OcsUserList response) {
                if (response == null || response.getUsers().isEmpty()){
                    return;
                }
                List<User> allFound = new ArrayList<>();
                if (foundInDB != null && !foundInDB.isEmpty()) {
                    allFound.addAll(foundInDB);
                }
                for (OcsUser user : response.getUsers()) {
                    User existingUser = db.getUserByUidDirectly(accountId, user.getId());
                    if (existingUser == null) {
                        User newUser = new User();
                        newUser.setStatus(DBStatus.UP_TO_DATE.getId());
                        newUser.setPrimaryKey(user.getId());
                        newUser.setUid(user.getId());
                        newUser.setDisplayname(user.getDisplayName());
                        long newUserId = db.createUser(accountId, newUser);
                        newUser.setId(newUserId);
                        allFound.add(newUser);
                    }
                }
                postValue(allFound);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });

        LiveData<List<User>> dbLiveData = db.searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedInACL, searchTerm);
        addSource(dbLiveData, changedData -> {
            foundInDB = changedData;
            removeSource(dbLiveData);
            postValue(changedData);
        });
    }
}
