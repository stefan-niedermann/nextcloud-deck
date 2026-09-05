package it.niedermann.nextcloud.deck.domain.usecases.export;

import org.reactivestreams.FlowAdapters;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.model.User;
import it.niedermann.nextcloud.deck.domain.repository.BoardRepository;
import it.niedermann.nextcloud.deck.domain.repository.CardRepository;
import it.niedermann.nextcloud.deck.domain.repository.ExportRepository;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import it.niedermann.nextcloud.deck.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class ExportBoardUseCase {

    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final ExportRepository exportRepository;

    @Inject
    public ExportBoardUseCase(CardRepository cardRepository, BoardRepository boardRepository, LabelRepository labelRepository, UserRepository userRepository, ExportRepository exportRepository) {
        this.cardRepository = cardRepository;
        this.boardRepository = boardRepository;
        this.labelRepository = labelRepository;
        this.userRepository = userRepository;
        this.exportRepository = exportRepository;
    }

    /// Each [Card] is mapped to one row. The [Column#title()] is exported as one column in the CSV
    /// @return [CSV](https://de.wikipedia.org/wiki/CSV_(Dateiformat)) of the board containing all information
    public Flow.Publisher<String> toCsv(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(boardRepository.getBoard(boardId))).firstOrError().toFlowable()
                        .flatMap(board -> Flowable.zip(
                                Flowable.fromPublisher(FlowAdapters.toPublisher(labelRepository.getNotDeletedLabels(boardId))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(userRepository.getNotDeletedUsers(board.accountId()))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getNotDeletedCardsByColumn(boardId))).firstOrError().toFlowable(),
                                (labels, users, cardsByColumn) -> {
                                    final Map<Label.ID, String> labelMap = labels.stream().collect(Collectors.toMap(Label::id, Label::title));
                                    final Map<User.ID, String> userMap = users.stream().collect(Collectors.toMap(User::id, User::displayName));
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("Title,Description,Column,Labels,Assignees\n");
                                    cardsByColumn.forEach((column, cards) -> {
                                        final String columnTitle = column.title();
                                        for (Card card : cards) {
                                            sb.append(escapeCsv(card.title())).append(",")
                                                    .append(escapeCsv(card.description())).append(",")
                                                    .append(escapeCsv(columnTitle)).append(",")
                                                    .append(escapeCsv(card.labels().stream().map(id -> labelMap.getOrDefault(id, id.value() + "")).collect(Collectors.joining("; ")))).append(",")
                                                    .append(escapeCsv(card.assignees().stream().map(id -> userMap.getOrDefault(id, id.value() + "")).collect(Collectors.joining("; ")))).append("\n");
                                        }
                                    });
                                    return sb.toString();
                                }
                        ))
        );
    }

    /// @return a [mermaid kanban chart](https://mermaid.ai/open-source/syntax/kanban.html)
    public Flow.Publisher<String> toMermaid(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(boardRepository.getBoard(boardId))).firstOrError().toFlowable()
                        .flatMap(board -> Flowable.zip(
                                Flowable.fromPublisher(FlowAdapters.toPublisher(labelRepository.getNotDeletedLabels(boardId))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(userRepository.getNotDeletedUsers(board.accountId()))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getNotDeletedCardsByColumn(boardId))).firstOrError().toFlowable(),
                                (labels, users, cardsByColumn) -> {
                                    final Map<Label.ID, String> labelMap = labels.stream().collect(Collectors.toMap(Label::id, Label::title));
                                    final Map<User.ID, String> userMap = users.stream().collect(Collectors.toMap(User::id, User::displayName));
                                    final StringBuilder sb = new StringBuilder();
                                    sb.append("kanban\n");
                                    sb.append("  %% Board: ").append(board.title()).append("\n");
                                    cardsByColumn.forEach((column, cards) -> {
                                        sb.append("  ").append(column.title()).append("\n");
                                        for (Card card : cards) {
                                            sb.append("    ").append(card.title().replace("\n", " "));
                                            final List<String> tags = new java.util.ArrayList<>();
                                            card.labels().forEach(id -> tags.add(labelMap.getOrDefault(id, id.value() + "")));
                                            card.assignees().forEach(id -> tags.add("@" + userMap.getOrDefault(id, id.value() + "")));
                                            if (!tags.isEmpty()) {
                                                sb.append(" [").append(String.join(", ", tags)).append("]");
                                            }
                                            sb.append("\n");
                                        }
                                    });
                                    return sb.toString();
                                }
                        ))
        );
    }

    public Flow.Publisher<String> toOdt(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(boardRepository.getBoard(boardId))).firstOrError().toFlowable()
                        .flatMap(board -> Flowable.zip(
                                Flowable.fromPublisher(FlowAdapters.toPublisher(labelRepository.getNotDeletedLabels(boardId))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(userRepository.getNotDeletedUsers(board.accountId()))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getNotDeletedCardsByColumn(boardId))).firstOrError().toFlowable(),
                                (labels, users, cardsByColumn) -> {
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
                                    sb.append("      <text:h text:outline-level=\"1\">").append(escapeXml(board.title())).append("</text:h>\n");
                                    cardsByColumn.forEach((column, cards) -> {
                                        sb.append("      <text:h text:outline-level=\"2\">").append(escapeXml(column.title())).append("</text:h>\n");
                                        for (Card card : cards) {
                                            sb.append("      <text:h text:outline-level=\"3\">").append(escapeXml(card.title())).append("</text:h>\n");
                                            if (!card.labels().isEmpty()) {
                                                sb.append("      <text:p>Labels: ").append(escapeXml(card.labels().stream().map(id -> labelMap.getOrDefault(id, id.value() + "")).collect(Collectors.joining(", ")))).append("</text:p>\n");
                                            }
                                            if (!card.assignees().isEmpty()) {
                                                sb.append("      <text:p>Assignees: ").append(escapeXml(card.assignees().stream().map(id -> userMap.getOrDefault(id, id.value() + "")).collect(Collectors.joining(", ")))).append("</text:p>\n");
                                            }
                                            sb.append("      <text:p>").append(escapeXml(card.description())).append("</text:p>\n");
                                        }
                                    });
                                    sb.append("""
                                                </office:text>
                                              </office:body>
                                            </office:document>""");
                                    return sb.toString();
                                }
                        ))
        );
    }

    public Flow.Publisher<byte[]> toPdf(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                Flowable.fromPublisher(FlowAdapters.toPublisher(boardRepository.getBoard(boardId))).firstOrError().toFlowable()
                        .flatMap(board -> Flowable.zip(
                                Flowable.fromPublisher(FlowAdapters.toPublisher(labelRepository.getNotDeletedLabels(boardId))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(userRepository.getNotDeletedUsers(board.accountId()))).firstOrError().toFlowable(),
                                Flowable.fromPublisher(FlowAdapters.toPublisher(cardRepository.getNotDeletedCardsByColumn(boardId))).firstOrError().toFlowable(),
                                (labels, users, cardsByColumn) -> {
                                    final Map<Label.ID, String> labelMap = labels.stream().collect(Collectors.toMap(Label::id, Label::title));
                                    final Map<User.ID, String> userMap = users.stream().collect(Collectors.toMap(User::id, User::displayName));
                                    return FlowAdapters.toPublisher(exportRepository.exportBoardToPdf(board, cardsByColumn, labelMap, userMap));
                                }
                        ).flatMap(f -> f))
        );
    }

    private static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains(";") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
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
