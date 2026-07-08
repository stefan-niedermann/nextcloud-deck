package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Flowable;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class LabelRepositoryImpl implements LabelRepository {

    @Inject
    public LabelRepositoryImpl() {

    }

    @Override
    public CompletableFuture<Void> createLabel(Label label) {
        System.out.println("[Mock][" + LabelRepositoryImpl.class.getSimpleName() + "/createLabel]: " + label);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateLabel(Label label) {
        System.out.println("[Mock][" + LabelRepositoryImpl.class.getSimpleName() + "/updateLabel]: " + label);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public Flow.Publisher<Set<Label>> getNotDeletedLabels(long boardId) {
        System.out.println("[Mock][" + LabelRepositoryImpl.class.getSimpleName() + "/getNotDeletedLabels]: " + boardId);
        return FlowAdapters.toFlowPublisher(Flowable.just(Set.of(MockData.MOCK_LABELS)));
    }

    @Override
    public Flow.Publisher<Set<Label>> getLabel(long labelId) {
        System.out.println("[Mock][" + LabelRepositoryImpl.class.getSimpleName() + "/getLabel]: " + labelId);
        //noinspection OptionalGetWithoutIsPresent
        return FlowAdapters.toFlowPublisher(Flowable.just(Set.of(Arrays.stream(MockData.MOCK_LABELS).filter(label -> label.id() == labelId).findAny().get())));
    }

    @Override
    public Flow.Publisher<Collection<Label>> find(String userText) {
        System.out.println("[Mock][" + LabelRepositoryImpl.class.getSimpleName() + "/find]: " + userText);
        return FlowAdapters.toFlowPublisher(Flowable.just(Arrays.stream(MockData.MOCK_LABELS).filter(label -> label.title().toLowerCase().contains(userText.trim().toLowerCase())).collect(Collectors.toSet())));
    }
}