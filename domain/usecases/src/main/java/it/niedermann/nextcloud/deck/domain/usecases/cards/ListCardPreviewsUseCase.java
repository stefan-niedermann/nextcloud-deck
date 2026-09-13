package it.niedermann.nextcloud.deck.domain.usecases.cards;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.FilterInformation;
import it.niedermann.nextcloud.deck.domain.model.query.PreviewCard;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import jakarta.inject.Inject;

public class ListCardPreviewsUseCase {

    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;

    @Inject
    public ListCardPreviewsUseCase(
            CardRepository cardRepository,
            ColumnRepository columnRepository
    ) {
        this.cardRepository = cardRepository;
        this.columnRepository = columnRepository;
    }

    public Flow.Publisher<List<PreviewCard>> execute(Board.ID boardId) {
        return execute(boardId, FilterInformation.EMPTY);
    }

    public Flow.Publisher<List<PreviewCard>> execute(Board.ID boardId, FilterInformation filter) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(columnRepository.getColumnIDs(boardId)))
                        .switchMap(columnIds -> {
                            final List<Flowable<List<PreviewCard>>> cardFlowables = columnIds.stream()
                                    .map(id -> Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getNotDeletedCardPreviews(id, filter))))
                                    .collect(Collectors.toList());
                            if (cardFlowables.isEmpty()) {
                                return Flowable.just(Collections.<PreviewCard>emptyList());
                            }
                            return Flowable.combineLatest(cardFlowables, args -> Arrays.stream(args)
                                    .flatMap(arg -> ((List<PreviewCard>) arg).stream())
                                    .collect(Collectors.toList()));
                        })
        );
    }

    public Flow.Publisher<List<PreviewCard>> execute(Column.ID columnId) {
        return execute(columnId, FilterInformation.EMPTY);
    }

    public Flow.Publisher<List<PreviewCard>> execute(Column.ID columnId, FilterInformation filter) {
        return cardRepository.getNotDeletedCardPreviews(columnId, filter);
    }
}
