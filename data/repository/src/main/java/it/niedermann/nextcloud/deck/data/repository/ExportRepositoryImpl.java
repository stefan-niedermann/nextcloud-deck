package it.niedermann.nextcloud.deck.data.repository;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import org.reactivestreams.FlowAdapters;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Card;
import it.niedermann.nextcloud.deck.domain.model.Column;
import it.niedermann.nextcloud.deck.domain.repository.ExportRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ExportRepositoryImpl implements ExportRepository {

    @Inject
    public ExportRepositoryImpl() {
    }

    @Override
    public Flow.Publisher<byte[]> exportCardToPdf(Card card, List<String> labelNames, List<String> assigneeNames) {
        return FlowAdapters.toFlowPublisher(Flowable.fromCallable(() -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Document document = new Document();
                PdfWriter.getInstance(document, baos);
                document.open();
                document.add(new Paragraph(card.title()));
                document.add(new Paragraph("\n"));
                if (labelNames != null && !labelNames.isEmpty()) {
                    document.add(new Paragraph("Labels: " + String.join(", ", labelNames)));
                }
                if (assigneeNames != null && !assigneeNames.isEmpty()) {
                    document.add(new Paragraph("Assignees: " + String.join(", ", assigneeNames)));
                }
                document.add(new Paragraph("\n"));
                document.add(new Paragraph(card.description()));
                document.close();
                return baos.toByteArray();
            }
        }));
    }

    @Override
    public Flow.Publisher<byte[]> exportBoardToPdf(Board board, Map<Column, List<Card>> cardsByColumn, Map<it.niedermann.nextcloud.deck.domain.model.Label.ID, String> labelNames, Map<it.niedermann.nextcloud.deck.domain.model.User.ID, String> userNames) {
        return FlowAdapters.toFlowPublisher(Flowable.fromCallable(() -> {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                Document document = new Document();
                PdfWriter.getInstance(document, baos);
                document.open();
                document.add(new Paragraph("Board: " + board.title()));
                document.add(new Paragraph("\n"));
                for (Map.Entry<Column, List<Card>> entry : cardsByColumn.entrySet()) {
                    document.add(new Paragraph("Column: " + entry.getKey().title()));
                    for (Card card : entry.getValue()) {
                        document.add(new Paragraph("  - " + card.title()));
                        final List<String> labels = card.labels().stream().map(id -> labelNames.getOrDefault(id, id.value() + "")).collect(Collectors.toList());
                        if (!labels.isEmpty()) {
                            document.add(new Paragraph("    Labels: " + String.join(", ", labels)));
                        }
                        final List<String> assignees = card.assignees().stream().map(id -> userNames.getOrDefault(id, id.value() + "")).collect(Collectors.toList());
                        if (!assignees.isEmpty()) {
                            document.add(new Paragraph("    Assignees: " + String.join(", ", assignees)));
                        }
                        if (card.description() != null && !card.description().isBlank()) {
                            document.add(new Paragraph("    Description: " + card.description()));
                        }
                    }
                    document.add(new Paragraph("\n"));
                }
                document.close();
                return baos.toByteArray();
            }
        }));
    }
}
