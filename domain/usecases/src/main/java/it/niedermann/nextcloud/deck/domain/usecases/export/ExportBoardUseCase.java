package it.niedermann.nextcloud.deck.domain.usecases.export;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;

public class ExportBoardUseCase {

    /// Each [Card] is mapped to one row. The [Column#title()] is exported as one column in the CSV
    /// @return [CSV](https://de.wikipedia.org/wiki/CSV_(Dateiformat)) of the board containing all information
    public Flow.Publisher<String> toCsv(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented")));
    }

    /// @return a [mermaid kanban chart](https://mermaid.ai/open-source/syntax/kanban.html)
    public Flow.Publisher<String> toMermaid(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented")));
    }
}
