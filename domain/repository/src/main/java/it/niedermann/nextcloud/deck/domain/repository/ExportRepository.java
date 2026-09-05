package it.niedermann.nextcloud.deck.domain.repository;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;

public interface ExportRepository {
    Flow.Publisher<byte[]> exportCardToPdf(Card card, List<String> labelNames, List<String> assigneeNames);

    Flow.Publisher<byte[]> exportBoardToPdf(Board board, Map<Column, List<Card>> cardsByColumn, Map<it.niedermann.nextcloud.deck.domain.model.Label.ID, String> labelNames, Map<it.niedermann.nextcloud.deck.domain.model.User.ID, String> userNames);
}
