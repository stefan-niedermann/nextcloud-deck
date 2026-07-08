package it.niedermann.nextcloud.deck.domain.repository;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import it.niedermann.nextcloud.deck.domain.model.Label;

public interface LabelRepository {

    CompletableFuture<Void> createLabel(Label label);

    CompletableFuture<Void> updateLabel(Label label);

    Flow.Publisher<Set<Label>> getNotDeletedLabels(long boardId);

    Flow.Publisher<Set<Label>> getLabel(long labelId);

    Flow.Publisher<Collection<Label>> find(String userText);
}
