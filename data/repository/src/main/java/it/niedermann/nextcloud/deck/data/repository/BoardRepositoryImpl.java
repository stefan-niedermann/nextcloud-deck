package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import jakarta.inject.Inject;

public class BoardRepositoryImpl implements BoardRepository {


    private static final Color[] sampleColors = new Color[]{
            Color.decode("#b6469d"),
            Color.decode("#bf678b"),
            Color.decode("#c98879"),
            Color.decode("#ddcb55"),
            Color.decode("#a5b872"),
            Color.decode("#6ea68f"),
            Color.decode("#3794ac"),
            Color.decode("#0082c9"),
            Color.decode("#2d73be"),
            Color.decode("#5b64b3"),
            Color.decode("#8855a8")
    };

    private static final Board[] sampleBoards = new Board[]{

            new Board(1, "Board #1", sampleColors[1], List.of(
                    new Column(1, "ToDo"),
                    new Column(2, "WiP"),
                    new Column(3, "Done")
            )),
            new Board(2, "Board #2", sampleColors[2], List.of(
                    new Column(4, "Erste Spalte"),
                    new Column(5, "Zweite Spalte"),
                    new Column(6, "Dritte Spalt")
            )),
            new Board(3, "Board #3", sampleColors[3], List.of(
                    new Column(7, "One"),
                    new Column(8, "Two"),
                    new Column(9, "Three")
            )),
            new Board(4, "Board #4", sampleColors[4], Collections.emptyList()),
            new Board(5, "Board #5", sampleColors[5], Collections.emptyList()),
            new Board(6, "Board #6", sampleColors[6], Collections.emptyList()),
            new Board(7, "Board #7", sampleColors[7], Collections.emptyList()),
            new Board(8, "Board #8", sampleColors[8], Collections.emptyList()),
            new Board(9, "Board #9", sampleColors[9], Collections.emptyList()),
            new Board(10, "Board #10", sampleColors[10], Collections.emptyList())
    };

    @Inject
    public BoardRepositoryImpl(
    ) {
    }

    @Override
    public CompletableFuture<Void> createBoard(Board board) {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Flow.Publisher<Board> getBoard(long boardId) {
        return FlowAdapters.toFlowPublisher(Flowable.just(sampleBoards[(int) boardId - 1]));
    }

    @SuppressWarnings("NewApi")
    @Override
    public Flow.Publisher<List<Board>> getNotDeletedBoards(long accountId) {
        return FlowAdapters.toFlowPublisher(Single.just(Arrays.stream(sampleBoards).toList()).toFlowable());
    }
}