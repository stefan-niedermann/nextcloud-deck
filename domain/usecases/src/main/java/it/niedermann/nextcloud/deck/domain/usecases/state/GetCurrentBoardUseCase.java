package it.niedermann.nextcloud.deck.domain.usecases.state;

import static org.reactivestreams.FlowAdapters.toFlowPublisher;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.domain.repository.StateRepository;
import jakarta.inject.Inject;

public class GetCurrentBoardUseCase {

    private final StateRepository stateRepository;
    private final BoardRepository boardRepository;

    @Inject
    public GetCurrentBoardUseCase(
            StateRepository stateRepository,
            BoardRepository boardRepository
    ) {
        this.stateRepository = stateRepository;
        this.boardRepository = boardRepository;
    }

    public Flow.Publisher<Board> execute(long accountId) {
        final var board = Flowable.fromPublisher(FlowAdapters.toPublisher(stateRepository.getCurrentBoardId(accountId)))
                .map((currentBoardId) -> {
                    return currentBoardId;
                })
                .map(boardRepository::getBoard)

                .map(FlowAdapters::toPublisher)
                .switchMap(Flowable::fromPublisher);

        return toFlowPublisher(board);
    }

}
