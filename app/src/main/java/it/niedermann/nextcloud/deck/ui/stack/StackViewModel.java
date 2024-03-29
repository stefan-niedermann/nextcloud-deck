package it.niedermann.nextcloud.deck.ui.stack;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.android.reactivelivedata.ReactiveLiveData;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.User;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullCard;
import it.niedermann.nextcloud.deck.model.internal.FilterInformation;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.ui.viewmodel.SyncViewModel;

public class StackViewModel extends SyncViewModel {

    public StackViewModel(@NonNull Application application, @NonNull Account account) throws NextcloudFilesAppAccountNotFoundException {
        super(application, account);
    }

    public void moveCard(long originAccountId, long originCardLocalId, long targetAccountId, long targetBoardLocalId, long targetStackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        syncRepository.moveCard(originAccountId, originCardLocalId, targetAccountId, targetBoardLocalId, targetStackLocalId, callback);
    }

    public LiveData<Account> getAccount(long accountId) {
        return new ReactiveLiveData<>(baseRepository.readAccount(accountId))
                .distinctUntilChanged();
    }

    public CompletableFuture<Account> getAccountFuture(long accountId) {
        return supplyAsync(() -> baseRepository.readAccountDirectly(accountId));
    }

    public LiveData<FullBoard> getFullBoard(long accountId, long boardId) {
        return new ReactiveLiveData<>(baseRepository.getFullBoardById(accountId, boardId))
                .distinctUntilChanged();
    }

    public LiveData<Integer> getBoardColor$(long accountId, long boardId) {
        return baseRepository.getBoardColor$(accountId, boardId);
    }

    public LiveData<List<FullCard>> getFullCardsForStack(long accountId, long localStackId, @Nullable FilterInformation filter) {
        return new ReactiveLiveData<>(baseRepository.getFullCardsForStack(accountId, localStackId, filter))
                .distinctUntilChanged();
    }

    public LiveData<Boolean> currentBoardHasEditPermission(long accountId, long boardId) {
        return new ReactiveLiveData<>(baseRepository.readAccount(accountId))
                .flatMap(account -> account.getServerDeckVersionAsObject().isSupported()
                        ? new ReactiveLiveData<>(baseRepository.getFullBoardById(accountId, boardId)).map(fullBoard -> fullBoard != null && fullBoard.getBoard().isPermissionEdit())
                        : new ReactiveLiveData<>(false))
                .distinctUntilChanged();
    }

    public void archiveCard(@NonNull FullCard card, @NonNull IResponseCallback<FullCard> callback) {
        syncRepository.archiveCard(card, callback);
    }


    public void deleteCard(@NonNull Card card, @NonNull IResponseCallback<EmptyResponse> callback) {
        syncRepository.deleteCard(card, callback);
    }

    public void assignUserToCard(@NonNull FullCard fullCard) {
        getAccountFuture(fullCard.getAccountId()).thenAcceptAsync(account -> syncRepository.assignUserToCard(getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard()));
    }

    public void unassignUserFromCard(@NonNull FullCard fullCard) {
        getAccountFuture(fullCard.getAccountId()).thenAcceptAsync(account -> syncRepository.unassignUserFromCard(getUserByUidDirectly(fullCard.getCard().getAccountId(), account.getUserName()), fullCard.getCard()));
    }

    private User getUserByUidDirectly(long accountId, String uid) {
        return baseRepository.getUserByUidDirectly(accountId, uid);
    }
}
