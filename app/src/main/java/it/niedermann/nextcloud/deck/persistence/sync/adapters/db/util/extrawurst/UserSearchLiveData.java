package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.extrawurst;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
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
    private List<User> foundOnServer;
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

        final String term = searchTerm;
        Account account = db.getAccountByIdDirectly(accountId);
        server.searchUser(term, new IResponseCallback<OcsUserList>(account) {
            @Override
            public void onResponse(OcsUserList response) {
                if (response == null || response.getUsers().isEmpty()){
                    return;
                }
                List<User> allFound = foundInDB == null? new ArrayList<>() : new ArrayList<>(foundInDB);
                for (OcsUser user : response.getUsers()) {
                    User existingUser = db.getUserByUidDirectly(accountId, user.getId());
                    if (existingUser == null) {
                        User newUser = new User();
                        newUser.setStatus(DBStatus.UP_TO_DATE.getId());
                        newUser.setPrimaryKey(user.getId());
                        newUser.setUid(user.getId());
                        newUser.setDisplayname(user.getDisplayName());
                        long newUserId = db.createUser(accountId, newUser);
                        newUser.setLocalId(newUserId);
                        allFound.add(newUser);
                    }
                }
                foundOnServer = allFound;
                List<User> distinctList = eliminateDuplicates(allFound);
                DeckLog.info("###DeckUserSearch: posted Server value for term " + term + ":\n" + distinctList + " \n\n from Server: " + foundOnServer + " \n\n from DB: " + foundInDB);
                postValue(distinctList);
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
            }
        });

        LiveData<List<User>> dbLiveData = db.searchUserByUidOrDisplayNameForACL(accountId, notYetAssignedInACL, term);
        addSource(dbLiveData, changedData -> {
            foundInDB = changedData;
            removeSource(dbLiveData);
            ArrayList<User> users = new ArrayList<>(foundInDB);
            if (foundOnServer != null && !foundOnServer.isEmpty()) {
                users.addAll(foundOnServer);
            }
            List<User> distinctList = eliminateDuplicates(users);
            postValue(distinctList);
            DeckLog.info("###DeckUserSearch: posted db-value for term " + term + ":\n" + distinctList + " \n\n from Server: " + foundOnServer + " \n\n from DB: " + foundInDB);
        });
    }
    private List<User> eliminateDuplicates(List<User> source) {
        List<User> retList = new ArrayList<>(source.size());
        // should be enough like this, since the account doesn't matter here, always the same.
        for (User user : source) {
            if (!retList.contains(user)){
                retList.add(user);
            }
        }
        return retList;
    }
}
