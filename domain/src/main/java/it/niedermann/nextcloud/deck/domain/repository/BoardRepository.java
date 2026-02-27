package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.adapter.database.DatabaseAdapter;
import it.niedermann.nextcloud.deck.domain.adapter.sync.SyncAdapter;
import it.niedermann.nextcloud.deck.domain.model.Board;

public class BoardRepository extends AbstractRepository {

    public BoardRepository(@NonNull DatabaseAdapter databaseAdapter, @NonNull SyncAdapter syncAdapter) {
        super(databaseAdapter, syncAdapter);
    }

    /// @return all [Board]s that are not `deleted`
    public Flowable<Collection<Board>> getBoards(long accountId) {
        return Flowable.just(List.of(
                new Board(),
                new Board(),
                new Board(),
                new Board(),
                new Board()
        ));
    }

    public Flowable<Integer> getUpcomingCardsView(long boardId) {
        return Flowable.just(3);
    }

    public Flowable<Board> getBoard(long boardId) {
        final var mock = new Board();
        mock.setId(boardId);
        return Flowable.just(mock);
    }
}
