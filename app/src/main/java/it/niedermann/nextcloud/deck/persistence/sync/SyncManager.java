package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;

public class SyncManager {


    private DataBaseAdapter dataBaseAdapter;
    private ServerAdapter serverAdapter;
    private Context applicationContext;
    private Activity sourceActivity;

    public SyncManager(Context applicationContext, Activity sourceActivity) {
        this.applicationContext = applicationContext.getApplicationContext();
        this.sourceActivity = sourceActivity;
        dataBaseAdapter = new DataBaseAdapter(this.applicationContext);
        this.serverAdapter = new ServerAdapter(this.applicationContext, sourceActivity);
    }

    private void doAsync(Runnable r) {
        new Thread(r).start();
    }

    public void synchronize(IResponseCallback<Boolean> responseCallback) {
        final long accountId = responseCallback.getAccount().getId();
        doAsync(() -> {
            SharedPreferences lastSyncPref = applicationContext.getSharedPreferences(
                    applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
            long lastSync = lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L);
            Date lastSyncDate = new Date(lastSync);
            Date now = new Date();

            new SyncHelper(serverAdapter, dataBaseAdapter, new IResponseCallback<Boolean>(responseCallback.getAccount()) {
                @Override
                public void onResponse(Boolean response) {
                    //TODO activate when done dev
//                lastSyncPref.edit().putLong(LAST_SYNC_KEY, now.getTime()).apply();
                    responseCallback.onResponse(response);
                }

                @Override
                public void onError(Throwable throwable) {
                    responseCallback.onError(throwable);
                }
            }).doSyncFor(new BoardDataProvider());
        });
    }

//    private void synchronizeCardOf(final FullStack stack, final Board syncedBoard, final IResponseCallback<Boolean> responseCallback) {
//        //sync cards
//        Account account = responseCallback.getAccount();
//        long accountId = account.getId();
//        FullStack syncedStack = dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, syncedBoard.getLocalId(), stack.getStack().getId());
//
//        for (FullCard c : stack.getCards()) {
//            DeckLog.log("requesting Card: " + c.getCard().getTitle());
//            serverAdapter.getCard(accountId, syncedBoard.getId(), syncedStack.getStack().getId(), c.getCard().getId(), new IResponseCallback<FullCard>(account) {
//                @Override
//                public void onResponse(FullCard card) {
//
//                    List<User> assignedUsers = card.getAssignedUsers();
//                    List<Label> labels = card.getLabels();
//                    card.getCard().setStackId(syncedStack.getStack().getLocalId());
//                    FullCard existingCard = dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, card.getCard().getId());
//                    if (existingCard == null) {
//                        DeckLog.log("creating Card...");
//                        dataBaseAdapter.createCard(accountId, card.getCard());
//                    } else {
//                        DeckLog.log("updating Card...");
//                        dataBaseAdapter.updateCard(applyUpdatesFromRemote(existingCard.getCard(), card.getCard(), accountId));
//                    }
//
//                    existingCard = dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, card.getCard().getId());
//                    dataBaseAdapter.createJoinStackWithCard(existingCard.getCard().getLocalId(), syncedStack.getStack().getLocalId());
//                    existingCard.setLabels(new ArrayList<>());
//                    existingCard.setAssignedUsers(new ArrayList<>());
//
//                    ArrayList<User> existingUsers = new ArrayList<>();
//                    dataBaseAdapter.deleteJoinedUsersForCard(existingCard.getCard().getLocalId());
//                    for (User user : assignedUsers) {
//                        User existingUser = dataBaseAdapter.getUserByRemoteIdDirectly(accountId, user.getId());
//                        if (existingUser == null) {
//                            DeckLog.log("creating user: " + user.getUid());
//                            dataBaseAdapter.createUser(accountId, user);
//                            existingUser = dataBaseAdapter.getUserByRemoteIdDirectly(accountId, user.getId());
//                        } else {
//                            DeckLog.log("updating user: " + user.getUid());
//                            existingUser = applyUpdatesFromRemote(existingUser, user, accountId);
//                            dataBaseAdapter.updateUser(accountId, existingUser);
//                        }
//                        dataBaseAdapter.createJoinCardWithUser(existingUser.getLocalId(), existingCard.getCard().getLocalId());
//                        existingUsers.add(existingUser);
//                    }
//                    ArrayList<Label> existingLabels = new ArrayList<>();
//                    dataBaseAdapter.deleteJoinedLabelsForCard(existingCard.getCard().getLocalId());
//                    for (Label label : labels) {
//                        Label existingLabel = dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, label.getId());
//                        if (existingLabel == null) {
//                            DeckLog.log("creating Label: " + label.getTitle());
//                            dataBaseAdapter.createLabel(accountId, label);
//                        } else {
//                            DeckLog.log("updating Label: " + label.getTitle());
//                            existingLabel = applyUpdatesFromRemote(existingLabel, label, accountId);
//                            dataBaseAdapter.updateLabel(accountId, existingLabel);
//                        }
//                        dataBaseAdapter.createJoinCardWithLabel(existingLabel.getLocalId(), existingCard.getCard().getLocalId());
//                        existingLabels.add(existingLabel);
//                    }
//
//                    existingCard.setAssignedUsers(existingUsers);
//                    existingCard.setLabels(existingLabels);
//                    dataBaseAdapter.updateCard(existingCard.getCard());
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//                    responseCallback.onError(throwable);
//                }
//            });
//        }
//    }

    private <T> IResponseCallback<T> wrapCallForUi(IResponseCallback<T> responseCallback) {
        Account account = responseCallback.getAccount();
        if (account == null || account.getId() == null) {
            throw new IllegalArgumentException("Bro. Please just give me a damn Account!");
        }
        return new IResponseCallback<T>(responseCallback.getAccount()) {
            @Override
            public void onResponse(T response) {
                sourceActivity.runOnUiThread(() -> {
                    fillAccountIDs(response);
                    responseCallback.onResponse(response);
                });
            }

            @Override
            public void onError(Throwable throwable) {
                responseCallback.onError(throwable);
            }
        };
    }

    private <T extends AbstractRemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if (!localEntity.getId().equals(remoteEntity.getId())
                || !accountId.equals(localEntity.getAccountId())) {
            throw new IllegalArgumentException("IDs of Account or Entity are not matching! WTF are you doin?!");
        }
        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }

    public void hasAccounts(IResponseCallback<Boolean> responseCallback) {
        dataBaseAdapter.hasAccounts(responseCallback);
    }

    public void createAccount(String accoutName, IResponseCallback<Account> responseCallback) {
        doAsync(() -> dataBaseAdapter.createAccount(accoutName, responseCallback));
    }

    public void deleteAccount(long id) {
        dataBaseAdapter.deleteAccount(id);
    }

    public void updateAccount(Account account) {
        dataBaseAdapter.updateAccount(account);
    }

    public LiveData<Account> readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    public LiveData<List<Account>> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    public void getBoards(long accountId, IResponseCallback<LiveData<List<Board>>> responseCallback) {
        dataBaseAdapter.getBoards(accountId, wrapCallForUi(responseCallback));
    }

    public void createBoard(long accountId, Board board) {
        doAsync(() -> {
            dataBaseAdapter.createBoard(accountId, board);
            serverAdapter.createBoard(accountId, board);
        });
    }

    public void deleteBoard(Board board) {

    }

    public void updateBoard(Board board) {

    }

    public void getStacks(long accountId, long localBoardId, IResponseCallback<LiveData<List<FullStack>>> responseCallback) {
        dataBaseAdapter.getStacks(accountId, localBoardId, wrapCallForUi(responseCallback));
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return dataBaseAdapter.getStack(accountId, localStackId);
    }

    public void createStack(long accountId, Stack stack) {
        dataBaseAdapter.createStack(accountId, stack);
        //TODO implement
    }

    public void deleteStack(Stack stack) {

    }

    public void updateStack(Stack stack) {

    }

    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<LiveData<FullCard>> responseCallback) {
        dataBaseAdapter.getCard(accountId, boardId, stackId, cardId, wrapCallForUi(responseCallback));
    }

    public void createCard(long accountId, Card card) {

    }

    public void deleteCard(Card card) {

    }

    public void updateCard(Card card) {

    }
}
