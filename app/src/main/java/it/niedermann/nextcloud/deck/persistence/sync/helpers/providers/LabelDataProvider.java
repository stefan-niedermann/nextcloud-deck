package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.time.Instant;
import java.util.List;

import it.niedermann.nextcloud.deck.DeckLog;
import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.exceptions.HandledServerErrors;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class LabelDataProvider extends AbstractSyncDataProvider<Label> {

    private final List<Label> labels;
    private final Board board;

    public LabelDataProvider(AbstractSyncDataProvider<?> parent, Board board, List<Label> labels) {
        super(parent);
        this.board = board;
        this.labels = labels;
        if (this.labels != null && board != null) {
            for (Label label : labels) {
                label.setBoardId(board.getLocalId());
            }
        }
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Label>> responder, Instant lastSync) {
        responder.onResponse(labels);
    }

    @Override
    public Label getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        return dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        Label existing = dataBaseAdapter.getLabelByBoardIdAndTitleDirectly(entity.getBoardId(), entity.getTitle());
        if (existing != null) {
            entity.setLocalId(existing.getLocalId());
            updateInDB(dataBaseAdapter, accountId, entity, false);
            return entity.getLocalId();
        } else {
            return dataBaseAdapter.createLabelDirectly(accountId, entity);
        }
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity, boolean setStatus) {
        dataBaseAdapter.updateLabel(entity, setStatus);
    }

    private IResponseCallback<Label> getLabelUniqueHandler(DataBaseAdapter dataBaseAdapter, Label entitiy, IResponseCallback<Label> responder) {
        return new IResponseCallback<Label>(responder.getAccount()) {
            @Override
            public void onResponse(Label response) {
                responder.onResponse(response);
            }

            @Override
            public void onError(Throwable throwable) {
                if (HandledServerErrors.LABELS_TITLE_MUST_BE_UNIQUE == HandledServerErrors.fromThrowable(throwable)) {
                    DeckLog.log(throwable.getCause().getMessage() + ": " + entitiy.toString());
                    dataBaseAdapter.deleteLabelPhysically(entitiy);
                    responder.onResponse(entitiy);
                } else {
                    responder.onError(throwable);
                }
            }
        };
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        updateInDB(dataBaseAdapter, accountId, entity, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Label> responder, Label entity) {
        entity.setBoardId(board.getId());
        serverAdapter.createLabel(board.getId(), entity, getLabelUniqueHandler(dataBaseAdapter, entity, responder));
    }

    @Override
    public void deleteInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label label) {
        dataBaseAdapter.deleteLabelPhysically(label);
    }

    @Override
    public void deleteOnServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<Void> callback, Label entity, DataBaseAdapter dataBaseAdapter) {
        serverAdapter.deleteLabel(board.getId(), entity, callback);
    }

    @Override
    public List<Label> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return labels;
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Label> callback, Label entity) {
        serverAdapter.updateLabel(board.getId(), entity, getLabelUniqueHandler(dataBaseAdapter, entity, callback));
    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<Label> entitiesFromServer) {
        List<Label> deletedLabels = findDelta(labels, dataBaseAdapter.getFullBoardByLocalIdDirectly(accountId, board.getLocalId()).getLabels());
        for (Label deletedLabel : deletedLabels) {
            if (deletedLabel.getId() != null) {
                // preserve new, unsynced card.
                dataBaseAdapter.deleteLabelPhysically(deletedLabel);
            }
        }
    }

    @Override
    public Label applyUpdatesFromRemote(Label localEntity, Label remoteEntity, Long accountId) {
        remoteEntity.setBoardId(board.getLocalId());
        return remoteEntity;
    }
}
