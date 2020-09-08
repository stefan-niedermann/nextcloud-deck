package it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.partial;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import it.niedermann.nextcloud.deck.api.IResponseCallback;
import it.niedermann.nextcloud.deck.model.Label;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.adapters.db.DataBaseAdapter;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.persistence.sync.helpers.providers.BoardDataProvider;

public class BoardWithStacksAndLabelsUpSyncDataProvider extends BoardDataProvider {

    private FullBoard board;

    public BoardWithStacksAndLabelsUpSyncDataProvider(FullBoard boardToSync) {
        board = boardToSync;
    }

    @Override
    public void updateInDB(DataBaseAdapter dataBaseAdapter, long accountId, FullBoard entity, boolean setStatus) {
        //merge labels (created at board creation)
        if (entity!= null && entity.getLabels() != null) {
            for (Label label : entity.getLabels()) {
                Label existing = dataBaseAdapter.getLabelByBoardIdAndTitleDirectly(board.getLocalId(), label.getTitle());
                if (existing != null) {
                    existing.setId(label.getId());
                    dataBaseAdapter.updateLabel(existing, false);
                }
            }
        }
        super.updateInDB(dataBaseAdapter, accountId, entity, setStatus);
    }

    @Override
    public List<FullBoard> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Date lastSync) {
        return Collections.singletonList(board);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer, IResponseCallback<Boolean> callback) {
        // do nothing!

    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<FullBoard> entitiesFromServer) {
        // do nothing!
    }
}
