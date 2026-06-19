package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Label;

public interface LabelRepository {

    CompletableFuture<Void> createLabel(Label card);

    CompletableFuture<Void> updateLabel(Label card);

    Flow.Publisher<Set<Label>> getNotDeletedLabels(long boardId);

    Flow.Publisher<Set<Label>> getLabel(long labelId);
}
