package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers;

import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;

public class LabelDataProvider implements IDataProvider<Label> {

    private List<Label> labels;

    public LabelDataProvider(List<Label> labels) {
        this.labels = labels;
    }

    @Override
    public void getAllFromServer(ServerAdapter serverAdapter, long accountId, IResponseCallback<List<Label>> responder) {
        responder.onResponse(labels);
    }

    @Override
    public Label getSingleFromDB(DataBaseAdapter dataBaseAdapter, long accountId, long remoteId) {
        return dataBaseAdapter.getLabelByRemoteIdDirectly(accountId, remoteId);
    }

    @Override
    public void createInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        dataBaseAdapter.createLabel(accountId, entity);
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, Label entity) {
        dataBaseAdapter.updateLabel(entity);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, Label existingEntity, Label entityFromServer) {
        // ain't goin' deeper <3
        return;
    }
}
