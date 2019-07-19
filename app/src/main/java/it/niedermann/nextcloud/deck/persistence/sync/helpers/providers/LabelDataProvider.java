package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Board;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;

public class LabelDataProvider extends AbstractSyncDataProvider<Label> {

    private List<Label> labels;
    private Board board;

    public LabelDataProvider(AbstractSyncDataProvider<?> parent, Board board, List<Label> labels) {
        super(parent);
        this.board = board;
        this.labels = labels;
        if (this.labels!= null && board != null){
            for (Label label : labels) {
                label.setBoardId(board.getLocalId());
            }
        }
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Label>> responder, Date lastSync) {
        responder.onResponse(labels);
    }

    @Override
    public Label getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        return dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, entity.getEntity().getId());
    }

    @Override
    public long createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        return dataBaseAdapter.createLabel(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        dataBaseAdapter.updateLabel(entity, false);
    }

    @Override
    public void createOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Label> responder, Label entity) {
        entity.setBoardId(board.getId());
        serverAdapter.createLabel(board.getId(), entity, responder);
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
    public List<Label> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return labels;
    }

    @Override
    public void updateOnServer(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, IResponseCallback<Label> callback, Label entity) {
        serverAdapter.updateLabel(board.getId(), entity, callback);
    }
}
