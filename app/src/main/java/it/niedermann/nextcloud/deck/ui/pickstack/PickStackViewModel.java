package it.niedermann.nextcloud.deck.ui.pickstack;

import static androidx.lifecycle.Transformations.distinctUntilChanged;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.ui.viewmodel.BaseViewModel;

@SuppressWarnings("WeakerAccess")
public class PickStackViewModel extends BaseViewModel {

    private Account selectedAccount;
    @Nullable
    private Board selectedBoard;
    @Nullable
    private Stack selectedStack;
    private boolean contentIsSatisfied = false;
    private boolean saveInProgress = false;

    private final MutableLiveData<Boolean> submitButtonEnabled$ = new MutableLiveData<>(false);

    public PickStackViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Long> getCurrentAccountId$() {
        return baseRepository.getCurrentAccountId$();
    }

    public LiveData<Long> getCurrentBoardId$(long accountId) {
        return baseRepository.getCurrentBoardId$(accountId);
    }

    public LiveData<Long> getCurrentStackId$(long accountId, long boardId) {
        return baseRepository.getCurrentStackId$(accountId, boardId);
    }

    public LiveData<Boolean> submitButtonEnabled() {
        return distinctUntilChanged(submitButtonEnabled$);
    }

    public void setContentIsSatisfied(boolean contentIsSatisfied) {
        this.contentIsSatisfied = contentIsSatisfied;
        updateSubmitButtonState();
    }

    public void setSelected(@NonNull Account selectedAccount, @Nullable Board selectedBoard, @Nullable Stack selectedStack) {
        this.selectedAccount = selectedAccount;
        this.selectedBoard = selectedBoard;
        this.selectedStack = selectedStack;
        updateSubmitButtonState();
    }

    public void setSubmitInProgress(boolean saveInProgress) {
        this.saveInProgress = saveInProgress;
        updateSubmitButtonState();
    }

    private void updateSubmitButtonState() {
        this.submitButtonEnabled$.setValue(!saveInProgress && contentIsSatisfied && selectedBoard != null && selectedStack != null);
    }

    public Account getAccount() {
        return this.selectedAccount;
    }

    public long getBoardLocalId() {
        if (this.selectedBoard == null) {
            throw new IllegalStateException("Check submitButtonEnabled() before calling this method.");
        }
        return this.selectedBoard.getLocalId();
    }

    public long getStackLocalId() {
        if (this.selectedStack == null) {
            throw new IllegalStateException("Check submitButtonEnabled() before calling this method.");
        }
        return this.selectedStack.getLocalId();
    }

    public LiveData<Boolean> hasAccounts() {
        return baseRepository.hasAccounts();
    }

    public LiveData<List<Account>> readAccounts() {
        return baseRepository.readAccounts();
    }

    public LiveData<List<Board>> getNotArchivedBoards(long accountId) {
        return baseRepository.getBoards(accountId, false);
    }

    public LiveData<List<Board>> getBoardsWithEditPermission(long accountId) {
        return baseRepository.getBoardsWithEditPermission(accountId);
    }

    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return baseRepository.getStacksForBoard(accountId, localBoardId);
    }
}
