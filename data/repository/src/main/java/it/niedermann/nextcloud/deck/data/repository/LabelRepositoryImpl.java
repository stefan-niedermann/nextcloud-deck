package it.niedermann.nextcloud.deck.data.repository;

import org.reactivestreams.FlowAdapters;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

import io.reactivex.rxjava3.schedulers.Schedulers;
import it.niedermann.nextcloud.deck.data.local.dao.LabelDao;
import it.niedermann.nextcloud.deck.data.local.mapper.LabelMapper;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Label;
import it.niedermann.nextcloud.deck.domain.repository.LabelRepository;
import jakarta.inject.Inject;

public class LabelRepositoryImpl implements LabelRepository {

    private final LabelDao labelDao;
    private final LabelMapper labelMapper;

    @Inject
    public LabelRepositoryImpl(LabelDao labelDao,
                               LabelMapper labelMapper) {
        this.labelDao = labelDao;
        this.labelMapper = labelMapper;
    }

    @Override
    public CompletableFuture<Void> createLabel(Label label) {
        // TODO: Local-first or Sync?
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> updateLabel(Label label) {
        return labelDao.updateRx(labelMapper.toEntity(label));
    }

    @Override
    public Flow.Publisher<Set<Label>> getNotDeletedLabels(Board.ID boardId) {
        return FlowAdapters.toFlowPublisher(
                labelDao.getLabelsByBoard(boardId.value())
                        .map(entities -> Set.copyOf(labelMapper.toTOList(entities)))
                        .subscribeOn(Schedulers.io())
        );
    }

    @Override
    public Flow.Publisher<Set<Label>> getLabel(Label.ID labelId) {
        // TODO: Check if getLabel is actually meant to return a Set of one label or something else
        return null;
    }

    @Override
    public Flow.Publisher<Collection<Label>> find(String userText) {
        // TODO: Implement search in LabelDao
        return null;
    }
}
