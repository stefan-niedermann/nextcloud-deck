package it.niedermann.nextcloud.deck.persistence.sync.adapters.db.util.extrawurst;

import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.OfflineException;
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

        final String term = String.copyValueOf(searchTerm.toCharArray());

        postCurrentFromDB(term);

        if (server.hasInternetConnection()) {
            try {
                Account account = db.getAccountByIdDirectly(accountId);
                server.searchUser(term, new IResponseCallback<OcsUserList>(account) {
                    @Override
                    public void onResponse(OcsUserList response) {
                        if (response == null || response.getUsers().isEmpty()){
                            return;
                        }
                        for (OcsUser user : response.getUsers()) {
                            User existingUser = db.getUserByUidDirectly(accountId, user.getId());
                            if (existingUser == null) {
                                User newUser = new User();
                                newUser.setStatus(DBStatus.UP_TO_DATE.getId());
                                newUser.setPrimaryKey(user.getId());
                                newUser.setUid(user.getId());
                                newUser.setDisplayname(user.getDisplayName());
                                db.createUser(accountId, newUser);
                            }
                        }
                        if (!term.equals(searchTerm)) {
                            return;
                        }
                        postCurrentFromDB(term);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onError(throwable);
                    }
                });
            } catch (OfflineException e) {
                DeckLog.logError(e);
            }
        }
    }

    private void postCurrentFromDB(String term) {
        List<User> foundInDB = db.searchUserByUidOrDisplayNameForACLDirectly(accountId, notYetAssignedInACL, term);
        postValue(foundInDB);
    }
}
