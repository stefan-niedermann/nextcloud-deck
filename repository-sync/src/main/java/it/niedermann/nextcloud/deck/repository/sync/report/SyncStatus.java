package it.niedermann.nextcloud.deck.repository.sync.report;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import it.niedermann.nextcloud.deck.shared.model.Account;
import it.niedermann.nextcloud.deck.shared.model.Board;


public class SyncStatus {

    @NonNull
    private final Account account;
    @NonNull
    private final Step step;
    @NonNull
    private final Collection<Board> boardsInProgress = new LinkedList<>();
    @Nullable
    private final Integer boardsTotalCount;
    @Nullable
    private final Integer boardsFinishedCount;
    @Nullable
    private final Throwable error;

    public SyncStatus(@NonNull Account account) {
        this(account, Step.START, Collections.emptySet(), null, null, null);
    }

    private SyncStatus(@NonNull Account account,
                       @NonNull Step step,
                       @Nullable Collection<Board> boardsInProgress,
                       @Nullable Integer boardsTotalCount,
                       @Nullable Integer boardsFinishedCount,
                       @Nullable Throwable error) {
        this.account = account;
        this.step = step;
        this.boardsInProgress.addAll(Optional.ofNullable(boardsInProgress).orElseGet(Collections::emptyList));
        this.boardsTotalCount = boardsTotalCount;
        this.boardsFinishedCount = boardsFinishedCount;
        this.error = error;
    }

    @NonNull
    public SyncStatus withBoardTotalCount(@Nullable Integer tablesTotalCount) {
        return new SyncStatus(account, step, boardsInProgress, tablesTotalCount, boardsFinishedCount, error);
    }

    @NonNull
    public SyncStatus withTableProgressStarting(@NonNull Board starting) {

        final var tablesInProgress = new LinkedList<>(this.boardsInProgress);

        tablesInProgress.add(starting);

        return new SyncStatus(account, Step.PROGRESS, tablesInProgress, increment(boardsTotalCount), boardsFinishedCount, error);

    }

    @NonNull
    public SyncStatus withTableProgressFinished(@NonNull Board finished) {

        if (!Objects.equals(account.getId(), finished.getAccountId())) {
            throw new IllegalArgumentException("Argument must have same accountId as initial " + Account.class.getSimpleName());
        }

        final var tablesInProgress = new LinkedList<>(this.boardsInProgress);

        tablesInProgress.removeIf(board -> Objects.equals(board.getId(), finished.getId()) || Objects.equals(board.getId(), finished.getId()));

        return new SyncStatus(account, step, tablesInProgress, boardsTotalCount, increment(boardsFinishedCount), error);

    }

    @NonNull
    public SyncStatus withError(@NonNull Throwable error) {
        return new SyncStatus(account, Step.ERROR, boardsInProgress, boardsTotalCount, boardsFinishedCount, error);
    }

    @NonNull
    public SyncStatus markAsFinished() {

        if (!boardsInProgress.isEmpty()) {
            throw new IllegalStateException(Step.FINISHED + "can not be set while tables still being in progress: " + boardsInProgress);
        }

        return new SyncStatus(account, Step.FINISHED, boardsInProgress, boardsTotalCount, boardsFinishedCount, error);

    }

    public boolean isFinished() {
        return step.isEndStep;
    }

    private int increment(@Nullable Integer source) {
        return Optional.ofNullable(source).map(value -> value + 1).orElse(1);
    }

    @NonNull
    public Step getStep() {
        return step;
    }

    @NonNull
    public Optional<Integer> getBoardsTotalCount() {
        return Optional.ofNullable(boardsTotalCount);
    }

    @NonNull
    public Optional<Integer> getBoardsFinishedCount() {
        return Optional.ofNullable(boardsFinishedCount);
    }

    @NonNull
    public Collection<String> getBoardsInProgress() {
        return boardsInProgress
                .stream()
                .map(Board::getTitle)
                .toList();
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    @NonNull
    public Account getAccount() {
        return account;
    }

    public enum Step {
        START(),
        PROGRESS(),
        FINISHED(true),
        ERROR(true),
        ;

        private final boolean isEndStep;

        Step() {
            this.isEndStep = false;
        }

        Step(boolean isEndStep) {
            this.isEndStep = isEndStep;
        }
    }
}
