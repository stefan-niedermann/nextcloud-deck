package it.niedermann.nextcloud.deck.persistence.sync;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckConsts;
import it.niedermann.nextcloud.deck.R;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.interfaces.RemoteEntity;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IDataBasePersistenceAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IDatabaseOnlyAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.IPersistenceAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;

public class SyncManager implements IDataBasePersistenceAdapter{



    private IDatabaseOnlyAdapter dataBaseAdapter;
    private IPersistenceAdapter serverAdapter;
    private Context applicationContext;
    private Activity sourceActivity;

    public SyncManager(Context applicationContext, Activity sourceActivity){
        this.applicationContext = applicationContext.getApplicationContext();
        this.sourceActivity = sourceActivity;
        dataBaseAdapter = new DataBaseAdapter(this.applicationContext);
        this.serverAdapter =  new ServerAdapter(this.applicationContext, sourceActivity);
    }

    private void doAsync(Runnable r){
        new Thread(r).start();
    }

    public void synchronize(long accountId, IResponseCallback<Boolean> responseCallback){
        doAsync(() -> {
                SharedPreferences lastSyncPref = applicationContext.getSharedPreferences(
                        applicationContext.getString(R.string.shared_preference_last_sync), Context.MODE_PRIVATE);
                long lastSync = lastSyncPref.getLong(DeckConsts.LAST_SYNC_KEY, 0L);
                Date lastSyncDate = new Date(lastSync);
                Date now = new Date();
                Account account = dataBaseAdapter.readAccount(accountId);

                Log.d("deck", "requesting boards...");
                // Call-Pyramid from Hell
                serverAdapter.getBoards(accountId, new IResponseCallback<List<Board>>(account) {
                    @Override
                    public void onResponse(List<Board> response) {
                        Log.d("deck", "boardCount: "+response.size());
                        for (Board b : response) {
                            Board existingBoard = dataBaseAdapter.getBoard(accountId, b.getId());
                            if (existingBoard==null) {
                                Log.d("deck", "creating board...");
                                dataBaseAdapter.createBoard(accountId, b);
                            } else {
                                Log.d("deck", "updating board...");
                                dataBaseAdapter.updateBoard(applyUpdatesFromRemote(existingBoard, b, accountId));
                            }

                            Log.d("deck", "requesting stacks...");
                            //sync stacks
                            final Board syncedBoard = dataBaseAdapter.getBoard(accountId, b.getId());
                            serverAdapter.getStacks(accountId, b.getId(), new IResponseCallback<List<Stack>>(account) {
                                @Override
                                public void onResponse(List<Stack> response) {
                                    Log.d("deck", "StackCount: "+response.size());
                                    for (Stack s: response) {
                                        s.setBoardId(syncedBoard.getLocalId());
                                        Stack existingStack = dataBaseAdapter.getStack(accountId, syncedBoard.getLocalId(), s.getId());
                                        if (existingStack==null) {
                                            Log.d("deck", "creating stack...");
                                            dataBaseAdapter.createStack(accountId, s);
                                        } else {
                                            Log.d("deck", "updating stack...");
                                            dataBaseAdapter.updateStack(applyUpdatesFromRemote(existingStack, s, accountId));
                                        }
                                        Stack syncedStack = dataBaseAdapter.getStack(accountId, syncedBoard.getLocalId(), s.getId());

                                        for (Card c :s.getCards()){
                                            Log.d("deck", "requesting Card: "+c.getTitle());
                                            serverAdapter.getCard(accountId, syncedBoard.getId(), syncedStack.getId(), c.getId(), new IResponseCallback<Card>(account) {
                                                @Override
                                                public void onResponse(Card response) {
                                                    response.setStack(syncedStack);
                                                    response.setStackId(syncedStack.getLocalId());
                                                    Card existingCard = dataBaseAdapter.getCard(accountId, syncedStack.getLocalId(), response.getId());
                                                    if (existingCard==null) {
                                                        Log.d("deck", "creating Card...");
                                                        dataBaseAdapter.createCard(accountId, response);
                                                    } else {
                                                        Log.d("deck", "updating Card...");
                                                        dataBaseAdapter.updateCard(applyUpdatesFromRemote(existingCard, response, accountId));
                                                    }
                                                }

                                                @Override
                                                public void onError(Throwable throwable) {
                                                    responseCallback.onError(throwable);
                                                }
                                            });
                                        }
                                    }
                                    //responseCallback.onResponse(true);
                                }

                                @Override
                                public void onError(Throwable throwable) {
                                    responseCallback.onError(throwable);
                                }
                            });
                        }
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

    private <T extends RemoteEntity> T applyUpdatesFromRemote(T localEntity, T remoteEntity, Long accountId) {
        if(!localEntity.getId().equals(remoteEntity.getId())
                || !remoteEntity.getAccount().getId().equals(localEntity.getAccount().getId())
                || !accountId.equals(remoteEntity.getAccount().getId())) {
            throw new IllegalArgumentException("IDs of Account or Entity are not matching! WTF are you doin?!");
        }
        remoteEntity.setLastModifiedLocal(remoteEntity.getLastModified()); // not an error! local-modification = remote-mod
        remoteEntity.setLocalId(localEntity.getLocalId());
        return remoteEntity;
    }

    public boolean hasAccounts() {
        return dataBaseAdapter.hasAccounts();
    }

    @Override
    public Account createAccount(String accoutName) {
        return dataBaseAdapter.createAccount(accoutName);
    }

    @Override
    public void deleteAccount(long id) {
        dataBaseAdapter.deleteAccount(id);
    }

    @Override
    public void updateAccount(Account account) {
        dataBaseAdapter.updateAccount(account);
    }

    @Override
    public Account readAccount(long id) {
        return dataBaseAdapter.readAccount(id);
    }

    @Override
    public List<Account> readAccounts() {
        return dataBaseAdapter.readAccounts();
    }

    @Override
    public void getBoards(long accountId, IResponseCallback<List<Board>> responseCallback) {
        this.synchronize(accountId, new IResponseCallback<Boolean>(new Account()) {
            @Override
            public void onResponse(Boolean response) {
                Log.d("decksync", "check.");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("decksync", "oops.", throwable);
            }
        });
        //serverAdapter.getBoards(accountId, responseCallback);
        dataBaseAdapter.getBoards(accountId, responseCallback);
    }

    @Override
    public void createBoard(long accountId, Board board) {
        doAsync(() -> {
            dataBaseAdapter.createBoard(accountId, board);
            serverAdapter.createBoard(accountId, board);
        });
    }

    @Override
    public void deleteBoard(Board board) {

    }

    @Override
    public void updateBoard(Board board) {

    }

    @Override
    public void getStacks(long accountId, long localBoardId, IResponseCallback<List<Stack>> responseCallback) {
        dataBaseAdapter.getStacks(accountId, localBoardId, responseCallback);
    }

    @Override
    public void getStack(long accountId, long localBoardId, long stackId, IResponseCallback<Stack> responseCallback) {
        dataBaseAdapter.getStack(accountId, localBoardId, stackId, responseCallback);
    }

    @Override
    public void createStack(long accountId, Stack stack) {
        dataBaseAdapter.createStack(accountId, stack);
        //TODO implement
    }

    @Override
    public void deleteStack(Stack stack) {

    }

    @Override
    public void updateStack(Stack stack) {

    }

    @Override
    public void getCard(long accountId, long boardId, long stackId, long cardId, IResponseCallback<Card> responseCallback) {
        dataBaseAdapter.getCard(accountId, boardId, stackId, cardId, responseCallback);
    }

    @Override
    public void createCard(long accountId, Card card) {

    }

    @Override
    public void deleteCard(Card card) {

    }

    @Override
    public void updateCard(Card card) {

    }
}
