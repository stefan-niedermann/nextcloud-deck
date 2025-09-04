package it.niedermann.nextcloud.deck.repository;

import android.content.Context;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Stack;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.model.full.FullStack;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.DataPropagationHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.StackDataProvider;

public class StackRepository extends AbstractRepository {

    public StackRepository(@NonNull Context context) {
        super(context);
    }


    public LiveData<List<Stack>> getStacksForBoard(long accountId, long localBoardId) {
        return dataBaseAdapter.getStacksForBoard(accountId, localBoardId);
    }

    public LiveData<FullStack> getStack(long accountId, long localStackId) {
        return dataBaseAdapter.getStack(accountId, localStackId);
    }

    public void countCardsInStackDirectly(long accountId, long localStackId, @NonNull IResponseCallback<Integer> callback) {
        dbReadHighPriorityExecutor.submit(() -> dataBaseAdapter.countCardsInStackDirectly(accountId, localStackId, callback));
    }

    public void createStack(long accountId, long boardLocalId, @NonNull String title, @NonNull IResponseCallback<FullStack> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            DeckLog.info("Create Stack in account", accountId, "on board with local ID ", boardLocalId);
            Stack stack = new Stack(title, boardLocalId);
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, stack.getBoardId());
            FullStack fullStack = new FullStack();
            stack.setOrder(dataBaseAdapter.getHighestStackOrderInBoard(stack.getBoardId()) + 1);
            stack.setAccountId(accountId);
            stack.setBoardId(board.getLocalId());
            fullStack.setStack(stack);
            fullStack.setAccountId(accountId);
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).createEntity(new StackDataProvider(null, board), fullStack, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void deleteStack(long accountId, long boardLocalId, long stackLocalId, @NonNull IResponseCallback<EmptyResponse> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            FullStack fullStack = dataBaseAdapter.getFullStackByLocalIdDirectly(stackLocalId);
            FullBoard board = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardLocalId);
            new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).deleteEntity(new StackDataProvider(null, board), fullStack, ResponseCallback.from(account, callback));
        });
    }

    @AnyThread
    public void updateStackTitle(long localStackId, @NonNull String newTitle, @NonNull IResponseCallback<FullStack> callback) {
        dbWriteHighPriorityExecutor.submit(() -> {
            FullStack stack = dataBaseAdapter.getFullStackByLocalIdDirectly(localStackId);
            FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(stack.getAccountId(), stack.getStack().getBoardId());
            Account account = dataBaseAdapter.getAccountByIdDirectly(stack.getAccountId());
            stack.getStack().setTitle(newTitle);
            updateStack(account, fullBoard, stack, callback);
        });
    }

    @AnyThread
    private void updateStack(@NonNull Account account, @NonNull FullBoard board, @NonNull FullStack stack, @NonNull IResponseCallback<FullStack> callback) {
        dbWriteHighPriorityExecutor.submit(() -> new DataPropagationHelper(getServerAdapter(account), dataBaseAdapter, dbWriteHighPriorityExecutor).updateEntity(new StackDataProvider(null, board), stack, ResponseCallback.from(account, callback)));
    }

    @WorkerThread
    public Stack getStackDirectly(long stackLocalId) {
        return dataBaseAdapter.getStackByLocalIdDirectly(stackLocalId);
    }

    /**
     * Reorders stacks and ensures order validity
     *
     * @param accountId    The ID of the Account
     * @param boardLocalId The ID of the Board the stack is in
     * @param stackLocalId The ID of the stack to move
     * @param moveToRight  <code>true</code> to move right, <code>false</code> to move left
     */
    @AnyThread
    public void reorderStack(long accountId, long boardLocalId, long stackLocalId, boolean moveToRight) {
        dbWriteHighPriorityExecutor.submit(() -> {
            final Account account = dataBaseAdapter.getAccountByIdDirectly(accountId);
            final FullBoard fullBoard = dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, boardLocalId);
            final List<FullStack> stacks = dataBaseAdapter.getFullStacksForBoardDirectly(accountId, boardLocalId);

            int lastOrderValue = -1;
            boolean moveDone = false;
            for (int i = 0; i < stacks.size(); i++) {
                FullStack s = stacks.get(i);
                boolean currentStackChanged = false;
                // ensure order validity
                if (lastOrderValue >= s.getStack().getOrder()) {
                    s.getStack().setOrder(lastOrderValue + 1);
                    currentStackChanged = true;
                }
                lastOrderValue = s.getStack().getOrder();

                if (!moveDone && i < stacks.size() - 1 && (moveToRight ? s : stacks.get(i + 1)).getLocalId() == stackLocalId) {
                    FullStack rightStack = stacks.get(i + 1);
                    // fix orders
                    rightStack.getStack().setOrder(lastOrderValue);
                    s.getStack().setOrder(lastOrderValue + 1);
                    // update the other one
                    updateStack(account, fullBoard, rightStack, IResponseCallback.empty());
                    // ensure the current one is updated as well
                    currentStackChanged = true;
                    stacks.set(i, stacks.get(i + 1));
                    stacks.set(i + 1, s);
                    moveDone = true;
                }

                if (currentStackChanged) {
                    updateStack(account, fullBoard, s, IResponseCallback.empty());
                }
            }
        });
    }
}
