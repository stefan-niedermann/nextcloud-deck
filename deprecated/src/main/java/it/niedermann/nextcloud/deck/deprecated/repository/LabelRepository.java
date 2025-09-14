package it.niedermann.nextcloud.deck.deprecated.repository;

import static java.util.Collections.emptyList;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;

import com.nextcloud.android.sso.api.EmptyResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.remote.api.IResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.providers.LabelDataProvider;

@AnyThread
public class LabelRepository extends AbstractRepository {

    public LabelRepository(@NonNull Context context) {
        super(context);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId) {
        return findProposalsForLabelsToAssign(accountId, boardId, -1L);
    }

    public LiveData<List<Label>> findProposalsForLabelsToAssign(final long accountId, final long boardId, long notAssignedToLocalCardId) {
        return dataBaseAdapter.findProposalsForLabelsToAssign(accountId, boardId, notAssignedToLocalCardId);
    }

    public LiveData<List<Label>> searchNotYetAssignedLabelsByTitle(@NonNull Account account, final long boardId, final long notYetAssignedToLocalCardId, @NonNull String searchTerm) {
        return dataBaseAdapter.searchNotYetAssignedLabelsByTitle(account.getId(), boardId, notYetAssignedToLocalCardId, searchTerm);
    }

    public CompletableFuture<Label> updateLabel(@NonNull Label label) {
        return readAccountAndBoardAsync(label.getAccountId(), label.getBoardId())
                .thenComposeAsync(accountAndBoard -> updateEntityAndScheduleSync(
                        accountAndBoard.account(), label,
                        new LabelDataProvider(null, accountAndBoard.board(), emptyList())));
    }

    public CompletableFuture<Label> createLabel(long accountId, @NonNull Label label, long localBoardId) {
        return supplyAsync(() -> {

            final var existingLabel = dataBaseAdapter.getLabelByBoardIdAndTitleDirectly(label.getBoardId(), label.getTitle());

            if (existingLabel != null)
                throw new SQLiteConstraintException("label \"" + label.getTitle() + "\" already exists for this board!");

            label.setAccountId(accountId);
            return readAccountAndBoard(accountId, localBoardId);

        }, dbReadHighPriorityExecutor)
                .thenComposeAsync(accountAndBoard -> createEntityAndScheduleSync(
                        accountAndBoard.account(), label,
                        new LabelDataProvider(null, accountAndBoard.board(), null),
                        (entity, response) -> response.setBoardId(accountAndBoard.board().getLocalId())));
    }

    public CompletableFuture<EmptyResponse> deleteLabel(@NonNull Label label) {
        return readAccountAndBoardAsync(label.getAccountId(), label.getBoardId())
                .thenComposeAsync(accountAndBoard -> deleteEntityAndScheduleSync(
                        accountAndBoard.account(), label,
                        new LabelDataProvider(null, accountAndBoard.board(), emptyList())
                ));
    }

    public CompletableFuture<Integer> countCardsWithLabel(long localLabelId) {
        final var result = new CompletableFuture<Integer>();
        runAsync(() -> dataBaseAdapter.countCardsWithLabel(localLabelId, IResponseCallback.forwardTo(result)), dbWriteHighPriorityExecutor);
        return result;
    }

    private CompletableFuture<AccountAndBoard> readAccountAndBoardAsync(long accountId, long boardId) {
        return supplyAsync(() -> readAccountAndBoard(accountId, boardId), dbReadHighPriorityExecutor);
    }

    @WorkerThread
    private AccountAndBoard readAccountAndBoard(long accountId, long boardId) {
        final var account = dataBaseAdapter.getAccountByIdDirectly(accountId);
        final var board = dataBaseAdapter.getBoardByLocalIdDirectly(boardId);
        return new AccountAndBoard(account, board);
    }

    private record AccountAndBoard(
            @NonNull Account account,
            @NonNull Board board) {
    }
}
