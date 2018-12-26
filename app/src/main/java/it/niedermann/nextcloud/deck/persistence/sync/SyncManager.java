package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

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

            serverAdapter.getBoards(accountId, new IResponseCallback<List<Board>>(responseCallback.getAccount()) {
                @Override
                public void onResponse(List<Board> response) {
                    for (Board b : response) {
                        Board existingBoard = dataBaseAdapter.getBoard(accountId, b.getId()).getValue();
                        if (existingBoard == null) {
                            dataBaseAdapter.createBoard(accountId, b);
                        } else {
                            dataBaseAdapter.updateBoard(applyUpdatesFromRemote(existingBoard, b, accountId));
                        }
                        synchronizeStacksOf(b, responseCallback);
                    }
                    responseCallback.onResponse(true);
                }

                @Override
                public void onError(Throwable throwable) {
                    responseCallback.onError(throwable);
                }
            });

            //TODO activate when done dev
//                lastSyncPref.edit().putLong(LAST_SYNC_KEY, now.getTime()).apply();
        });
    }

    private void synchronizeStacksOf(final Board board, final IResponseCallback<Boolean> responseCallback) {
        //sync stacks
        Account account = responseCallback.getAccount();
        long accountId = account.getId();
        final Board syncedBoard = dataBaseAdapter.getBoard(accountId, board.getId()).getValue();
        serverAdapter.getStacks(accountId, board.getId(), new IResponseCallback<List<FullStack>>(account) {
            @Override
            public void onResponse(List<FullStack> response) {
                for (FullStack stack : response) {
                    stack.getStack().setBoardId(syncedBoard.getLocalId());
                    FullStack existingStack = dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, syncedBoard.getLocalId(), stack.getStack().getId());
                    if (existingStack == null) {
                        dataBaseAdapter.createStack(accountId, stack.getStack());
                    } else {
                        dataBaseAdapter.updateStack(applyUpdatesFromRemote(existingStack.getStack(), stack.getStack(), accountId));
                    }
                    existingStack = dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, syncedBoard.getLocalId(), stack.getStack().getId());
                    dataBaseAdapter.deleteJoinedCardsForStack(existingStack.getStack().getLocalId());
                    synchronizeCardOf(stack, syncedBoard, responseCallback);
                }
                //responseCallback.onResponse(true);
            }


            @Override
            public void onError(Throwable throwable) {
                responseCallback.onError(throwable);
            }
        });
    }

    private void synchronizeCardOf(final FullStack stack, final Board syncedBoard, final IResponseCallback<Boolean> responseCallback) {
        //sync cards
        Account account = responseCallback.getAccount();
        long accountId = account.getId();
        FullStack syncedStack = dataBaseAdapter.getFullStackByRemoteIdDirectly(accountId, syncedBoard.getLocalId(), stack.getStack().getId());

        for (FullCard c : stack.getCards()) {
            DeckLog.log("requesting Card: " + c.getCard().getTitle());
            serverAdapter.getCard(accountId, syncedBoard.getId(), syncedStack.getStack().getId(), c.getCard().getId(), new IResponseCallback<FullCard>(account) {
                @Override
                public void onResponse(FullCard card) {

                    List<User> assignedUsers = card.getAssignedUsers();
                    List<Label> labels = card.getLabels();
                    card.getCard().setStackId(syncedStack.getStack().getLocalId());
                    FullCard existingCard = dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, card.getCard().getId());
                    if (existingCard == null) {
                        DeckLog.log("creating Card...");
                        dataBaseAdapter.createCard(accountId, card.getCard());
                    } else {
                        DeckLog.log("updating Card...");
                        dataBaseAdapter.updateCard(applyUpdatesFromRemote(existingCard.getCard(), card.getCard(), accountId));
                    }

                    existingCard = dataBaseAdapter.getFullCardByRemoteIdDirectly(accountId, card.getCard().getId());
                    dataBaseAdapter.createJoinStackWithCard(existingCard.getCard().getLocalId(), syncedStack.getStack().getLocalId());
                    existingCard.setLabels(new ArrayList<>());
                    existingCard.setAssignedUsers(new ArrayList<>());

                    ArrayList<User> existingUsers = new ArrayList<>();
                    dataBaseAdapter.deleteJoinedUsersForCard(existingCard.getCard().getLocalId());
                    for (User user : assignedUsers) {
                        User existingUser = dataBaseAdapter.getUserByRemoteIdDirectly(accountId, user.getId());
                        if (existingUser == null) {
                            DeckLog.log("creating user: " + user.getUid());
                            dataBaseAdapter.createUser(accountId, user);
                            existingUser = dataBaseAdapter.getUserByRemoteIdDirectly(accountId, user.getId());
                        } else {
                            DeckLog.log("updating user: " + user.getUid());
                            existingUser = applyUpdatesFromRemote(existingUser, user, accountId);
                            dataBaseAdapter.updateUser(accountId, existingUser);
                        }
                        dataBaseAdapter.createJoinCardWithUser(existingUser.getLocalId(), existingCard.getCard().getLocalId());
                        existingUsers.add(existingUser);
                    }
                    ArrayList<Label> existingLabels = new ArrayList<>();
                    dataBaseAdapter.deleteJoinedLabelsForCard(existingCard.getCard().getLocalId());
                    for (Label label : labels) {
                        Label existingLabel = dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, label.getId());
                        if (existingLabel == null) {
                            DeckLog.log("creating Label: " + label.getTitle());
                            dataBaseAdapter.createLabel(accountId, label);
                        } else {
                            DeckLog.log("updating Label: " + label.getTitle());
                            existingLabel = applyUpdatesFromRemote(existingLabel, label, accountId);
                            dataBaseAdapter.updateLabel(accountId, existingLabel);
                        }
                        dataBaseAdapter.createJoinCardWithLabel(existingLabel.getLocalId(), existingCard.getCard().getLocalId());
                        existingLabels.add(existingLabel);
                    }

                    existingCard.setAssignedUsers(existingUsers);
                    existingCard.setLabels(existingLabels);
                    dataBaseAdapter.updateCard(existingCard.getCard());
                }

                @Override
                public void onError(Throwable throwable) {
                    responseCallback.onError(throwable);
                }
            });
        }
    }

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

    private <T extends RemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
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

    public LiveData<Account> createAccount(String accoutName) {
        return dataBaseAdapter.createAccount(accoutName);
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

    public void getStack(long accountId, long localBoardId, long stackId, IResponseCallback<LiveData<FullStack>> responseCallback) {
        dataBaseAdapter.getStackByRemoteId(accountId, localBoardId, stackId, wrapCallForUi(responseCallback));
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
