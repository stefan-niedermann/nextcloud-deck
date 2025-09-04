package it.niedermann.nextcloud.deck.remote.helpers.providers.partial;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import it.niedermann.nextcloud.deck.database.DataBaseAdapter;
import it.niedermann.nextcloud.deck.model.full.FullBoard;
import it.niedermann.nextcloud.deck.remote.adapters.ServerAdapter;
import it.niedermann.nextcloud.deck.remote.api.ResponseCallback;
import it.niedermann.nextcloud.deck.remote.helpers.SyncHelper;
import it.niedermann.nextcloud.deck.remote.helpers.providers.BoardDataProvider;

public class BoardWithStacksAndLabelsUpSyncDataProvider extends BoardDataProvider {

    private FullBoard board;

    public BoardWithStacksAndLabelsUpSyncDataProvider(FullBoard boardToSync) {
        board = boardToSync;
    }

    @Override
    public List<FullBoard> getAllChangedFromDB(DataBaseAdapter dataBaseAdapter, long accountId, Instant lastSync) {
        return Collections.singletonList(board);
    }

    @Override
    public void goDeeper(SyncHelper syncHelper, FullBoard existingEntity, FullBoard entityFromServer, ResponseCallback<Boolean> callback) {
        // do nothing!

    }

    @Override
    public void handleDeletes(ServerAdapter serverAdapter, DataBaseAdapter dataBaseAdapter, long accountId, List<FullBoard> entitiesFromServer) {
        // do nothing!
    }
}
