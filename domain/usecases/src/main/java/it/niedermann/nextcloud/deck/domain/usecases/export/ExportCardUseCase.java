package it.niedermann.nextcloud.deck.domain.usecases.export;

import org.reactivestreams.FlowAdapters;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.ColumnRepository;
import it.niedermann.nextcloud.deck.domain.repository.ExportRepository;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class ExportCardUseCase {

    private final CardRepository cardRepository;
    private final ExportRepository exportRepository;
    private final BoardRepository boardRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;

    @Inject
    public ExportCardUseCase(CardRepository cardRepository, ExportRepository exportRepository, BoardRepository boardRepository, LabelRepository labelRepository, UserRepository userRepository, ColumnRepository columnRepository) {
        this.cardRepository = cardRepository;
        this.exportRepository = exportRepository;
        this.boardRepository = boardRepository;
        this.labelRepository = labelRepository;
        this.userRepository = userRepository;
        this.columnRepository = columnRepository;
    }

    public Flow.Publisher<byte[]> toPdf(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getCard(cardId))).firstOrError().toFlowable()
                        .flatMap(card -> Flowable.fromPublisher(FlowAdapters.toPublisher(columnRepository.getColumn(card.columnId()))).firstOrError().toFlowable()
                                .flatMap(column -> Flowable.fromPublisher(FlowAdapters.toPublisher(boardRepository.getBoard(column.boardId()))).firstOrError().toFlowable()
                                        .flatMap(board -> Flowable.zip(
                                                Flowable.fromPublisher(FlowAdapters.toPublisher(labelRepository.getNotDeletedLabels(board.id()))).firstOrError().toFlowable(),
                                                Flowable.fromPublisher(FlowAdapters.toPublisher(userRepository.getNotDeletedUsers(board.accountId()))).firstOrError().toFlowable(),
                                                (labels, users) -> {
                                                    final Map<Label.ID, String> labelMap = labels.stream().collect(Collectors.toMap(Label::id, Label::title));
                                                    final Map<User.ID, String> userMap = users.stream().collect(Collectors.toMap(User::id, User::displayName));
                                                    final List<String> labelNames = card.labels().stream().map(id -> labelMap.getOrDefault(id, id.value() + "")).collect(Collectors.toList());
                                                    final List<String> assigneeNames = card.assignees().stream().map(id -> userMap.getOrDefault(id, id.value() + "")).collect(Collectors.toList());
                                                    return FlowAdapters.toPublisher(exportRepository.exportCardToPdf(card, labelNames, assigneeNames));
                                                }
                                        ).flatMap(f -> f))
                                )
                        )
        );
    }

    public Flow.Publisher<String> toOdt(Card.ID cardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getCard(cardId))).firstOrError().toFlowable()
                        .flatMap(card -> Flowable.fromPublisher(FlowAdapters.toPublisher(columnRepository.getColumn(card.columnId()))).firstOrError().toFlowable()
                                .flatMap(column -> Flowable.fromPublisher(FlowAdapters.toPublisher(boardRepository.getBoard(column.boardId()))).firstOrError().toFlowable()
                                        .flatMap(board -> Flowable.zip(
                                                Flowable.fromPublisher(FlowAdapters.toPublisher(labelRepository.getNotDeletedLabels(board.id()))).firstOrError().toFlowable(),
                                                Flowable.fromPublisher(FlowAdapters.toPublisher(userRepository.getNotDeletedUsers(board.accountId()))).firstOrError().toFlowable(),
                                                (labels, users) -> {
                                                    final Map<Label.ID, String> labelMap = labels.stream().collect(Collectors.toMap(Label::id, Label::title));
                                                    final Map<User.ID, String> userMap = users.stream().collect(Collectors.toMap(User::id, User::displayName));
                                                    final StringBuilder sb = new StringBuilder();
                                                    sb.append("""
                                                            <?xml version="1.0" encoding="UTF-8"?>
                                                            <office:document xmlns:office="urn:oasis:names:tc:opendocument:xmlns:office:1.0"
                                                                             xmlns:text="urn:oasis:names:tc:opendocument:xmlns:text:1.0"
                                                                             office:mimetype="application/vnd.oasis.opendocument.text"
                                                                             office:version="1.2">
                                                              <office:body>
                                                                <office:text>
                                                            """);
                                                    sb.append("      <text:h text:outline-level=\"1\">").append(escapeXml(card.title())).append("</text:h>\n");
                                                    if (!card.labels().isEmpty()) {
                                                        sb.append("      <text:p>Labels: ").append(escapeXml(card.labels().stream().map(id -> labelMap.getOrDefault(id, id.value() + "")).collect(Collectors.joining(", ")))).append("</text:p>\n");
                                                    }
                                                    if (!card.assignees().isEmpty()) {
                                                        sb.append("      <text:p>Assignees: ").append(escapeXml(card.assignees().stream().map(id -> userMap.getOrDefault(id, id.value() + "")).collect(Collectors.joining(", ")))).append("</text:p>\n");
                                                    }
                                                    sb.append("      <text:p>").append(escapeXml(card.description())).append("</text:p>\n");
                                                    sb.append("""
                                                                </office:text>
                                                              </office:body>
                                                            </office:document>""");
                                                    return sb.toString();
                                                }
                                        ))))
        );
    }

    private static String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
