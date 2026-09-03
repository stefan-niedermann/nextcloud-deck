package it.niedermann.nextcloud.deck.domain.usecases.export;

import org.reactivestreams.FlowAdapters;

import java.util.concurrent.Flow;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;

public class ExportCardUseCase {

    public Flow.Publisher<byte[]> toPdf(Card.ID boardId) {
        return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented")));
    }

    public Flow.Publisher<String> toOdt(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(Flowable.error(new UnsupportedOperationException("Not yet implemented")));
    }
}
